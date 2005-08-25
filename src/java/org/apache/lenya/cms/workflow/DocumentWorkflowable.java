/*
 * Copyright  1999-2004 The Apache Software Foundation
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
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

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.avalon.framework.container.ContainerUtil;
import org.apache.avalon.framework.logger.AbstractLogEnabled;
import org.apache.avalon.framework.logger.Logger;
import org.apache.lenya.cms.metadata.LenyaMetaData;
import org.apache.lenya.cms.metadata.MetaData;
import org.apache.lenya.cms.publication.Document;
import org.apache.lenya.cms.publication.DocumentException;
import org.apache.lenya.cms.publication.ResourceType;
import org.apache.lenya.workflow.Situation;
import org.apache.lenya.workflow.Version;
import org.apache.lenya.workflow.Workflow;
import org.apache.lenya.workflow.Workflowable;

/**
 * Workflowable around a CMS document.
 * 
 * @version $Id$
 */
public class DocumentWorkflowable extends AbstractLogEnabled implements Workflowable {

    /**
     * Ctor.
     * @param document The document.
     * @param logger The logger.
     */
    public DocumentWorkflowable(Document document, Logger logger) {
        this.document = document;
        ContainerUtil.enableLogging(this, logger);
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

    /**
     * @see org.apache.lenya.workflow.Workflowable#getVersions()
     */
    public Version[] getVersions() {
        if (this.versions == null) {
            try {
                MetaData meta = this.document.getMetaDataManager().getLenyaMetaData();

                String[] versionStrings = meta.getValues(LenyaMetaData.ELEMENT_WORKFLOW_VERSION);
                this.versions = new Version[versionStrings.length];
                for (int i = 0; i < versionStrings.length; i++) {
                    String string = versionStrings[i];
                    int spaceIndex = string.indexOf(" ");
                    String numberString = string.substring(0, spaceIndex);
                    int number = Integer.parseInt(numberString);
                    String versionString = string.substring(spaceIndex + 1);
                    Version version = decodeVersion(versionString);
                    this.versions[number] = version;
                }

            } catch (DocumentException e) {
                throw new RuntimeException();
            }
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
     *      org.apache.lenya.workflow.Version, org.apache.lenya.workflow.Situation)
     */
    public void newVersion(Workflow workflow, Version version, Situation situation) {
        Version[] newVersions = new Version[getVersions().length + 1];
        for (int i = 0; i < getVersions().length; i++) {
            newVersions[i] = getVersions()[i];
        }

        int number = newVersions.length - 1;
        newVersions[number] = version;

        String string = number + " " + encodeVersion(workflow, version, (LenyaSituation) situation);
        try {
            MetaData meta = this.document.getMetaDataManager().getLenyaMetaData();
            meta.addValue(LenyaMetaData.ELEMENT_WORKFLOW_VERSION, string);
            meta.save();
        } catch (DocumentException e) {
            throw new RuntimeException(e);
        }
    }

    protected String encodeVersion(Workflow workflow, Version version, LenyaSituation situation) {

        String string = "event:" + version.getEvent();
        string += " state:" + version.getState();

        string += " user:" + situation.getUserId();
        string += " machine:" + situation.getMachineIp();

        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss");
        string += " date:" + format.format(new Date());

        String names[] = workflow.getVariableNames();
        for (int i = 0; i < names.length; i++) {
            String value = Boolean.toString(version.getValue(names[i]));
            string += " var:" + names[i] + "=" + value;
        }
        return string;
    }

    protected Version decodeVersion(String string) {

        String event = null;
        String state = null;
        Map variables = new HashMap();

        String[] parts = string.split(" ");
        for (int i = 0; i < parts.length; i++) {
            String[] steps = parts[i].split(":");
            if (steps[0].equals("event")) {
                event = steps[1];
            }
            if (steps[0].equals("state")) {
                state = steps[1];
            }
            if (steps[0].equals("var")) {
                String[] nameValue = steps[1].split("=");
                variables.put(nameValue[0], nameValue[1]);
            }
        }
        Version version = new LenyaVersion(event, state);
        for (Iterator i = variables.keySet().iterator(); i.hasNext();) {
            String name = (String) i.next();
            String value = (String) variables.get(name);
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
            uri = this.document.getPublication().getSourceURI() + "/config/workflow/"
                + schema;
            uri = uri.substring("lenya://".length());
            uri = "context://" + uri;
	}
        return uri;
    }

}
