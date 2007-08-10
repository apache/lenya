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

/* $Id: RCML.java 473861 2006-11-12 03:51:14Z gregor $  */

package org.apache.lenya.cms.repository;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import org.apache.avalon.framework.service.ServiceManager;
import org.apache.excalibur.source.SourceResolver;
import org.apache.lenya.cms.cocoon.source.SourceUtil;
import org.apache.lenya.cms.rc.CheckInEntry;
import org.apache.lenya.cms.rc.CheckOutEntry;
import org.apache.lenya.cms.rc.RCML;
import org.apache.lenya.cms.rc.RCMLEntry;
import org.apache.lenya.cms.rc.RevisionControlException;
import org.apache.lenya.util.Assert;
import org.apache.lenya.xml.DocumentHelper;
import org.apache.lenya.xml.NamespaceHelper;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Handle with the RCML file
 */
public class SourceNodeRCML implements RCML {

    protected static final String NAMESPACE = "";

    private boolean dirty = false;
    private int maximalNumberOfEntries = 5;
    private Vector entries;

    private ServiceManager manager;

    private String contentSourceUri;
    private String metaSourceUri;

    private static Map ELEMENTS = new HashMap();
    protected static final String ELEMENT_CHECKIN = "CheckIn";
    protected static final String ELEMENT_CHECKOUT = "CheckOut";
    protected static final String ELEMENT_BACKUP = "Backup";
    protected static final String ELEMENT_TIME = "Time";
    protected static final String ELEMENT_VERSION = "Version";
    protected static final String ELEMENT_IDENTITY = "Identity";
    protected static final String ELEMENT_XPSREVISIONCONTROL = "XPSRevisionControl";
    protected static final String ELEMENT_SESSION = "session";

    {
        ELEMENTS.put(new Short(ci), ELEMENT_CHECKIN);
        ELEMENTS.put(new Short(co), ELEMENT_CHECKOUT);
    }

    /**
     * @param contentSourceUri The content source URI.
     * @param metaSourceUri The meta source URI.
     * @param manager The service manager.
     */
    public SourceNodeRCML(String contentSourceUri, String metaSourceUri, ServiceManager manager) {
        this.maximalNumberOfEntries = 10;
        this.maximalNumberOfEntries = (2 * this.maximalNumberOfEntries) + 1;
        this.manager = manager;
        this.contentSourceUri = contentSourceUri;
        this.metaSourceUri = metaSourceUri;
    }

    protected static final String RCML_EXTENSION = ".rcml";

    protected String getRcmlSourceUri() {
        return this.contentSourceUri + RCML_EXTENSION;
    }

    /**
     * Call the method write, if the document is dirty
     * 
     * @throws IOException if an error occurs
     * @throws Exception if an error occurs
     */
    protected void finalize() throws IOException, Exception {
        if (this.isDirty()) {
            write();
        }
    }

    /**
     * Write the XML RCML-document in the RCML-file.
     * @throws RevisionControlException if an error occurs
     */
    public synchronized void write() throws RevisionControlException {
        NamespaceHelper helper = saveToXml();
        Assert.notNull("XML document", helper);
        try {
            SourceUtil.writeDOM(helper.getDocument(), getRcmlSourceUri(), this.manager);
        } catch (Exception e) {
            throw new RevisionControlException(e);
        }
        clearDirty();
    }

