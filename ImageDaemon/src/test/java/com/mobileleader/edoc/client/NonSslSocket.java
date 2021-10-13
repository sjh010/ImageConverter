package com.mobileleader.edoc.client;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;

import com.mobileleader.image.model.ConvertRequest;

public class NonSslSocket {

	 private String host;
	    private int port;
	    
	    private boolean isEnd;

	    public boolean isEnd() {
			return isEnd;
		}

		public NonSslSocket(String host2, int port2) {
	    	this.host = host2;
	    	this.port = port2;
	    	isEnd = false;
		}

		public void run(ConvertRequest request) {
	        try {
	            Socket socket = new Socket();
	            SocketAddress address = new InetSocketAddress(host, port);
	            socket.connect(address);

	            ClientSocket clientSocket = new ClientSocket(socket);
	            clientSocket.sendRequest(request);
	            isEnd = true;
	        } catch (IOException e) {
	            e.printStackTrace();
	        }
	    }
}
