/*
$Id: AbstractPublication.java,v 1.1 2003/11/26 18:22:47 andreas Exp $
<License>

 ============================================================================
                   The Apache Software License, Version 1.1
 ============================================================================

 Copyright (C) 1999-2003 The Apache Software Foundation. All rights reserved.

 Redistribution and use in source and binary forms, with or without modifica-
 tion, are permitted provided that the following conditions are met:

 1. Redistributions of  source code must  retain the above copyright  notice,
    this list of conditions and the following disclaimer.

 2. Redistributions in binary form must reproduce the above copyright notice,
    this list of conditions and the following disclaimer in the documentation
    and/or other materials provided with the distribution.

 3. The end-user documentation included with the redistribution, if any, must
    include  the following  acknowledgment:  "This product includes  software
    developed  by the  Apache Software Foundation  (http://www.apache.org/)."
    Alternately, this  acknowledgment may  appear in the software itself,  if
    and wherever such third-party acknowledgments normally appear.

 4. The names "Apache Lenya" and  "Apache Software Foundation"  must  not  be
    used to  endorse or promote  products derived from  this software without
    prior written permission. For written permission, please contact
    apache@apache.org.

 5. Products  derived from this software may not  be called "Apache", nor may
    "Apache" appear  in their name,  without prior written permission  of the
    Apache Software Foundation.

 THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED WARRANTIES,
 INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 FITNESS  FOR A PARTICULAR  PURPOSE ARE  DISCLAIMED.  IN NO  EVENT SHALL  THE
 APACHE SOFTWARE  FOUNDATION  OR ITS CONTRIBUTORS  BE LIABLE FOR  ANY DIRECT,
 INDIRECT, INCIDENTAL, SPECIAL,  EXEMPLARY, OR CONSEQUENTIAL  DAMAGES (INCLU-
 DING, BUT NOT LIMITED TO, PROCUREMENT  OF SUBSTITUTE GOODS OR SERVICES; LOSS
 OF USE, DATA, OR  PROFITS; OR BUSINESS  INTERRUPTION)  HOWEVER CAUSED AND ON
 ANY  THEORY OF LIABILITY,  WHETHER  IN CONTRACT,  STRICT LIABILITY,  OR TORT
 (INCLUDING  NEGLIGENCE OR  OTHERWISE) ARISING IN  ANY WAY OUT OF THE  USE OF
 THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

 This software  consists of voluntary contributions made  by many individuals
 on  behalf of the Apache Software  Foundation and was  originally created by
 Michael Wechner <michi@apache.org>. For more information on the Apache Soft-
 ware Foundation, please see <http://www.apache.org/>.

 Lenya includes software developed by the Apache Software Foundation, W3C,
 DOM4J Project, BitfluxEditor, Xopus, and WebSHPINX.
</License>
*/
package org.apache.lenya.cms.publication;

import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.DefaultConfigurationBuilder;
import org.apache.lenya.cms.publishing.PublishingEnvironment;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

/**
 * A publication.
 *
 * @author <a href="mailto:andreas.hartmann@wyona.org">Andreas Hartmann</a>
 */
public abstract class AbstractPublication implements Publication {
    private static final String[] areas =
        { AUTHORING_AREA, STAGING_AREA, LIVE_AREA, ADMIN_AREA, ARCHIVE_AREA, TRASH_AREA };

    private String id;
    private PublishingEnvironment environment;
    private File servletContext;
    private DocumentIdToPathMapper mapper = null;
    private ArrayList languages = new ArrayList();
    private String defaultLanguage = null;
    private String breadcrumbprefix = null;
    private HashMap siteTrees = new HashMap();

    /** 
     * Creates a new instance of Publication
     * 
     * @param id the publication id
     * @param servletContextPath the servlet context of this publication
     * 
     * @throws PublicationException if there was a problem reading the config file
     */
    protected AbstractPublication(String id, String servletContextPath)
        throws PublicationException {
        assert id != null;
        this.id = id;

        assert servletContextPath != null;

        File servletContext = new File(servletContextPath);
        assert servletContext.exists();
        this.servletContext = servletContext;

        // FIXME: remove PublishingEnvironment from publication
        environment = new PublishingEnvironment(servletContextPath, id);

        File configFile = new File(getDirectory(), CONFIGURATION_FILE);
        DefaultConfigurationBuilder builder = new DefaultConfigurationBuilder();

        Configuration config;

        String pathMapperClassName = null;
        String documentBuilderClassName = null;

        try {
            config = builder.buildFromFile(configFile);

            try {
                pathMapperClassName = config.getChild(ELEMENT_PATH_MAPPER).getValue();
                Class pathMapperClass = Class.forName(pathMapperClassName);
                this.mapper = (DocumentIdToPathMapper) pathMapperClass.newInstance();
            } catch (ClassNotFoundException e) {
                throw new PublicationException(
                    "Cannot instantiate documentToPathMapper: [" + pathMapperClassName + "]",
                    e);
            }

            try {
                documentBuilderClassName = config.getChild(ELEMENT_DOCUMENT_BUILDER).getValue();
                Class documentBuilderClass = Class.forName(documentBuilderClassName);
                this.documentBuilder = (DocumentBuilder) documentBuilderClass.newInstance();
            } catch (ClassNotFoundException e) {
                throw new PublicationException(
                    "Cannot instantiate document builder: [" + pathMapperClassName + "]",
                    e);
            }

            Configuration[] languages = config.getChild(LANGUAGES).getChildren();
            for (int i = 0; i < languages.length; i++) {
                Configuration languageConfig = languages[i];
                String language = languageConfig.getValue();
                this.languages.add(language);
                if (languageConfig.getAttribute(DEFAULT_LANGUAGE_ATTR, null) != null) {
                    defaultLanguage = language;
                }
            }

        } catch (PublicationException e) {
            throw e;
        } catch (Exception e) {
            throw new PublicationException(
                "Problem with config file: " + configFile.getAbsolutePath(),
                e);
        }

        breadcrumbprefix = config.getChild(BREADCRUMB_PREFIX).getValue("");

    }

