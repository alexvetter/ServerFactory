package org.kaffeezusatz.serverfactory;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.net.SocketException;

public abstract class Handler extends Thread implements Closeable {
	/**
	 * Socket to client we're handling
	 */
	protected Socket s;
	
	private Boolean closed = Boolean.FALSE;

	protected int timeout = 5000;

	private HandlerPool pool;
	
	public Handler() {
		super("Handler");
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
	
	public Socket setSocketSettings(final Socket s) throws SocketException {
		s.setSoTimeout(timeout);
		return s;
	}

	protected void setHandlerPool(HandlerPool pool) {
		this.pool = pool;
	}

	public final synchronized void run() {
		while (!isClosed()) {
			if (s == null) {
				try {
					wait();
				} catch (InterruptedException ignore) {
					//ignore
				}
			}
			
			try {
				/* do something */
				if (s != null && !s.isClosed()) {
					setSocketSettings(s);

					final InputStreamReader in = new InputStreamReader(s.getInputStream(), "UTF-8");
					final OutputStreamWriter out = new OutputStreamWriter(s.getOutputStream(), "UTF-8");
					
					handleRequest(in, out);
				}
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				try {
					if (s != null) {
						s.close();
					}
				} catch (IOException ignore) {
					// ignore
				}
			}
			
			/*
			 * go back in waiting list
			 */
			pool.addHandler(this);
		}
	}
	
	public boolean isClosed() {
		synchronized (closed) {
			return closed;
		}
	}
	
	public synchronized void close() {
		if (isClosed()) {
			return;
		}
		
		try {
			if (s != null) {
				s.close();
			}
		} catch (IOException ignore) {
			//ignore
		}
		s = null;
		
		closed = Boolean.TRUE;
	}
	
	public abstract void handleRequest(InputStreamReader in, OutputStreamWriter out) throws IOException;
}