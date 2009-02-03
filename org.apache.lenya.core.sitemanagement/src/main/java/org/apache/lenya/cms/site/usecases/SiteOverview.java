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
package org.apache.lenya.cms.site.usecases;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

import org.apache.avalon.framework.service.ServiceException;
import org.apache.avalon.framework.service.ServiceSelector;
import org.apache.lenya.cms.publication.Document;
import org.apache.lenya.cms.publication.DocumentException;
import org.apache.lenya.cms.publication.DocumentFactory;
import org.apache.lenya.cms.publication.Publication;
import org.apache.lenya.cms.publication.PublicationException;
import org.apache.lenya.cms.publication.PublicationUtil;
import org.apache.lenya.cms.publication.ResourceType;
import org.apache.lenya.cms.repository.Node;
import org.apache.lenya.cms.repository.RepositoryException;
import org.apache.lenya.cms.site.SiteException;
import org.apache.lenya.cms.site.SiteManager;
import org.apache.lenya.cms.usecase.AbstractUsecase;
import org.apache.lenya.cms.workflow.WorkflowUtil;
import org.apache.lenya.workflow.Version;
import org.apache.lenya.workflow.Workflow;
import org.apache.lenya.workflow.WorkflowException;
import org.apache.lenya.workflow.Workflowable;

/**
 * Overview over all documents.
 */
public class SiteOverview extends AbstractUsecase {

    protected static final String ALL_DOCUMENTS = "allDocuments";
    protected static final String DOCUMENTS = "documents";
    protected static final String FILTER_WORKFLOW_STATE_VALUES = "filterWorkflowStateValues";
    protected static final String FILTER_RESOURCE_TYPE_VALUES = "filterResourceTypeValues";

    protected static final String KEY_PATH = "keyPath";
    protected static final String KEY_RESOURCE_TYPE = "keyResourceType";
    protected static final String KEY_WORKFLOW_STATE = "keyWorkflowState";
    protected static final String KEY_LANGUAGE = "keyLanguage";
    protected static final String KEY_LAST_MODIFIED = "keyLastModified";
    protected static final String KEY_URL = "keyUrl";
    protected static final String KEY_CHECKED_OUT = "keyCheckedOut";
    protected static final String KEY_CONTENT_LENGTH = "keyContentLength";
    protected static final String PARAMETER_KEYS = "keys";

    protected static final String[] KEYS = { KEY_PATH, KEY_LANGUAGE, KEY_RESOURCE_TYPE,
            KEY_WORKFLOW_STATE, KEY_LAST_MODIFIED, KEY_CONTENT_LENGTH, KEY_CHECKED_OUT };

    protected static final String FILTER_RESOURCE_TYPE = "filterResourceType";
    protected static final String FILTER_WORKFLOW_STATE = "filterWorkflowState";
    protected static final String FILTER_LANGUAGE = "filterLanguage";
    protected static final String PARAMETER_FILTERS = "filters";

    protected static final String[] FILTERS = { FILTER_LANGUAGE, FILTER_RESOURCE_TYPE,
            FILTER_WORKFLOW_STATE };

    protected static final String VALUE_ALL = "- all -";

    protected static final String SORT = "sort";

    protected static final String ORDER = "order";

    protected static final String DESC = "desc";
    protected static final String ASC = "asc";

