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

import org.apache.excalibur.source.Source;
import org.apache.excalibur.source.SourceResolver;
import org.apache.lenya.cms.cocoon.source.SourceUtil;
import org.apache.lenya.cms.linking.LinkConverter;
import org.apache.lenya.cms.repository.Node;
import org.apache.lenya.cms.usecase.DocumentUsecase;
import org.apache.lenya.cms.usecase.UsecaseException;
import org.apache.lenya.cms.workflow.WorkflowUtil;

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
        SourceResolver resolver = null;
        Source source = null;
        try {
            resolver = (SourceResolver) this.manager.lookup(SourceResolver.ROLE);
            SourceUtil.copy(resolver,
                    getParameterAsString(SOURCE_URI),
                    getSourceDocument().getSourceURI());
            
            LinkConverter converter = new LinkConverter(this.manager, getLogger());
            converter.convertUrlsToUuids(getSourceDocument());

            WorkflowUtil.invoke(this.manager,
                    getSession(),
                    getLogger(),
                    getSourceDocument(),
                    "edit");
        } finally {
            if (resolver != null) {
                if (source != null) {
                    resolver.release(source);
                }
                this.manager.release(resolver);
            }
        }
    }

    /**
     * @see org.apache.lenya.cms.usecase.AbstractUsecase#getNodesToLock()
     */
    protected Node[] getNodesToLock() throws UsecaseException {
        if (getLogger().isDebugEnabled()) {
            getLogger().debug("EditDocument::getObjectsToLock() called on source document ["
                    + getSourceDocument().getId() + "]");
        }

        Node[] objects = { getSourceDocument().getRepositoryNode() };
        return objects;
    }

}
