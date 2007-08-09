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

import org.apache.avalon.framework.logger.AbstractLogEnabled;
import org.apache.avalon.framework.logger.Logger;
import org.apache.lenya.cms.repository.Node;

/**
 * Controller for the reserved check-in, check-out, the backup versions and the
 * rollback
 */
public class RevisionController extends AbstractLogEnabled {

    /**
     * <code>systemUsername</code> The system user name. This is used for -
     * creating dummy checkin events in a new RCML file when it is created
     * on-the-fly - system override on checkin, i.e. you can force a checkin
     * into the repository if you use this username as identity parameter to
     * reservedCheckIn()
     */
    public static final String systemUsername = "System";

    /**
     * Creates a new RevisionController object.
     * @param logger The logger.
     */
    public RevisionController(Logger logger) {
        enableLogging(logger);
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
     * Checks if a source can be checked out.
     * @param node The node.
     * @param identity The identity who requests checking out.
     * @return A boolean value.
     * @throws Exception when something went wrong.
     */
    public boolean canCheckOut(Node node, String identity) throws Exception {
        RCML rcml = node.getRcml();

        RCMLEntry entry = rcml.getLatestEntry();
        boolean checkedOutByOther = entry != null && entry.getType() != RCML.ci
                && !entry.getIdentity().equals(identity);
        return !checkedOutByOther;
    }

    /**
     * delete the revisions
     * @param node of the document
     * @throws RevisionControlException when something went wrong
     */
    public void deleteRevisions(Node node) throws RevisionControlException {
        node.getRcml().deleteRevisions();
    }

    /**
     * Delete the revision history.
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

    /**
     * Copies the revisions from one node to another.
     * @param source The source node.
     * @param destination The destination node.
     * @throws RevisionControlException if an error occurs.
     */
    public void copyRCML(Node source, Node destination) throws RevisionControlException {
        destination.getRcml().copyFrom(destination, source);
    }

}