/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 *  contributor license agreements.  See the NOTICE file distributed with
 *  this work for additional information regarding copyright ownership.
 *  The ASF licenses this file to You under the Apache License, Version 2.0
 *  (the "License"); you may not use this file except in compliance with
 *  the License.  You may obtain a copy of the License at
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
package org.apache.lenya.cms.site.usecases;

import java.util.ArrayList;
import java.util.List;

import org.apache.lenya.cms.publication.Document;
import org.apache.lenya.cms.publication.DocumentException;
import org.apache.lenya.cms.site.SiteException;
import org.apache.lenya.cms.site.SiteNode;
import org.apache.lenya.util.ServletHelper;

/**
 * Usecase to add Assets to a resource.
 * 
 * @version $Id$
 */
public class Assets extends SiteUsecase {
    
    protected static final String PARAMETER_UPLOAD_ENABLED = "uploadEnabled";
    protected static final String MESSAGE_UPLOAD_DISABLED = "upload-disabled";

    protected void prepareView() throws Exception {
        super.prepareView();
        
        if (getSourceDocument() != null) {
            try {
                Document[] resourceDocs = getResourceDocuments();
                setParameter("resourceDocuments", resourceDocs);
            } catch (final Exception e) {
                throw new RuntimeException(e);
            }
            
            if (!ServletHelper.isUploadEnabled(this.manager)) {
                addErrorMessage(MESSAGE_UPLOAD_DISABLED);
                setParameter(PARAMETER_UPLOAD_ENABLED, Boolean.FALSE);
            }
        }
    }

    protected Document[] getResourceDocuments() throws DocumentException, SiteException {
        List list = new ArrayList();
        Document[] docs = getSourceDocument().area().getDocuments();
        SiteNode node = getSourceDocument().getLink().getNode();
        for (int i = 0; i < docs.length; i++) {
            if (docs[i].hasLink() && !docs[i].getLink().getNode().isTopLevel()
                    && docs[i].getLink().getNode().getParent().getPath().equals(node.getPath())
                    && docs[i].getResourceType().getName().equals("resource")) {
                list.add(docs[i]);
            }
        }
        return (Document[]) list.toArray(new Document[list.size()]);
    }

}
