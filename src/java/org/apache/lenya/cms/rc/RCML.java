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

/* $Id$  */

package org.apache.lenya.cms.rc;

import java.io.IOException;
import java.util.Vector;

import javax.xml.parsers.ParserConfigurationException;

/**
 * Handle with the RCML file
 */
public interface RCML {

    /**
     * <code>co</code> Checkout
     */
    short co = 0;
    /**
     * <code>ci</code> Checkin
     */
    short ci = 1;

    /**
     * initialise the RCML-document. Delete all entries
     * @throws ParserConfigurationException
     */
    public void initDocument() throws ParserConfigurationException ;

    /**
     * Write the xml RCML-document in the RCML-file.
     * @throws IOException if an error occurs
     * @throws Exception if an error occurs
     */
    public void write() throws Exception;

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
            throws Exception;

    /**
     * get the latest check out
     * @return CheckOutEntry The entry of the check out
     * @throws Exception if an error occurs
     */
    public CheckOutEntry getLatestCheckOutEntry() throws Exception;

    /**
     * get the latest check in
     * @return CheckInEntry The entry of the check in
     * @throws Exception if an error occurs
     */
    public CheckInEntry getLatestCheckInEntry() throws Exception;

    /**
     * get the latest entry (a check out or check in)
     * @return RCMLEntry The entry of the check out/in
     * @throws Exception if an error occurs
     */
    public RCMLEntry getLatestEntry() throws Exception;

    /**
     * get all check in and check out
     * @return Vector of all check out and check in entries in this RCML-file
     * @throws Exception if an error occurs
     */
    public Vector getEntries() throws Exception;

    /**
     * get all backup entries
     * @return Vector of all entries in this RCML-file with a backup
     * @throws Exception if an error occurs
     */
    public Vector getBackupEntries() throws Exception;
    
    /**
     * Creates a backup.
     * @param time The time.
     * @throws RevisionControlException
     */
    public void makeBackup(long time) throws RevisionControlException;

    /**
     * Restores a backup.
     * @param time The time.
     * @throws RevisionControlException
     */
    public void restoreBackup(long time) throws RevisionControlException;

    /**
     * Prune the list of entries and delete the corresponding backups. Limit the number of entries
     * to the value maximalNumberOfEntries (2maxNumberOfRollbacks(configured)+1)
     * @param backupDir The backup directory
     * @throws Exception if an error occurs
     */
    public void pruneEntries() throws Exception;

    /**
     * Get a clone document
     * @return org.w3c.dom.Document The clone document
     * @throws Exception if an error occurs
     */
    public org.w3c.dom.Document getDOMDocumentClone() throws Exception;

    /**
     * Check if the document is dirty
     * @return boolean dirty
     */
    public boolean isDirty();

    /**
     * Delete the latest check in
     * @throws Exception if an error occurs
     */
    public void deleteFirstCheckIn() throws Exception;

    /**
     * Delete the latest check in
     * @throws Exception if an error occurs
     */
    public void deleteFirstCheckOut() throws Exception;

    /**
     * get the time's value of the backups
     * @return String[] the times
     * @throws Exception if an error occurs
     */
    public String[] getBackupsTime() throws Exception;

    /**
     * delete the rcml file and the directory if this one is empty
     * @return boolean true, if the file was deleted
     */
    public boolean delete();
    
    /**
     * Delete all revisions.
     * @throws RevisionControlException if an error occurs.
     */
    void deleteRevisions() throws RevisionControlException;
}