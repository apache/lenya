/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.lenya.cms.cocoon.components.context;

import java.util.Map;

import org.apache.avalon.framework.component.Component;
import org.apache.avalon.framework.context.Context;
import org.apache.avalon.framework.context.ContextException;
import org.apache.avalon.framework.context.Contextualizable;
import org.apache.avalon.framework.logger.AbstractLogEnabled;
import org.apache.cocoon.components.ContextHelper;
import org.apache.cocoon.environment.Request;
import org.apache.cocoon.environment.Response;

/**
 * Utility class for getting the context, request, response and
 * object model of the current request.
 */
public class ContextUtility extends AbstractLogEnabled implements
        Component, Contextualizable {
    /**
     * The component's role.
     */
    public static final String ROLE = ContextUtility.class.getName();

    protected Context context;

    
    /**
     * @see org.apache.avalon.framework.context.Contextualizable#contextualize(org.apache.avalon.framework.context.Context)
     */
    public void contextualize(Context context) throws ContextException {
        this.context = context;
    }

    /**
     * Get the context object of the current request.
     * @return The context object of the current request.
     */
    public Context getContext() {
        return context;
    }
    
    /**
     * Get the request object of the current request.
     * @return The request object of the current request.
     */
    public Request getRequest() {
        return ContextHelper.getRequest(context);
    }
    
    /**
     * Get the response object of the current request.
     * @return The response object of the current request.
     */
    public Response getResponse() {
        return ContextHelper.getResponse(context);
    }
    
    /**
     * Get the object model of the current request.
     * @return The object model of the current request.
     */
    public Map getObjectModel() {
        return ContextHelper.getObjectModel(context);
    }
}
