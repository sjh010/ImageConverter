package com.mobileleader.image.socket.server;

import java.nio.charset.Charset;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.gson.Gson;
import com.mobileleader.image.deamon.ConvertDaemon;
import com.mobileleader.image.model.ConvertRequest;
import com.mobileleader.image.model.ConvertResponse;
import com.mobileleader.image.type.ResponseCodeType;
import com.mobileleader.image.type.JobType;
import com.mobileleader.image.util.JsonUtils;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.CharsetUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@Sharable
@RequiredArgsConstructor
public class RequestHandler extends ChannelInboundHandlerAdapter {	
	
	@Autowired
	private ConvertDaemon convertDaemon;

	/**
	 * 클라이언트로 부터 메시지 수신
	 */
	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		
		ByteBuf mBuf = (ByteBuf) msg;
		
		ConvertRequest request = new ConvertRequest();
		
		request = JsonUtils.fromJson(mBuf.toString(Charset.forName("UTF-8")), ConvertRequest.class);

		ChannelFuture channelFuture = ctx.write(Unpooled.EMPTY_BUFFER);
		
		// 이미지 변환용 쓰레드 개수 변경 요청
		if ("Y".equalsIgnoreCase(request.getThreadChangeYn()) && request.getThreadChangeCount() > 0) {
			convertDaemon.changeCoreThreadCount(request.getThreadChangeCount());
			
			channelFuture.channel().writeAndFlush(Unpooled.copiedBuffer(JsonUtils.toJson(new ConvertResponse(ResponseCodeType.SUCEESS.getCode())), CharsetUtil.UTF_8));	
			channelFuture.addListener(ChannelFutureListener.CLOSE);
		} else { // 이미지 변환 요청
			// 요청 큐에 삽입
			convertDaemon.pushRequest(request, channelFuture);
			
			if (JobType.BATCH.getCode().equalsIgnoreCase(request.getJobType())) {
				// status code 추기
				channelFuture.channel().writeAndFlush(Unpooled.copiedBuffer(JsonUtils.toJson(new ConvertResponse("200")), CharsetUtil.UTF_8));	
				channelFuture.addListener(ChannelFutureListener.CLOSE);
			}
		}		
	}

	@Override
	public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
		ctx.flush();
	}
	
	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		super.exceptionCaught(ctx, cause);
	}
	
}
