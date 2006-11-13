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
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import javax.xml.parsers.ParserConfigurationException;

import org.apache.avalon.framework.service.ServiceManager;
import org.apache.lenya.cms.cocoon.source.SourceUtil;
import org.apache.lenya.cms.rc.CheckInEntry;
import org.apache.lenya.cms.rc.CheckOutEntry;
import org.apache.lenya.cms.rc.RCML;
import org.apache.lenya.cms.rc.RCMLEntry;
import org.apache.lenya.cms.rc.RevisionControlException;
import org.apache.lenya.cms.rc.RevisionController;
import org.apache.lenya.xml.DocumentHelper;
import org.apache.lenya.xml.NamespaceHelper;
import org.apache.xpath.XPathAPI;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Handle with the RCML file
 */
public class SourceNodeRCML implements RCML {


    private SourceNode node;
    private Document document = null;
    private boolean dirty = false;
    private int maximalNumberOfEntries = 5;
    
    private ServiceManager manager;

    private static Map ELEMENTS = new HashMap();
    protected static final String ELEMENT_CHECKIN = "CheckIn";
    protected static final String ELEMENT_CHECKOUT = "CheckOut";
    protected static final String ELEMENT_BACKUP = "Backup";

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

        if (!SourceUtil.exists(getRcmlSourceUri(), manager)) {
            // The rcml file does not yet exist, so we create it now...
            //
            long lastModified = 0;
            if (node.exists()) {
                lastModified = node.getLastModified();
            }

            initDocument();

            // Create a "fake" checkin entry so it looks like the
            // system checked the document in. We use the filesystem
            // modification date as checkin time.
            //
            checkOutIn(SourceNodeRCML.ci, RevisionController.systemUsername, lastModified, false);

            write();
        } else {
            try {
                this.document = SourceUtil.readDOM(getRcmlSourceUri(), this.manager);
            } catch (Exception e) {
                throw new RevisionControlException("Could not read RC file ["
                        + getRcmlSourceUri() + "]");
            }
        }
    }
    
    protected static final String RCML_EXTENSION = ".rcml";
    
    protected String getRcmlSourceUri() {
        return this.node.getRealSourceURI() + RCML_EXTENSION;
    }

    /**
     * initialise the RCML-document. Delete all entries
     * @throws ParserConfigurationException
     */
    public void initDocument() throws ParserConfigurationException {
        this.document = DocumentHelper.createDocument(null, "XPSRevisionControl", null);
    }

    /**
     * Call the methode write, if the document is dirty
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
     * Write the xml RCML-document in the RCML-file.
     * @throws IOException if an error occurs
     * @throws Exception if an error occurs
     */
    public void write() throws Exception {
        if (this.document == null) {
            throw new IllegalStateException("The XML for RC source [" + getRcmlSourceUri() + "] is null!");
        }
        SourceUtil.writeDOM(this.document, getRcmlSourceUri(), this.manager);
        clearDirty();
    }

    /**
     * Write a new entry for a check out or a check in the RCML-File made by the user with identity
     * at time
     * @param type co for a check out, ci for a check in
     * @param identity The identity of the user
     * @param time Time at which the check in/out is made
     * @param backup Create backup element
     * @throws IOException if an error occurs
     * @throws Exception if an error occurs
     */
    public void checkOutIn(short type, String identity, long time, boolean backup)
            throws IOException, Exception {
        
        if (identity == null) {
            throw new IllegalArgumentException("The identity must not be null!");
        }

        if (type != co && type != ci) {
            throw new IllegalArgumentException("ERROR: " + this.getClass().getName()
                    + ".checkOutIn(): No such type");
        }

        NamespaceHelper helper = new NamespaceHelper(null, "", this.document);

        Element identityElement = helper.createElement("Identity", identity);
        Element timeElement = helper.createElement("Time", "" + time);

        String elementName = (String) ELEMENTS.get(new Short(type));
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
            Element versionElement = helper.createElement("Version", "" + version);
            checkOutElement.appendChild(versionElement);
        }

        if (backup) {
            Element backupElement = helper.createElement(ELEMENT_BACKUP);
            checkOutElement.appendChild(backupElement);
        }

        Element root = this.document.getDocumentElement();
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
        if (type == co) {
            write();
        }
    }

    /**
     * get the latest check out
     * @return CheckOutEntry The entry of the check out
     * @throws Exception if an error occurs
     */
    public CheckOutEntry getLatestCheckOutEntry() throws Exception {
        Element parent = this.document.getDocumentElement();
        Node identity = null;
        Node time = null;
        String rcIdentity = null;

        identity = XPathAPI.selectSingleNode(parent,
                "/XPSRevisionControl/CheckOut[1]/Identity/text()");
        time = XPathAPI.selectSingleNode(parent, "/XPSRevisionControl/CheckOut[1]/Time/text()");

        if (identity == null && time == null) {
            // No checkout at all
            return null;
        }
        rcIdentity = identity.getNodeValue();
        long rcTime = new Long(time.getNodeValue()).longValue();

        return new CheckOutEntry(rcIdentity, rcTime);
    }

    /**
     * get the latest check in
     * @return CheckInEntry The entry of the check in
     * @throws Exception if an error occurs
     */
    public CheckInEntry getLatestCheckInEntry() throws Exception {
        Element parent = this.document.getDocumentElement();

        Node identity = XPathAPI.selectSingleNode(parent,
                "/XPSRevisionControl/CheckIn[1]/Identity/text()");
        Node time = XPathAPI.selectSingleNode(parent, "/XPSRevisionControl/CheckIn[1]/Time/text()");
        Node versionNode = XPathAPI.selectSingleNode(parent,
                "/XPSRevisionControl/CheckIn[1]/Version/text()");

        if (identity == null && time == null) {
            // No checkout at all
            return null;
        }
        String rcIdentity = identity.getNodeValue();
        long rcTime = new Long(time.getNodeValue()).longValue();
        int version = 0;
        if (versionNode != null) {
            version = new Integer(versionNode.getNodeValue()).intValue();
        }

        return new CheckInEntry(rcIdentity, rcTime, version);
    }

    /**
     * get the latest entry (a check out or check in)
     * @return RCMLEntry The entry of the check out/in
     * @throws Exception if an error occurs
     */
    public RCMLEntry getLatestEntry() throws Exception {
        CheckInEntry cie = getLatestCheckInEntry();
        CheckOutEntry coe = getLatestCheckOutEntry();

        if ((cie != null) && (coe != null)) {
            if (cie.getTime() > coe.getTime()) {
                return cie;
            }
            return coe;
        }

        if (cie != null) {
            return cie;
        }
        return coe;
    }

    /**
     * get all check in and check out
     * @return Vector of all check out and check in entries in this RCML-file
     * @throws Exception if an error occurs
     */
    public Vector getEntries() throws Exception {
        Element parent = this.document.getDocumentElement();
        NodeList entries = XPathAPI.selectNodeList(parent,
                "/XPSRevisionControl/CheckOut|/XPSRevisionControl/CheckIn");
        Vector RCMLEntries = new Vector();

        for (int i = 0; i < entries.getLength(); i++) {
            Element elem = (Element) entries.item(i);
            String time = elem.getElementsByTagName("Time").item(0).getFirstChild().getNodeValue();
            String identity = elem.getElementsByTagName("Identity")
                    .item(0)
                    .getFirstChild()
                    .getNodeValue();

            if (elem.getTagName().equals("CheckOut")) {
                RCMLEntries.add(new CheckOutEntry(identity, new Long(time).longValue()));
            } else {
                NodeList versionElements = elem.getElementsByTagName("Version");
                int version = 0;
                if (versionElements.getLength() > 0) {
                    String versionString = versionElements.item(0).getFirstChild().getNodeValue();
                    version = new Integer(versionString).intValue();
                }

                RCMLEntries.add(new CheckInEntry(identity, new Long(time).longValue(), version));
            }
        }

        return RCMLEntries;
    }

    /**
     * get all backup entries
     * @return Vector of all entries in this RCML-file with a backup
     * @throws Exception if an error occurs
     */
    public Vector getBackupEntries() throws Exception {
        Element parent = this.document.getDocumentElement();
        NodeList entries = XPathAPI.selectNodeList(parent, "/XPSRevisionControl/CheckOut["
                + ELEMENT_BACKUP + "]|/XPSRevisionControl/CheckIn[" + ELEMENT_BACKUP + "]");
        Vector RCMLEntries = new Vector();

        for (int i = 0; i < entries.getLength(); i++) {
            Element elem = (Element) entries.item(i);
            String time = elem.getElementsByTagName("Time").item(0).getFirstChild().getNodeValue();
            String identity = elem.getElementsByTagName("Identity")
                    .item(0)
                    .getFirstChild()
                    .getNodeValue();

            NodeList versionElements = elem.getElementsByTagName("Version");
            int version = 0;
            if (versionElements.getLength() > 0) {
                String versionString = versionElements.item(0).getFirstChild().getNodeValue();
                version = new Integer(versionString).intValue();
            }

            RCMLEntries.add(new CheckInEntry(identity, new Long(time).longValue(), version));
        }

        return RCMLEntries;
    }
    
    public void makeBackup(long time) throws RevisionControlException {
        String backupSourceUri = getBackupSourceUri(time);
        try {
            SourceUtil.copy(this.manager, this.node.getRealSourceURI(), backupSourceUri);
        } catch (Exception e) {
            throw new RevisionControlException(e);
        }
    }

    public void restoreBackup(long time) throws RevisionControlException {
        String backupSourceUri = getBackupSourceUri(time);
        try {
            SourceUtil.copy(this.manager, backupSourceUri, this.node.getRealSourceURI());
        } catch (Exception e) {
            throw new RevisionControlException(e);
        }
    }

    protected String getBackupSourceUri(long time) {
        String backupSourceUri = this.node.getRealSourceURI() + "." + time + ".bak";
        return backupSourceUri;
    }

    /**
     * Prune the list of entries and delete the corresponding backups. Limit the number of entries
     * to the value maximalNumberOfEntries (2maxNumberOfRollbacks(configured)+1)
     * @param backupDir The backup directory
     * @throws Exception if an error occurs
     */
    public void pruneEntries() throws Exception {
        Element parent = this.document.getDocumentElement();
        NodeList entries = XPathAPI.selectNodeList(parent,
                "/XPSRevisionControl/CheckOut|/XPSRevisionControl/CheckIn");

        for (int i = this.maximalNumberOfEntries; i < entries.getLength(); i++) {
            Element current = (Element) entries.item(i);

            // remove the backup file associated with this entry
            String time = current.getElementsByTagName("Time")
                    .item(0)
                    .getFirstChild()
                    .getNodeValue();
            SourceUtil.delete(getBackupSourceUri(Long.valueOf(time).longValue()), this.manager);
            // remove the entry from the list
            current.getParentNode().removeChild(current);
        }
    }

    /**
     * Get a clone document
     * @return org.w3c.dom.Document The clone document
     * @throws Exception if an error occurs
     */
    public org.w3c.dom.Document getDOMDocumentClone() throws Exception {
        Document documentClone = DocumentHelper.createDocument(null, "dummy", null);
        documentClone.removeChild(documentClone.getDocumentElement());
        documentClone.appendChild(documentClone.importNode(this.document.getDocumentElement(), true));

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
        Node root = this.document.getDocumentElement();
        Node firstCheckIn = XPathAPI.selectSingleNode(root, "/XPSRevisionControl/CheckIn[1]");
        root.removeChild(firstCheckIn);
        root.removeChild(root.getFirstChild()); // remove EOL (end of line)
        setDirty();
    }

    /**
     * Delete the latest check in
     * @throws Exception if an error occurs
     */
    public void deleteFirstCheckOut() throws Exception {
        Node root = this.document.getDocumentElement();
        Node firstCheckIn = XPathAPI.selectSingleNode(root, "/XPSRevisionControl/CheckOut[1]");
        root.removeChild(firstCheckIn);
        root.removeChild(root.getFirstChild()); // remove EOL (end of line)
        setDirty();
    }

    /**
     * get the time's value of the backups
     * @return String[] the times
     * @throws Exception if an error occurs
     */
    public String[] getBackupsTime() throws Exception {
        Node root = this.document.getDocumentElement();
        NodeList entries = XPathAPI.selectNodeList(root, "/XPSRevisionControl/CheckIn");

        ArrayList times = new ArrayList();

        for (int i = 0; i < entries.getLength(); i++) {
            Element elem = (Element) entries.item(i);
            String time = elem.getElementsByTagName("Time").item(0).getFirstChild().getNodeValue();
            NodeList backupNodes = elem.getElementsByTagName(ELEMENT_BACKUP);
            if (backupNodes != null && backupNodes.getLength() > 0) {
                times.add(time);
            }
        }
        return (String[]) times.toArray(new String[times.size()]);

    }

    /**
     * delete the rcml file and the directory if this one is empty
     * @return boolean true, if the file was deleted
     */
    public boolean delete() {
        try {
            SourceUtil.delete(getRcmlSourceUri(), this.manager);
            SourceUtil.deleteEmptyCollections(getRcmlSourceUri(), this.manager);
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
        return true;
    }

    /**
     * delete the revisions
     * @param node of the document
     * @throws RevisionControlException when somthing went wrong
     */
    public void deleteRevisions() throws RevisionControlException {
        try {
            String[] times = getBackupsTime();
            for (int i = 0; i < times.length; i++) {
                long time = new Long(times[i]).longValue();
                String backupSourceUri = getBackupSourceUri(time);
                SourceUtil.delete(backupSourceUri, this.manager);
                SourceUtil.deleteEmptyCollections(backupSourceUri, this.manager);
            }
        } catch (Exception e) {
            throw new RevisionControlException(e);
        }
    }

}