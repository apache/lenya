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
import java.util.Arrays;

import org.apache.avalon.framework.container.ContainerUtil;
import org.apache.avalon.framework.context.DefaultContext;
import org.apache.cocoon.Constants;
import org.apache.cocoon.core.container.ContainerTestCase;
import org.apache.cocoon.environment.Context;
import org.apache.cocoon.environment.Request;
import org.apache.cocoon.environment.commandline.CommandLineContext;
import org.apache.cocoon.environment.commandline.CommandLineRequest;
import org.apache.cocoon.environment.mock.MockEnvironment;
import org.apache.cocoon.util.IOUtils;
import org.apache.commons.lang.SystemUtils;
import org.apache.excalibur.source.SourceResolver;

/**
 * Base class for Lenya tests which need the context information.
 */
public class LenyaTestCase extends ContainerTestCase {

    protected DefaultContext context;

    protected void addContext(DefaultContext context) {
        super.addContext(context);

        this.context = context;

        String tempPath = System.getProperty("tempDir");
        String contextRoot = System.getProperty("contextRoot");
        getLogger().info("Adding context root entry [" + contextRoot + "]");

        File contextRootDir = new File(contextRoot);
        context.put("context-root", contextRootDir);

        Context envContext = new CommandLineContext(contextRoot);
        ContainerUtil.enableLogging(envContext, getLogger());
        context.put(Constants.CONTEXT_ENVIRONMENT_CONTEXT, envContext);

        File tempDir = new File(tempPath);

        File workDir = new File(tempDir, "work");
        context.put("work-directory", workDir);

        File cacheDir = new File(tempDir, "cache");
        context.put(Constants.CONTEXT_CACHE_DIR, cacheDir);

        File uploadDir = new File(tempDir, "upload");
        context.put(Constants.CONTEXT_UPLOAD_DIR, uploadDir);

        context.put(Constants.CONTEXT_CLASS_LOADER, LenyaTestCase.class.getClassLoader());
        context.put(Constants.CONTEXT_CLASSPATH, getClassPath(contextRoot));
        // context.put(Constants.CONTEXT_CONFIG_URL, conf.toURL());
        context.put(Constants.CONTEXT_DEFAULT_ENCODING, "ISO-8859-1");

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
        String pathInfo = getWebappUrl();

        Request request = new CommandLineRequest(env, contextRoot, "", pathInfo);
        context.put("object-model.request", request);
    }
    
    protected String getWebappUrl() {
        return "";
    }

    /**
     * This builds the important ClassPath used by this class. It does so in a neutral way. It
     * iterates in alphabetical order through every file in the lib directory and adds it to the
     * classpath.
     * 
     * Also, we add the files to the ClassLoader for the Cocoon system. In order to protect
     * ourselves from skitzofrantic classloaders, we need to work with a known one.
     * 
     * @param context The context path
     * @return a <code>String</code> value
     */
    protected String getClassPath(final String context) {
        StringBuffer buildClassPath = new StringBuffer();

        String classDir = context + "/WEB-INF/classes";
        buildClassPath.append(classDir);

        File root = new File(context + "/WEB-INF/lib");
        if (root.isDirectory()) {
            File[] libraries = root.listFiles();
            Arrays.sort(libraries);
            for (int i = 0; i < libraries.length; i++) {
                if (libraries[i].getAbsolutePath().endsWith(".jar")) {
                    buildClassPath.append(File.pathSeparatorChar)
                            .append(IOUtils.getFullFilename(libraries[i]));
                }
            }
        }

        buildClassPath.append(File.pathSeparatorChar).append(SystemUtils.JAVA_CLASS_PATH);

        // Extra class path is necessary for non-classloader-aware java compilers to compile XSPs
        // buildClassPath.append(File.pathSeparatorChar)
        // .append(getExtraClassPath(context));

        getLogger().info("Context classpath: " + buildClassPath);
        return buildClassPath.toString();
    }
}
