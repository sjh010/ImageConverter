package com.mobileleader.edoc.client;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;
import com.mobileleader.image.model.ConvertRequest;
import com.mobileleader.image.model.ConvertResponse;
import com.mobileleader.image.model.IcMaskingInfo;
import com.mobileleader.image.type.ConvertExtentionType;
import com.mobileleader.image.type.ConvertType;
import com.mobileleader.image.type.JobType;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

public class EchoClientHandler extends ChannelInboundHandlerAdapter {

	@Override
	// 소켓 채널이 최초 활성화되었을 때 실행된다.
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		
		ConvertRequest request = new ConvertRequest();
		request.setConvType(ConvertType.IMAGE_TO_PDF.getCode());
		request.setRstType(ConvertExtentionType.PDF.getCode());
		request.setJobId("jobId1");
		request.setSrcPath("D:\\convert\\sample_jpg.jpg");
		request.setDesRootPath("D:\\convert\\");
		request.setDesFileName("convertedFile.pdf");
		request.setJobType(JobType.REALTIME.getCode());
		
		List<IcMaskingInfo> maskingInfos = new ArrayList<IcMaskingInfo>();
		maskingInfos.add(new IcMaskingInfo(100, 100, 300, 50));
		maskingInfos.add(new IcMaskingInfo(100, 200, 300, 50));
		maskingInfos.add(new IcMaskingInfo(100, 300, 300, 50));
		maskingInfos.add(new IcMaskingInfo(100, 400, 300, 50));
		maskingInfos.add(new IcMaskingInfo(100, 500, 300, 50));
		
		request.setMaskInfos(maskingInfos);
		
		Gson gson = new Gson();
		String jsonStr = gson.toJson(request);
		
		ByteBuf messageBuffer = Unpooled.buffer();
		messageBuffer.writeBytes(jsonStr.getBytes());
		
		StringBuilder builder = new StringBuilder();
		builder.append("전송한 문자열 [");
		builder.append(jsonStr);
		builder.append("]");
		
		System.out.println(builder.toString());
		
		ctx.writeAndFlush(messageBuffer);
	}

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		String readMessage = ((ByteBuf) msg).toString(Charset.defaultCharset());
	
		Gson gson = new Gson();
		ConvertResponse response = gson.fromJson(readMessage, ConvertResponse.class);
		
		System.out.println("Response : " + response.toString());
	
	}

	@Override
	public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
		ctx.close();
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		cause.printStackTrace();
		ctx.close();
	}

	
}
