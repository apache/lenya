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
package org.apache.lenya.cms.cocoon.selection;

import org.apache.cocoon.selection.Selector;
import org.apache.excalibur.source.Source;
import org.apache.excalibur.source.SourceNotFoundException;
import org.apache.excalibur.source.SourceResolver;
import org.apache.avalon.framework.activity.Disposable;
import org.apache.avalon.framework.logger.AbstractLogEnabled;
import org.apache.avalon.framework.parameters.Parameters;
import org.apache.avalon.framework.service.ServiceException;
import org.apache.avalon.framework.service.ServiceManager;
import org.apache.avalon.framework.service.Serviceable;
import org.apache.avalon.framework.thread.ThreadSafe;

import java.util.Map;

/**
 * <p>
 * Last Modified Source Selector.
 * </p>
 * 
 * <pre>
 *  &lt;map:selector name="last-mod" src="org.apache.lenya.cms.cocoon.selection.LastModSourceSelector"/&gt;
 *
 *   &lt;map:select type="last-mod"&gt;
 *      &lt;map:parameter name="compare-to" value="{sourceToCompareTo}"/&gt;
 *
 *      &lt;map:when test="cachedsource"&gt;
 *         &lt;!-- executes iff cachedsource last-modified  &gt; courceToCompareTo last-modified --&gt;
 *         &lt;map:read src="{cachedsource}" mime-type="text/xml; charset=utf-8"/&gt;
 *      &lt;/map:when&gt;
 *      &lt;map:otherwise&gt;
 *         &lt;map:read src="{sourceToCompareTo}" mime-type="text/xml; charset=utf-8"/&gt;
 *      &lt;/map:otherwise&gt;
 *   &lt;/map:select&gt;
 * </pre>
 */

public class LastModSourceSelector extends AbstractLogEnabled
                 implements ThreadSafe, Serviceable, Disposable, Selector {

    private ServiceManager manager;
    private SourceResolver resolver;
    private Source source = null;
    private Source compare = null;

    public void service(ServiceManager manager) throws ServiceException {
        this.manager = manager;
        this.resolver = (SourceResolver)manager.lookup(SourceResolver.ROLE);
    }

    public void dispose() {
        if (null != this.source) {
            resolver.release(this.source);
            this.source = null;
        }
        if (null != this.compare) {
            resolver.release(this.compare);
            this.compare = null;
        }
        this.manager.release(this.resolver);
        this.resolver = null;
        this.manager = null;
    }

    public boolean select(String expression, Map objectModel, Parameters parameters) {
        String sourceToCompare = parameters.getParameter("compare-to",null);
        String compareToSource = expression;
        long sourceModDate = 0;
        long compareModDate = 0;
        try {
            source = resolver.resolveURI(sourceToCompare);
            sourceModDate = source.getLastModified();
            compare = resolver.resolveURI(compareToSource);
            compareModDate = compare.getLastModified();
        } catch (SourceNotFoundException e) {
            return false;
        } catch (Exception e) {
            getLogger().warn("Exception resolving resource ", e);
            return false;
        }
        boolean isNewer = (compareModDate > sourceModDate);        

        return (sourceToCompare != null && isNewer);
        
    }
}