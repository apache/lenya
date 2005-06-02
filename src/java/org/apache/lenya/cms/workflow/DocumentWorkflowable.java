/*
 * Created on 27.05.2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
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
import org.apache.avalon.framework.service.ServiceManager;
import org.apache.lenya.cms.metadata.LenyaMetaData;
import org.apache.lenya.cms.publication.Document;
import org.apache.lenya.cms.publication.DocumentException;
import org.apache.lenya.cms.publication.DocumentType;
import org.apache.lenya.workflow.Situation;
import org.apache.lenya.workflow.Version;
import org.apache.lenya.workflow.Workflow;
import org.apache.lenya.workflow.Workflowable;

/**
 * @author nobby
 * 
 * TODO To change the template for this generated type comment go to Window - Preferences - Java -
 * Code Style - Code Templates
 */
public class DocumentWorkflowable extends AbstractLogEnabled implements Workflowable {

    /**
     * Ctor.
     * @param document The document.
     * @param manager The service manager.
     * @param logger The logger.
     */
    public DocumentWorkflowable(Document document, ServiceManager manager, Logger logger) {
        this.document = document;
        this.manager = manager;
        ContainerUtil.enableLogging(this, logger);
    }

    private Document document;
    private ServiceManager manager;

    protected String getWorkflowName() {
        String workflowName = null;
        try {
            DocumentType doctype = document.getResourceType();
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
                LenyaMetaData meta = this.document.getMetaDataManager().getLenyaMetaData();

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

        String string = number + " " + encodeVersion(version, (CMSSituation) situation);
        try {
            LenyaMetaData meta = this.document.getMetaDataManager().getLenyaMetaData();
            meta.addValue(LenyaMetaData.ELEMENT_WORKFLOW_VERSION, string);
            meta.save();
        } catch (DocumentException e) {
            throw new RuntimeException(e);
        }
    }

    protected String encodeVersion(Version version, CMSSituation situation) {

        String string = "event:" + version.getEvent();
        string += " state:" + version.getState();

        string += " user:" + situation.getUserId();
        string += " machine:" + situation.getMachineIp();

        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss");
        string += " date:" + format.format(new Date());

        WorkflowResolver resolver = null;
        try {
            resolver = (WorkflowResolver) this.manager.lookup(WorkflowResolver.ROLE);
            Workflow workflow = resolver.getWorkflowSchema(this.document);
            String names[] = workflow.getVariableNames();
            for (int i = 0; i < names.length; i++) {
                String value = Boolean.toString(version.getValue(names[i]));
                string += " var:" + names[i] + "=" + value;
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            if (resolver != null) {
                this.manager.release(resolver);
            }
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
        Version version = new CMSVersion(event, state);
        for (Iterator i = variables.keySet().iterator(); i.hasNext();) {
            String name = (String) i.next();
            String value = (String) variables.get(name);
            version.setValue(name, Boolean.valueOf(value).booleanValue());
        }
        return version;
    }

}