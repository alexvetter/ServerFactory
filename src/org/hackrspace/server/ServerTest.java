package org.hackrspace.server;

import java.io.IOException;

import org.hackrspace.server.handler.PrintRequestHandler;

public class ServerTest {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		synchronized (Thread.currentThread()) {	
			Server server = ServerFactory.getServer(80, new PrintRequestHandler());
			
			try {
				server.start();
			} catch (IOException e) {
				System.err.println(e.getMessage());
				server.shutdown();
			}
			
			
			System.out.println("in the end");
		}
	}

}
