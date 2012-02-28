package org.kaffeezusatz.serverfactory;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.net.SocketException;

public abstract class Handler extends Thread implements Runnable {
	/**
	 * Socket to client we're handling
	 */
	protected Socket s;

	protected int timeout = 5000;

	private HandlerPool pool;
	
	public Handler() {
		super("Request Handler");
	}
	
	public synchronized void setSoTimeout(int timeout) {
		this.timeout = timeout;
	}
	
	public synchronized void setSocket(final Socket s) {
		this.s = s;
		if (this.s != null) {
			notify();
		}
	}
	
	public Socket setSocketSettings(Socket s) throws SocketException {
		s.setSoTimeout(timeout);
		return s;
	}

	protected void setWorkerPool(HandlerPool pool) {
		this.pool = pool;
	}

	public final synchronized void run() {
		while (true) {
			if (s == null) {
				/* nothing to do */
				try {
					wait();
				} catch (InterruptedException e) {
					/* should not happen */
					continue;
				}
			}

			try {
				if (!s.isClosed()) {
					setSocketSettings(s);

					final InputStreamReader in = new InputStreamReader(s.getInputStream(), "UTF-8");
					final OutputStreamWriter out = new OutputStreamWriter(s.getOutputStream(), "UTF-8");
					
					handleRequest(in, out);
				}
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				try {
					s.close();
				} catch (IOException ignore) {
					// ignore
				}
			}

			/*
			 * go back in wait queue if there's fewer than numHandler
			 * connections.
			 */
			s = null;
			pool.addWorker(this);
		}
	}
	
	public abstract void handleRequest(InputStreamReader in, OutputStreamWriter out) throws IOException;
}