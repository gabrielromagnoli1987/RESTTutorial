package org.koushik.javabrains.messenger.exception;

// esta es una clase exception custom que hereda de RuntimeException utilizada para atrapar las
// exceptions en los llamados a la API

public class DataNotFoundException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6328286661536343936L;

	
	public DataNotFoundException(String message) {
		super(message);
	}
	
}
