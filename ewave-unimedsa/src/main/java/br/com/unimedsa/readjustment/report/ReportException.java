package br.com.unimedsa.readjustment.report;

/**
 * It represents an exception occurred in the report exporting.
 * 
 * @author Diogo Variani (diogo.variani@ewave.com.br)
 */
public class ReportException extends Exception {

	private static final long serialVersionUID = -3879936322169376000L;

	/**
	 * Creates an report exception with no message or cause.
	 */
	public ReportException() {
		super();
	}

	/**
	 * Creates an report exception with a message and a cause specified.
	 * 
	 * @param message the message used to create the exception.
	 * @param cause the real cause of the problem.
	 */
	public ReportException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * Creates an report exception with a message and with no cause.
	 * 
	 * @param message the message used to create the exception.
	 */
	public ReportException(String message) {
		super(message);
	}

	/**
	 * Creates an report exception with a cause and with no message.
	 * 
	 * @param cause the cause used to create the exception.
	 */
	public ReportException(Throwable cause) {
		super(cause);
	}
}
