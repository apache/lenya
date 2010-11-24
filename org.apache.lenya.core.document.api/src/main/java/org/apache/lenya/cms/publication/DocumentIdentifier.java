package org.apache.lenya.cms.publication;

public interface DocumentIdentifier {

	/**
	 * @return The UUID.
	 */
	public abstract String getUUID();

	/**
	 * @return The area.
	 */
	public abstract String getArea();

	/**
	 * @return The language.
	 */
	public abstract String getLanguage();

	/**
	 * @return The publication ID.
	 */
	public abstract String getPublicationId();

	public abstract boolean equals(Object obj);

	public abstract int hashCode();

	public abstract String toString();

}