    /**
     * Returns the publication ID.
     * @return A string value.
     */
    public String getId() {
        return id;
    }

    /**
     * Returns the publishing environment of this publication.
     * @return A {@link PublishingEnvironment} object.
     * @deprecated It is planned to decouple the environments from the publication.
     */
    public PublishingEnvironment getEnvironment() {
        return environment;
    }

    /**
     * Returns the servlet context this publication belongs to
     * (usually, the <code>webapps/lenya</code> directory).
     * @return A <code>File</code> object.
     */
    public File getServletContext() {
        return servletContext;
    }

    /**
     * Returns the publication directory.
     * @return A <code>File</code> object.
     */
    public File getDirectory() {
        return new File(getServletContext(), PUBLICATION_PREFIX + File.separator + getId());
    }

    /**
     * Return the directory of a specific area.
     * 
     * @param area a <code>File</code> representing the root of the area content directory.
     * 
     * @return the directory of the given content area. 
     */
    public File getContentDirectory(String area) {
        return new File(getDirectory(), CONTENT_PATH + File.separator + area);
    }

    /**
     * DOCUMENT ME!
     *
     * @param mapper DOCUMENT ME!
     */
    public void setPathMapper(DefaultDocumentIdToPathMapper mapper) {
        assert mapper != null;
        this.mapper = mapper;
    }

    /**
     * Returns the path mapper.
     * 
     * @return a <code>DocumentIdToPathMapper</code>
     */
    public DocumentIdToPathMapper getPathMapper() {
        return mapper;
    }

    /**
     * Returns if a given string is a valid area name.
     * @param area The area string to test.
     * @return A boolean value.
     */
    public static boolean isValidArea(String area) {
        return area != null && Arrays.asList(areas).contains(area);
    }

    /**
     * Get the default language
     * 
     * @return the default language
     */
    public String getDefaultLanguage() {
        return defaultLanguage;
    }

    /**
     * Set the default language
     * 
     * @param language the default language
     */
    public void setDefaultLanguage(String language) {
        defaultLanguage = language;
    }

    /**
     * Get all available languages for this publication
     * 
     * @return an <code>Array</code> of languages
     */
    public String[] getLanguages() {
        return (String[]) languages.toArray(new String[languages.size()]);
    }

    /**
     * Get the breadcrumb prefix. It can be used as a prefix if a publication is part of a larger site
     * 
     * @return the breadcrumb prefix
     */
    public String getBreadcrumbPrefix() {
        return breadcrumbprefix;
    }

    /**
     * Get the sitetree for a specific area of this publication. 
     * Sitetrees are created on demand and are cached.
     * 
     * @param area the area
     * @return the sitetree for the specified area
     * 
     * @throws SiteTreeException if an error occurs 
     */
    public DefaultSiteTree getSiteTree(String area) throws SiteTreeException {

        DefaultSiteTree sitetree = null;

        if (siteTrees.containsKey(area)) {
            sitetree = (DefaultSiteTree) siteTrees.get(area);
        } else {
            sitetree = new DefaultSiteTree(getDirectory(), area);
            siteTrees.put(area, sitetree);
        }
        return sitetree;
    }

    private DocumentBuilder documentBuilder;

    /**
     * Returns the document builder of this instance.
     * @return A document builder.
     */
    public DocumentBuilder getDocumentBuilder() {
        return documentBuilder;
    }

    /**
     * Creates a version of the document object in another area.
     * @param document The document to clone.
     * @param destinationArea The destination area.
     * @return A document.
     * @throws PublicationException when an error occurs.
     */
    protected Document cloneDocument(Document document, String destinationArea)
        throws PublicationException {
        DocumentBuilder builder = getDocumentBuilder();
        String url =
            builder.buildCanonicalUrl(
                this,
                destinationArea,
                document.getId(),
                document.getLanguage());
        Document destinationDocument = builder.buildDocument(this, url);
        return destinationDocument;
    }

}