    /**
     * Write a new entry for a check out or a check in the RCML-File made by the
     * user with identity at time
     * @param node The node.
     * @param type co for a check out, ci for a check in
     * @param time
     * @param backup Create backup element
     * @throws RevisionControlException if an error occurs
     */
    public synchronized void checkOutIn(Node node, short type, long time, boolean backup)
            throws RevisionControlException {

        String identity = node.getSession().getIdentity().getUser().getId();

        Vector entries = getEntries();
        if (entries.size() == 0) {
            if (type == ci) {
                throw new IllegalStateException("Can't check in - not checked out.");
            }
        } else {
            RCMLEntry latestEntry = getLatestEntry();
            if (type == latestEntry.getType()) {
                String elementName = (String) ELEMENTS.get(new Short(type));
                throw new IllegalStateException("RCML entry type <" + elementName
                        + "> not allowed twice in a row. Before: [" + latestEntry.getIdentity()
                        + "], now: [" + identity + "], node: [" + this.contentSourceUri + "]");
            }
        }

        String sessionId = node.getSession().getId();

        RCMLEntry entry;
        switch (type) {
        case RCML.ci:
            int version = 0;
            CheckInEntry latestEntry = getLatestCheckInEntry();
            if (latestEntry != null) {
                version = latestEntry.getVersion();
            }
            version++;
            entry = new CheckInEntry(sessionId, identity, time, version, backup);
            break;
        case RCML.co:
            entry = new CheckOutEntry(sessionId, identity, time);
            break;
        default:
            throw new IllegalArgumentException("No such type: [" + type + "]");
        }

        entries.add(0, entry);
        setDirty();
        write();
    }

    protected Element saveToXml(NamespaceHelper helper, RCMLEntry entry)
            throws RevisionControlException {
        String elementName = (String) ELEMENTS.get(new Short(entry.getType()));
        Element sessionElement = helper.createElement(ELEMENT_SESSION, entry.getSessionId());
        Element identityElement = helper.createElement(ELEMENT_IDENTITY, entry.getIdentity());
        Element timeElement = helper.createElement(ELEMENT_TIME, Long.toString(entry.getTime()));

        Element entryElement = helper.createElement(elementName);

        entryElement.appendChild(identityElement);
        entryElement.appendChild(timeElement);
        entryElement.appendChild(sessionElement);

        if (entry.getType() == ci) {
            CheckInEntry checkInEntry = (CheckInEntry) entry;
            Element versionElement = helper.createElement(ELEMENT_VERSION, Integer
                    .toString(checkInEntry.getVersion()));
            entryElement.appendChild(versionElement);
            if (checkInEntry.hasBackup()) {
                Element backupElement = helper.createElement(ELEMENT_BACKUP);
                entryElement.appendChild(backupElement);
            }
        }

        return entryElement;
    }

    protected NamespaceHelper saveToXml() throws RevisionControlException {
        try {
            NamespaceHelper helper = new NamespaceHelper(NAMESPACE, "", ELEMENT_XPSREVISIONCONTROL);
            Element root = helper.getDocument().getDocumentElement();
            Vector entries = getEntries();
            for (Iterator i = entries.iterator(); i.hasNext();) {
                RCMLEntry entry = (RCMLEntry) i.next();
                Element element = saveToXml(helper, entry);
                root.appendChild(element);
            }
            return helper;
        } catch (Exception e) {
            throw new RevisionControlException("Could create revision control XML ["
                    + getRcmlSourceUri() + "]", e);
        }
    }

    protected Element getLatestElement(NamespaceHelper helper, String type)
            throws RevisionControlException {
        Element parent = helper.getDocument().getDocumentElement();
        return helper.getFirstChild(parent, type);
    }

    /**
     * get the latest check out
     * @return CheckOutEntry The entry of the check out
     * @throws RevisionControlException if an error occurs
     */
    public CheckOutEntry getLatestCheckOutEntry() throws RevisionControlException {
        return (CheckOutEntry) getLatestEntry(RCML.co);
    }

    /**
     * get the latest check in
     * @return CheckInEntry The entry of the check in
     * @throws RevisionControlException if an error occurs
     */
    public CheckInEntry getLatestCheckInEntry() throws RevisionControlException {
        return (CheckInEntry) getLatestEntry(RCML.ci);
    }

    /**
     * get the latest entry (a check out or check in)
     * @param type The type.
     * @return RCMLEntry The entry of the check out/in
     * @throws RevisionControlException if an error occurs
     */
    public RCMLEntry getLatestEntry(short type) throws RevisionControlException {
        Vector entries = getEntries();
        for (Iterator i = entries.iterator(); i.hasNext(); ) {
            RCMLEntry entry = (RCMLEntry) i.next();
            if (entry.getType() == type) {
                return entry;
            }
        }
        return null;
    }

