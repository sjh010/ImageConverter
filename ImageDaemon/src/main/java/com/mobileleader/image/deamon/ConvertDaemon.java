package com.mobileleader.image.deamon;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import com.google.gson.Gson;
import com.mobileleader.image.data.dto.ConvertStatus;
import com.mobileleader.image.data.mapper.ConvertStatusMapper;
import com.mobileleader.image.model.ConvertRequest;
import com.mobileleader.image.model.ConvertResponse;
import com.mobileleader.image.service.ImageConvertService;
import com.mobileleader.image.socket.client.ImageConvertOutboundClient;
import com.mobileleader.image.type.JobType;
import com.mobileleader.image.type.ResponseCodeType;
import com.mobileleader.image.util.JsonUtils;
import com.mobileleader.image.util.TimeLogUtils;

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.util.CharsetUtil;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class ConvertDaemon {
	
	@Autowired
	@Qualifier("convertExecutor")
	private ThreadPoolTaskExecutor convertExecutor;						// thread pool 관리를 위한 executor

	@Autowired
	private ImageConvertService imageConvertService;					// 이미지 변환 담당 서비스
	
	@Autowired
	private ConvertStatusMapper convertStatusMapper;					// 배치 변환 요청 상태 매퍼
	
	@Autowired
	private ImageConvertOutboundClient imageConvertOutboundClient;		// 배치 요청 응답을 위한 client
	
	private PriorityQueue<ConvertRequest> requestQueue;					// 이미지 변환 요청 큐(우선순위 큐)
	
	private Map<String, ChannelFuture> channelFutureMap;				// 이미지 요청 JOB ID별 응답을 위한 ChannelFuture Map
	
	public ConvertDaemon() {
		this.requestQueue = new PriorityQueue<ConvertRequest>();
		this.channelFutureMap = new HashMap<String, ChannelFuture>();
	}
	
	/**
	 * 이미지 변환 중에 데몬이 비정상 종료 되었을 경우, 
	 * 데몬 재기동 시에 비정상 종료 전 이미지 변환 작업에 대한 실패 응답 전송  
	 */
	@PostConstruct
	public void sendConvertFailResponse() {
		List<ConvertStatus> statusList = convertStatusMapper.selectConvertFailList();
		
		if (!ObjectUtils.isEmpty(statusList)) {
			for (ConvertStatus status : statusList) {
				ConvertResponse response = new ConvertResponse();
				response.setJobId(status.getJobId());
				response.setResponseCode(ResponseCodeType.SHUTDOWN_DAEMON.getCode());
				imageConvertOutboundClient.sendResponse(response);
				convertStatusMapper.deleteBatchConvertStatus(status.getJobId());
			}
		}
	}
	
	/**
	 * 이미지 변환 작업에 사용할 Thread 개수 변경
	 */
	public void changeCoreThreadCount(int threadChangeCount) {
		log.info("Change thread count : {} -> {}", convertExecutor.getCorePoolSize(), threadChangeCount);
		
		convertExecutor.setCorePoolSize(threadChangeCount);
		convertExecutor.setMaxPoolSize(threadChangeCount);
		
		log.info("Thread core pool size : {}", convertExecutor.getCorePoolSize());
		log.info("Thread max pool size : {}", convertExecutor.getMaxPoolSize());
	}

	/**
	 * 이미지 변환 요청을 이미지 변환 요청 큐에 삽입 후, 변환 작업 수행
	 */
	public void addRequest(ConvertRequest request, ChannelFuture channelFuture) {
		log.info("[{}] add to Queue", request.getJobId());
		
		this.requestQueue.add(request);
		this.channelFutureMap.put(request.getJobId(), channelFuture);
		
		if (JobType.BATCH.getCode().equalsIgnoreCase(request.getJobType())) {
			// DB
			ConvertStatus convertStatus = new ConvertStatus();
			convertStatus.setFilePath(request.getSrcPath());
			convertStatus.setJobId(request.getJobId());
			
			long startTime = System.currentTimeMillis();
			convertStatusMapper.insertBatchConvertRequest(convertStatus);
			TimeLogUtils.logging(startTime, System.currentTimeMillis(), "[" + request.getJobId() +"] DB insert");
		}
		
		process();
	}
	
	/**
	 * 이미지 변환 수행
	 */
	private void process() {
		// 이미지 변환 요청에 Queue에 쌓여 있는지 확인
		if (!requestQueue.isEmpty()) {
			convertExecutor.execute(new Runnable() {
				@Override
				@Async
				public void run() {
					ConvertRequest request = requestQueue.poll();
					
					log.info("Request : {}", JsonUtils.ObjectPrettyPrint(request));
					
					ChannelFuture channelFuture = channelFutureMap.get(request.getJobId());
					
					long startTime = System.currentTimeMillis();
					
					ConvertResponse response = imageConvertService.convert(request); // 이미지 변환
					response.setDesRootPath(request.getDesRootPath());
					
					TimeLogUtils.logging(startTime, System.currentTimeMillis(), "[" + request.getJobId() + "] Convert image");
					
					if (JobType.REALTIME.getCode().equalsIgnoreCase(request.getJobType())) {		// 실시간 요청
						try {
							startTime = System.currentTimeMillis();
							
							channelFuture.channel().writeAndFlush(Unpooled.copiedBuffer(new Gson().toJson(response), CharsetUtil.UTF_8));
							channelFuture.addListener(ChannelFutureListener.CLOSE);
							
							TimeLogUtils.logging(startTime, System.currentTimeMillis(), "[" + request.getJobId() + "] Send response (R)");
						} catch (Exception e) {
							log.error("Error", e);
							channelFuture.addListener(ChannelFutureListener.CLOSE);
						} 
					} else if (JobType.BATCH.getCode().equalsIgnoreCase(request.getJobType())) {	// 배치 요청
						try {
							startTime = System.currentTimeMillis();
							
							imageConvertOutboundClient.sendResponse(response);
							
							TimeLogUtils.logging(startTime, System.currentTimeMillis(), "[" + request.getJobId() + "] Send response (B)");
							startTime = System.currentTimeMillis();
							
							convertStatusMapper.deleteBatchConvertStatus(request.getJobId());
							
							TimeLogUtils.logging(startTime, System.currentTimeMillis(), "[" + request.getJobId() + "] DB delete");
						} catch (Exception e) {
							e.printStackTrace();
						}
					} else {
						channelFuture.channel().writeAndFlush(Unpooled.EMPTY_BUFFER);
						channelFuture.addListener(ChannelFutureListener.CLOSE);
					}
				}
			});
		}
	}
}
