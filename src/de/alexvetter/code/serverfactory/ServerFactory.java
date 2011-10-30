package de.alexvetter.code.serverfactory;

import de.alexvetter.code.serverfactory.handler.Handler;

public class ServerFactory {
	private ServerFactory() {
		//do nothing
	}
	
	public static Server getServer(Integer port, Handler handler) {
		return new Server(port, handler);
	}
}
