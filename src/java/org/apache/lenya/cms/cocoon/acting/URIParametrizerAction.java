/*
 * Copyright  1999-2004 The Apache Software Foundation
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

/* $Id: URIParametrizerAction.java,v 1.22 2004/03/01 16:18:21 gregor Exp $  */

package org.apache.lenya.cms.cocoon.acting;

import java.util.Map;

import org.apache.avalon.framework.parameters.Parameters;
import org.apache.cocoon.acting.ConfigurableServiceableAction;
import org.apache.cocoon.environment.ObjectModelHelper;
import org.apache.cocoon.environment.Redirector;
import org.apache.cocoon.environment.Request;
import org.apache.cocoon.environment.SourceResolver;
import org.apache.lenya.cms.cocoon.uriparameterizer.URIParameterizer;

/**
 * Action to access the URI parameterizer.
 * The map returned by this action is the map returned by the
 * {@link org.apache.lenya.cms.cocoon.uriparameterizer.URIParameterizer}.
 * The <code>src</code> attribute is the URI to parameterize.
 */
public class URIParametrizerAction extends ConfigurableServiceableAction {

    /**
     * @see org.apache.cocoon.acting.Action#act(org.apache.cocoon.environment.Redirector, org.apache.cocoon.environment.SourceResolver, java.util.Map, java.lang.String, org.apache.avalon.framework.parameters.Parameters)
     */
    public Map act(
        Redirector redirector,
        SourceResolver resolver,
        Map objectModel,
        String src,
        Parameters parameters)
        throws Exception {

        Request request = ObjectModelHelper.getRequest(objectModel);
        String uri = request.getRequestURI();

        URIParameterizer parameterizer = null;
        Map map = null;
        try {
            parameterizer = (URIParameterizer) manager.lookup(URIParameterizer.ROLE);
            map = parameterizer.parameterize(uri, src, parameters);
        } finally {
            if (parameterizer != null) {
                manager.release(parameterizer);
            }
        }

        return map;

    }

}
