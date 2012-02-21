package org.kaffeezusatz.serverfactory;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;

public abstract class Handler extends Thread implements Runnable {
	/**
	 * Socket to client we're handling
	 */
	protected Socket s;

	protected int timeout;

	private HandlerPool pool;
	
	private List<HandlerActionListener> listener;

	public Handler(int timeout) {
		super("Request Handler");
		
		this.timeout = timeout;
		listener = new ArrayList<HandlerActionListener>();
	}

	public void addListener(HandlerActionListener listener) {
		this.listener.add(listener);
	}
	
	public void removeListener(HandlerActionListener listener) {
		this.listener.remove(listener);
	}
	
	protected void fireNewRequest() {
		for (HandlerActionListener listener : this.listener) {
			listener.newRequest();
		}
	}
	
	protected void fireFinishedRequest() {
		for (HandlerActionListener listener : this.listener) {
			listener.finishedRequest();
		}
	}
	
	protected void setWorkerPool(HandlerPool pool) {
		this.pool = pool;
	}

	public synchronized void setSocketNotify(final Socket s) {
		this.s = s;
		if (this.s != null) {
			notify();
		}
	}

	public synchronized void run() {
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
					//final BufferedReader in = new BufferedReader(new InputStreamReader(s.getInputStream(), "UTF-8"));
					//final BufferedWriter out = new BufferedWriter(new OutputStreamWriter(s.getOutputStream(), "UTF-8"));
					
					final InputStreamReader in = new InputStreamReader(s.getInputStream(), "UTF-8");
					final OutputStreamWriter out = new OutputStreamWriter(s.getOutputStream(), "UTF-8");

					fireNewRequest();
					
					handleClient(in, out);
					
					fireFinishedRequest();
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

	public Socket setSocketSettings(Socket s) throws SocketException {
		s.setSoTimeout(timeout);
		return s;
	}
	
	public abstract void handleClient(InputStreamReader in, OutputStreamWriter out) throws IOException;
}