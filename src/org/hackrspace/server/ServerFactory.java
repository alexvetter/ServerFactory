package org.hackrspace.server;

import org.hackrspace.server.handler.Handler;

public class ServerFactory {
	private ServerFactory() {
		//do nothing
	}
	
	public static Server getServer(Integer port, Handler handler) {
		return new Server(port, handler);
	}
}
