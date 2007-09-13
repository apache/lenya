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
package org.apache.lenya.cms.cocoon.components.modules.input;

import java.util.Collections;
import java.util.Iterator;
import java.util.Map;

import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.avalon.framework.service.ServiceException;
import org.apache.avalon.framework.service.ServiceManager;
import org.apache.avalon.framework.service.Serviceable;
import org.apache.cocoon.components.modules.input.AbstractInputModule;
import org.apache.cocoon.environment.ObjectModelHelper;
import org.apache.excalibur.source.SourceResolver;
import org.apache.lenya.cms.publication.Publication;
import org.apache.lenya.cms.publication.PublicationException;
import org.apache.lenya.cms.publication.PublicationFactory;

/**
 * Gets the content directory of a specific publication and specific area
 */
public class PublicationContentDirModule extends AbstractInputModule implements Serviceable {

    /* (non-Javadoc)
     * @see org.apache.cocoon.components.modules.input.InputModule#getAttribute(java.lang.String, org.apache.avalon.framework.configuration.Configuration, java.util.Map)
     */
    public Object getAttribute(String name, Configuration modeConf, Map objectModel)
        throws ConfigurationException {

    	if (getLogger().isDebugEnabled()) {
            getLogger().debug("Resolving directory path for [" + name + "]");
        }
    	String[] pubidAndArea = name.split(",");
        if (pubidAndArea.length < 2) {
            throw new ConfigurationException("Invalid number of parameters: " + pubidAndArea.length
                    + ". Expected pub, area.");
        }
           
        String id = pubidAndArea[0];
        String area = pubidAndArea[1];
    	String contextPath = ObjectModelHelper.getContext(objectModel).getRealPath("");
    	
    	try {
			Publication pub = PublicationFactory.getPublication(id, contextPath);
			return pub.getContentDirectory(area);
		} catch (PublicationException e) {
            throw new ConfigurationException("Obtaining value for [" + name + "] failed: ", e);
		}
    	
    }

    /* (non-Javadoc)
     * @see org.apache.cocoon.components.modules.input.InputModule#getAttributeNames(org.apache.avalon.framework.configuration.Configuration, java.util.Map)
     */
    public Iterator getAttributeNames(Configuration modeConf, Map objectModel)
        throws ConfigurationException {
        return Collections.EMPTY_SET.iterator();
    }

    private ServiceManager manager;
    private SourceResolver resolver;

    /* (non-Javadoc)
     * @see org.apache.avalon.framework.service.Serviceable#service(org.apache.avalon.framework.service.ServiceManager)
     */
    public void service(ServiceManager manager) throws ServiceException {
        this.manager = manager;
        this.resolver = (SourceResolver) manager.lookup(SourceResolver.ROLE);
    }

    /* (non-Javadoc)
     * @see org.apache.avalon.framework.activity.Disposable#dispose()
     */
    public void dispose() {
        super.dispose();
        this.manager.release(this.resolver);
        this.resolver = null;
        this.manager = null;
    }

    /* (non-Javadoc)
     * @see org.apache.cocoon.components.modules.input.InputModule#getAttributeValues(java.lang.String, org.apache.avalon.framework.configuration.Configuration, java.util.Map)
     */
    public Object[] getAttributeValues(String name, Configuration modeConf, Map objectModel)
        throws ConfigurationException {
        Object result = this.getAttribute(name, modeConf, objectModel);
        return (result == null ? null : new Object[] {result});
    }

}
