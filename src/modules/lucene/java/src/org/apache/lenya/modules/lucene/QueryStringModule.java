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
import org.apache.lucene.index.Term;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.TermQuery;

/**
 * Creates a Lucene query string from a <em>queryString</em> request parameter
 * which can be passed to the search generator.
 */
public class QueryStringModule extends AbstractInputModule implements Serviceable {

    protected static final String PARAM_QUERY_STRING = "queryString";
    protected static final String[] DEFAULT_FIELDS = { "body" };
    
    protected static final char[] ESCAPED_CHARACTERS = { '+', '-', '&', '|', '!', '(', ')', '{',
        '}', '[', ']', '^', '"', '~', '*', '?', ':', '\\' };

    protected ServiceManager manager;

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
        
        if (isEmpty(searchTerm)) {
            return "";
        }
        
        if (searchTerm.indexOf(' ') > -1) {
            searchTerm = "(" + searchTerm + ")";
        }

        BooleanQuery query = getQuery(searchTerm);
        return query.toString();
    }

    protected boolean isEmpty(String searchTerm) {
        return searchTerm == null || searchTerm.trim().equals("");
    }

    protected BooleanQuery getQuery(String searchTerm) {
        BooleanQuery query = new BooleanQuery();

        for (int i = 0; i < DEFAULT_FIELDS.length; i++) {
            TermQuery termQuery = new TermQuery(getTerm(DEFAULT_FIELDS[i], searchTerm));
            query.add(termQuery, false, false);
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
                        TermQuery termQuery = new TermQuery(getTerm(field, searchTerm));
                        query.add(termQuery, false, false);
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
        return query;
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
        StringBuffer buffer = new StringBuffer();
        StringCharacterIterator i = new StringCharacterIterator(prefix);
        char c = i.current();
        while (c != CharacterIterator.DONE) {
            if (shallEscape(c)) {
                buffer.append('\\');
            }
            buffer.append(c);
            c = i.next();
        }
        return buffer.toString();
    }
    
    protected Term getTerm(String field, String value) {
        return new Term(escape(field), value);
    }

    public void service(ServiceManager manager) throws ServiceException {
        this.manager = manager;
    }

}
