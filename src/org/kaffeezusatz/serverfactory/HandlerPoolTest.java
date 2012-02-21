package org.kaffeezusatz.serverfactory;

import static org.junit.Assert.*;

import java.io.IOException;

import org.junit.Test;
import org.kaffeezusatz.serverfactory.handler.DebugHttpWorker;

public class HandlerPoolTest {

	@Test
	public void test() throws IOException {
		HandlerPool hp = new HandlerPool(8080, DebugHttpWorker.class);
		hp.startWorker();
	}
}
