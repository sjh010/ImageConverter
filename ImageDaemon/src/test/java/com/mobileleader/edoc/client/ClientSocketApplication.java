package com.mobileleader.edoc.client;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import com.mobileleader.image.model.ConvertRequest;

public class ClientSocketApplication {

	public static void main(String[] args) {
		String host = "localhost";
		int port = 10500;
		
		Scanner sc = new Scanner(System.in);
		List<NonSslSocket> sockets = new ArrayList<NonSslSocket>();
		try {
			
			
				for (int i=0; i < 10; i++) {
					ConvertRequest convertRequest = new ConvertRequest();
					convertRequest.setJobId("jobId" + i);
					convertRequest.setJobType("B");
					convertRequest.setSrcPath("D:\\convert\\multiPDF2.pdf");
					convertRequest.setDesRootPath("D:\\convert\\");
					convertRequest.setDesFileName("testTiff"+i+".tif");
					convertRequest.setConvType("02");
					convertRequest.setRstType("02");
					
					String ping = "PING";
					
					NonSslSocket socket = new NonSslSocket(host, port);
					socket.run(convertRequest);
					sockets.add(socket);
				}
//				Thread.sleep(1000);
				for(NonSslSocket socket:sockets) {
					while(!socket.isEnd()) {
						Thread.sleep(100);
					}
				}
			
			
		
		
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			sc.close();
		}
	}
}
