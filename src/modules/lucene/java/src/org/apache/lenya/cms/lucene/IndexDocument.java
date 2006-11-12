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
package org.apache.lenya.cms.lucene;

import java.util.Arrays;

import org.apache.excalibur.source.Source;
import org.apache.excalibur.source.SourceResolver;
import org.apache.lenya.cms.repository.Node;
import org.apache.lenya.cms.usecase.DocumentUsecase;
import org.apache.lenya.cms.usecase.UsecaseException;
import org.xml.sax.InputSource;

/**
 * Usecase to maintain lucene index.
 */
public class IndexDocument extends DocumentUsecase {

    /**
     * The URI to copy the document source from.
     */
    public static final String SOURCE_URI = "sourceUri";

    public static final String INDEX_ACTION = "indexAction";
    public static final String INDEX = "index";
    public static final String DELETE = "delete";
    public static final String INDEX_AREA = "indexArea";

    /**
     * @see org.apache.lenya.cms.usecase.AbstractUsecase#doExecute()
     */
    protected void doExecute() throws Exception {
        super.doExecute();
        SourceResolver resolver = null;
        Source source = null;

        String action = super.getParameterAsString(INDEX_ACTION);
        String area = super.getParameterAsString(INDEX_AREA);

        try {
            String[] formats = getSourceDocument().getResourceType().getFormats();
            if (!Arrays.asList(formats).contains("luceneIndex")) {
                getLogger().warn("Document ["+getSourceDocument()+"] is not being indexed because resource type [" + getSourceDocument().getResourceType().getName() + "] does not support indexing!");
                return;
            }
            resolver = (SourceResolver) this.manager.lookup(SourceResolver.ROLE);
            if (action.equals(INDEX)) {
                // index
                source = resolver.resolveURI("cocoon://modules/lucene/index-" + area + ".xml");
                InputSource xmlInputSource = org.apache.cocoon.components.source.SourceUtil.getInputSource(source);
            } else if (action.equals(DELETE)) {
                // delete
                source = resolver.resolveURI("cocoon://modules/lucene/delete-" + area + ".xml");
                InputSource xmlInputSource = org.apache.cocoon.components.source.SourceUtil.getInputSource(source);
            }
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
            getLogger().debug("IndexDocument::getObjectsToLock() called on source document ["
                    + getSourceDocument() + "]");
        }

        Node[] objects = { getSourceDocument().getRepositoryNode() };
        return objects;
    }

}
