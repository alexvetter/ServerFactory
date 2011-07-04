package org.hackrspace.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class Server {
	private final Integer port;
	private final Handler handler;
	private ExecutorService pool;

	public Server(Integer port, Handler handler) {
		this.port = port;
		this.handler = handler;

		if (SingleThreadHandler.class.isInstance(this.handler)) {
			this.pool = Executors.newSingleThreadExecutor();
		} else if (MultiThreadHandler.class.isInstance(this.handler)) {
			this.pool = Executors.newCachedThreadPool();
		} else {
			// TODO throw new Exception
		}
	}

	public Integer getPort() {
		return port;
	}

	public Handler getHandler() {
		return handler;
	}

	public void start() {
		try {
			ServerSocket serverSocket = new ServerSocket(getPort());
			handler.setServerSocket(serverSocket);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			return;
		}

		ThreadHelper helper = new ThreadHelper(pool, handler);
		helper.start();
	}

	public void shutdown() {
		pool.shutdown(); // Disable new tasks from being submitted
		try {
			// Wait a while for existing tasks to terminate
			if (!pool.awaitTermination(60, TimeUnit.SECONDS)) {
				pool.shutdownNow(); // Cancel currently executing tasks
				// Wait a while for tasks to respond to being cancelled
				if (!pool.awaitTermination(60, TimeUnit.SECONDS))
					System.err.println("Pool did not terminate");
			}
		} catch (InterruptedException ie) {
			// (Re-)Cancel if current thread also interrupted
			pool.shutdownNow();
			// Preserve interrupt status
			Thread.currentThread().interrupt();
		}
	}
}

class ThreadHelper extends Thread {
	protected final Handler handler;
	protected final ExecutorService pool;

	public ThreadHelper(ExecutorService pool, Handler handler) {
		this.pool = pool;
		this.handler = handler;
	}
	
	public void run() {
		while (true) {
			pool.execute(handler.initialize());
			System.out.println("added");
		}
	}
}