package org.kaffeezusatz.serverfactory.handler;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;


public abstract class Handler implements Runnable {
	private ServerSocket serverSocket;
	private Socket socket;

	public abstract void handle(Socket socket);
	
	public void setServerSocket(ServerSocket serverSocket) {
		if(this.serverSocket != null) {
			//TODO throw new Exception();
		}
		
		if (serverSocket.isClosed()) {
			//TODO throw new Exception();
		}
		
		this.serverSocket = serverSocket;
	}
	
	public ServerSocket getServerSocket() {
		return serverSocket;
	}
	
	public Socket getSocket() {
		return socket;
	}
	
	public void setSocket(Socket socket) {
		this.socket = socket;
	}
	
	public final Handler initialize() {
		try {
			setSocket(getServerSocket().accept());
		} catch (IOException e) {
			// TODO Auto-generated catch block
		}
		
		return this;
	}
	
	public final void run() {
		handle(getSocket());
	}
}
