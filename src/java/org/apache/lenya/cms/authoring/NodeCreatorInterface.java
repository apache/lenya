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

/**
 * Interface for creation of nodes in the document hierarchy
 * @version $Id$
 * @deprecated This interface and it's implementations will be removed during the 1.4 development
 *             cycle
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
     * @param document The document to create.
     * @param parameters additional parameters that can be used when creating the document
     * 
     * @exception Exception if an error occurs
     */
    void create(String initialContentsURI, org.apache.lenya.cms.repo.Document document,
            Map parameters) throws Exception;

}