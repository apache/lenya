package org.apache.lenya.xml;

public interface Schema {

	/**
	 * @return The language.
	 * @see org.apache.cocoon.components.validation.Validator
	 */
	public abstract String getLanguage();

	/**
	 * @return The URI to read the schema from.
	 */
	public abstract String getURI();

}