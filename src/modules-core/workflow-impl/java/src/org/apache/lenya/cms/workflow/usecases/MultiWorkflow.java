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
package org.apache.lenya.cms.workflow.usecases;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.lenya.cms.publication.Document;
import org.apache.lenya.cms.site.SiteException;
import org.apache.lenya.cms.site.SiteNode;
import org.apache.lenya.cms.usecase.DocumentUsecase;
import org.apache.lenya.cms.usecase.UsecaseInvoker;
import org.apache.lenya.cms.usecase.UsecaseMessage;
import org.apache.lenya.util.Assert;

/**
 * Manage the workflow of multiple documents.
 */
public class MultiWorkflow extends DocumentUsecase {

    protected void initParameters() {
        super.initParameters();

        try {
            List preOrder = getPreOrder(getSourceDocument().getLink().getNode());
            List wrappers = new ArrayList();
            SortedSet states = new TreeSet();
            for (Iterator i = preOrder.iterator(); i.hasNext();) {
                Document doc = (Document) i.next();
                WorkflowableWrapper wrapper = new WorkflowableWrapper(this, this.manager,
                        getDocumentFactory().getSession(), doc, getLogger());
                wrappers.add(wrapper);
                states.addAll(Arrays.asList(wrapper.getStates()));
            }
            setParameter("documents", wrappers);
            setParameter("states", states);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    protected List getPreOrder(SiteNode node) throws SiteException {
        List preOrder = new ArrayList();
        String[] langs = node.getLanguages();
        Arrays.sort(langs);
        for (int i = 0; i < langs.length; i++) {
            preOrder.add(node.getLink(langs[i]).getDocument());
        }
        SiteNode[] children = node.getChildren();
        for (int i = 0; i < children.length; i++) {
            preOrder.addAll(getPreOrder(children[i]));
        }
        return preOrder;
    }

    protected void doExecute() throws Exception {
        super.doExecute();
        
        String usecase = getParameterAsString("usecaseName");
        Assert.notNull("usecase", usecase);
        String url = getParameterAsString("url");
        Assert.notNull("url", url);
        
        UsecaseInvoker invoker = null;
        try {
            invoker = (UsecaseInvoker) this.manager.lookup(UsecaseInvoker.ROLE);
            invoker.invoke(url, usecase, new HashMap());
            
            if (invoker.getResult() != UsecaseInvoker.SUCCESS) {
                List messages = invoker.getErrorMessages();
                for (Iterator i = messages.iterator(); i.hasNext(); ) {
                    UsecaseMessage message = (UsecaseMessage) i.next();
                    addErrorMessage(message.getMessage(), message.getParameters());
                }
            }
        }
        finally {
            if (invoker == null) {
                this.manager.release(invoker);
            }
        }
    }
    
    private Map usecase2event = new HashMap();

    public void configure(Configuration config) throws ConfigurationException {
        super.configure(config);
        
        Configuration[] usecaseConfigs = config.getChildren("usecase");
        for (int i = 0; i < usecaseConfigs.length; i++) {
            String usecase = usecaseConfigs[i].getAttribute("name");
            String event = usecaseConfigs[i].getAttribute("event");
            this.usecase2event.put(usecase, event);
        }
    }
    
    /**
     * @param event An event.
     * @return All usecases associated with this event.
     */
    public String[] getUsecases(String event) {
        SortedSet usecases = new TreeSet();
        for (Iterator i = this.usecase2event.keySet().iterator(); i.hasNext(); ) {
            String usecase = (String) i.next();
            if (this.usecase2event.get(usecase).equals(event)) {
                usecases.add(usecase);
            }
        }
        return (String[]) usecases.toArray(new String[usecases.size()]);
    }

}
