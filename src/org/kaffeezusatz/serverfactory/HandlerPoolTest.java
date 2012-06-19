package org.kaffeezusatz.serverfactory;

import static org.junit.Assert.assertEquals;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import org.junit.Test;
import org.kaffeezusatz.serverfactory.handler.DebugHttpWorker;

public class HandlerPoolTest {

	@Test
	public void test() throws IOException, InterruptedException {
		HandlerPool hp = new HandlerPool(8080, DebugHttpWorker.class);
		hp.start();

		BlockingHttpRequest request = new BlockingHttpRequest("http://localhost:8080/");

		int requests = 10000;
		
		int counter = 0;
		for (int i = 0; i < requests; i++) {
			if (!request.get().isEmpty()) {
				counter++;
			}
		}

		synchronized (this) {
			while (hp.isAlive()) {
				System.out.println("...still alive");
				hp.close();
				wait(2000);
			}
		}
		
		assertEquals(requests, counter);
	}
	
	class BlockingHttpRequest {
		private final URL url;

		public BlockingHttpRequest(final String url) throws MalformedURLException {
			this.url = new URL(url);
		}

		public String get() throws IOException {
			final URLConnection yc = url.openConnection();
			final BufferedReader in = new BufferedReader(new InputStreamReader(yc.getInputStream()));
			
			final StringBuilder body = new StringBuilder();
			String line = null;
			while ((line = in.readLine()) != null) {
				body.append(line);
			}
			
			in.close();
			return body.toString();
		}
	}
}
