package org.kaffeezusatz.serverfactory;

import java.io.IOException;

import org.kaffeezusatz.serverfactory.handler.PrintRequestHandler;


public class ServerTest {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		synchronized (Thread.currentThread()) {	
			Server server = ServerFactory.getServer(8080, new PrintRequestHandler());
			
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
