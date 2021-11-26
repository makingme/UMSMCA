package kr.uracle.ums.agent.exception;

public class NONConfigVariableSetException extends RuntimeException{

	private static final long serialVersionUID = 1;

	public NONConfigVariableSetException() {
		super();
	}
	
	public NONConfigVariableSetException(String msg) {
		super(msg);
	}
	
	public NONConfigVariableSetException(Throwable cause) {
		super(cause);
	}
	
	public NONConfigVariableSetException(String msg, Throwable cause) {
		super(msg, cause);
	}
}
