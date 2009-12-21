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

import java.util.Map;

import org.apache.cocoon.environment.ObjectModelHelper;
import org.apache.cocoon.environment.Request;
import org.apache.lenya.cms.metadata.MetaDataRegistry;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.TermQuery;

/**
 * Same as {@link QueryStringModule}, but searches only for images.
 * If the <em>queryString</em> request parameter is empty, all images are shown.
 */
public class ImageQueryStringModule extends QueryStringModule {
    
    protected static final String DOCUMENT_METADATA_NAMESPACE = "http://apache.org/lenya/metadata/document/1.0";

    protected String getQueryString(Map objectModel) {
        Request request = ObjectModelHelper.getRequest(objectModel);
        String searchTerm = request.getParameter(PARAM_QUERY_STRING);
        
        if (searchTerm != null && searchTerm.indexOf(' ') > -1) {
            searchTerm = "(" + searchTerm + ")";
        }

        BooleanQuery query = getQuery(searchTerm);
        return query.toString();
    }

    protected BooleanQuery getQuery(String searchTerm) {
        
        BooleanQuery query = new BooleanQuery();
        
        if (!isEmpty(searchTerm)) {
            query.add(super.getQuery(searchTerm), true, false);
        }
        
        MetaDataRegistry registry = null;
        MetaDataFieldRegistry fieldRegistry = null;
        try {
            registry = (MetaDataRegistry) this.manager.lookup(MetaDataRegistry.ROLE);
            fieldRegistry = (MetaDataFieldRegistry) this.manager.lookup(MetaDataFieldRegistry.ROLE);
            
            String typeField = fieldRegistry.getFieldName(DOCUMENT_METADATA_NAMESPACE, "resourceType");
            Term typeTerm = getTerm(typeField, "resource");
            query.add(new TermQuery(typeTerm), true, false);
            
            String mimeTypeField = fieldRegistry.getFieldName(DOCUMENT_METADATA_NAMESPACE, "mimeType");
            Term mimeTypeTerm = getTerm(mimeTypeField, "image*");
            query.add(new TermQuery(mimeTypeTerm), true, false);
            
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


}
