/*
$Id: ResourcePublisher.java,v 1.3 2003/10/27 16:57:33 egli Exp $
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
package org.apache.lenya.cms.ant;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.lenya.cms.publication.Document;
import org.apache.lenya.cms.publication.DocumentBuilder;
import org.apache.lenya.cms.publication.Publication;
import org.apache.lenya.cms.publication.ResourcesManager;
import org.apache.lenya.util.FileUtil;
import org.apache.tools.ant.BuildException;

/**
 * @author andreas
 *
 * To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
public class ResourcePublisher extends PublicationTask {

    private String documentId;

    /**
     * @see org.apache.tools.ant.Task#execute()
     */
    public void execute() throws BuildException {

        try {
            DocumentBuilder builder = getPublication().getDocumentBuilder();

            String authoringUrl =
                builder.buildCanonicalUrl(getPublication(), Publication.AUTHORING_AREA, documentId);
            Document authoringDocument = builder.buildDocument(getPublication(), authoringUrl);
            ResourcesManager authoringManager = new ResourcesManager(authoringDocument);

            String liveUrl =
                builder.buildCanonicalUrl(getPublication(), Publication.LIVE_AREA, documentId);
            Document liveDocument = builder.buildDocument(getPublication(), liveUrl);
            ResourcesManager liveManager = new ResourcesManager(liveDocument);
            
            // find all resource files and their associated meta files
            List resourcesList =
                new ArrayList(Arrays.asList(authoringManager.getResources()));
            resourcesList.addAll(
                Arrays.asList(authoringManager.getMetaFiles()));
            File[] resources =
                (File[])resourcesList.toArray(new File[resourcesList.size()]);
            File liveDirectory = liveManager.getPath();
            
            for (int i = 0; i < resources.length; i++) {
                File liveResource = new File(liveDirectory, resources[i].getName());
                String destPath = liveResource.getAbsolutePath();

                log("Copy file [" + resources[i].getAbsolutePath() + "] to [" + destPath + "]");
                FileUtil.copy(resources[i].getAbsolutePath(), destPath);
            }

        } catch (Exception e) {
            throw new BuildException(e);
        }
    }

    /**
     * Returns the document ID.
     * @return A document ID.
     */
    public String getDocumentId() {
        return documentId;
    }

    /**
     * Sets the document ID.
     * @param documentId A document ID.
     */
    public void setDocumentId(String documentId) {
        this.documentId = documentId;
    }

}
