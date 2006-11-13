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

import java.io.FileNotFoundException;
import java.util.Date;

import org.apache.lenya.cms.repository.Node;
import org.apache.log4j.Logger;

/**
 * Controller for the reserved check-in, check-out, the backup versions and the rollback
 */
public class RevisionController {
    private static Logger log = Logger.getLogger(RevisionController.class);

    /**
     * <code>systemUsername</code> The system user name. This is used for - creating dummy checkin
     * events in a new RCML file when it is created on-the-fly - system override on checkin, i.e.
     * you can force a checkin into the repository if you use this username as identity parameter to
     * reservedCheckIn()
     */
    public static final String systemUsername = "System";

    /**
     * Creates a new RevisionController object.
     */
    public RevisionController() {
    }

    /**
     * Returns the latest version of a source.
     * @param node The node to control.
     * @return A version number.
     * @throws Exception if an error occurs.
     */
    public int getLatestVersion(Node node) throws Exception {
        RCML rcml = node.getRcml();
        CheckInEntry entry = rcml.getLatestCheckInEntry();
        int version = 0;
        if (entry != null) {
            version = entry.getVersion();
        }
        return version;
    }

    /**
     * Try to make a reserved check out of the file source for a user with identity
     * 
     * @param node The node to check out
     * @param identity The identity of the user
     * @throws Exception if an error occurs
     */
    public void reservedCheckOut(Node node, String identity) throws Exception {

        RCML rcml = node.getRcml();

        RCMLEntry entry = rcml.getLatestEntry();

        // The same user is allowed to check out repeatedly without
        // having to check back in first.
        //
        if (entry != null) {
            log.debug("entry: " + entry);
            log.debug("entry.type:" + entry.getType());
            log.debug("entry.identity" + entry.getIdentity());
        }

        if ((entry != null) && (entry.getType() != RCML.ci)
                && !entry.getIdentity().equals(identity)) {
            throw new FileReservedCheckOutException(node.getSourceURI(), rcml);
        }

        rcml.checkOutIn(RCML.co, identity, new Date().getTime(), false);
    }

    /**
     * Checks if a source can be checked out.
     * @param node The node.
     * @param identity The identity who requests checking out.
     * @return A boolean value.
     * @throws Exception when something went wrong.
     */
    public boolean canCheckOut(Node node, String identity) throws Exception {
        RCML rcml = node.getRcml();

        RCMLEntry entry = rcml.getLatestEntry();

        // The same user is allowed to check out repeatedly without
        // having to check back in first.
        //
        if (entry != null) {
            log.debug("entry: " + entry);
            log.debug("entry.type:" + entry.getType());
            log.debug("entry.identity" + entry.getIdentity());
        }

        boolean checkedOutByOther = entry != null && entry.getType() != RCML.ci
                && !entry.getIdentity().equals(identity);

        return !checkedOutByOther;
    }

    /**
     * @param node A node.
     * @return If the node is checked out.
     * @throws Exception if an error occurs.
     */
    public boolean isCheckedOut(Node node) throws Exception {
        RCML rcml = node.getRcml();

        RCMLEntry entry = rcml.getLatestEntry();

        // The same user is allowed to check out repeatedly without
        // having to check back in first.
        //
        if (entry != null) {
            log.debug("entry: " + entry);
            log.debug("entry.type:" + entry.getType());
            log.debug("entry.identity" + entry.getIdentity());
        }
        return entry != null && entry.getType() == RCML.co;
    }

