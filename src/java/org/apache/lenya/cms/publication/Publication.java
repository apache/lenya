/*
$Id
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

import org.apache.lenya.cms.publishing.PublishingEnvironment;

import java.io.File;
import java.util.Arrays;


/**
 * A publication.
 *
 * @author <a href="mailto:andreas.hartmann@wyona.org">Andreas Hartmann</a>
 */
public class Publication {
    public static final String AUTHORING_AREA = "authoring";
    public static final String LIVE_AREA = "live";
    public static final String INFO_AREA = "info";
    
    private final static String[] areas = {
        AUTHORING_AREA, LIVE_AREA, INFO_AREA
    };

    /** Creates a new instance of Publication */
    protected Publication(String id, String servletContextPath) {
        assert id != null;
        this.id = id;

        assert servletContextPath != null;

        File servletContext = new File(servletContextPath);
        assert servletContext.exists();
        this.servletContext = servletContext;

        // FIXME: remove PublishingEnvironment from publication
        environment = new PublishingEnvironment(servletContextPath, id);
    }

    private String id;

    /**
     * Returns the publication ID.
     * @return A string value.
     */
    public String getId() {
        return id;
    }

    private PublishingEnvironment environment;

    /**
     * Returns the publishing environment of this publication.
     * @return A {@link PublishingEnvironment} object.
     * @deprecated It is planned to decouple the environments from the publication.
     */
    public PublishingEnvironment getEnvironment() {
        return environment;
    }

    private File servletContext;

    /**
     * Returns the servlet context this publication belongs to
     * (usually, the <code>webapps/lenya</code> directory).
     * @return A <code>File</code> object.
     */
    public File getServletContext() {
        return servletContext;
    }

    public static final String PUBLICATION_PREFIX = "lenya" + File.separator + "pubs";

    /**
     * Returns the publication directory.
     * @return A <code>File</code> object.
     */
    public File getDirectory() {
        return new File(getServletContext(), PUBLICATION_PREFIX + File.separator + getId());
    }

    private DocumentIdToPathMapper mapper = new DefaultDocumentIdToPathMapper();

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
     * @return
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
}
