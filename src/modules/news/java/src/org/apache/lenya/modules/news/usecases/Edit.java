/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 *  contributor license agreements.  See the NOTICE file distributed with
 *  this work for additional information regarding copyright ownership.
 *  The ASF licenses this file to You under the Apache License, Version 2.0
 *  (the "License"); you may not use this file except in compliance with
 *  the License.  You may obtain a copy of the License at
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
package org.apache.lenya.modules.news.usecases;

import java.util.ArrayList;
import java.util.List;

import org.apache.lenya.cms.publication.Document;
import org.apache.lenya.cms.workflow.usecases.InvokeWorkflow;
import org.apache.lenya.modules.collection.CollectionWrapper;
import org.apache.lenya.modules.news.NewsWrapper;

/**
 * Edit the properties of a news document.
 */
public class Edit extends InvokeWorkflow {

    protected static final String INCLUDE_ITEM_NUMBER = "includeItems";
    protected static final String SHOW_ALL_LANGUAGES = "showAllLanguages";
    protected static final String NEWS_WRAPPER = "newsWrapper";
    protected static final String NUMBERS = "numbers";
    protected static final String TYPE = "type";
    protected static final String HREF = "href";
    
    protected void initParameters() {
        super.initParameters();
        Document doc = getSourceDocument();
        if (doc != null) {
            NewsWrapper news = new NewsWrapper(doc, getLogger());
            setParameter(NEWS_WRAPPER, news);
            
            setParameter(INCLUDE_ITEM_NUMBER, new Short(news.getIncludeItemNumber()));
            setParameter(SHOW_ALL_LANGUAGES, Boolean.valueOf(news.showAllLanguages()));
            setParameter(TYPE, news.getType());
            setParameter(HREF, news.getHref());
            
            List numbers = new ArrayList();
            for (int i = 1; i <= 10; i++) {
                numbers.add(new Integer(i));
            }
            setParameter(NUMBERS, numbers);
        }
    }

    protected void doExecute() throws Exception {
        super.doExecute();
        
        String numberString = getParameterAsString(INCLUDE_ITEM_NUMBER);
        short number = Short.parseShort(numberString);
        
        String allLanguagesString = getBooleanCheckboxParameter(SHOW_ALL_LANGUAGES);
        boolean allLanguages = Boolean.valueOf(allLanguagesString).booleanValue();
        
        // we must create a new wrapper, because a new (modifiable) session is used
        NewsWrapper news = new NewsWrapper(getSourceDocument(), getLogger());
        news.setIncludeItemNumber(number);
        news.setShowAllLanguages(allLanguages);
        String type = getParameterAsString(TYPE);
        news.setType(type);
        if (type.equals(CollectionWrapper.TYPE_LINK)) {
            news.setHref(getParameterAsString(HREF));
        }
        news.save();
    }

}
