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
package org.apache.lenya.cms.publication.file;

import java.io.File;
import java.io.IOException;

import org.apache.avalon.excalibur.io.FileUtil;
import org.apache.lenya.cms.publication.AbstractDocumentManager;
import org.apache.lenya.cms.publication.Document;
import org.apache.lenya.cms.publication.PublicationException;

/**
 * Filesystem-based document manager.
 *
 * @version $Id:$ 
 */
public class FileDocumentManager extends AbstractDocumentManager {

    /**
     * @see org.apache.lenya.cms.publication.AbstractDocumentManager#copyDocumentSource(org.apache.lenya.cms.publication.Document,
     *      org.apache.lenya.cms.publication.Document)
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
     * @see org.apache.lenya.cms.publication.AbstractDocumentManager#deleteDocumentSource(org.apache.lenya.cms.publication.Document)
     */
    protected void deleteDocumentSource(Document document) throws PublicationException {
        File file = document.getFile();
        boolean deleted = file.delete();
        if (!deleted) {
            throw new PublicationException("Source file [" + file + "] of document [" + document
                    + "] could not be deleted!");
        }
    }

}