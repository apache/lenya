/*
 * Created on Nov 26, 2003
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package org.apache.lenya.cms.publication;

import java.io.File;

import org.apache.lenya.cms.publishing.PublishingEnvironment;

/**
 * @author andreas
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public interface Publication {

    String AUTHORING_AREA = "authoring";
    String STAGING_AREA = "staging";
    String LIVE_AREA = "live";
    String ADMIN_AREA = "admin";
    String ARCHIVE_AREA = "archive";
    String TRASH_AREA = "trash";
    String ELEMENT_PATH_MAPPER = "path-mapper";
    String ELEMENT_DOCUMENT_BUILDER = "document-builder";
    String ELEMENT_SITE_STRUCTURE = "site-structure";
    String ATTRIBUTE_TYPE = "type";
    String LANGUAGES = "languages";
    String LANGUAGE = "language";
    String DEFAULT_LANGUAGE_ATTR = "default";
    String BREADCRUMB_PREFIX = "breadcrumb-prefix";
    String PUBLICATION_PREFIX = "lenya" + File.separator + "pubs";
    String PUBLICATION_PREFIX_URI = "lenya/pubs";
    String CONFIGURATION_PATH = "config";
    String CONTENT_PATH = "content";
    String INFO_AREA_PREFIX = "info-";
    String CONFIGURATION_FILE = CONFIGURATION_PATH + File.separator + "publication.xconf";

    /**
     * Returns the publication ID.
     * @return A string value.
     */
    String getId();

    /**
     * Returns the publishing environment of this publication.
     * @return A {@link PublishingEnvironment} object.
     * @deprecated It is planned to decouple the environments from the publication.
     */
    PublishingEnvironment getEnvironment();

    /**
     * Returns the servlet context this publication belongs to
     * (usually, the <code>webapps/lenya</code> directory).
     * @return A <code>File</code> object.
     */
    File getServletContext();

    /**
     * Returns the publication directory.
     * @return A <code>File</code> object.
     */
    File getDirectory();

    /**
     * Return the directory of a specific area.
     * 
     * @param area a <code>File</code> representing the root of the area content directory.
     * 
     * @return the directory of the given content area. 
     */
    File getContentDirectory(String area);

    /**
     * DOCUMENT ME!
     *
     * @param mapper DOCUMENT ME!
     */
    void setPathMapper(DefaultDocumentIdToPathMapper mapper);

    /**
     * Returns the path mapper.
     * 
     * @return a <code>DocumentIdToPathMapper</code>
     */
    DocumentIdToPathMapper getPathMapper();

    /**
     * Get the default language
     * 
     * @return the default language
     */
    String getDefaultLanguage();

    /**
     * Set the default language
     * 
     * @param language the default language
     */
    void setDefaultLanguage(String language);

    /**
     * Get all available languages for this publication
     * 
     * @return an <code>Array</code> of languages
     */
    String[] getLanguages();

    /**
     * Get the breadcrumb prefix. It can be used as a prefix if a publication is part of a larger site
     * 
     * @return the breadcrumb prefix
     */
    String getBreadcrumbPrefix();

    /**
     * Get the sitetree for a specific area of this publication. 
     * Sitetrees are created on demand and are cached.
     * 
     * @param area the area
     * @return the sitetree for the specified area
     * 
     * @throws SiteTreeException if an error occurs 
     */
    DefaultSiteTree getSiteTree(String area) throws SiteTreeException;

    /**
     * Returns the document builder of this instance.
     * @return A document builder.
     */
    DocumentBuilder getDocumentBuilder();

    /**
     * Copies a document from one location to another location.
     * @param sourceDocument The document to copy.
     * @param destinationDocument The destination document.
     * @throws PublicationException if a document which destinationDocument depends on
     * does not exist.
     */
    void copyDocument(Document sourceDocument, Document destinationDocument)
        throws PublicationException;

    /**
     * Copies a document to another area.
     * @param sourceDocument The document to copy.
     * @param destinationArea The destination area.
     * @throws PublicationException if a document which the
     * destination document depends on does not exist.
     */
    void copyDocumentToArea(Document sourceDocument, String destinationArea)
        throws PublicationException;

    /**
     * Copies a document set to another area.
     * @param documentSet The document set to copy.
     * @param destinationArea The destination area.
     * @throws PublicationException if a document which one of the
     * destination documents depends on does not exist.
     */
    void copyDocumentSetToArea(DocumentSet documentSet, String destinationArea)
        throws PublicationException;
        
    /**
     * Deletes a document.
     * @param document The document to delete.
     * @throws PublicationException when something went wrong.
     */
    void deleteDocument(Document document) throws PublicationException;

    /**
     * Creates a version of the document object in another area.
     * @param document The document to clone.
     * @param area The destination area.
     * @return A document.
     * @throws PublicationException when an error occurs.
     */
    Document getAreaVersion(Document document, String area) throws PublicationException;

    /**
     * Returns the documents a document depends on (can't exist without).
     * For instance, in a site tree, a document depends on its parent document.
     * It is not required that these documents really exist. 
     * @param document A document.
     * @return An array of documents.
     * @throws PublicationException when an error occurs.
     */
    Document[] getRequiredDocuments(Document document) throws PublicationException;

    /**
     * Checks if a document depends on another document.
     * @param dependingDocument The depending document.
     * @param requiredDocument The required document.
     * @return A boolean value.
     * @throws PublicationException when an error occurs.
     */
    boolean dependsOn(Document dependingDocument, Document requiredDocument)
        throws PublicationException;

}