package com.mobileleader.edoc.thread;

import java.util.PriorityQueue;

import com.mobileleader.image.model.ConvertRequest;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ThreadPoolQueue {

	private PriorityQueue<ConvertRequest> requestQueue;					// 이미지 변환 요청 큐(우선순위 큐)
	
	private int MAX_QUEUE_SIZE = 100;
	
	// 디버그를 위한 콘솔 출력 변수
	private boolean DEBUG = false;

	public ThreadPoolQueue(int MAX_QUEUE_SIZE) {
		this.MAX_QUEUE_SIZE = MAX_QUEUE_SIZE;
	}
	
	public synchronized void enqueue(ConvertRequest convertRequest) throws InterruptedException {
		// 현재 큐가 최대 사이즈면 멈춤
		while (requestQueue.size() == MAX_QUEUE_SIZE) {
			log.info("enqueue waiting...");
			wait();
		}
		
		// 현재 큐가 비어있으면
		if (requestQueue.size() == 0) {
			log.info("enqueue notifyAll..");
			notifyAll();
		}
		
		log.info("enqueue put...");
		requestQueue.add(convertRequest);
	}
	
	public synchronized ConvertRequest dequeue() throws InterruptedException {
		// 반환할 아이템이 없으면 멈춤
		while(requestQueue.size() == 0) {
			log.info("dequeue waiting..");
			wait();
		}
		
		// 반환할 아이템이 가득 참
		if (requestQueue.size() == MAX_QUEUE_SIZE) {
			log.info("dequeue notifyAll..");
			notifyAll();
		}
		
		log.info("dequeue removing..");
		return requestQueue.poll();
	}
}
