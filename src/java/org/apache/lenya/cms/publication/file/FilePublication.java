/*
$Id: FilePublication.java,v 1.5 2004/02/12 17:56:07 andreas Exp $
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
package org.apache.lenya.cms.publication.file;

import java.io.File;
import java.io.IOException;

import org.apache.avalon.excalibur.io.FileUtil;
import org.apache.lenya.cms.publication.AbstractPublication;
import org.apache.lenya.cms.publication.Document;
import org.apache.lenya.cms.publication.DocumentSet;
import org.apache.lenya.cms.publication.DublinCoreImpl;
import org.apache.lenya.cms.publication.DublinCoreProxy;
import org.apache.lenya.cms.publication.PublicationException;

/**
 * A file-based publication.
 * 
 * @author <a href="mailto:andreas@apache.org">Andreas Hartmann</a>
 */
public class FilePublication extends AbstractPublication {

    /**
     * Ctor.
     * @param id The publication ID.
     * @param servletContextPath The servlet context path.
     * @throws PublicationException when something went wrong.
     */
    public FilePublication(String id, String servletContextPath) throws PublicationException {
        super(id, servletContextPath);
    }

    /**
     * Returns the directory where documents of a certain area are located.
     * @param area
     * @return
     */
    protected File getAreaDirectory(String area) {
        File areaDirectory = new File(getDirectory(), "content" + File.separator + area);
        return areaDirectory;
    }

    /**
     * @see org.apache.lenya.cms.publication.AbstractPublication#copyDocumentToArea(org.apache.lenya.cms.publication.Document, java.lang.String)
     */
    public void copyDocumentToArea(Document document, String destinationArea)
        throws PublicationException {
        Document destinationDocument = getAreaVersion(document, destinationArea);
        copyDocument(document, destinationDocument);
    }

    /**
     * @see org.apache.lenya.cms.publication.AbstractPublication#copyDocumentSource(org.apache.lenya.cms.publication.Document, org.apache.lenya.cms.publication.Document)
     */
    public void copyDocumentSource(Document sourceDocument, Document destinationDocument)
        throws PublicationException {
        File file = sourceDocument.getFile();
        File destinationDirectory = destinationDocument.getFile().getParentFile();
        try {
            if (!destinationDirectory.isDirectory()) {
                destinationDirectory.mkdirs();
            }
            FileUtil.copyFileToDirectory(file, destinationDirectory);
            DublinCoreProxy proxy = (DublinCoreProxy) destinationDocument.getDublinCore();
            DublinCoreImpl dublinCore = (DublinCoreImpl) proxy.instance();
            dublinCore.refresh();
        } catch (IOException e) {
            throw new PublicationException(e);
        }
    }

    /**
     * @see org.apache.lenya.cms.publication.Publication#copyDocumentSetToArea(org.apache.lenya.cms.publication.DocumentSet, java.lang.String)
     */
    public void copyDocumentSetToArea(DocumentSet documentSet, String destinationArea)
        throws PublicationException {
        Document[] documents = documentSet.getDocuments();
        for (int i = 0; i < documents.length; i++) {
            copyDocumentToArea(documents[i], destinationArea);
        }
    }

    /**
     * @see org.apache.lenya.cms.publication.AbstractPublication#deleteDocumentSource(org.apache.lenya.cms.publication.Document)
     */
    protected void deleteDocumentSource(Document document) throws PublicationException {
        File file = document.getFile();
        boolean deleted = file.delete();
        if (!deleted) {
            throw new PublicationException(
                "Source file of document [" + document.getId() + "] could not be deleted!");
        }
    }

}
