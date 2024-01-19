package com.atsustudio.Exceptions;

public class DatabaseException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public DatabaseException(String errorMessage) {
	    // calling the constructor of parent Exception
	    super(errorMessage);
	  }
}