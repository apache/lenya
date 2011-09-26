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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
//import org.apache.lenya.cms.publication.Document;
//florent import org.apache.lenya.cms.publication.DocumentImpl;
/*import org.apache.lenya.cms.publication.Publication;
import org.apache.lenya.cms.repository.Node;
import org.apache.lenya.cms.repository.Session;*/

/**
 * Factory to create repository events.
 */
//public class RepositoryEventFactory implements RepositoryEventFactory {
public class RepositoryEventFactory {

    private static final Log logger = LogFactory.getLog(RepositoryEventFactory.class);

    /**
     * Creates a repository event without source..
     * @param session The session.
     * @param descriptor The descriptor.
     * @return An event.
     */
    /*public static final RepositoryEvent createEvent(Session session, Object descriptor) {
        return new RepositoryEventImpl(session, descriptor);
    }*/
    public static final RepositoryEvent createEvent(Object descriptor) {
        return new RepositoryEventImpl(descriptor);
    }

    /**
     * Creates a repository event for a node.
     * @param doc The document.
     * @param descriptor The descriptor.
     * @return An event.
     */
    /* florent : seems not used and create a dependencie to lenya-document-impl
    public static final RepositoryEvent createEvent(Document doc, Object descriptor) {
        Node node = ((DocumentImpl) doc).getRepositoryNode();
        RepositoryEvent event = new RepositoryEvent(node.getRepositorySession(),
                new DocumentEventSource(doc), descriptor);
        event.setNodeUri(node.getSourceURI());
        int[] revisions = node.getHistory().getRevisionNumbers();
        if (revisions.length > 0) {
            event.setRevision(revisions[0]);
        }
        return event;

    }*/

    /**
     * Creates a repository event for a node.
     * @param node The node.
     * @param descriptor The descriptor.
     * @return An event.
     */
    /*
    public static final RepositoryEvent createEvent(Node node, Object descriptor) {
        RepositoryEvent event;
        Document doc = null;
        if (!node.getSourceURI().endsWith("meta")) {
            doc = getDocument(node);
        }
        if (doc != null) {
            event = createEvent(doc, descriptor);
        } else {
            event = new RepositoryEventImpl(node.getRepositorySession(), descriptor);
            event.setNodeUri(node.getSourceURI());
        }
        return event;
    }*/

    /**
     * @param node The node.
     * @return The document represented by the node or <code>null</code> if the node doesn't
     *         represent a document.
     */
    /*
    protected static final Document getDocument(Node node) {

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

            org.apache.lenya.cms.publication.Session session = (org.apache.lenya.cms.publication.Session) node
                    .getRepositorySession();
            Publication pub = session.getPublication(pubId);
            String docPath = path.substring((pubId + "/content/" + area).length());

            String uuid = docPath.substring(1, docPath.length() - "/en".length());
            String language = docPath.substring(docPath.length() - "en".length());

            doc = pub.getArea(area).getDocument(uuid, language);

            if (doc == null) {
                // this happens if the node was not a document node
                logger.info("No document found for node [" + sourceUri + "]");
            }

        //florent } catch (Exception e) {
        } catch (org.apache.cocoon.ResourceNotFoundException e) {
            
            throw new RuntimeException(e);
        }
        return doc;
    }*/

}
