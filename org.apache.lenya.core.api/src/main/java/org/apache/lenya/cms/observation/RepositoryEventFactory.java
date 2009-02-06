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
package org.apache.lenya.cms.observation;

import org.apache.cocoon.spring.configurator.WebAppContextUtils;
import org.apache.commons.logging.Log;
import org.apache.lenya.cms.publication.Document;
import org.apache.lenya.cms.publication.DocumentException;
import org.apache.lenya.cms.publication.DocumentFactory;
import org.apache.lenya.cms.publication.DocumentFactoryBuilder;
import org.apache.lenya.cms.publication.Publication;
import org.apache.lenya.cms.repository.Node;
import org.apache.lenya.cms.repository.Session;

/**
 * Factory to create repository events.
 */
public class RepositoryEventFactory {

    /**
     * Creates a repository event for a node.
     * @param session The session.
     * @param logger The logger.
     * @param descriptor The descriptor.
     * @return An event.
     */
    public static final RepositoryEvent createEvent(Session session, Log logger, Object descriptor) {
        return new RepositoryEvent(session, descriptor);
    }

    /**
     * Creates a repository event for a node.
     * @param doc The document.
     * @param logger The logger.
     * @param descriptor The descriptor.
     * @return An event.
     */
    public static final RepositoryEvent createEvent(Document doc, Log logger, Object descriptor) {
        try {
            Node node = doc.getRepositoryNode();
            RepositoryEvent event = new DocumentEvent(node.getSession(), doc.getPublication()
                    .getId(), doc.getArea(), doc.getUUID(), doc.getLanguage(), doc
                    .getResourceType(), descriptor);
            event.setNodeUri(node.getSourceURI());
            int[] revisions = node.getHistory().getRevisionNumbers();
            if (revisions.length > 0) {
                event.setRevision(revisions[0]);
            }
            return event;
        } catch (DocumentException e) {
            throw new RuntimeException(e);
        }

    }

    /**
     * Creates a repository event for a node.
     * @param manager The service manager.
     * @param node The node.
     * @param logger The logger.
     * @param descriptor The descriptor.
     * @return An event.
     */
    public static final RepositoryEvent createEvent(Node node, Log logger, Object descriptor) {
        RepositoryEvent event;
        Document doc = null;
        if (!node.getSourceURI().endsWith("meta")) {
            doc = getDocument(node, logger);
        }
        if (doc != null) {
            event = createEvent(doc, logger, descriptor);
        } else {
            event = new RepositoryEvent(node.getSession(), descriptor);
            event.setNodeUri(node.getSourceURI());
        }
        return event;
    }

    /**
     * @param node The node.
     * @param logger The logger.
     * @return The document represented by the node or <code>null</code> if the node doesn't
     *         represent a document.
     */
    protected static final Document getDocument(Node node, Log logger) {

        final String sourceUri = node.getSourceURI();
        if (sourceUri.endsWith(".xml")) {
            return null;
        }

        Document doc = null;

        if (!sourceUri.startsWith("lenya://")) {
            throw new IllegalStateException("The source URI [" + sourceUri
                    + "] doesn't start with lenya://");
        }

        String path = sourceUri.substring("lenya://lenya/pubs/".length());

        String[] steps = path.split("/");
        String pubId = steps[0];
        String area = steps[2];

        try {

            DocumentFactoryBuilder builder = (DocumentFactoryBuilder) WebAppContextUtils
                    .getCurrentWebApplicationContext().getBean(
                            DocumentFactoryBuilder.class.getName());
            DocumentFactory factory = builder.createDocumentFactory(node.getSession());
            Publication pub = factory.getPublication(pubId);
            String docPath = path.substring((pubId + "/content/" + area).length());

            String uuid = docPath.substring(1, docPath.length() - "/en".length());
            String language = docPath.substring(docPath.length() - "en".length());

            doc = factory.get(pub, area, uuid, language);

            if (doc == null) {
                // this happens if the node was not a document node
                logger.info("No document found for node [" + sourceUri + "]");
            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return doc;
    }

}
