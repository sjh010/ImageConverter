package com.mobileleader.image.socket.server.config;

import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import com.mobileleader.image.socket.server.ImageConvertInboundServer;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ApplicationStartupTask implements ApplicationListener<ApplicationReadyEvent> {

	private final ImageConvertInboundServer imageConvertInboundServer;

	@Override
	public void onApplicationEvent(ApplicationReadyEvent event) {
		imageConvertInboundServer.start();
	}

}
