package com.mobileleader.edoc.client;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;

import com.google.gson.Gson;

public class NonSslSocket {

	 private String host;
	    private int port;

	    public NonSslSocket(String host2, int port2) {
	    	this.host = host2;
	    	this.port = port2;
		}

		public void run(ConvertRequest request) {
	        try {
	            Socket socket = new Socket();
	            SocketAddress address = new InetSocketAddress(host, port);
	            socket.connect(address);

	           
	            
	            
	            
	            ClientSocket clientSocket = new ClientSocket(socket);
	            clientSocket.sendRequest(request);
	            
	        } catch (IOException e) {
	            e.printStackTrace();
	        }
	    }
}
