package org.tooling.exception;


public class WaitingFailureException extends RuntimeException{

	private static final long serialVersionUID = 9122907673091172L;

	public WaitingFailureException(){
		super("The expected event never occurred.");
	}
	
}
