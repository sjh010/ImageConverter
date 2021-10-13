package com.mobileleader.edoc.thread;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ThreadPoolRunnable implements Runnable {

	private int id; // Runnable ID
	private ThreadPoolQueue queue;
	private volatile boolean running = true;
	
	
	
	public ThreadPoolRunnable(int THREAD_ID, ThreadPoolQueue queue) {
		this.id = THREAD_ID;
		this.queue = queue;
		log.info("ThreadPoolRunnable[{}] is created.", THREAD_ID);
	}

	@Override
	public void run() {
		while (running) {
			try {
				Thread.sleep(10);
				log.info("ThreadPoolRunnable[{}] is working.", this.id);
				Runnable r = (Runnable) queue.dequeue();
				r.run();
			} catch (InterruptedException e) {
				stop(); // 인터럽트 예외 발생시 해당 Runnable 정지
			}
		}
		log.info("ThreadPoolRunnable[{}] is dead.", this.id);
	}
	
	public void stop() {
		running = false;
		log.info("ThreadPoolRunnable[{}] is stopped.", this.id);
	}

}
