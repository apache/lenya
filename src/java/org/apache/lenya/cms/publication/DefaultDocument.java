/*
 * $Id
 * <License>
 * The Apache Software License
 *
 * Copyright (c) 2002 lenya. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice, this
 *    list of conditions and the following disclaimer in the documentation and/or
 *    other materials provided with the distribution.
 *
 * 3. All advertising materials mentioning features or use of this software must
 *    display the following acknowledgment: "This product includes software developed
 *    by lenya (http://www.lenya.org)"
 *
 * 4. The name "lenya" must not be used to endorse or promote products derived from
 *    this software without prior written permission. For written permission, please
 *    contact contact@lenya.org
 *
 * 5. Products derived from this software may not be called "lenya" nor may "lenya"
 *    appear in their names without prior written permission of lenya.
 *
 * 6. Redistributions of any form whatsoever must retain the following acknowledgment:
 *    "This product includes software developed by lenya (http://www.lenya.org)"
 *
 * THIS SOFTWARE IS PROVIDED BY lenya "AS IS" WITHOUT ANY WARRANTY EXPRESS OR IMPLIED,
 * INCLUDING THE WARRANTY OF NON-INFRINGEMENT AND THE IMPLIED WARRANTIES OF MERCHANTI-
 * BILITY AND FITNESS FOR A PARTICULAR PURPOSE. lenya WILL NOT BE LIABLE FOR ANY DAMAGES
 * SUFFERED BY YOU AS A RESULT OF USING THIS SOFTWARE. IN NO EVENT WILL lenya BE LIABLE
 * FOR ANY SPECIAL, INDIRECT OR CONSEQUENTIAL DAMAGES OR LOST PROFITS EVEN IF lenya HAS
 * BEEN ADVISED OF THE POSSIBILITY OF THEIR OCCURRENCE. lenya WILL NOT BE LIABLE FOR ANY
 * THIRD PARTY CLAIMS AGAINST YOU.
 *
 * Lenya includes software developed by the Apache Software Foundation, W3C,
 * DOM4J Project, BitfluxEditor and Xopus.
 * </License>
 */
package org.apache.lenya.cms.publication;

import java.io.File;

/**
 * A typical CMS document.
 * 
 * @author <a href="mailto:andreas.hartmann@wyona.org">Andreas Hartmann</a>
 */
public class DefaultDocument implements Document {

    /**
     * Creates a new instance of DefaultDocument.
     * @param publication The publication the document belongs to.
     * @param id The document ID (starting with a slash).
     * @deprecated Use {@link DefaultDocumentBuilder} instead.
     */
    public DefaultDocument(Publication publication, String id) {
        assert id != null;
        assert id.startsWith("/");
        this.id = id;

        assert publication != null && !"".equals(publication);
        this.publication = publication;
    }

    /**
     * Creates a new instance of DefaultDocument.
     * @param publication The publication the document belongs to.
     * @param id The document ID (starting with a slash).
     * @param area The area.
     */
    protected DefaultDocument(Publication publication, String id, String area) {
        assert id != null;
        assert id.startsWith("/");
        this.id = id;

        assert publication != null && !"".equals(publication);
        this.publication = publication;

        setArea(area);
    }

    private String id;
    private Publication publication;

    /**
     * @see org.apache.lenya.cms.publication.Document#getFile()
     */
    public String getId() {
        return id;
    }

    /**
     * @see org.apache.lenya.cms.publication.Document#getPublication()
     */
    public Publication getPublication() {
        return publication;
    }

    /**
     * Returns the file for this document.
     * @return A file object.
     */
    public File getFile() {
        return getPublication().getPathMapper().getFile(
            getPublication(),
            getArea(),
            getId(),
            getLanguage());
    }

    private String language = "";

    /**
     * @see org.apache.lenya.cms.publication.Document#getLanguage()
     */
    public String getLanguage() {
        return language;
    }

    /**
     * Sets the language of this document.
     * @param language The language.
     */
    public void setLanguage(String language) {
        assert language != null;
        this.language = language;
    }

    private String area;

    /**
     * @see org.apache.lenya.cms.publication.Document#getArea()
     */
    public String getArea() {
        return area;
    }

    /**
     * @see Document#getCompleteUrl(String)
     */
    public String getCompleteUrl() {
        String languageSuffix = "".equals(getLanguage()) ? "" : "_" + getLanguage();

        String extensionSuffix = "".equals(getExtension()) ? "" : "." + getExtension();

        return "/" + getPublication().getId() + "/" + getArea() + getDocumentUrl();
    }

    /**
     * Sets the area.
     * @param area A string.
     */
    protected void setArea(String area) {
        assert area != null
            && (area.equals(Publication.AUTHORING_AREA) || area.equals(Publication.LIVE_AREA));
        this.area = area;
    }

    private String extension = "html";

    /**
     * @see org.apache.lenya.cms.publication.Document#getExtension()
     */
    public String getExtension() {
        return extension;
    }

    /**
     * Sets the extension of the file in the URL.
     * @param extension A string.
     */
    protected void setExtension(String extension) {
        assert extension != null;
        this.extension = extension;
    }

    /**
     * @see org.apache.lenya.cms.publication.Document#getDocumentUrl()
     */
    public String getDocumentUrl() {
        String languageSuffix = "".equals(getLanguage()) ? "" : "_" + getLanguage();
        String extensionSuffix = "".equals(getExtension()) ? "" : "." + getExtension();
        return getId() + languageSuffix + extensionSuffix;
    }

}
