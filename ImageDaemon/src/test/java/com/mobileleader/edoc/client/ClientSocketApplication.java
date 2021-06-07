package com.mobileleader.edoc.client;

import java.util.Scanner;

public class ClientSocketApplication {

	public static void main(String[] args) {
		String host = "localhost";
		int port = 8991;
		
		Scanner sc = new Scanner(System.in);
		
		try {
			for (int i=0; i < 10; i++) {
				
				 ConvertRequest request = new ConvertRequest();
//	            request.setTargetPath("D:\\convert\\target\\target.jpg");
//	            request.setDestPath("D:\\convert\\dest\\dest.pdf");
	            request.setId("" + (i+1));
	            request.setMode(i);
//	            request.setExtention("PDF");
				
				NonSslSocket socket = new NonSslSocket(host, port);
				socket.run(request);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			sc.close();
		}
	}
}
