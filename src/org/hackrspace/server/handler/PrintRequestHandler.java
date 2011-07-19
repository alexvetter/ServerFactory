package org.hackrspace.server.handler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;

public class PrintRequestHandler extends MultiThreadHandler {
	@Override
	public void handle(Socket socket) {
		String nachricht = null;
		try {
			BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			char[] buffer = new char[200];
			int anzahlZeichen = bufferedReader.read(buffer, 0, 200);
			nachricht = new String(buffer, 0, anzahlZeichen);

			System.out.println(nachricht);
		} catch (IOException e) {
			if (socket.isClosed()) {
				return;
			}
			
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		try {
			PrintWriter printWriter = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()));
			printWriter.print(nachricht);
			printWriter.flush();
			if (socket.isConnected()) {
				socket.close();
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
