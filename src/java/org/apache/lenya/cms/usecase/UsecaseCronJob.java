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
package org.apache.lenya.cms.usecase;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

import org.apache.avalon.framework.CascadingRuntimeException;
import org.apache.avalon.framework.parameters.Parameters;
import org.apache.cocoon.components.cron.ConfigurableCronJob;
import org.apache.cocoon.components.cron.ServiceableCronJob;
import org.apache.excalibur.source.Source;
import org.apache.excalibur.source.SourceResolver;

/**
 * Job to schedule usecase execution.
 * 
 * @version $Id:$
 */
public class UsecaseCronJob extends ServiceableCronJob implements ConfigurableCronJob {

    /**
     * @see org.apache.cocoon.components.cron.CronJob#execute(java.lang.String)
     */
    public void execute(String jobname) {
        if (getLogger().isDebugEnabled()) {
            getLogger().debug("UsecaseCronJob: " + jobname);
            getLogger().debug("URI: " + getRequestURI());
        }

        SourceResolver resolver = null;
        Source source = null;
        try {
            resolver = (SourceResolver) this.manager.lookup(SourceResolver.ROLE);
            final String url = "cocoon:/" + getRequestURI();
            source = resolver.resolveURI(url, url, this.requestParameters);

            InputStream is = source.getInputStream();
            InputStreamReader reader = new InputStreamReader(is);
            StringBuffer sb = new StringBuffer();
            char[] b = new char[8192];
            int n;
            while ((n = reader.read(b)) > 0) {
                sb.append(b, 0, n);
            }
            reader.close();
        } catch (Exception e) {
            throw new CascadingRuntimeException("UsecaseCronJob: " + jobname
                    + ", raised an exception: ", e);
        } finally {
            if (resolver != null) {
                resolver.release(source);
                this.manager.release(resolver);
                resolver = null;
                source = null;
            }
        }
    }

    private String requestUri;
    private Map requestParameters;

    /**
     * @return The request URI.
     */
    protected String getRequestURI() {
        return requestUri;
    }
    
    protected static final String SOURCE_URL = "sourceUrl";
    protected static final String USECASE_NAME = "usecaseName";

    /**
     * @see org.apache.cocoon.components.cron.ConfigurableCronJob#setup(org.apache.avalon.framework.parameters.Parameters,
     *      java.util.Map)
     */
    public void setup(Parameters params, Map objects) {

        try {
            final String sourceUri = (String) objects.get(SOURCE_URL);
            if (sourceUri == null) {
                throw new RuntimeException("Source URI not set!");
            }
            
            final String usecaseName = (String) objects.get(USECASE_NAME);
            if (usecaseName == null) {
                throw new RuntimeException("Usecase name not set!");
            }

            this.requestParameters = new HashMap(Parameters.toProperties(params));
            this.requestParameters.put("lenya.usecase", usecaseName);
            this.requestParameters.put("lenya.schedule", "true");

//            String requestUri = sourceUri + "?lenya.usecase=" + usecaseName + "&lenya.schedule=true";

            String[] names = params.getNames();
            for (int i = 0; i < names.length; i++) {
                requestUri += "&" + names[i] + "=" + params.getParameter(names[i]);
            }
            this.requestUri = sourceUri;
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}