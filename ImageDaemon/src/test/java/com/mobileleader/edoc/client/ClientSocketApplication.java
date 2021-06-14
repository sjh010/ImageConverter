package com.mobileleader.edoc.client;

import java.util.Scanner;

import com.mobileleader.image.model.ConvertRequest;

public class ClientSocketApplication {

	public static void main(String[] args) {
		String host = "localhost";
		int port = 10500;
		
		Scanner sc = new Scanner(System.in);
		
		try {
			
			ConvertRequest request = new ConvertRequest();
			request.setJobId("jobId1");
			request.setJobType("R");
			request.setSrcPath("C:\\sample\\sample_jpg.jpg");
			request.setDesRootPath("C:\\sample");
			request.setDesFileName("converted.pdf");
			request.setConvType("01");
			request.setRstType("00");
			
			NonSslSocket socket = new NonSslSocket(host, port);
			socket.run(request);
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			sc.close();
		}
	}
}
