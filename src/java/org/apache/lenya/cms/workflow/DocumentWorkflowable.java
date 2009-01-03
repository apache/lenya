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
package org.apache.lenya.cms.workflow;

import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import org.apache.avalon.framework.container.ContainerUtil;
import org.apache.avalon.framework.logger.AbstractLogEnabled;
import org.apache.avalon.framework.logger.Logger;
import org.apache.avalon.framework.service.ServiceManager;
import org.apache.lenya.ac.Identity;
import org.apache.lenya.ac.User;
import org.apache.lenya.cms.metadata.MetaData;
import org.apache.lenya.cms.observation.RepositoryEvent;
import org.apache.lenya.cms.observation.RepositoryEventFactory;
import org.apache.lenya.cms.publication.Document;
import org.apache.lenya.cms.publication.ResourceType;
import org.apache.lenya.cms.repository.Session;
import org.apache.lenya.util.Assert;
import org.apache.lenya.workflow.Version;
import org.apache.lenya.workflow.Workflow;
import org.apache.lenya.workflow.Workflowable;

/**
 * Workflowable around a CMS document.
 * 
 * @version $Id: DocumentWorkflowable.java 416648 2006-06-23 09:15:28Z andreas $
 */
class DocumentWorkflowable extends AbstractLogEnabled implements Workflowable {

    /**
     * Ctor.
     * @param manager The service manager.
     * @param session The repository session.
     * @param document The document.
     * @param logger The logger.
     */
    public DocumentWorkflowable(ServiceManager manager, Session session, Document document,
            Logger logger) {
        if (session.getIdentity() == null) {
            throw new IllegalArgumentException("The session must have an identity.");
        }
        this.document = document;
        this.session = session;
        this.manager = manager;
        ContainerUtil.enableLogging(this, logger);
    }

    private Session session;

    private ServiceManager manager;

    /**
     * @return The service manager.
     */
    public ServiceManager getServiceManager() {
        return this.manager;
    }

    /**
     * @return The repository session.
     */
    public Session getSession() {
        return session;
    }

    private Document document;

    protected Document getDocument() {
        return this.document;
    }

