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

import java.util.Vector;

import org.apache.lenya.cms.repository.Node;
import org.apache.lenya.cms.repository.Session;

/**
 * An object of this class handles the revisions of a node. The node is passed as a parameter so an
 * RCML object can be shared between sessions for synchronization purposes.
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
     * Check the RCML in.
     * @param node The node.
     * @param backup If a backup shall be created.
     * @param newVersion If the revision number should be increased.
     * @throws RevisionControlException if an error occurs.
     */
    void checkIn(Node node, boolean backup, boolean newVersion) throws RevisionControlException;

    /**
     * Check the RCML out with restriction to the current session.
     * @param node The node.
     * @throws RevisionControlException if an error occurs.
     */
    void checkOut(Node node) throws RevisionControlException;

    /**
     * Check the RCML out.
     * @param node The node.
     * @param restrictedToSession If only the current session may check the node in, or all sessions
     *        belonging to this user.
     * @throws RevisionControlException if an error occurs.
     */
    void checkOut(Node node, boolean restrictedToSession) throws RevisionControlException;

    /**
     * get the latest check out
     * @return CheckOutEntry The entry of the check out
     * @throws RevisionControlException if an error occurs
     */
    CheckOutEntry getLatestCheckOutEntry() throws RevisionControlException;

    /**
     * get the latest check in
     * @return CheckInEntry The entry of the check in
     * @throws RevisionControlException if an error occurs
     */
    CheckInEntry getLatestCheckInEntry() throws RevisionControlException;

    /**
     * get the latest entry (a check out or check in)
     * @return RCMLEntry The entry of the check out/in
     * @throws RevisionControlException if an error occurs
     */
    RCMLEntry getLatestEntry() throws RevisionControlException;

    /**
     * get all check in and check out
     * @return Vector of all check out and check in entries in this RCML-file
     * @throws RevisionControlException if an error occurs
     */
    Vector getEntries() throws RevisionControlException;

    /**
     * get all backup entries
     * @return Vector of all entries in this RCML-file with a backup
     * @throws Exception if an error occurs
     */
    Vector getBackupEntries() throws Exception;

    /**
     * Creates a backup.
     * @param time The time.
     * @throws RevisionControlException
     */
    void makeBackup(long time) throws RevisionControlException;

    /**
     * Restores a backup.
     * @param node The node to restore the backup to.
     * @param time The time.
     * @throws RevisionControlException
     */
    void restoreBackup(Node node, long time) throws RevisionControlException;

    /**
     * Prune the list of entries and delete the corresponding backups. Limit the number of entries
     * to the value maximalNumberOfEntries (2maxNumberOfRollbacks(configured)+1)
     * @throws Exception if an error occurs
     */
    void pruneEntries() throws Exception;

    /**
     * Check if the document is dirty
     * @return boolean dirty
     */
    boolean isDirty();

    /**
     * get the time's value of the backups
     * @return String[] the times
     * @throws Exception if an error occurs
     */
    String[] getBackupsTime() throws Exception;

    /**
     * delete the RCML file and the directory if this one is empty
     * @return boolean true, if the file was deleted
     */
    boolean delete();

    /**
     * Delete all revisions.
     * @throws RevisionControlException if an error occurs.
     */
    void deleteRevisions() throws RevisionControlException;

    /**
     * @param node The target node.
     * @param otherNode The source node.
     * @throws RevisionControlException if an error occurs.
     */
    void copyFrom(Node node, Node otherNode) throws RevisionControlException;

    /**
     * @return if the RCML is checked out.
     * @throws RevisionControlException if an error occurs.
     */
    boolean isCheckedOut() throws RevisionControlException;

    /**
     * @param session The session.
     * @return if the RCML is checked out by this session.
     * @throws RevisionControlException if an error occurs.
     */
    boolean isCheckedOutBySession(Session session) throws RevisionControlException;

}