    public RCMLEntry getLatestEntry() throws RevisionControlException {
        Vector entries = getEntries();
        if (entries.isEmpty()) {
            return null;
        }
        else {
            return (RCMLEntry) entries.firstElement();
        }
    }

    protected RCMLEntry getEntry(NamespaceHelper helper, Element element) {
        String type = element.getLocalName();
        String sessionId = getChildValue(helper, element, ELEMENT_SESSION, "");
        String identity = getChildValue(helper, element, ELEMENT_IDENTITY);
        String timeString = getChildValue(helper, element, ELEMENT_TIME);
        long time = new Long(timeString).longValue();
        if (type.equals(ELEMENT_CHECKIN)) {
            String versionString = getChildValue(helper, element, ELEMENT_VERSION);
            int version = new Integer(versionString).intValue();
            boolean backup = helper.getChildren(element, ELEMENT_BACKUP).length > 0;
            return new CheckInEntry(sessionId, identity, time, version, backup);
        } else if (type.equals(ELEMENT_CHECKOUT)) {
            return new CheckOutEntry(sessionId, identity, time);
        } else {
            throw new RuntimeException("Unsupported RCML entry type: [" + type + "]");
        }
    }

    protected String getChildValue(NamespaceHelper helper, Element element, String childName,
            String defaultValue) {
        Element child = DocumentHelper.getFirstChild(element, NAMESPACE, childName);
        if (child == null) {
            return defaultValue;
        } else {
            return DocumentHelper.getSimpleElementText(child);
        }
    }

    protected String getChildValue(NamespaceHelper helper, Element element, String childName) {
        Element child = helper.getFirstChild(element, childName);
        if (child == null) {
            throw new RuntimeException("The element <" + element.getNodeName()
                    + "> has no child element <" + childName + ">. Source URI: ["
                    + getRcmlSourceUri() + "[");
        }
        return DocumentHelper.getSimpleElementText(child);
    }

    /**
     * get all check in and check out
     * @return Vector of all check out and check in entries in this RCML-file
     * @throws RevisionControlException if an error occurs
     */
    public synchronized Vector getEntries() throws RevisionControlException {
        if (this.entries == null) {
            this.entries = new Vector();
            String uri = getRcmlSourceUri();
            try {
                if (SourceUtil.exists(uri, this.manager)) {
                    Document xml = SourceUtil.readDOM(uri, this.manager);
                    NamespaceHelper helper = new NamespaceHelper(NAMESPACE, "", xml);
                    Element parent = xml.getDocumentElement();
                    Element[] elements = helper.getChildren(parent);
                    for (int i = 0; i < elements.length; i++) {
                        RCMLEntry entry = getEntry(helper, elements[i]);
                        entries.add(entry);
                    }
                }
            } catch (Exception e) {
                throw new RevisionControlException(e);
            }

        }
        return this.entries;
    }

    /**
     * get all backup entries
     * @return Vector of all entries in this RCML-file with a backup
     * @throws Exception if an error occurs
     */
    public synchronized Vector getBackupEntries() throws Exception {
        Vector entries = getEntries();
        Vector backupEntries = new Vector();
        for (Iterator i = entries.iterator(); i.hasNext();) {
            RCMLEntry entry = (RCMLEntry) i.next();
            if (entry.getType() == RCML.ci && ((CheckInEntry) entry).hasBackup()) {
                backupEntries.add(entry);
            }
        }
        return backupEntries;
    }

    public synchronized void makeBackup(long time) throws RevisionControlException {
        makeBackup(this.contentSourceUri, time);
        makeBackup(this.metaSourceUri, time);
    }

    protected synchronized void makeBackup(String sourceUri, long time)
            throws RevisionControlException {
        String backupSourceUri = getBackupSourceUri(sourceUri, time);
        try {
            if (SourceUtil.exists(sourceUri, manager)) {
                SourceUtil.copy(this.manager, sourceUri, backupSourceUri);
            }
        } catch (Exception e) {
            throw new RevisionControlException(e);
        }
    }

