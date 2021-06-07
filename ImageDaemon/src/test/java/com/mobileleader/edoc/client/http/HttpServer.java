package com.mobileleader.edoc.client.http;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;
import io.netty.handler.logging.LoggingHandler;

public class HttpServer {

	private int port = 8991;

	private static Logger logger = LoggerFactory.getLogger(HttpServer.class);

	// constructor

	// main method, same as simple protocol server

	public void run() throws Exception {

		ServerBootstrap b = new ServerBootstrap();
		b.group(new NioEventLoopGroup(1), new NioEventLoopGroup()).channel(NioServerSocketChannel.class).handler(new LoggingHandler())
				.childHandler(new ChannelInitializer() {
					@Override
					protected void initChannel(Channel ch) throws Exception {
						// TODO Auto-generated method stub
						ChannelPipeline p = ch.pipeline();
						p.addLast(new HttpRequestDecoder());
						p.addLast(new HttpResponseEncoder());
						p.addLast(new CustomHttpServerHandler());
					}
				});
		
		Channel channel = b.bind(8991).sync().channel();
		ChannelFuture channelFuture = channel.closeFuture();
		channelFuture.sync();

	}
	
	public static void main(String[] args) {
		HttpServer server = new HttpServer();
		
		try {
			server.run();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
