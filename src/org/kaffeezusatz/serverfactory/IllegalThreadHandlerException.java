package org.kaffeezusatz.serverfactory;

public class IllegalThreadHandlerException extends RuntimeException {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	/**
	 * 
	 * @param message
	 */
	public IllegalThreadHandlerException(String message) {
		super(message);
	}
}
