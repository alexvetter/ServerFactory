package org.hackrspace.server;

public class ServerFactory {
	private ServerFactory() {
		//do nothing
	}
	
	public static Server getServer(Integer port, Handler handler) {
		return new Server(port, handler);
	}
}
