/*
 * Copyright  1999-2004 The Apache Software Foundation
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 */

/* $Id: FilePublication.java,v 1.8 2004/03/01 16:18:27 gregor Exp $  */

package org.apache.lenya.cms.publication.file;

import java.io.File;
import java.io.IOException;

import org.apache.avalon.excalibur.io.FileUtil;
import org.apache.lenya.cms.publication.AbstractPublication;
import org.apache.lenya.cms.publication.Document;
import org.apache.lenya.cms.publication.DocumentSet;
import org.apache.lenya.cms.publication.PublicationException;

/**
 * A file-based publication.
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
            destinationDocument.getDublinCore().replaceBy(sourceDocument.getDublinCore());
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
                "Source file [" + file + "] of document [" + document + "] could not be deleted!");
        }
    }

}
