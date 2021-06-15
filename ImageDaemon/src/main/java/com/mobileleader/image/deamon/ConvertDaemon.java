package com.mobileleader.image.deamon;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
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
import com.mobileleader.image.type.ResponseCodeType;
import com.mobileleader.image.type.JobType;
import com.mobileleader.image.util.JsonUtils;

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
	private ImageConvertOutboundClient imageConvertOutboundClient;		// 배치 요청 응답을 위한 client

	@Autowired
	private ImageConvertService imageConvertService;					// 이미지 변환 담당 서비스
	
	@Autowired
	private ConvertStatusMapper convertStatusMapper;					// 배치 변환 요청 상태 매퍼
	
	private PriorityQueue<ConvertRequest> requestQueue;					// 이미지 변환 요청 큐(우선순위 큐)
	
	private Map<String, ChannelFuture> channelFutureMap;				// 이미지 요청 ID별 응답을 위한 ChannelFuture Map
	
	@PostConstruct
	public void sendConvertFailResponse() {
		List<ConvertStatus> statusList = convertStatusMapper.selectConvertFailList();
		
		if (!ObjectUtils.isEmpty(statusList)) {
			for (ConvertStatus status : statusList) {
				ConvertResponse response = new ConvertResponse();
				response.setJobId(status.getJobId());
				response.setResponseCode(ResponseCodeType.UNKNOWN.getCode());
				imageConvertOutboundClient.sendResponse(response);
				convertStatusMapper.deleteBatchConvertStatus(status.getJobId());
			}
		}
	}
	
	public ConvertDaemon() {
		this.requestQueue = new PriorityQueue<ConvertRequest>();
		this.channelFutureMap = new HashMap<String, ChannelFuture>();
	}

	public void pushRequest(ConvertRequest request, ChannelFuture channelFuture) {
		this.requestQueue.add(request);
		this.channelFutureMap.put(request.getJobId(), channelFuture);
		
		if (JobType.BATCH.getCode().equalsIgnoreCase(request.getJobType())) {
			// DB
			ConvertStatus convertStatus = new ConvertStatus();
			convertStatus.setFilePath(request.getSrcPath());
			convertStatus.setJobId(request.getJobId());
			
			int dbResult = convertStatusMapper.insertBatchConvertRequest(convertStatus);
		}
	}
	
	public void changeCoreThreadCount(int threadChangeCount) {
		log.info("Change Thread Count : {} -> {}", convertExecutor.getCorePoolSize(), threadChangeCount);
		convertExecutor.setCorePoolSize(threadChangeCount);
		log.info("Thread core pool size : {}", convertExecutor.getCorePoolSize());
		
	}
	
	@Scheduled(cron = "* * * * * *")
	public void processs() {
		
		// 큐에 요청이 쌓여 있는지 감시
		if (!requestQueue.isEmpty()) {
			convertExecutor.execute(new Runnable() {
				@Override
				@Async
				public void run() {
					log.info(requestQueue.toString());
					ConvertRequest request = requestQueue.poll();
					
					log.info("Request : {}", JsonUtils.ObjectPrettyPrint(request));
					
					ChannelFuture channelFuture = channelFutureMap.get(request.getJobId());
					
					ConvertResponse response = imageConvertService.convert(request); // 이미지 변환
					response.setDesRootPath(request.getDesRootPath());
					
					if (JobType.REALTIME.getCode().equalsIgnoreCase(request.getJobType())) {		// 실시간 요청
						try {
							channelFuture.channel().writeAndFlush(Unpooled.copiedBuffer(new Gson().toJson(response), CharsetUtil.UTF_8));
							channelFuture.addListener(ChannelFutureListener.CLOSE);
						} catch (Exception e) {
							log.error("Error", e);
							channelFuture.addListener(ChannelFutureListener.CLOSE);
						} 
					} else if (JobType.BATCH.getCode().equalsIgnoreCase(request.getJobType())) {	// 배치 요청
						try {
							imageConvertOutboundClient.sendResponse(response);
							
							convertStatusMapper.deleteBatchConvertStatus(request.getJobId());
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
