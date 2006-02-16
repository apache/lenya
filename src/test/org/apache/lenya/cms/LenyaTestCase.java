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
package org.apache.lenya.cms;

import java.io.File;
import java.net.URL;

import org.apache.avalon.framework.container.ContainerUtil;
import org.apache.avalon.framework.context.DefaultContext;
import org.apache.cocoon.core.container.ContainerTestCase;
import org.apache.cocoon.environment.Context;
import org.apache.cocoon.environment.Request;
import org.apache.cocoon.environment.commandline.CommandLineContext;
import org.apache.cocoon.environment.commandline.CommandLineRequest;
import org.apache.cocoon.environment.mock.MockEnvironment;
import org.apache.excalibur.source.SourceResolver;

/**
 * Base class for Lenya tests which need the context information.
 */
public class LenyaTestCase extends ContainerTestCase {

    private DefaultContext context;

    protected void addContext(DefaultContext context) {
        super.addContext(context);

        this.context = context;

        String contextRoot = System.getProperty("contextRoot");
        getLogger().info("Adding context root entry [" + contextRoot + "]");
        context.put("context-root", new File(contextRoot));

        Context envContext = new CommandLineContext(contextRoot);
        ContainerUtil.enableLogging(envContext, getLogger());
        context.put("environment-context", envContext);

    }

    protected void prepare() throws Exception {
        final String resourceName = LenyaTestCase.class.getName().replace('.', '/') + ".xtest";
        URL resource = getClass().getClassLoader().getResource(resourceName);

        if (resource != null) {
            getLogger().debug("Loading resource " + resourceName);
            prepare(resource.openStream());
        } else {
            getLogger().debug("Resource not found " + resourceName);
        }

        SourceResolver resolver = (SourceResolver) getManager().lookup(SourceResolver.ROLE);
        MockEnvironment env = new MockEnvironment(resolver);

        String contextRoot = System.getProperty("contextRoot");
        String pathInfo = "";

        Request request = new CommandLineRequest(env, "", contextRoot, pathInfo);
        context.put("object-model.request", request);
    }
}
