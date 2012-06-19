package org.kaffeezusatz.serverfactory;

import java.io.Closeable;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class HandlerPool extends Thread implements Closeable {
	/**
	 * max # waiting handler threads
	 */
	final static int handlers = 10;

	/**
	 * List of waiting handler threads.
	 */
	final static List<Handler> waitingThreads = new ArrayList<Handler>(handlers);

	/**
	 * The server socket.
	 */
	final ServerSocket ss;

	/**
	 * Is this pool closed.
	 */
	private Boolean closed;

	/**
	 * Initializes the pool.
	 * 
	 * @param port
	 * @param handlerClass
	 * @throws IOException
	 */
	public HandlerPool(final Integer port, final Class<? extends Handler> handlerClass) throws IOException {
		ss = new ServerSocket(port);

		try {
			initHandler(handlerClass);
		} catch (Exception e) {
			throw new IllegalArgumentException(e);
		}
	}

	/**
	 * Initializes the handler pool.
	 * 
	 * @param handlerClass
	 * 
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 */
	private void initHandler(Class<? extends Handler> handlerClass) throws InstantiationException, IllegalAccessException {
		synchronized (waitingThreads) {
			/* start waiting handler threads */
			while (waitingThreads.size() < handlers) {
				Handler h = addHandler(handlerClass.newInstance());

				h.setHandlerPool(this);
				h.start(); // start this handler thread
			}
			
			closed = Boolean.FALSE;
		}
	}

	/**
	 * Run handler pool.
	 */
	public void run() {
		synchronized (ss) {
			while (!isClosed()) {
				try {
					/* wait till request */
					Socket s = ss.accept();
					Handler w = getHandler();
					/* give the request to worker thread */
					/* and start request handling */
					w.setSocket(s);
					/* start over */
				} catch (Exception e) {
					if (!isClosed()) {
						e.printStackTrace();
					}
				}
			}
		}
	}

	/**
	 * Add an idling handler.
	 * 
	 * @param w
	 * @return
	 */
	protected Handler addHandler(Handler h) {
		h.setSocket(null);
		
		synchronized (waitingThreads) {
			waitingThreads.add(h);
			waitingThreads.notify(); // notify getHandler()
		}
		
		return h;
	}

	/**
	 * Wait till and idling worker thread is available. Then returns the idling
	 * worker thread and remove's it from pool.
	 * 
	 * The pool is only for idling waitingThreads.
	 * 
	 * @return Handler thread from pool
	 */
	private Handler getHandler() {
		synchronized (waitingThreads) {
			while (waitingThreads.isEmpty()) {
				try {
					waitingThreads.wait(2000);
				} catch (InterruptedException ignore) {
					// should not happen
				}

				if (!waitingThreads.isEmpty()) {
					break;
				}
			}

			Handler w = waitingThreads.remove(0);
			return w;
		}
	}

	/**
	 * Is this pool closed?
	 * 
	 * @return true or false
	 */
	public boolean isClosed() {
		synchronized (closed) {
			return closed;
		}
	}
	
	/**
	 * Closes ServerSocket and its Handler waitingThreads.
	 */
	public void close() {
		if (isClosed()) {
			return;
		}
		
		synchronized (waitingThreads) {
			for (Handler thread : waitingThreads) {
				synchronized (thread) {
					thread.notify();
					thread.close();					
				}
			}
			waitingThreads.clear();
		}

		try {
			ss.close();
		} catch (IOException ignore) {
			// ignore
		}

		closed = Boolean.TRUE;
	}
}