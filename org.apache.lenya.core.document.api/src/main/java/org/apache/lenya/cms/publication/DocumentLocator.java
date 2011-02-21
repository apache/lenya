package org.apache.lenya.cms.publication;

/**
 * A DocumentLocator describes a document based on its path in the site structure. The actual
 * document doesn't have to exist.
 * It helps to locate a document inside a publication
 */
public interface DocumentLocator {

	/**
	 * @return The area of the document.
	 */
	public abstract String getArea();

	/**
	 * @return The language of the document.
	 */
	public abstract String getLanguage();

	/**
	 * @return The path of the document in the site structure.
	 */
	public abstract String getPath();

	/**
	 * @return The publication ID.
	 */
	public abstract String getPublicationId();

	/**
	 * Returns a locator with the same publication ID, area, and language, but a different path in
	 * the site structure.
	 * @param path The path.
	 * @return A document locator.
	 */
	public abstract DocumentLocator getPathVersion(String path);

	/**
	 * Returns a descendant of this locator.
	 * @param relativePath The relative path which must not begin with a slash and must not be
	 *            empty.
	 * @return A document locator.
	 */
	public abstract DocumentLocator getDescendant(String relativePath);

	/**
	 * Returns a child of this locator.
	 * @param step The relative path to the child, it must not contain a slash.
	 * @return A document locator.
	 */
	public abstract DocumentLocator getChild(String step);

	/**
	 * Returns the parent of this locator.
	 * @return A document locator or <code>null</code> if this is the root locator.
	 */
	public abstract DocumentLocator getParent();

	/**
	 * Returns the parent of this locator.
	 * @param defaultPath The path of the locator to return if this is the root locator.
	 * @return A document locator.
	 */
	public abstract DocumentLocator getParent(String defaultPath);

	/**
	 * Returns a locator with the same publication ID, area, and path, but with a different
	 * language.
	 * @param language The language.
	 * @return A document locator.
	 */
	public abstract DocumentLocator getLanguageVersion(String language);

	public abstract boolean equals(Object obj);

	public abstract int hashCode();

	public abstract String toString();

	/**
	 * Returns a locator with the same publication ID, path, and language, but with a different
	 * area.
	 * @param area The area.
	 * @return A document locator.
	 */
	public abstract DocumentLocator getAreaVersion(String area);

}