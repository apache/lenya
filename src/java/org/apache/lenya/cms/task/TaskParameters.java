/*
 * Copyright 1999-2004 The Apache Software Foundation
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 *  
 */

/* $Id$ */

package org.apache.lenya.cms.task;

import java.util.Map;

import org.apache.avalon.framework.service.ServiceManager;
import org.apache.lenya.cms.publication.Publication;
import org.apache.lenya.cms.publication.PublicationException;
import org.apache.lenya.cms.publication.PublicationUtil;

/**
 * Task Parameters
 */
public class TaskParameters extends ParameterWrapper {
    /**
     * <code>REQUIRED_KEYS</code> Required task parameters
     */
    public static final String[] REQUIRED_KEYS = { Task.PARAMETER_SERVLET_CONTEXT,
            Task.PARAMETER_SERVER_URI, Task.PARAMETER_SERVER_PORT, Task.PARAMETER_CONTEXT_PREFIX,
            Task.PARAMETER_PUBLICATION_ID };
    /**
     * <code>PREFIX</code> The task prefix
     */
    public static final String PREFIX = "task";

    private ServiceManager manager;

    /**
     * Ctor.
     * @param prefixedParameters The prefixed parameters .
     * @param manager The service manager.
     */
    public TaskParameters(Map prefixedParameters, ServiceManager manager) {
        super(prefixedParameters);
    }

    /**
     * @see org.apache.lenya.cms.task.ParameterWrapper#getPrefix()
     */
    public String getPrefix() {
        return PREFIX;
    }

    /**
     * @see org.apache.lenya.cms.task.ParameterWrapper#getRequiredKeys()
     */
    protected String[] getRequiredKeys() {
        return REQUIRED_KEYS;
    }

    /**
     * Returns the publication.
     * @return A publication.
     * @throws ExecutionException when something went wrong.
     */
    public Publication getPublication() throws ExecutionException {
        Publication publication;
        try {
            publication = PublicationUtil.getPublication(this.manager, get(Task.PARAMETER_PUBLICATION_ID));
        } catch (PublicationException e) {
            throw new ExecutionException(e);
        }
        return publication;
    }

    /**
     * Sets the publication.
     * @param publication A publication.
     */
    public void setPublication(Publication publication) {
        put(Task.PARAMETER_PUBLICATION_ID, publication.getId());
        put(Task.PARAMETER_SERVLET_CONTEXT, publication.getServletContext().getAbsolutePath());
    }

    /**
     * Sets the servlet context path.
     * @param servletContextPath A string.
     */
    public void setServletContextPath(String servletContextPath) {
        put(Task.PARAMETER_SERVLET_CONTEXT, servletContextPath);
    }

}