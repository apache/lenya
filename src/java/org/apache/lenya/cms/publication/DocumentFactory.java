/*
 * Created on 17.09.2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.apache.lenya.cms.publication;

/**
 * @author nobby
 * 
 * TODO To change the template for this generated type comment go to Window - Preferences - Java -
 * Code Style - Code Templates
 */
public class DocumentFactory {

    private DocumentIdentityMap identityMap;

    /**
     * Ctor.
     * @param identityMap The identity map.
     */
    protected DocumentFactory(DocumentIdentityMap identityMap) {
        this.identityMap = identityMap;
    }

    /**
     * Returns the identity map.
     * @return An identity map.
     */
    protected DocumentIdentityMap getIdentityMap() {
        return this.identityMap;
    }

    /**
     * Checks if a webapp URL represents a document.
     * @param webappUrl A web application URL.
     * @return A boolean value.
     * @throws DocumentBuildException if an error occurs.
     */
    public boolean isDocument(String webappUrl) throws DocumentBuildException {
        Publication pub = getIdentityMap().getPublication();
        return pub.getDocumentBuilder().isDocument(pub, webappUrl);
    }

    /**
     * Builds a document in the default language.
     * @param area The area.
     * @param documentId The document ID.
     * @return A document.
     * @throws DocumentBuildException if an error occurs.
     */
    public Document get(String area, String documentId) throws DocumentBuildException {
        return getIdentityMap().get(area, documentId,
                getIdentityMap().getPublication().getDefaultLanguage());
    }

    /**
     * Builds a document.
     * @param area The area.
     * @param documentId The document ID.
     * @param language The language.
     * @return A document.
     * @throws DocumentBuildException if an error occurs.
     */
    public Document get(String area, String documentId, String language)
            throws DocumentBuildException {
        return getIdentityMap().get(area, documentId, language);
    }

    /**
     * Builds a document from a URL.
     * @param webappUrl The web application URL.
     * @return A document.
     * @throws DocumentBuildException if an error occurs.
     */
    public Document getFromURL(String webappUrl) throws DocumentBuildException {
        return getIdentityMap().getFromURL(webappUrl);
    }

    /**
     * Builds a clone of a document for another language.
     * @param document The document to clone.
     * @param language The language of the target document.
     * @return A document.
     * @throws DocumentBuildException if an error occurs.
     */
    public Document getLanguageVersion(Document document, String language)
            throws DocumentBuildException {
        return get(document.getArea(), document.getId(), language);
    }

    /**
     * Builds a clone of a document for another area.
     * @param document The document to clone.
     * @param area The area of the target document.
     * @return A document.
     * @throws DocumentBuildException if an error occurs.
     */
    public Document getAreaVersion(Document document, String area) throws DocumentBuildException {
        return get(area, document.getId(), document.getLanguage());
    }

    /**
     * Returns the parent of a document.
     * @param document A document.
     * @return A document or <code>null</code> if the document has no parent.
     * @throws DocumentBuildException if an error occurs.
     */
    public Document getParent(Document document) throws DocumentBuildException {
        Document parent = null;
        int lastSlashIndex = document.getId().lastIndexOf("/");
        if (lastSlashIndex > 0) {
            String parentId = document.getId().substring(0, lastSlashIndex);
            parent = get(document.getArea(), parentId, document.getLanguage());
        }
        return parent;
    }

    /**
     * Returns the parent of a document.
     * @param document A document.
     * @param defaultDocumentId The document ID to use if the document has no parent.
     * @return A document.
     * @throws DocumentBuildException if an error occurs.
     */
    public Document getParent(Document document, String defaultDocumentId)
            throws DocumentBuildException {
        Document parent = getParent(document);
        if (parent == null) {
            parent = get(document.getArea(), defaultDocumentId, document.getLanguage());
        }
        return parent;
    }

}