    /**
     * Try to make a reserved check in of the file destination for a user with identity. A backup
     * copy can be made.
     * 
     * @param node The node to control.
     * @param identity The identity of the user
     * @param backup if true, a backup will be created, else no backup will be made.
     * @param newVersion If true, a new version will be created.
     * 
     * @return long The time.
     * 
     * @exception FileReservedCheckInException if the document couldn't be checked in (for instance
     *                because it is already checked out by someone other ...)
     * @exception Exception if other problems occur
     */
    public long reservedCheckIn(Node node, String identity, boolean backup,
            boolean newVersion) throws FileReservedCheckInException, Exception {

        RCML rcml;
        long time = new Date().getTime();

        rcml = node.getRcml();

        CheckOutEntry coe = rcml.getLatestCheckOutEntry();
        CheckInEntry cie = rcml.getLatestCheckInEntry();

        // If there has never been a checkout for this object
        // *or* if the user attempting the checkin right now
        // is the system itself, we will skip any checks and proceed
        // right away to the actual checkin.
        // In all other cases we enforce the revision control
        // rules inside this if clause:
        //
        if (!((coe == null) || identity.equals(RevisionController.systemUsername))) {
            /*
             * Possible cases and rules:
             * 
             * 1.) we were able to read the latest checkin and it is later than latest checkout
             * (i.e. there is no open checkout to match this checkin, an unusual case) 1.1.)
             * identity of latest checkin is equal to current user -> checkin allowed, same user
             * may check in repeatedly 1.2.) identity of latest checkin is not equal to current
             * user -> checkin rejected, may not overwrite the revision which another user
             * checked in previously 2.) there was no checkin or the latest checkout is later
             * than latest checkin (i.e. there is an open checkout) 2.1.) identity of latest
             * checkout is equal to current user -> checkin allowed, user checked out and may
             * check in again (the most common case) 2.2.) identity of latest checkout is not
             * equal to current user -> checkin rejected, may not check in while another user is
             * working on this document
             *  
             */
            if ((cie != null) && (cie.getTime() > coe.getTime())) {
                // We have case 1
                if (!cie.getIdentity().equals(identity)) {
                    // Case 1.2., abort...
                    //
                    throw new FileReservedCheckInException(node.getSourceURI(), rcml);
                }
            } else {
                // Case 2
                if (!coe.getIdentity().equals(identity)) {
                    // Case 2.2., abort...
                    //
                    throw new FileReservedCheckInException(node.getSourceURI(), rcml);
                }
            }
        }
        
        rcml.makeBackup(time);

        if (newVersion) {
            rcml.checkOutIn(RCML.ci, identity, time, backup);
        } else {
            rcml.deleteFirstCheckOut();
        }
        rcml.pruneEntries();
        rcml.write();

        return time;
    }

    /**
     * Rolls back to the given point in time.
     * @param node The node which will be rolled back
     * @param identity The identity of the user
     * @param backupFlag If true, a backup of the current version will be made before the rollback
     * @param time The time point of the desired version
     * @return long The time of the version to roll back to.
     * @exception FileReservedCheckInException if the current version couldn't be checked in again
     * @exception FileReservedCheckOutException if the current version couldn't be checked out
     * @exception FileNotFoundException if a file couldn't be found
     * @exception Exception if another problem occurs
     */
    public long rollback(Node node, String identity, boolean backupFlag, long time)
            throws Exception {

        // Make sure the old version exists
        RCML rcml = node.getRcml();

        // Try to check out current version
        reservedCheckOut(node, identity);
        rcml.restoreBackup(time);

        // Try to check back in, this might cause
        // a backup of the current version to be created if
        // desired by the user.
        //XXX:  what is the use of a backup if doc isn't versioned, can't rollback?
        //long newtime = reservedCheckIn(destination, identity, backupFlag, false);
        long newtime = reservedCheckIn(node, identity, backupFlag, backupFlag);

        return newtime;
    }

    /**
     * Delete the check in and roll back the file to the backup at time
     * @param time The time point of the back version we want to retrieve
     * @param node The node for which we want undo the check in
     * @exception Exception FileNotFoundException if the back version or the current version
     *                couldn't be found
     */
    public void undoCheckIn(long time, Node node) throws Exception {
        RCML rcml = node.getRcml();
        rcml.restoreBackup(time);
        rcml.deleteFirstCheckIn();
    }

    /**
     * delete the revisions
     * @param node of the document
     * @throws RevisionControlException when somthing went wrong
     */
    public void deleteRevisions(Node node) throws RevisionControlException {
        node.getRcml().deleteRevisions();
    }

    /**
     * delete the rcml file
     * @param node of the document
     * @throws RevisionControlException if something went wrong
     */
    public void deleteRCML(Node node) throws RevisionControlException {
        try {
            RCML rcml = node.getRcml();
            boolean deleted = rcml.delete();
            if (!deleted) {
                throw new RevisionControlException("The rcml file could not be deleted!");
            }
        } catch (Exception e) {
            throw new RevisionControlException(e);
        }
    }

}