package org.apache.lenya.cms.publication;

public interface Proxy {

	/**
	 * @param area The area.
	 * @return The proxy URL if no proxy is declared in {@link PublicationConfiguration#CONFIGURATION_FILE}.
	 */
	public String getDefaultUrl();

	/**
	 * Returns the absolute URL of a particular document.
	 * @param document The document.
	 * @return A string.
	 */
	public String getURL(Document document);

	/**
	 * Returns the proxy URL.
	 * @return A string.
	 */
	public String getUrl();

	/**
	 * Sets the proxy URL.
	 * @param _url The url to set.
	 */
	public void setUrl(String _url);

	/**
	 * @see java.lang.Object#toString()
	 */
	public String toString();

}