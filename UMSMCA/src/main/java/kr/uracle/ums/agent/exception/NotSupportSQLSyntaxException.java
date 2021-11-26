package kr.uracle.ums.agent.exception;

public class NotSupportSQLSyntaxException extends RuntimeException{

	private static final long serialVersionUID = 1;

	public NotSupportSQLSyntaxException() {
		super();
	}
	
	public NotSupportSQLSyntaxException(String msg) {
		super(msg);
	}
	
	public NotSupportSQLSyntaxException(Throwable cause) {
		super(cause);
	}
	
	public NotSupportSQLSyntaxException(String msg, Throwable cause) {
		super(msg, cause);
	}
}
