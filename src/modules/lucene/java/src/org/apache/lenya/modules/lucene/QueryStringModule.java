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
package org.apache.lenya.modules.lucene;

import java.text.CharacterIterator;
import java.text.StringCharacterIterator;
import java.util.Map;

import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.avalon.framework.service.ServiceException;
import org.apache.avalon.framework.service.ServiceManager;
import org.apache.avalon.framework.service.Serviceable;
import org.apache.cocoon.components.modules.input.AbstractInputModule;
import org.apache.cocoon.environment.ObjectModelHelper;
import org.apache.cocoon.environment.Request;
import org.apache.lenya.cms.metadata.Element;
import org.apache.lenya.cms.metadata.ElementSet;
import org.apache.lenya.cms.metadata.MetaDataRegistry;

/**
 * Creates a Lucene query string from a <em>queryString</em> request parameter
 * which can be passed to the search generator.
 */
public class QueryStringModule extends AbstractInputModule implements Serviceable {

    protected static final String PARAM_QUERY_STRING = "queryString";
    protected static final String[] DEFAULT_FIELDS = { "body" };
    
    protected static final char[] ESCAPED_CHARACTERS = { '+', '-', '&', '|', '!', '(', ')', '{',
        '}', '[', ']', '^', '"', '~', '*', '?', ':', '\\' };

    private ServiceManager manager;

    public Object getAttribute(String name, Configuration modeConf, Map objectModel)
            throws ConfigurationException {

        if (name.equals("queryString")) {
            return getQueryString(objectModel);
        } else {
            throw new IllegalArgumentException("The attribute [" + name + "] is not supported.");
        }

    }

    protected String getQueryString(Map objectModel) {
        Request request = ObjectModelHelper.getRequest(objectModel);
        String searchTerm = request.getParameter(PARAM_QUERY_STRING);
        
        if (searchTerm == null || searchTerm.trim().equals("")) {
            return "";
        }

        StringBuffer queryString = new StringBuffer();

        for (int i = 0; i < DEFAULT_FIELDS.length; i++) {
            appendTerm(queryString, DEFAULT_FIELDS[i], searchTerm);
        }

        MetaDataRegistry registry = null;
        MetaDataFieldRegistry fieldRegistry = null;
        try {
            registry = (MetaDataRegistry) this.manager.lookup(MetaDataRegistry.ROLE);
            fieldRegistry = (MetaDataFieldRegistry) this.manager.lookup(MetaDataFieldRegistry.ROLE);
            String[] namespaces = registry.getNamespaceUris();
            for (int n = 0; n < namespaces.length; n++) {
                ElementSet elementSet = registry.getElementSet(namespaces[n]);
                Element[] elements = elementSet.getElements();
                for (int e = 0; e < elements.length; e++) {
                    if (elements[e].isSearchable()) {
                        String field = fieldRegistry.getFieldName(namespaces[n], elements[e].getName());
                        appendTerm(queryString, field, searchTerm);
                    }
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            if (registry != null) {
                this.manager.release(registry);
            }
            if (fieldRegistry != null) {
                this.manager.release(fieldRegistry);
            }
        }
        return queryString.toString();
    }

    protected boolean shallEscape(char c) {
        for (int i = 0; i < ESCAPED_CHARACTERS.length; i++) {
            if (ESCAPED_CHARACTERS[i] == c) {
                return true;
            }
        }
        return false;
    }

    protected String escape(final String prefix) {
        StringBuilder builder = new StringBuilder();
        StringCharacterIterator i = new StringCharacterIterator(prefix);
        char c = i.current();
        while (c != CharacterIterator.DONE) {
            if (shallEscape(c)) {
                builder.append('\\');
            }
            builder.append(c);
            c = i.next();
        }
        return builder.toString();
    }
    
    protected void appendTerm(StringBuffer queryString, String field, String searchTerm) {
        if (queryString.length() > 0) {
            queryString.append(" OR ");
        }
        String term = getTerm(field, searchTerm);
        queryString.append(term);
    }

    protected String getTerm(String field, String searchTerm) {
        return escape(field) + ":" + searchTerm;
    }

    public void service(ServiceManager manager) throws ServiceException {
        this.manager = manager;
    }

}