    /**
     * @return The name of the workflow schema.
     */
    protected String getWorkflowSchema() {
        String workflowName = null;
        try {
            ResourceType doctype = document.getResourceType();
            if (doctype != null) {
                workflowName = document.getPublication().getWorkflowSchema(doctype);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return workflowName;
    }

    private Version[] versions = null;

    private int revision = 0;

    protected static final String METADATA_NAMESPACE = "http://apache.org/lenya/metadata/workflow/1.0";
    protected static final String METADATA_VERSION = "workflowVersion";

    /**
     * @see org.apache.lenya.workflow.Workflowable#getVersions()
     */
    public Version[] getVersions() {
        try {
            MetaData meta = this.document.getMetaData(METADATA_NAMESPACE);
            
            org.apache.lenya.cms.repository.History history = this.document.getRepositoryNode().getHistory();
            boolean checkedIn = history.getRevisionNumbers().length > 0;
            if (this.versions == null || (checkedIn && history.getLatestRevision().getNumber() > this.revision)) {
                String[] versionStrings = meta.getValues(METADATA_VERSION);
                this.versions = new Version[versionStrings.length];
                
                SortedMap number2version = new TreeMap();
                
                for (int i = 0; i < versionStrings.length; i++) {
                    String string = versionStrings[i];
                    int spaceIndex = string.indexOf(" ");
                    String numberString = string.substring(0, spaceIndex);
                    int number = Integer.parseInt(numberString);
                    String versionString = string.substring(spaceIndex + 1);
                    Version version = decodeVersion(versionString);
                    number2version.put(new Integer(number), version);
                }
                
                int number = 0;
                for (Iterator i = number2version.keySet().iterator(); i.hasNext(); ) {
                    Version version = (Version) number2version.get(i.next());
                    this.versions[number] = version;
                    number++;
                }
                
                if (checkedIn) {
                    this.revision = history.getLatestRevision().getNumber();
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return this.versions;
    }

    /**
     * @see org.apache.lenya.workflow.Workflowable#getLatestVersion()
     */
    public Version getLatestVersion() {
        Version version = null;
        Version[] versions = getVersions();
        if (versions.length > 0) {
            version = versions[versions.length - 1];
        }
        return version;
    }

    /**
     * @see org.apache.lenya.workflow.Workflowable#newVersion(org.apache.lenya.workflow.Workflow,
     *      org.apache.lenya.workflow.Version)
     */
    public void newVersion(Workflow workflow, Version version) {
        Version[] newVersions = new Version[getVersions().length + 1];
        for (int i = 0; i < getVersions().length; i++) {
            newVersions[i] = getVersions()[i];
        }

        int number = newVersions.length - 1;
        newVersions[number] = version;

        String string = number + " " + encodeVersion(workflow, version);
        addToMetaData(string);
        
        WorkflowEventDescriptor descriptor = new WorkflowEventDescriptor(version);
        RepositoryEvent event = RepositoryEventFactory.createEvent(
                this.manager, getDocument(), getLogger(), descriptor);
        getDocument().getRepositoryNode().getSession().enqueueEvent(event);
    }

    protected void addToMetaData(String versionString) {
        try {
            String[] areas = getDocument().getPublication().getAreaNames();
            for (int i = 0; i < areas.length; i++) {
                if (getDocument().existsAreaVersion(areas[i])) {
                    Document doc = getDocument().getAreaVersion(areas[i]);
                    MetaData meta = doc.getMetaData(METADATA_NAMESPACE);
                    meta.addValue(METADATA_VERSION, versionString);
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    protected String encodeVersion(Workflow workflow, Version version) {

        StringBuffer stringBuf = new StringBuffer("event:").append(version.getEvent());
        stringBuf.append(" state:").append(version.getState());

        Identity identity = getSession().getIdentity();
        User user = identity.getUser();
        if (user != null) {
            stringBuf.append(" user:").append(identity.getUser().getId());
        }
        stringBuf.append(" machine:").append(identity.getMachine().getIp());

        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss");
        stringBuf.append(" date:").append(format.format(new Date()));

        String names[] = workflow.getVariableNames();
        for (int i = 0; i < names.length; i++) {
            String value = Boolean.toString(version.getValue(names[i]));
            stringBuf.append(" var:").append(names[i]);
            stringBuf.append("=").append(value);
        }
        return stringBuf.toString();
    }

    protected Version decodeVersion(String string) {

        String event = null;
        String state = null;
        String user = null;
        String machine = null;
        Date date = null;
        Map variables = new HashMap();

        String[] parts = string.split(" ");
        for (int i = 0; i < parts.length; i++) {
            String[] steps = parts[i].split(":", 2);
            if (steps[0].equals("event")) {
                event = steps[1];
            } else if (steps[0].equals("state")) {
                state = steps[1];
            } else if (steps[0].equals("user")) {
                user = steps[1];
            } else if (steps[0].equals("date")) {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss", Locale.US);
                date = sdf.parse(steps[1], new ParsePosition(0));
            } else if (steps[0].equals("machine")) {
                machine = steps[1];
            } else if (steps[0].equals("var")) {
                String[] nameValue = steps[1].split("=");
                variables.put(nameValue[0], nameValue[1]);
            }
        }

        Version version = new LenyaVersion(event, state);
        for (Iterator i = variables.keySet().iterator(); i.hasNext();) {
            String name = (String) i.next();
            String value = (String) variables.get(name);
            version.setUserId(user);
            version.setDate(date);
            version.setIPAddress(machine);
            version.setValue(name, Boolean.valueOf(value).booleanValue());
        }
        return version;
    }

    /**
     * @see org.apache.lenya.workflow.Workflowable#getWorkflowSchemaURI()
     */
    public String getWorkflowSchemaURI() {
        String uri = null;
        String schema = getWorkflowSchema();
        if (schema != null) {

            if (schema.indexOf("://") != -1) {
                return schema;
            } else {
                uri = this.document.getPublication().getSourceURI() + "/config/workflow/" + schema;
                uri = uri.substring("lenya://".length());
                uri = "context://" + uri;
            }
        }
        return uri;
    }

    public String toString() {
        return this.document.toString();
    }

}
