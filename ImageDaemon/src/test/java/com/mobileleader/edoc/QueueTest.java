package com.mobileleader.edoc;

import java.util.PriorityQueue;

import com.mobileleader.image.model.ConvertRequest;

public class QueueTest {

	public static void main(String[] args) throws InterruptedException {
		
		PriorityQueue<ConvertRequest> requestQueue = new PriorityQueue<ConvertRequest>();					// 이미지 변환 요청 큐(우선순위 큐)
		
		ConvertRequest r1 = new ConvertRequest();
		Thread.sleep(50);
		ConvertRequest r2 = new ConvertRequest();
		ConvertRequest r3 = new ConvertRequest();
		
		r1.setJobType("B");
		
		r2.setJobType("B");
		r3.setJobType("R");
		
		r1.setJobId("1");
		r2.setJobId("2");
		r3.setJobId("3");
		
		requestQueue.add(r1);
		requestQueue.add(r2);
		requestQueue.add(r3);
		
		
		System.out.println(requestQueue.poll().toString());
		System.out.println(requestQueue.poll().toString());
		System.out.println(requestQueue.poll().toString());
		
		String num = "2";
		
		switch (num) {
		case "1":
		case "2":
		
			System.out.println("TEST!");
			
			break;

		default:
			break;
		}
				
	}
}
