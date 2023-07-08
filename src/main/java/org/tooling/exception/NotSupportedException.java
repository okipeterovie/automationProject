package org.tooling.exception;


public class NotSupportedException extends RuntimeException{

	private static final long serialVersionUID = 9122907673091178612L;

	public NotSupportedException(){
		super("Operation not supported on this page");
	}

	public NotSupportedException(String msg){
		super(msg);
	}

	public NotSupportedException(Throwable e){
		super(e);
	}

}
