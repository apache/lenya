package org.apache.lenya.transaction;

public interface Lock {

	/**
	 * @return The version number.
	 */
	public abstract int getVersion();

}