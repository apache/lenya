
package org.apache.lenya.cms.ac;

/**
 * @author egli
 * 
 * 
 */
public class AccessControlException extends Exception {

	/**
	 * Create an AccessControlException
	 * 
	 */
	public AccessControlException() {
		super();
	}

	/**
	 * Create an AccessControlException
	 * 
	 * @param message The message.
	 */
	public AccessControlException(String message) {
		super(message);
	}

	/**
	 * Create an AccessControlException
	 * 
	 * @param message The message.
	 * @param cause The cause.
	 */
	public AccessControlException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * Create an AccessControlException.
	 * 
	 * @param cause The cause.
	 */
	public AccessControlException(Throwable cause) {
		super(cause);
	}

}
