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
package org.apache.lenya.cms.editors;

import org.apache.lenya.cms.cocoon.source.SourceUtil;
import org.apache.lenya.cms.linking.LinkConverter;
import org.apache.lenya.cms.publication.Document;
import org.apache.lenya.cms.publication.ResourceType;
import org.apache.lenya.cms.repository.Node;
import org.apache.lenya.cms.usecase.DocumentUsecase;
import org.apache.lenya.cms.usecase.UsecaseException;
import org.apache.lenya.cms.usecase.xml.UsecaseErrorHandler;
import org.apache.lenya.cms.workflow.WorkflowUtil;
import org.apache.lenya.xml.Schema;
import org.apache.lenya.xml.ValidationUtil;

/**
 * Usecase to edit documents.
 * 
 * @version $Id$
 */
public class EditDocument extends DocumentUsecase {

    /**
     * The URI to copy the document source from.
     */
    public static final String SOURCE_URI = "sourceUri";

    /**
     * @see org.apache.lenya.cms.usecase.AbstractUsecase#doExecute()
     */
    protected void doExecute() throws Exception {
        super.doExecute();

        Document sourceDoc = getSourceDocument();

        String sourceUri = getParameterAsString(SOURCE_URI);
        org.w3c.dom.Document xmlDoc = SourceUtil.readDOM(sourceUri, this.manager);
        
        ResourceType resourceType = sourceDoc.getResourceType();
        Schema schema = resourceType.getSchema();
        ValidationUtil.validate(this.manager, xmlDoc, schema, new UsecaseErrorHandler(this));

        if (!hasErrors()) {
            SourceUtil.writeDOM(xmlDoc, sourceDoc.getSourceURI(), this.manager);
            
            LinkConverter converter = new LinkConverter(this.manager, getLogger());
            converter.convertUrlsToUuids(getSourceDocument());

            WorkflowUtil.invoke(this.manager, getSession(), getLogger(), getSourceDocument(),
                    "edit");
        }
    }

    /**
     * @see org.apache.lenya.cms.usecase.AbstractUsecase#getNodesToLock()
     */
    protected Node[] getNodesToLock() throws UsecaseException {
        Node[] objects = { getSourceDocument().getRepositoryNode() };
        return objects;
    }

}