    public synchronized void restoreBackup(Node node, long time) throws RevisionControlException {
        SourceNode sourceNode = (SourceNode) node;
        restoreBackup(sourceNode.getContentSource(), time);
        restoreBackup(sourceNode.getMetaSource(), time);
    }

    protected synchronized void restoreBackup(SourceWrapper wrapper, long time) throws RevisionControlException {
        String backupSourceUri = getBackupSourceUri(wrapper, time);
        SourceResolver resolver = null;
        try {
            resolver = (SourceResolver) this.manager.lookup(SourceResolver.ROLE);
            SourceUtil.copy(resolver, backupSourceUri, wrapper.getOutputStream());
        } catch (Exception e) {
            throw new RevisionControlException(e);
        } finally {
            if (resolver != null) {
                this.manager.release(resolver);
            }
        }
    }

    protected String getBackupSourceUri(SourceWrapper wrapper, long time) {
        String uri = wrapper.getRealSourceUri();
        return getBackupSourceUri(uri, time);
    }

    protected String getBackupSourceUri(String uri, long time) {
        return uri + "." + time + ".bak";
    }

    /**
     * Prune the list of entries and delete the corresponding backups. Limit the
     * number of entries to the value maximalNumberOfEntries
     * (2maxNumberOfRollbacks(configured)+1)
     * @throws RevisionControlException if an error occurs
     */
    public synchronized void pruneEntries() throws RevisionControlException {
        Vector entries = getEntries();
        RCMLEntry[] array = (RCMLEntry[]) entries.toArray(new RCMLEntry[entries.size()]);

        for (int i = this.maximalNumberOfEntries; i < entries.size(); i++) {
            // remove the backup file associated with this entry
            RCMLEntry entry = array[i];
            if (entry.getType() == ci && ((CheckInEntry) entry).hasBackup()) {
                long time = entry.getTime();
                deleteBackup(this.contentSourceUri, time);
                deleteBackup(this.metaSourceUri, time);
                this.entries.remove(entry);
            }
        }
        setDirty();
    }

    protected synchronized void deleteBackup(String sourceUri, long time) throws RevisionControlException {
        String uri = getBackupSourceUri(sourceUri, time);
        try {
            SourceUtil.delete(uri, this.manager);
            SourceUtil.deleteEmptyCollections(uri, this.manager);
        } catch (Exception e) {
            throw new RevisionControlException(e);
        }
    }

    /**
     * Check if the document is dirty
     * @return boolean dirty
     */
    public boolean isDirty() {
        return this.dirty;
    }

    /**
     * Set the value dirty to true
     */
    protected void setDirty() {
        this.dirty = true;
    }

    /**
     * Set the value dirty to false
     */
    protected void clearDirty() {
        this.dirty = false;
    }

    /**
     * Delete the latest check in
     * @throws RevisionControlException if an error occurs
     */
    public synchronized void deleteFirstCheckIn() throws RevisionControlException {
        deleteLatestEntry(ci);
    }

    /**
     * Delete the latest check in
     * @throws RevisionControlException if an error occurs
     */
    public synchronized void deleteFirstCheckOut() throws RevisionControlException {
        deleteLatestEntry(co);
    }

    protected synchronized void deleteLatestEntry(short type) throws RevisionControlException {
        RCMLEntry entry = getLatestEntry(type);
        this.entries.remove(entry);
        setDirty();
    }

    /**
     * get the time's value of the backups
     * @return String[] the times
     * @throws Exception if an error occurs
     */
    public String[] getBackupsTime() throws Exception {

        Vector entries = getEntries();
        List times = new ArrayList();
        for (Iterator i = entries.iterator(); i.hasNext(); ) {
            RCMLEntry entry = (RCMLEntry) i.next();
            if (entry.getType() == ci && ((CheckInEntry) entry).hasBackup()) {
                times.add(Long.toString(entry.getTime()));
            }
        }
        return (String[]) times.toArray(new String[times.size()]);

    }

