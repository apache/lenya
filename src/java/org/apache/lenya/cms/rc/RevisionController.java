/*
 * $Id: RevisionController.java,v 1.14 2003/04/24 13:52:59 gregor Exp $
 * <License>
 * The Apache Software License
 *
 * Copyright (c) 2002 lenya. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice, this
 *    list of conditions and the following disclaimer in the documentation and/or
 *    other materials provided with the distribution.
 *
 * 3. All advertising materials mentioning features or use of this software must
 *    display the following acknowledgment: "This product includes software developed
 *    by lenya (http://www.lenya.org)"
 *
 * 4. The name "lenya" must not be used to endorse or promote products derived from
 *    this software without prior written permission. For written permission, please
 *    contact contact@lenya.org
 *
 * 5. Products derived from this software may not be called "lenya" nor may "lenya"
 *    appear in their names without prior written permission of lenya.
 *
 * 6. Redistributions of any form whatsoever must retain the following acknowledgment:
 *    "This product includes software developed by lenya (http://www.lenya.org)"
 *
 * THIS SOFTWARE IS PROVIDED BY lenya "AS IS" WITHOUT ANY WARRANTY EXPRESS OR IMPLIED,
 * INCLUDING THE WARRANTY OF NON-INFRINGEMENT AND THE IMPLIED WARRANTIES OF MERCHANTI-
 * BILITY AND FITNESS FOR A PARTICULAR PURPOSE. lenya WILL NOT BE LIABLE FOR ANY DAMAGES
 * SUFFERED BY YOU AS A RESULT OF USING THIS SOFTWARE. IN NO EVENT WILL lenya BE LIABLE
 * FOR ANY SPECIAL, INDIRECT OR CONSEQUENTIAL DAMAGES OR LOST PROFITS EVEN IF lenya HAS
 * BEEN ADVISED OF THE POSSIBILITY OF THEIR OCCURRENCE. lenya WILL NOT BE LIABLE FOR ANY
 * THIRD PARTY CLAIMS AGAINST YOU.
 *
 * Lenya includes software developed by the Apache Software Foundation, W3C,
 * DOM4J Project, BitfluxEditor and Xopus.
 * </License>
 */
package org.apache.lenya.cms.rc;

import org.apache.log4j.Category;

import org.apache.lenya.util.XPSFileOutputStream;
import org.apache.lenya.cms.publishing.PublishingEnvironment;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import java.util.Date;


/**
 * DOCUMENT ME!
 *
 * @author Michael Wechner (http://www.lenya.com)
 * @version 1.5.1
 */
public class RevisionController {
    static Category log = Category.getInstance(RevisionController.class);

    // System username. This is used for 
    // - creating dummy checkin events in a new RCML file
    //   when it is created on-the-fly
    // - system override on checkin, i.e. you can force
    //   a checkin into the repository if you use this
    //   username as identity parameter to reservedCheckIn()
    //
    public static final String systemUsername = "System";
    String rcmlDir = null;
    String rootDir = null;
    String backupDir = null;

    /**
     * Creates a new RevisionController object.
     */
    public RevisionController() {
        Configuration conf = new Configuration();
        rcmlDir = conf.rcmlDirectory;
        backupDir = conf.backupDirectory;
        rootDir = "conf.rootDirectory";
    }

    /**
     * Creates a new RevisionController object.
     *
     * @param rcmlDir DOCUMENT ME!
     * @param backupDirectory DOCUMENT ME!
     */
    public RevisionController(String rcmlDirectory, String backupDirectory, String rootDirectory) {
        this.rcmlDir = rcmlDirectory;
        this.backupDir = backupDirectory;
        this.rootDir = rootDirectory;
    }

    /**
     * Creates a new RevisionController object.
     *
     * @param rootDir DOCUMENT ME!
     */
    public RevisionController(String rootDir) {
        this();
        this.rootDir = rootDir;
    }

