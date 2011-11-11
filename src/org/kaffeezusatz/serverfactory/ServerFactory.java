package org.kaffeezusatz.serverfactory;

import org.kaffeezusatz.serverfactory.handler.Handler;

public class ServerFactory {
	private ServerFactory() {
		//do nothing
	}
	
	public static Server getServer(Integer port, Handler handler) {
		return new Server(port, handler);
	}
}