    /**
     * Delete the revisions, the RCML source and the collection if the latter is
     * empty.
     * @return boolean true, if the file was deleted
     */
    public synchronized boolean delete() {
        try {
            deleteRevisions();
            SourceUtil.delete(getRcmlSourceUri(), this.manager);
            SourceUtil.deleteEmptyCollections(getRcmlSourceUri(), this.manager);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return true;
    }

    /**
     * delete the revisions
     * @throws RevisionControlException when somthing went wrong
     */
    public synchronized void deleteRevisions() throws RevisionControlException {
        try {
            String[] times = getBackupsTime();
            for (int i = 0; i < times.length; i++) {
                long time = new Long(times[i]).longValue();
                deleteBackup(this.contentSourceUri, time);
                deleteBackup(this.metaSourceUri, time);
            }
        } catch (Exception e) {
            throw new RevisionControlException(e);
        }
    }

    public synchronized void copyFrom(Node node, Node otherNode) throws RevisionControlException {

        SourceNode otherSourceNode = (SourceNode) otherNode;
        SourceNode sourceNode = (SourceNode) node;
        SourceNodeRCML otherRcml = (SourceNodeRCML) ((SourceNode) otherNode).getRcml();

        try {

            Vector backupEntries = otherRcml.getBackupEntries();
            for (Iterator i = backupEntries.iterator(); i.hasNext();) {
                RCMLEntry entry = (RCMLEntry) i.next();
                long time = entry.getTime();
                String otherContentUri = otherRcml.getBackupSourceUri(otherSourceNode
                        .getContentSource(), time);
                String thisContentUri = this
                        .getBackupSourceUri(sourceNode.getContentSource(), time);
                SourceUtil.copy(this.manager, otherContentUri, thisContentUri);

                String otherMetaUri = otherRcml.getBackupSourceUri(otherSourceNode.getMetaSource(),
                        time);
                String thisMetaUri = this.getBackupSourceUri(sourceNode.getMetaSource(), time);
                SourceUtil.copy(this.manager, otherMetaUri, thisMetaUri);
            }

            this.entries = new Vector();
            Vector otherEntries = otherRcml.getEntries();
            for (Iterator i = otherEntries.iterator(); i.hasNext();) {
                RCMLEntry entry = (RCMLEntry) i.next();
                RCMLEntry newEntry = null;
                switch (entry.getType()) {
                case co:
                    newEntry = new CheckOutEntry(entry.getSessionId(), entry.getIdentity(), entry
                            .getTime());
                    break;
                case ci:
                    CheckInEntry ciEntry = (CheckInEntry) entry;
                    newEntry = new CheckInEntry(ciEntry.getSessionId(), ciEntry.getIdentity(),
                            ciEntry.getTime(), ciEntry.getVersion(), ciEntry.hasBackup());
                    break;
                }
                this.entries.add(newEntry);
            }

            write();
        } catch (Exception e) {
            throw new RevisionControlException(e);
        }
    }

    public synchronized boolean isCheckedOut() throws RevisionControlException {
        RCMLEntry entry = getLatestEntry();
        return entry != null && entry.getType() == RCML.co;
    }

    public synchronized void checkIn(Node node, boolean backup, boolean newVersion)
            throws RevisionControlException {

        long time = new Date().getTime();

        if (backup) {
            makeBackup(time);
        }

        if (newVersion) {
            checkOutIn(node, RCML.ci, time, backup);
        } else {
            deleteFirstCheckOut();
        }
        pruneEntries();
        write();
    }

    public synchronized void checkOut(Node node) throws RevisionControlException {
        checkOutIn(node, RCML.co, new Date().getTime(), false);
    }

    public boolean isCheckedOutBySession(Session session) throws RevisionControlException {
        Vector entries = getEntries();
        if (entries.size() > 0) {
            RCMLEntry entry = (RCMLEntry) entries.get(0);
            return entry.getType() == co && entry.getSessionId().equals(session.getId());
        }
        return false;
    }

}