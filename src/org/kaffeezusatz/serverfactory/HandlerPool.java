package org.kaffeezusatz.serverfactory;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class HandlerPool {
	/**
	 * max # handler threads
	 */
	final static int handlers = 10;

	/**
	 * Where handler threads stand idle
	 */
	final static List<Handler> threads = new ArrayList<Handler>(handlers);

	/**
	 * The actual server.
	 */
	final ServerSocket ss;

	public HandlerPool(Integer port, Class<? extends Handler> workerClass) throws IOException {
		ss = new ServerSocket(port);

		try {
			initWorker(workerClass);
		} catch (Exception e) {
			throw new IllegalArgumentException(e);
		}
	}

	private void initWorker(Class<? extends Handler> workerClass) throws InstantiationException, IllegalAccessException {
		synchronized (threads) {
			/* start worker threads */
			while (threads.size() < handlers) {
				Handler w = addWorker(workerClass.newInstance());

				w.setWorkerPool(this);
				w.start();
			}
		}
	}

	public synchronized void startWorker() {
		while (true) {
			try {
				/* wait till request */
				Socket s = ss.accept();
				Handler w = getWorker();
				/* give the request to worker thread */
				/* and start request handling */
				w.setSocketNotify(s);
				/* start over */
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public Handler addWorker(Handler w) {
		synchronized (threads) {
			threads.add(w);
			threads.notify();
		}
		return w;
	}

	/**
	 * Wait till and idling worker thread is available. Then returns the idling
	 * worker thread and remove's it from pool.
	 * 
	 * The pool is only for idling threads.
	 * 
	 * @return Handler thread from pool
	 */
	public Handler getWorker() {
		synchronized (threads) {
			if (threads.isEmpty()) {
				try {
					threads.wait();
				} catch (InterruptedException ignore) {
					// should not happen
				}
			}

			if (!threads.isEmpty()) {
				Handler w = threads.remove(0);
				return w;
			}
		}

		return null;
	}
}