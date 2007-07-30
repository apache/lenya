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
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Vector;

import org.apache.avalon.framework.service.ServiceException;
import org.apache.avalon.framework.service.ServiceManager;
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
    
    private SourceNode node;
    private Document xml = null;
    private boolean dirty = false;
    private int maximalNumberOfEntries = 5;

    private ServiceManager manager;

    private static Map ELEMENTS = new HashMap();
    protected static final String ELEMENT_CHECKIN = "CheckIn";
    protected static final String ELEMENT_CHECKOUT = "CheckOut";
    protected static final String ELEMENT_BACKUP = "Backup";
    protected static final String ELEMENT_TIME = "Time";
    protected static final String ELEMENT_VERSION = "Version";
    protected static final String ELEMENT_IDENTITY = "Identity";
    protected static final String ELEMENT_XPSREVISIONCONTROL = "XPSRevisionControl";

    {
        ELEMENTS.put(new Short(ci), ELEMENT_CHECKIN);
        ELEMENTS.put(new Short(co), ELEMENT_CHECKOUT);
    }

    /**
     * create a RCML-File if no one exists already
     * @param node The node to control.
     * @param manager The service manager.
     * @throws Exception if an error occurs
     */
    public SourceNodeRCML(SourceNode node, ServiceManager manager) throws Exception {

        this.maximalNumberOfEntries = 10;
        this.maximalNumberOfEntries = (2 * this.maximalNumberOfEntries) + 1;

        this.node = node;
        this.manager = manager;

    }

    protected static final String RCML_EXTENSION = ".rcml";

    protected String getRcmlSourceUri() {
        return this.node.getContentSource().getRealSourceUri() + RCML_EXTENSION;
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
    
    private static final Object classLock = SourceNodeRCML.class;
    
    /**
     * Write the xml RCML-document in the RCML-file.
     * @throws IOException if an error occurs
     * @throws Exception if an error occurs
     */
    public synchronized void write() throws Exception {
        Document xml = getDocument();
        Assert.notNull("XML document", xml);
        synchronized (classLock) {
            SourceUtil.writeDOM(xml, getRcmlSourceUri(), this.manager);
        }
        clearDirty();
    }

    /**
     * Write a new entry for a check out or a check in the RCML-File made by the
     * user with identity at time
     * @param type co for a check out, ci for a check in
     * @param identity The identity of the user
     * @param time Time at which the check in/out is made
     * @param backup Create backup element
     * @throws IOException if an error occurs
     * @throws Exception if an error occurs
     */
    public synchronized void checkOutIn(short type, String identity, long time, boolean backup)
            throws IOException, Exception {

        NamespaceHelper helper = getNamespaceHelper();
        String elementName = (String) ELEMENTS.get(new Short(type));

        Vector entries = getEntries();
        if (entries.size() == 0) {
            if (type == ci) {
                throw new IllegalStateException("Can't check in - not checked out.");
            }
        } else {
            RCMLEntry latestEntry = getLatestEntry();
            if (type == latestEntry.getType()) {
                throw new IllegalStateException("RCML entry type <" + elementName
                        + "> not allowed twice in a row.");
            }
        }

        if (identity == null) {
            throw new IllegalArgumentException("The identity must not be null!");
        }

        if (type != co && type != ci) {
            throw new IllegalArgumentException("ERROR: " + this.getClass().getName()
                    + ".checkOutIn(): No such type");
        }

        Element identityElement = helper.createElement(ELEMENT_IDENTITY, identity);
        Element timeElement = helper.createElement(ELEMENT_TIME, Long.toString(time));

        Element checkOutElement = helper.createElement(elementName);

        checkOutElement.appendChild(identityElement);
        checkOutElement.appendChild(timeElement);

        if (type == ci) {
            int version = 0;
            CheckInEntry latestEntry = getLatestCheckInEntry();
            if (latestEntry != null) {
                version = latestEntry.getVersion();
            }
            version++;
            Element versionElement = helper.createElement(ELEMENT_VERSION, Integer
                    .toString(version));
            checkOutElement.appendChild(versionElement);
        }

        if (backup) {
            Element backupElement = helper.createElement(ELEMENT_BACKUP);
            checkOutElement.appendChild(backupElement);
        }

        Element root = helper.getDocument().getDocumentElement();
        root.insertBefore(checkOutElement, root.getFirstChild());

        setDirty();

        // If this is a checkout, we write back the changed state
        // to the file immediately because otherwise another
        // process might read the file and think there is no open
        // checkout (as it is only visible in our private DOM tree
        // at this time).
        //
        // If, however, this is a checkin, we do not yet write it
        // out because then another process might again check it
        // out immediately and manipulate the file contents
        // *before* our caller has finished writing back the
        // changed data to the destination file. We therefore rely
        // on either our caller invoking the write() method when
        // finished or the garbage collector calling the finalize()
        // method.
        //
        // if (type == co) {
        // }
        write();
    }

    private long lastModified = 0;

    protected NamespaceHelper getNamespaceHelper() throws RevisionControlException {
        return new NamespaceHelper(NAMESPACE, "", getDocument());
    }

    protected Document getDocument() throws RevisionControlException {
        try {
            String uri = getRcmlSourceUri();
            if (SourceUtil.exists(uri, this.manager)) {
                long sourceLastModified = SourceUtil.getLastModified(uri, manager);
                if (this.xml == null || sourceLastModified > this.lastModified) {
                    this.xml = SourceUtil.readDOM(getRcmlSourceUri(), this.manager);
                    this.lastModified = sourceLastModified;
                }
            } else {
                if (this.xml == null) {
                    NamespaceHelper helper = new NamespaceHelper(NAMESPACE, "",
                            ELEMENT_XPSREVISIONCONTROL);
                    this.xml = helper.getDocument();
                }
            }
        } catch (Exception e) {
            throw new RevisionControlException("Could not read RC file [" + getRcmlSourceUri()
                    + "]");
        }
        return this.xml;
    }

    protected Element getLatestElement(NamespaceHelper helper, String type) throws Exception {
        Element parent = helper.getDocument().getDocumentElement();
        return helper.getFirstChild(parent, type);
    }

    /**
     * get the latest check out
     * @return CheckOutEntry The entry of the check out
     * @throws Exception if an error occurs
     */
    public CheckOutEntry getLatestCheckOutEntry() throws Exception {
        return (CheckOutEntry) getLatestEntry(getNamespaceHelper(), (String) ELEMENTS
                .get(new Short(RCML.co)));
    }

    /**
     * get the latest check in
     * @return CheckInEntry The entry of the check in
     * @throws Exception if an error occurs
     */
    public CheckInEntry getLatestCheckInEntry() throws Exception {
        return (CheckInEntry) getLatestEntry(getNamespaceHelper(), (String) ELEMENTS.get(new Short(
                RCML.ci)));
    }

    /**
     * get the latest entry (a check out or check in)
     * @return RCMLEntry The entry of the check out/in
     * @throws Exception if an error occurs
     */
    public RCMLEntry getLatestEntry() throws Exception {
        return getLatestEntry(getNamespaceHelper(), "*");
    }

    protected RCMLEntry getLatestEntry(NamespaceHelper helper, String elementName) throws Exception {
        Element element = getLatestElement(helper, elementName);
        if (element == null) {
            return null;
        } else {
            return getEntry(helper, element);
        }
    }

    protected RCMLEntry getEntry(NamespaceHelper helper, Element element) {
        String type = element.getLocalName();
        String identity = getChildValue(helper, element, ELEMENT_IDENTITY);
        String timeString = getChildValue(helper, element, ELEMENT_TIME);
        long time = new Long(timeString).longValue();
        if (type.equals(ELEMENT_CHECKIN)) {
            String versionString = getChildValue(helper, element, ELEMENT_VERSION);
            int version = new Integer(versionString).intValue();
            return new CheckInEntry(identity, time, version);
        } else if (type.equals(ELEMENT_CHECKOUT)) {
            return new CheckOutEntry(identity, time);
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
     * @throws Exception if an error occurs
     */
    public Vector getEntries() throws Exception {
        NamespaceHelper helper = getNamespaceHelper();
        Element parent = helper.getDocument().getDocumentElement();
        Element[] elements = helper.getChildren(parent);
        Vector entries = new Vector();
        for (int i = 0; i < elements.length; i++) {
            RCMLEntry entry = getEntry(helper, elements[i]);
            entries.add(entry);
        }
        return entries;
    }

    /**
     * get all backup entries
     * @return Vector of all entries in this RCML-file with a backup
     * @throws Exception if an error occurs
     */
    public Vector getBackupEntries() throws Exception {
        NamespaceHelper helper = getNamespaceHelper();
        Element parent = helper.getDocument().getDocumentElement();
        Element[] elements = helper.getChildren(parent, ELEMENT_CHECKIN);

        Vector entries = new Vector();

        for (int i = 0; i < elements.length; i++) {
            if (helper.getChildren(elements[i], ELEMENT_BACKUP).length > 0) {
                RCMLEntry entry = getEntry(helper, elements[i]);
                entries.add(entry);
            }
        }

        return entries;
    }

    public void makeBackup(long time) throws RevisionControlException {
        makeBackup(this.node.getContentSource(), time);
        makeBackup(this.node.getMetaSource(), time);
    }

    protected void makeBackup(SourceWrapper wrapper, long time) throws RevisionControlException {
        String backupSourceUri = getBackupSourceUri(wrapper, time);
        try {
            String uri = wrapper.getRealSourceUri();
            if (SourceUtil.exists(uri, manager)) {
                SourceUtil.copy(this.manager, uri, backupSourceUri);
            }
        } catch (Exception e) {
            throw new RevisionControlException(e);
        }
    }

    public void restoreBackup(long time) throws RevisionControlException {
        restoreBackup(this.node.getContentSource(), time);
        restoreBackup(this.node.getMetaSource(), time);
    }

    protected void restoreBackup(SourceWrapper wrapper, long time) throws RevisionControlException {
        String backupSourceUri = getBackupSourceUri(wrapper, time);
        try {
            SourceUtil.copy(this.manager, backupSourceUri, wrapper.getRealSourceUri());
        } catch (Exception e) {
            throw new RevisionControlException(e);
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
     * @throws Exception if an error occurs
     */
    public void pruneEntries() throws Exception {
        NamespaceHelper helper = getNamespaceHelper();
        Element parent = helper.getDocument().getDocumentElement();
        Element[] elements = helper.getChildren(parent);

        for (int i = this.maximalNumberOfEntries; i < elements.length; i++) {

            // remove the backup file associated with this entry
            String time = getChildValue(helper, elements[i], ELEMENT_TIME);
            long timeLong = Long.valueOf(time).longValue();
            deleteBackup(this.node.getContentSource(), timeLong);
            deleteBackup(this.node.getMetaSource(), timeLong);

            parent.removeChild(elements[i]);
        }
        setDirty();
    }

    protected void deleteBackup(SourceWrapper wrapper, long time) throws ServiceException,
            MalformedURLException, IOException {
        String uri = getBackupSourceUri(wrapper, time);
        SourceUtil.delete(uri, this.manager);
        SourceUtil.deleteEmptyCollections(uri, this.manager);
    }

    /**
     * Get a clone document
     * @return org.w3c.dom.Document The clone document
     * @throws Exception if an error occurs
     */
    public org.w3c.dom.Document getDOMDocumentClone() throws Exception {
        Document documentClone = DocumentHelper.createDocument(null, "dummy", null);
        documentClone.removeChild(documentClone.getDocumentElement());
        documentClone.appendChild(documentClone
                .importNode(getDocument().getDocumentElement(), true));

        return documentClone;
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
        this.lastModified = new Date().getTime();
    }

    /**
     * Set the value dirty to false
     */
    protected void clearDirty() {
        this.dirty = false;
    }

    /**
     * Delete the latest check in
     * @throws Exception if an error occurs
     */
    public void deleteFirstCheckIn() throws Exception {
        deleteLatestEntry(ELEMENT_CHECKIN);
    }

    /**
     * Delete the latest check in
     * @throws Exception if an error occurs
     */
    public void deleteFirstCheckOut() throws Exception {
        deleteLatestEntry(ELEMENT_CHECKOUT);
    }

    protected void deleteLatestEntry(String type) throws RevisionControlException, Exception {
        NamespaceHelper helper = getNamespaceHelper();
        Element parent = helper.getDocument().getDocumentElement();
        Element element = getLatestElement(helper, type);
        parent.removeChild(element);
        // parent.removeChild(parent.getFirstChild()); // remove EOL (end of
        // line)
        setDirty();
    }

    /**
     * get the time's value of the backups
     * @return String[] the times
     * @throws Exception if an error occurs
     */
    public String[] getBackupsTime() throws Exception {
        NamespaceHelper helper = getNamespaceHelper();
        Element parent = helper.getDocument().getDocumentElement();
        Element[] elements = helper.getChildren(parent, ELEMENT_CHECKIN);

        ArrayList times = new ArrayList();
        for (int i = 0; i < elements.length; i++) {
            String time = getChildValue(helper, elements[i], ELEMENT_TIME);
            if (helper.getChildren(elements[i], ELEMENT_BACKUP).length > 0) {
                times.add(time);
            }
        }
        return (String[]) times.toArray(new String[times.size()]);

    }

    /**
     * Delete the revisions, the RCML source and the collection if the latter is
     * empty.
     * @return boolean true, if the file was deleted
     */
    public boolean delete() {
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
    public void deleteRevisions() throws RevisionControlException {
        try {
            String[] times = getBackupsTime();
            for (int i = 0; i < times.length; i++) {
                long time = new Long(times[i]).longValue();
                deleteBackup(this.node.getContentSource(), time);
                deleteBackup(this.node.getMetaSource(), time);
            }
        } catch (Exception e) {
            throw new RevisionControlException(e);
        }
    }

    public void copyFrom(RCML otherRcml) throws RevisionControlException {

        SourceNodeRCML other = (SourceNodeRCML) otherRcml;

        try {

            Vector backupEntries = other.getBackupEntries();
            for (Iterator i = backupEntries.iterator(); i.hasNext();) {
                RCMLEntry entry = (RCMLEntry) i.next();
                long time = entry.getTime();
                String otherContentUri = other.getBackupSourceUri(other.node.getContentSource(),
                        time);
                String thisContentUri = this.getBackupSourceUri(this.node.getContentSource(), time);
                SourceUtil.copy(this.manager, otherContentUri, thisContentUri);
                String otherMetaUri = other.getBackupSourceUri(other.node.getMetaSource(), time);
                String thisMetaUri = this.getBackupSourceUri(this.node.getMetaSource(), time);
                SourceUtil.copy(this.manager, otherMetaUri, thisMetaUri);
            }

            this.xml = other.getDocument();
            write();
        } catch (Exception e) {
            throw new RevisionControlException(e);
        }
    }

}