    protected void prepareView() throws Exception {
        super.prepareView();
        setDefaultParameter(SORT, KEY_PATH);
        setDefaultParameter(ORDER, ASC);
        
        try {
            Document[] documents = getDocuments();
            List entries = new ArrayList();
            for (int i = 0; i < documents.length; i++) {
                entries.add(createEntry(documents[i]));
            }

            prepareFilters(entries);
            
            List filteredDocuments = filter(entries);
            sort(filteredDocuments);
            setParameter(DOCUMENTS, filteredDocuments);

            setParameter(PARAMETER_KEYS, Arrays.asList(KEYS));

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    
    protected void setDefaultParameter(String name, String value) {
        if (getParameter(name) == null) {
            setParameter(name, value);
        }
    }

    protected Entry createEntry(Document doc) throws DocumentException, WorkflowException,
            RepositoryException {
        Entry entry = new Entry();
        if (doc.hasLink()) {
            entry.setValue(KEY_PATH, doc.getPath());
        } else {
            entry.setValue(KEY_PATH, "not in site structure");
        }
        entry.setValue(KEY_RESOURCE_TYPE, ResourceType.I18N_PREFIX
                + doc.getResourceType().getName());
        entry.setValue(KEY_LANGUAGE, doc.getLanguage());
        entry.setValue(KEY_URL, doc.getCanonicalWebappURL());
        entry.setValue(KEY_CONTENT_LENGTH, "" + (doc.getContentLength() / 1000));

        DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String lastModified = format.format(new Date(doc.getLastModified()));
        entry.setValue(KEY_LAST_MODIFIED, lastModified);

        if (WorkflowUtil.hasWorkflow(this.manager, getSession(), getLogger(), doc)) {
            Workflowable workflowable = WorkflowUtil.getWorkflowable(this.manager,
                    getSession(), getLogger(), doc);
            Version latestVersion = workflowable.getLatestVersion();
            String state;
            if (latestVersion != null) {
                state = latestVersion.getState();
            } else {
                Workflow workflow = WorkflowUtil.getWorkflowSchema(this.manager,
                        getSession(), getLogger(), doc);
                state = workflow.getInitialState();
            }
            entry.setValue(KEY_WORKFLOW_STATE, state);
        } else {
            entry.setValue(KEY_WORKFLOW_STATE, "");
        }

        Node node = doc.getRepositoryNode();
        if (node.isCheckedOut()) {
            entry.setValue(KEY_CHECKED_OUT, node.getCheckoutUserId());
        } else {
            entry.setValue(KEY_CHECKED_OUT, "");
        }
        return entry;
    }

    protected void prepareFilters(List entries) {
        for (int i = 0; i < FILTERS.length; i++) {
            SortedSet filterValues = new TreeSet();
            filterValues.add(VALUE_ALL);

            String key = "key" + FILTERS[i].substring("filter".length());

            for (Iterator docs = entries.iterator(); docs.hasNext();) {
                Entry entry = (Entry) docs.next();
                filterValues.add(entry.getValue(key));
            }
            setParameter(FILTERS[i] + "Values", filterValues);
            setDefaultParameter(FILTERS[i], VALUE_ALL);
        }
        setParameter(PARAMETER_FILTERS, Arrays.asList(FILTERS));
    }

    protected void sort(List documents) {
        String sort = getParameterAsString(SORT);
        String order = getParameterAsString(ORDER, ASC);
        if (sort != null) {
            Comparator comparator = new EntryComparator(sort, order);
            Collections.sort(documents, comparator);
        }
    }

    protected List filter(List entries) {
        List filteredDocuments = new ArrayList(entries);

        for (int i = 0; i < FILTERS.length; i++) {
            String key = "key" + FILTERS[i].substring("filter".length());
            String filterValue = getParameterAsString(FILTERS[i]);
            if (!filterValue.equals(VALUE_ALL)) {
                Entry[] allEntries = (Entry[]) filteredDocuments.toArray(new Entry[filteredDocuments
                        .size()]);
                for (int entryIndex = 0; entryIndex < allEntries.length; entryIndex++) {
                    if (!allEntries[entryIndex].getValue(key).equals(filterValue)) {
                        filteredDocuments.remove(allEntries[entryIndex]);
                    }
                }
            }
        }
        return filteredDocuments;
    }

    /**
     * @return The documents in the authoring area.
     * @throws PublicationException if an error occurs.
     * @throws SiteException if an error occurs.
     */
    protected Document[] getDocuments() throws PublicationException, SiteException {
        Publication publication = getPublication();
        DocumentFactory identityMap = getDocumentFactory();
        Document[] documents;

        ServiceSelector selector = null;
        SiteManager siteManager = null;
        try {
            selector = (ServiceSelector) this.manager.lookup(SiteManager.ROLE + "Selector");
            siteManager = (SiteManager) selector.select(publication.getSiteManagerHint());
            documents = siteManager.getDocuments(identityMap, publication,
                    Publication.AUTHORING_AREA);
        } catch (ServiceException e) {
            throw new RuntimeException(e);
        } finally {
            if (selector != null) {
                if (siteManager != null) {
                    selector.release(siteManager);
                }
                this.manager.release(selector);
            }
        }

        return documents;
    }

    /**
     * @return The publication.
     * @throws PublicationException if an error occurs.
     */
    protected Publication getPublication() throws PublicationException {
        return PublicationUtil.getPublicationFromUrl(this.manager, getDocumentFactory(),
                getSourceURL());
    }

    /**
     * Comparator for entries.
     */
    public static class EntryComparator implements Comparator {

        private String key;
        private String order;

        /**
         * @param key The key to compare.
         * @param order The order string ({@link SiteOverview#ASC} or {@link SiteOverview#DESC}).
         */
        public EntryComparator(String key, String order) {
            this.key = key;
            this.order = order;
        }

        /**
         * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
         */
        public int compare(Object arg0, Object arg1) {
            Entry e1 = (Entry) arg0;
            Entry e2 = (Entry) arg1;

            String value1 = e1.getValue(this.key);
            String value2 = e2.getValue(this.key);
            if (this.order.equals(DESC))
                return value2.compareTo(value1);
            else
                return value1.compareTo(value2);
        }

    }

    /**
     * Stores document-related information.
     */
    public static class Entry {

        private Map values = new HashMap();

        /**
         * Ctor.
         */
        public Entry() {
        }

        /**
         * @param key The key.
         * @param value The value.
         */
        public void setValue(String key, String value) {
            this.values.put(key, value);
        }

        /**
         * @param key The key.
         * @return The value.
         */
        public String getValue(String key) {
            return (String) this.values.get(key);
        }

    }

}
