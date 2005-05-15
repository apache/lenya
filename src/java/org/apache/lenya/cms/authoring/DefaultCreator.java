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
import org.apache.avalon.framework.container.ContainerUtil;
import org.apache.avalon.framework.logger.AbstractLogEnabled;
import org.apache.avalon.framework.logger.Logger;
import org.apache.avalon.framework.service.ServiceManager;
import org.apache.lenya.cms.cocoon.source.SourceUtil;
import org.apache.lenya.cms.publication.DocumentBuildException;
import org.apache.lenya.cms.publication.DocumentException;
import org.w3c.dom.Document;

/**
 * Base creator for creating documents
 * @version $Id$
 */
public abstract class DefaultCreator extends AbstractLogEnabled implements NodeCreatorInterface  {

    private String sampleResourceName = null;
    private ServiceManager manager;

    /**
     * @see org.apache.lenya.cms.authoring.NodeCreatorInterface#init(Configuration, ServiceManager, Logger)
     */
    public void init(Configuration conf, ServiceManager _manager, Logger _logger) {
        // parameter conf ignored: nothing to configure in current implementation
        this.manager = _manager;
        ContainerUtil.enableLogging(this, _logger);
    }

    /**
     * Generate a tree id by returning the child ID.
     * @param childId a <code>String</code> value
     * @param childType a <code>short</code> value
     * @return a <code>String</code> value
     * @exception Exception if an error occurs
     */
    public String generateTreeId(String childId, short childType)
        throws Exception {
        return childId;
    }

    /**
     * Return the child type by simply returning the child type.
     * @param childType a <code>short</code> value
     * @return a <code>short</code> value
     * @exception Exception if an error occurs
     */
    public short getChildType(short childType) throws Exception {
        return childType;
    }

    /**
     * Create Child Name for tree entry
     * @param childname a <code>String</code> value
     * @return a <code>String</code> for Child Name for tree entry
     * @exception Exception if an error occurs
     */
    public String getChildName(String childname) throws Exception {
        if (childname.length() != 0) {
            return childname;
        }
        return "abstract_default";
    }

    /**
     * @see NodeCreatorInterface#create(String, String, String, short, String, Map)
      */
    public void create(
        String initialContentsURI,
        String newURI,
        String childId,
        short childType,
        String childName,
        Map parameters)
        throws Exception {

        if (getLogger().isDebugEnabled())
            getLogger().debug("DefaultCreator::create() called with\n"
               + "\t initialContentsURI [" + initialContentsURI + "]\n"
               + "\t newURI [" + newURI + "]\n"
               + "\t childId [" + childId + "]\n"
               + "\t childType [" + childType + "]\n"
               + "\t childName [" + childName + "]\n"
               + "\t non-empty parameters [" + (parameters != null) + "]\n"
               );

        // 
        String id = generateTreeId(childId, childType);

        // Read initial contents as DOM
        if (getLogger().isDebugEnabled())
            getLogger().debug("DefaultCreator::create(), ready to read initial contents from URI [" + initialContentsURI + "]");

        Document doc = null;
        try {
           doc = SourceUtil.readDOM(initialContentsURI, manager);
        }
        catch (Exception e) {
	    throw new DocumentException("could not read document at location [ " + initialContentsURI + "]", e);
        }

        if (getLogger().isDebugEnabled())
            getLogger().debug("transform sample file: ");

        // transform the xml if needed
        transformXML(doc, id, childType, childName, parameters);

        // write the document 
        try {
            SourceUtil.writeDOM(doc, newURI, manager);
        }
        catch (Exception e) {
            throw new DocumentBuildException("could not write document to URI [" + newURI + "], exception " + e.toString(), e);
        }
    }

    /**
     * Apply some transformation on the newly created child.
     * @param doc the xml document
     * @param childId the id of the child
     * @param childType the type of child
     * @param childName the name of the child
     * @param parameters additional parameters that can be used in the transformation
     * @throws Exception if the transformation fails
     */
    protected void transformXML(
        Document doc,
        String childId,
        short childType,
        String childName,
        Map parameters)
        throws Exception {
	    // do nothing
    }

    /**
     * @see org.apache.lenya.cms.authoring.NodeCreatorInterface#getNewDocumentURI(String, String, String, String)
     */
    public abstract String getNewDocumentURI(
        String contentBaseURI,
        String parentId,
        String newId,
        String language);

    /**
     * Create the language suffix for a file name given a language string
     * @param language the language
     * @return the suffix for the language dependant file name
     */
    protected String getLanguageSuffix(String language) {
        return (language != null) ? "_" + language : "";
    }
}