    /**
     * DOCUMENT ME!
     *
     * @param args DOCUMENT ME!
     */
/* FIXME
    public static void main(String[] args) {
        if (args.length != 4) {
            log.info("Usage: " + new RevisionController().getClass().getName() +
                " username(user who checkout) source(filename without the rootDirectory of the document to checkout) username(user who checkin) destination(filename without the rootDirectory of document to checkin)");

            return;
        }

        String identityS = args[0];
        String source = args[1];
        String identityD = args[2];
        String destination = args[3];
        RevisionController rc = new RevisionController();
        File in = null;

        try {
            in = rc.reservedCheckOut(source, identityS);
        } catch (FileNotFoundException e) // No such source file
         {
            log.error(e);
        } catch (FileReservedCheckOutException e) // Source has been checked out already
         {
            log.error(e);
            log.error(e.source + "is already check out by " + e.checkOutUsername + " since " +
                e.checkOutDate);
            return;

        } catch (IOException e) { // Cannot create rcml file
            log.error(e);
            return;

        } catch (Exception e) {
            log.error(e);
            return;
        }

        try {
            rc.reservedCheckIn(destination, identityD, true);
        } catch (FileReservedCheckInException e) {
            log.error(e);
        } catch (Exception e) {
            log.error(e);
        }
    }
*/
    /**
     * Shows Configuration
     *
     * @return DOCUMENT ME!
     */
    public String toString() {
        return "rcmlDir=" + rcmlDir + " , rcbakDir=" + backupDir + " , rootDir=" + rootDir;
    }

    /**
     * Get the RCML File for the file source
     *
     * @param source The filename of a document.
     *
     * @return RCML The corresponding RCML file.
     *
     * @throws FileNotFoundException DOCUMENT ME!
     * @throws IOException DOCUMENT ME!
     * @throws Exception DOCUMENT ME!
     */
    public RCML getRCML(String source) throws FileNotFoundException, IOException, Exception {
        File file = new File(rootDir + source);

        if (!file.isFile()) {
            throw new FileNotFoundException(file.getAbsolutePath());
        }

        return new RCML(rcmlDir, source, rootDir);
    }

    /**
     * Try to make a reserved check out of the file source for a user with identity
     *
     * @param source The filename of the file to check out
     * @param identity The identity of the user
     *
     * @return File File to check out
     *
     * @exception FileNotFoundException if the file couldn't be found
     * @exception FileReservedCheckOutException if the document is already checked out by another
     *            user
     * @throws IOException DOCUMENT ME!
     * @exception Exception if another problem occurs
     */
    public File reservedCheckOut(String source, String identity)
        throws FileNotFoundException, FileReservedCheckOutException, IOException, Exception {
        File file = new File(rootDir + source);

        if (!file.isFile()) {
            throw new FileNotFoundException(file.getAbsolutePath());
        }

        RCML rcml = new RCML(rcmlDir, source, rootDir);

        RCMLEntry entry = rcml.getLatestEntry();

        // The same user is allowed to check out repeatedly without
        // having to check back in first.
        //
        log.debug("entry: " + entry);
        log.debug("entry.type:" + entry.type);
        log.debug("entry.identity" + entry.identity);

        if ((entry != null) && (entry.type != RCML.ci) && !entry.identity.equals(identity)) {
            throw new FileReservedCheckOutException(rootDir+source, rcml);
        }

        rcml.checkOutIn(RCML.co, identity, new Date().getTime());

        return file;
    }

    /**
     * Try to make a reserved check in of the file destination for a user with identity. A backup
     * copy can be made.
     *
     * @param destination The file we want to check in
     * @param identity The identity of the user
     * @param backup if true, a backup will be created, else no backup will be made.
     *
     * @return DOCUMENT ME!
     *
     * @exception FileReservedCheckInException if the document couldn't be checked in (for instance
     *            because it is already checked out by someone other ...)
     * @exception Exception if other problems occur
     */
    public long reservedCheckIn(String destination, String identity, boolean backup)
        throws FileReservedCheckInException, Exception {
        RCML rcml = new RCML(rcmlDir, destination, rootDir);

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
             *     (i.e. there is no open checkout to match this checkin, an unusual case)
             *     1.1.) identity of latest checkin is equal to current user
             *           -> checkin allowed, same user may check in repeatedly
             *     1.2.) identity of latest checkin is not equal to current user
             *           -> checkin rejected, may not overwrite the revision which
             *              another user checked in previously
             * 2.) there was no checkin or the latest checkout is later than latest checkin
             *     (i.e. there is an open checkout)
             *     2.1.) identity of latest checkout is equal to current user
             *           -> checkin allowed, user checked out and may check in again
             *              (the most common case)
             *     2.2.) identity of latest checkout is not equal to current user
             *           -> checkin rejected, may not check in while another
             *              user is working on this document
             *
             */
            if ((cie != null) && (cie.time > coe.time)) {
                // We have case 1
                if (!cie.identity.equals(identity)) {
                    // Case 1.2., abort...
                    //
                    throw new FileReservedCheckInException(rootDir+destination, rcml);
                }
            } else {
                // Case 2
                if (!coe.identity.equals(identity)) {
                    // Case 2.2., abort...
                    //
                    throw new FileReservedCheckInException(rootDir+destination, rcml);
                }
            }
        }

        File originalFile = new File(rootDir + destination);
        long time = new Date().getTime();

        if (backup && originalFile.isFile()) {
            File backupFile = new File(backupDir + "/" + destination + ".bak." + time);
            File parent = new File(backupFile.getParent());

            if (!parent.isDirectory()) {
                parent.mkdirs();
            }

            log.info("Backup: copy " + originalFile.getAbsolutePath() + " to " +
                backupFile.getAbsolutePath());

            InputStream in = new FileInputStream(originalFile.getAbsolutePath());

            OutputStream out = new XPSFileOutputStream(backupFile.getAbsolutePath());
            byte[] buffer = new byte[512];
            int length;

            while ((length = in.read(buffer)) != -1) {
                out.write(buffer, 0, length);
            }

            out.close();
        }

        rcml.checkOutIn(RCML.ci, identity, time);
        rcml.write();

	// FIXME: If we reuse the observer pattern as implemented in
	// xps this would be the place to notify the observers,
	// e.g. like so:
