package com.mobileleader.edoc.client;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Arrays;

import com.google.gson.Gson;

public class ClientSocket {

	private Socket socket;
	
	public ClientSocket(Socket socket) {
		super();
		this.socket = socket;
	}

	public void sendRequest(ConvertRequest request) {
		System.out.println("sendRequest");
		
		BufferedReader br;
		PrintWriter pw;
		try {
			
			pw = new PrintWriter(socket.getOutputStream());
			
			Gson gson = new Gson();
            
			
			pw.print(gson.toJson(request));
			pw.flush();
			Thread.sleep(500);

			
		} catch (IOException | InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		try {
			InputStream is = socket.getInputStream();
			byte[] reply = new byte[2048];
		//	ByteStreams.read(is, reply, 0, reply.length);
			
			System.out.println("Response : " + new String(reply));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}
	
	public void sendFixedLength(int messageLength) {
		int delimiterLength = 256;

		StringBuilder stringBuilder = new StringBuilder();
		for (int i = 0; i < messageLength; i++)
			stringBuilder.append("A");
		byte[] totalData = stringBuilder.toString().getBytes();

		System.out.println("Sending message");
		try {
			OutputStream os = socket.getOutputStream();

			for (int i = 0; i < messageLength / delimiterLength; i++) {
				byte[] sending = Arrays.copyOfRange(totalData, i * delimiterLength, (i + 1) * delimiterLength);
				System.out.println("sending... " + (i + 1));
				os.write(sending);
				os.flush();
				Thread.sleep(500);
			}
		} catch (InterruptedException | IOException e) {
			e.printStackTrace();
		}

		System.out.println("Receiving message");
		try {
			InputStream is = socket.getInputStream();

			byte[] reply = new byte[messageLength];
			ByteArrayInputStream ba = new ByteArrayInputStream(reply);
			
			ba.read(reply, 0, reply.length);

			//ByteStreams.read(is, reply, 0, reply.length);
			System.out.println(new String(reply));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
