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
package org.apache.lenya.cms.cluster.impl;

import java.util.HashMap;
import java.util.Map;

import org.apache.avalon.framework.parameters.Parameters;
import org.apache.avalon.framework.service.ServiceException;
import org.apache.avalon.framework.service.ServiceManager;
import org.apache.avalon.framework.service.Serviceable;
import org.apache.avalon.framework.thread.ThreadSafe;
import org.apache.cocoon.acting.AbstractAction;
import org.apache.cocoon.environment.ObjectModelHelper;
import org.apache.cocoon.environment.Redirector;
import org.apache.cocoon.environment.Request;
import org.apache.cocoon.environment.SourceResolver;
import org.apache.lenya.cms.cluster.ClusterManager;
import org.apache.lenya.cms.publication.Publication;
import org.apache.lenya.cms.publication.URLInformation;

/**
 * Cluster action.
 * Executes sitemap inside statements if Lenya instance
 * is running as slave in cluster mode. 
 */
public class ClusterAction extends AbstractAction
implements Serviceable, ThreadSafe
{
    private ClusterManager clusterManager;

    @Override
    public Map act(Redirector redirector, SourceResolver resolver,
            Map objectModel, String source, Parameters parameters)
    throws Exception
    {
        Request request = ObjectModelHelper.getRequest(objectModel);
        if (clusterManager.isSlave() &&
                Publication.AUTHORING_AREA.equals(getArea(request)))
        {
            if (getLogger().isDebugEnabled())
                getLogger().debug("Lenya instance is running as slave in " +
                        "cluster mode. Access to authoring denied.");
            // Deny access to authoring. Returning non-null value.
            return new HashMap();
        } else {
            // Allow access to authoring.
            return null;
        }
    }

    private String getArea(Request request) {
        String context = request.getContextPath();
        String requestURI = request.getRequestURI();
        String webappUrl = requestURI.substring(context.length());
        URLInformation lenyaURL = new URLInformation(webappUrl);
        String area = lenyaURL.getArea();
        return area;
    }

    @Override
    public void service(ServiceManager manager) throws ServiceException {
        clusterManager = (ClusterManager) manager.lookup(ClusterManager.ROLE);
    }

}
