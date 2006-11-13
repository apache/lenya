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
import org.apache.lenya.cms.publication.DocumentFactory;
import org.apache.lenya.cms.publication.Publication;
import org.apache.lenya.cms.publication.PublicationException;
import org.apache.lenya.cms.publication.PublicationUtil;
import org.apache.lenya.cms.rc.RCML;
import org.apache.lenya.cms.rc.RCMLEntry;
import org.apache.lenya.cms.rc.RevisionController;
import org.apache.lenya.cms.site.SiteException;
import org.apache.lenya.cms.site.SiteManager;
import org.apache.lenya.cms.usecase.AbstractUsecase;
import org.apache.lenya.cms.usecase.UsecaseException;
import org.apache.lenya.cms.workflow.WorkflowUtil;
import org.apache.lenya.workflow.Version;
import org.apache.lenya.workflow.Workflow;
import org.apache.lenya.workflow.Workflowable;

/**
 * Overview over all documents.
 */
public class SiteOverview extends AbstractUsecase {

    protected static final String ALL_DOCUMENTS = "allDocuments";
    protected static final String DOCUMENTS = "documents";
    protected static final String FILTER_WORKFLOW_STATE_VALUES = "filterWorkflowStateValues";
    protected static final String FILTER_RESOURCE_TYPE_VALUES = "filterResourceTypeValues";

    protected static final String KEY_DOCUMENT_ID = "keyDocumentId";
    protected static final String KEY_RESOURCE_TYPE = "keyResourceType";
    protected static final String KEY_WORKFLOW_STATE = "keyWorkflowState";
    protected static final String KEY_LANGUAGE = "keyLanguage";
    protected static final String KEY_LAST_MODIFIED = "keyLastModified";
    protected static final String KEY_URL = "keyUrl";
    protected static final String KEY_CHECKED_OUT = "keyCheckedOut";
    protected static final String PARAMETER_KEYS = "keys";

    protected static final String[] KEYS = { KEY_DOCUMENT_ID, KEY_LANGUAGE, KEY_RESOURCE_TYPE,
            KEY_WORKFLOW_STATE, KEY_LAST_MODIFIED, KEY_CHECKED_OUT };

    protected static final String FILTER_RESOURCE_TYPE = "filterResourceType";
    protected static final String FILTER_WORKFLOW_STATE = "filterWorkflowState";
    protected static final String FILTER_LANGUAGE = "filterLanguage";
    protected static final String PARAMETER_FILTERS = "filters";

    protected static final String[] FILTERS = { FILTER_LANGUAGE, FILTER_RESOURCE_TYPE,
            FILTER_WORKFLOW_STATE };

    protected static final String VALUE_ALL = "- all -";

    protected static final String SORT = "sort";

    /**
     * @see org.apache.lenya.cms.usecase.AbstractUsecase#initParameters()
     */
    protected void initParameters() {
        super.initParameters();
        try {
            Document[] documents = getDocuments();

            List entries = new ArrayList();
            for (int i = 0; i < documents.length; i++) {

                Entry entry = new Entry();
                entry.setValue(KEY_DOCUMENT_ID, documents[i].getUUID());
                entry.setValue(KEY_RESOURCE_TYPE, documents[i].getResourceType().getName());
                entry.setValue(KEY_LANGUAGE, documents[i].getLanguage());
                entry.setValue(KEY_URL, documents[i].getCanonicalWebappURL());

                DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String lastModified = format.format(new Date(documents[i].getLastModified()));
                entry.setValue(KEY_LAST_MODIFIED, lastModified);

                if (WorkflowUtil.hasWorkflow(this.manager, getSession(), getLogger(), documents[i])) {
                    Workflowable workflowable = WorkflowUtil.getWorkflowable(this.manager,
                            getSession(),
                            getLogger(),
                            documents[i]);
                    Version latestVersion = workflowable.getLatestVersion();
                    String state;
                    if (latestVersion != null) {
                        state = latestVersion.getState();
                    } else {
                        Workflow workflow = WorkflowUtil.getWorkflowSchema(this.manager,
                                getSession(),
                                getLogger(),
                                documents[i]);
                        state = workflow.getInitialState();
                    }
                    entry.setValue(KEY_WORKFLOW_STATE, state);
                } else {
                    entry.setValue(KEY_WORKFLOW_STATE, "");
                }

                if (documents[i].getRepositoryNode().isCheckedOut()) {
                    RCML rcml = documents[i].getRepositoryNode().getRcml();
                    RCMLEntry lastEntry = rcml.getLatestCheckOutEntry();
                    String userId = lastEntry.getIdentity();
                    entry.setValue(KEY_CHECKED_OUT, userId);
                } else {
                    entry.setValue(KEY_CHECKED_OUT, "");
                }
                entries.add(entry);
            }

            for (int i = 0; i < FILTERS.length; i++) {
                SortedSet filterValues = new TreeSet();
                filterValues.add(VALUE_ALL);

                String key = "key" + FILTERS[i].substring("filter".length());

                for (Iterator docs = entries.iterator(); docs.hasNext();) {
                    Entry entry = (Entry) docs.next();
                    filterValues.add(entry.getValue(key));
                }
                setParameter(FILTERS[i] + "Values", filterValues);
                setParameter(FILTERS[i], VALUE_ALL);
            }
            setParameter(PARAMETER_FILTERS, Arrays.asList(FILTERS));

            setParameter(ALL_DOCUMENTS, new ArrayList(entries));
            setParameter(DOCUMENTS, entries);

            setParameter(PARAMETER_KEYS, Arrays.asList(KEYS));

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    protected RevisionController getRevisionController() {
        return new RevisionController();
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
            documents = siteManager.getDocuments(identityMap,
                    publication,
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
        return PublicationUtil.getPublicationFromUrl(this.manager,
                getDocumentFactory(),
                getSourceURL());
    }

    /**
     * @see org.apache.lenya.cms.usecase.Usecase#advance()
     */
    public void advance() throws UsecaseException {
        super.advance();

        List allDocuments = (List) getParameter(ALL_DOCUMENTS);
        List filteredDocuments = new ArrayList(allDocuments);

        for (int i = 0; i < FILTERS.length; i++) {
            String key = "key" + FILTERS[i].substring("filter".length());
            String filterValue = getParameterAsString(FILTERS[i]);
            if (!filterValue.equals(VALUE_ALL)) {
                Entry[] entries = (Entry[]) filteredDocuments.toArray(new Entry[filteredDocuments.size()]);
                for (int entryIndex = 0; entryIndex < entries.length; entryIndex++) {
                    if (!entries[entryIndex].getValue(key).equals(filterValue)) {
                        filteredDocuments.remove(entries[entryIndex]);
                    }
                }
            }
        }

        String sort = getParameterAsString(SORT);
        if (sort != null) {
            Comparator comparator = new EntryComparator(sort);
            Collections.sort(filteredDocuments, comparator);
        }

        setParameter(DOCUMENTS, filteredDocuments);
    }

    /**
     * Comparator for entries.
     */
    public static class EntryComparator implements Comparator {

        private String key;

        /**
         * @param key The key to compare.
         */
        public EntryComparator(String key) {
            this.key = key;
        }

        /**
         * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
         */
        public int compare(Object arg0, Object arg1) {
            Entry e1 = (Entry) arg0;
            Entry e2 = (Entry) arg1;

            String value1 = e1.getValue(this.key);
            String value2 = e2.getValue(this.key);

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
