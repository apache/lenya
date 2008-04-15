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
import org.apache.lenya.cms.publication.Area;
import org.apache.lenya.cms.publication.Document;
import org.apache.lenya.cms.publication.DocumentBuildException;
import org.apache.lenya.cms.publication.DocumentException;
import org.apache.lenya.cms.publication.DocumentFactory;
import org.apache.lenya.cms.publication.Publication;
import org.apache.lenya.cms.publication.PublicationException;
import org.apache.lenya.cms.publication.URLInformation;
import org.apache.lenya.cms.site.SiteException;
import org.apache.lenya.cms.site.SiteNode;
import org.apache.lenya.cms.site.SiteStructure;
import org.apache.lenya.cms.usecase.AbstractUsecase;

/**
 * Manage the workflow of multiple documents.
 */
public class MultiWorkflow extends AbstractUsecase {

    protected void prepareView() {
        try {
            List preOrder = getNodes();
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
            setParameter("usecases", this.usecases);
            setParameter("variables", this.variables);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    protected List getNodes() throws DocumentBuildException, DocumentException, PublicationException {
        List preOrder;
        String sourceUrl = getSourceURL();
        DocumentFactory factory = getDocumentFactory();
        if (getDocumentFactory().isDocument(sourceUrl)) {
            Document doc = factory.getFromURL(sourceUrl);
            preOrder = getPreOrder(doc.getLink().getNode());
        } else {
            preOrder = new ArrayList();
            URLInformation info = new URLInformation(getSourceURL());
            Publication pub = factory.getPublication(info.getPublicationId());
            Area area = pub.getArea(info.getArea());
            SiteStructure site = area.getSite();
            SiteNode[] topLevelNodes = site.getTopLevelNodes();
            for (int i = 0; i < topLevelNodes.length; i++) {
                preOrder.addAll(getPreOrder(topLevelNodes[i]));
            }
        }
        return preOrder;
    }

    protected List getPreOrder(SiteNode node) throws SiteException {
        List preOrder = new ArrayList();
        if (node.getUuid() != null) {
            String[] langs = node.getLanguages();
            Arrays.sort(langs);
            for (int i = 0; i < langs.length; i++) {
                preOrder.add(node.getLink(langs[i]).getDocument());
            }
        }
        SiteNode[] children = node.getChildren();
        for (int i = 0; i < children.length; i++) {
            preOrder.addAll(getPreOrder(children[i]));
        }
        return preOrder;
    }

    private List usecases = new ArrayList();
    private Map usecase2event = new HashMap();
    private List variables = new ArrayList();

    public void configure(Configuration config) throws ConfigurationException {
        super.configure(config);

        Configuration[] usecaseConfigs = config.getChildren("usecase");
        for (int i = 0; i < usecaseConfigs.length; i++) {
            String usecase = usecaseConfigs[i].getAttribute("name");
            String event = usecaseConfigs[i].getAttribute("event");
            this.usecases.add(usecase);
            this.usecase2event.put(usecase, event);
        }
        Configuration[] varConfigs = config.getChildren("variable");
        for (int i = 0; i < varConfigs.length; i++) {
            this.variables.add(varConfigs[i].getAttribute("name"));
        }
    }

    /**
     * @param event An event.
     * @return All usecases associated with this event.
     */
    public String[] getUsecases(String event) {
        SortedSet usecases = new TreeSet();
        for (Iterator i = this.usecase2event.keySet().iterator(); i.hasNext();) {
            String usecase = (String) i.next();
            if (this.usecase2event.get(usecase).equals(event)) {
                usecases.add(usecase);
            }
        }
        return (String[]) usecases.toArray(new String[usecases.size()]);
    }

    public String getEvent(String usecase) {
        return (String) this.usecase2event.get(usecase);
    }

}
