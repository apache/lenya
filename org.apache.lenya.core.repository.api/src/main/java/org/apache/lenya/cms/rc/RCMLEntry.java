package org.apache.lenya.cms.rc;

public interface RCMLEntry {

	/**
	 * Get the identity of the creator (i.e. the user name)
	 * FIXME: this should be changed to an o.a.l.ac.Identity object
	 * @return the identity
	 */
	public abstract String getIdentity();

	/**
	 * Get the creation time.
	 * @return the time
	 */
	public abstract long getTime();

	/**
	 * Get the type (checkin or checkout).
	 * @see org.apache.lenya.cms.rc.RCML.ci
	 * @see org.apache.lenya.cms.rc.RCML.co
	 * @return the type
	 */
	public abstract short getType();

	public abstract String getSessionId();

}