// 	StatusChangeSignalHandler.emitSignal("file:" + originalFile.getAbsolutePath(),
// 					     "reservedCheckIn");
        return time;
    }

    public String getBackupFilename(long time, String filename) {
        File backup = new File(backupDir + "/" + filename + ".bak." + time);

        return backup.getAbsolutePath();
    }

    /**
     * Rolls back to the given point in time
     *
     * @param destination File which will be rolled back
     * @param identity The identity of the user
     * @param backupFlag If true, a backup of the current version will be made before the rollback
     * @param time The time point of the desired version
     *
     * @return DOCUMENT ME!
     *
     * @exception FileReservedCheckInException if the current version couldn't be checked in again
     * @exception FileReservedCheckOutException if the current version couldn't be checked out
     * @exception FileNotFoundException if a file couldn't be found
     * @exception Exception if another problem occurs
     */
    public long rollback(String destination, String identity, boolean backupFlag, long time)
        throws FileReservedCheckInException, FileReservedCheckOutException, FileNotFoundException, 
            Exception {
        // Make sure the old version exists
        //
        File backup = new File(backupDir + "/" + destination + ".bak." + time);
        File current = new File(rootDir + destination);

        if (!backup.isFile()) {
            throw new FileNotFoundException(backup.getAbsolutePath());
        }

        if (!current.isFile()) {
            throw new FileNotFoundException(current.getAbsolutePath());
        }

        // Try to check out current version
        //
        reservedCheckOut(destination, identity);

        // Try to check back in, this might cause
        // a backup of the current version to be created if
        // desired by the user.
        //
        long newtime = reservedCheckIn(destination, identity, backupFlag);

        // Now roll back to the old state
        //
        FileInputStream in = new FileInputStream(backup.getAbsolutePath());

        XPSFileOutputStream out = new XPSFileOutputStream(current.getAbsolutePath());
        byte[] buffer = new byte[512];
        int length;

        while ((length = in.read(buffer)) != -1) {
            out.write(buffer, 0, length);
        }

        out.close();

        return newtime;
    }

    /**
     * Delete the check in and roll back the file to the backup at time
     *
     * @param time The time point of the back version we want to retrieve
     * @param destination The File for which we want undo the check in
     *
     * @exception Exception FileNotFoundException if the back  version or the current version
     *            couldn't be found
     * @throws FileNotFoundException DOCUMENT ME!
     */
    public void undoCheckIn(long time, String destination)
        throws Exception {
        File backup = new File(backupDir + "/" + destination + ".bak." + time);
        File current = new File(rootDir + destination);

        RCML rcml = new RCML(rcmlDir, destination, rootDir);

        if (!backup.isFile()) {
            throw new FileNotFoundException(backup.getAbsolutePath());
        }

        if (!current.isFile()) {
            throw new FileNotFoundException(current.getAbsolutePath());
        }

        FileInputStream in = new FileInputStream(backup.getAbsolutePath());

        XPSFileOutputStream out = new XPSFileOutputStream(current.getAbsolutePath());
        byte[] buffer = new byte[512];
        int length;

        while ((length = in.read(buffer)) != -1) {
            out.write(buffer, 0, length);
        }

        log.info("Undo: copy " + backup.getAbsolutePath() + " " + current.getAbsolutePath());

        rcml.deleteFirstCheckIn();
        out.close();
    }
}
