package org.kaffeezusatz.serverfactory.handler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.net.SocketException;
import java.util.HashMap;
import java.util.Map;

import org.kaffeezusatz.serverfactory.Handler;

public class DebugHttpWorker extends Handler {
	public DebugHttpWorker() {
		super();
	}
	
	@Override
	public Socket setSocketSettings(Socket s) throws SocketException {
		s.setKeepAlive(false);
		return super.setSocketSettings(s);
	}

	public void handleRequest(InputStreamReader in, OutputStreamWriter out) throws IOException {
		Map<String, String> headers = new HashMap<String, String>();
		
		StringBuffer request = new StringBuffer();
		String requestLine = null;
		
		{
			BufferedReader br = new BufferedReader(in);
			String line = "";
			while((line = br.readLine()) != null) {
				if (line.trim().length() == 0) {
					break;
				}
				
				if (requestLine == null) {
					requestLine = line;
				} else {
					String[] header = line.toLowerCase().split(":");
					headers.put(header[0], header[1]);
				}
				
				request.append(line + "\n");
			}
		}
		
		System.out.println(requestLine + " " + headers.toString());
		
		out.append("HTTP/1.1 200 OK\n");
		out.append("Content-Length: " + request.length() + "\n");
		out.append("Content-Type: text/plain; charset=UTF-8\n");
		out.append("Connection: close\n");
		
		out.flush();
		
		out.append("\n" + request.toString() + "\n");
		
		out.flush();
	}
}