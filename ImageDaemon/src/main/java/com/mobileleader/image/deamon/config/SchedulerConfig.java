package com.mobileleader.image.deamon.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
@EnableScheduling
@EnableAsync
public class SchedulerConfig {
	
	@Bean(name = "convertExecutor")
	public ThreadPoolTaskExecutor convertExecutor() {
		ThreadPoolTaskExecutor convertExecutor = new ThreadPoolTaskExecutor();
		
		// 실행 할 pool size를 지정한다. 기본값 1, 기본적으로 설정한 pool size 대로 thread가 실행되고,
		// pool size 보다 더 많은 요청이 오는 경우 queue capacity에 쌓이고 queue capacity에 설정한
		// 값보다 넘는 대기열이 있는 경우에 max pool size에 설정한 값 안에서 thread를 추가로 생성하게 된다.
		
		// pool size가 처음에는 0인데 10으로 설정한 경우  core pool size가 10이 최과되면 keepAliveSeconds 시간 이후에
		// thread를 kill 하는데 core pool size 이하로는 kill을 하지 않는다. pool size가 10 이상이 된 이후로는
		// 최소 10 pool size를 계속 유지한다.
		convertExecutor.setCorePoolSize(8);
		
		// 최대 pool size 지정. 기본값 Integer.MAX, 기본적으로 core pool size 안에서 해결하고
		// core pool size + queue capacity를 넘는 요청이 왔을 때만 max pool size 안에서 pool을 생성한다.
		//executor.setMaxPoolSize(30);
		
		// 대기열 size 지정. 기본값 Integer.MAX, 0으로 설정한 경우 queue type이 SynchronousQueue를, 0 초과로 설정한 경우
		// LinkedBlockingQueue를 생성한다.
		//convertExecutor.setQueueCapacity(100);
		
		// core thread를 유지한 timeout(초) 지정. 기본값 60(초). setAllowCoreThreadTimeOut이 true일 경우에만 작동한다.
		//convertExecutor.setKeepAliveSeconds(10);
		
		// core thread를 유휴시간(keepAliveSeconds)이 지나서 kill 할지 여부, 기본값 false, true로 설정한 경우 core pool도
		// keepAliveSeconds 시간 이후에 thread를 kill 한다. jdk 1.6 이상부터 사용가능
		//convertExecutor.setAllowCoreThreadTimeOut(true);
		
		// prefix thread 명 지정
		convertExecutor.setThreadNamePrefix("Executor - ");
		
		// queue 대기열 및 task가 완료된 이후에 shutdown 여부
		convertExecutor.setWaitForTasksToCompleteOnShutdown(true);
		
		// executor 초기화
		convertExecutor.initialize();
		
		return convertExecutor;
	}
}
