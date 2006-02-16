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

import org.apache.avalon.framework.container.ContainerUtil;
import org.apache.avalon.framework.context.DefaultContext;
import org.apache.cocoon.core.container.ContainerTestCase;
import org.apache.cocoon.environment.Context;
import org.apache.cocoon.environment.commandline.CommandLineContext;

/**
 * Base class for Lenya tests which need the context information.
 */
public class LenyaTestCase extends ContainerTestCase {
    
    protected void addContext(DefaultContext context) {
        super.addContext(context);
        String contextRoot = System.getProperty("contextRoot");
        getLogger().info("Adding context root entry [" + contextRoot + "]");
        context.put("context-root", new File(contextRoot));
        
        Context envContext = new CommandLineContext(contextRoot);
        ContainerUtil.enableLogging(envContext, getLogger());
        context.put("environment-context", envContext);
    }

}
