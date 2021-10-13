package com.mobileleader.edoc.thread;

import java.util.ArrayList;
import java.util.List;

public class ThreadPool {

	private ThreadPoolQueue queue;
	
	private List<ThreadPoolRunnable> runnableList = new ArrayList<ThreadPoolRunnable>();
	
	private volatile boolean running = true;
	
	// 쓰레드풀 초기화
	public ThreadPool(int MAX_THREAD_NUM, int MAX_QUEUE_SIZE) {
		queue = new ThreadPoolQueue(MAX_QUEUE_SIZE);
		
		for (int i=0; i < MAX_THREAD_NUM; i++) {
			runnableList.add(new ThreadPoolRunnable(i, queue));
		}
		
		for (ThreadPoolRunnable r : runnableList) {
			new Thread(r).start();
		}
	}
	
	public synchronized void execute(Runnable item) throws Exception {
		if (!running) {
			throw new Exception("Thread Pool is not running.");
		}
		
		//queue.enqueue(item);
	}
}
