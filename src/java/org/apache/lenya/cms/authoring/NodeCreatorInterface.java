/*
 * Copyright  1999-2005 The Apache Software Foundation
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

package org.apache.lenya.cms.authoring;

import java.util.Map;

import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.logger.Logger;
import org.apache.avalon.framework.service.ServiceManager;
import org.w3c.dom.Document;

/**
 * Interface for creation of nodes in the document hierarchy
 * @version $Id$
 */
public interface NodeCreatorInterface {

    /**
     * Configures the Creator, based on a configuration file.
     * 
     * @param doctypeConf A configuration.
     * @param manager the service manager
     * @param logger A logger
     */
    void init(Configuration doctypeConf, ServiceManager manager, Logger logger);
    /**
     * Describe <code>getChildName</code> method here.
     *
     * @param childname a <code>String</code> value
     * @return a <code>String</code> value
     * @exception Exception if an error occurs
     */
    String getChildName(String childname) throws Exception;

    /**
     * Create a physical representation for a new document.
     *
     * @param initialContentsURI the URI where initial content for this document can be found.
     * @param newURI the URI under which the new node is to be created. Can be retrieved via getNewDocumentURI()
     * @param childId the document id of the new document
     * @param childName the name of the new document.
     * @param parameters additional parameters that can be used when creating the document
     * 
     * @see #getNewDocumentURI(String,String,String,String)
     * @exception Exception if an error occurs
     */
    void create(
        String initialContentsURI,
        String newURI,
        String childId,
        String childName,
        Map parameters)
        throws Exception;

    /**
     * Get the URI of a new document
     * @param contentBaseURI the base URI of where contents are found
     * @param parentId the id of the parent, if known
     * @param newId the id of the new document
     * @param language for which the document is created
     * @return the new URI
     */
    String getNewDocumentURI(
        String contentBaseURI,
        String parentId,
        String newId,
        String language);

    /**
     * Apply some transformation on the newly created document.
     * @param doc the xml document
     * @param childId the id of the child
     * @param childName the name of the child
     * @param parameters additional parameters that can be used in the transformation
     * @throws Exception if the transformation fails
     */
    void transformXML(
        Document doc,
        String childId,
        String childName,
        Map parameters)
        throws Exception;


}
