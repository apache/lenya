/*
$Id
<License>

 ============================================================================
                   The Apache Software License, Version 1.1
 ============================================================================

 Copyright (C) 1999-2003 The Apache Software Foundation. All rights reserved.

 Redistribution and use in source and binary forms, with or without modifica-
 tion, are permitted provided that the following conditions are met:

 1. Redistributions of  source code must  retain the above copyright  notice,
    this list of conditions and the following disclaimer.

 2. Redistributions in binary form must reproduce the above copyright notice,
    this list of conditions and the following disclaimer in the documentation
    and/or other materials provided with the distribution.

 3. The end-user documentation included with the redistribution, if any, must
    include  the following  acknowledgment:  "This product includes  software
    developed  by the  Apache Software Foundation  (http://www.apache.org/)."
    Alternately, this  acknowledgment may  appear in the software itself,  if
    and wherever such third-party acknowledgments normally appear.

 4. The names "Apache Lenya" and  "Apache Software Foundation"  must  not  be
    used to  endorse or promote  products derived from  this software without
    prior written permission. For written permission, please contact
    apache@apache.org.

 5. Products  derived from this software may not  be called "Apache", nor may
    "Apache" appear  in their name,  without prior written permission  of the
    Apache Software Foundation.

 THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED WARRANTIES,
 INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 FITNESS  FOR A PARTICULAR  PURPOSE ARE  DISCLAIMED.  IN NO  EVENT SHALL  THE
 APACHE SOFTWARE  FOUNDATION  OR ITS CONTRIBUTORS  BE LIABLE FOR  ANY DIRECT,
 INDIRECT, INCIDENTAL, SPECIAL,  EXEMPLARY, OR CONSEQUENTIAL  DAMAGES (INCLU-
 DING, BUT NOT LIMITED TO, PROCUREMENT  OF SUBSTITUTE GOODS OR SERVICES; LOSS
 OF USE, DATA, OR  PROFITS; OR BUSINESS  INTERRUPTION)  HOWEVER CAUSED AND ON
 ANY  THEORY OF LIABILITY,  WHETHER  IN CONTRACT,  STRICT LIABILITY,  OR TORT
 (INCLUDING  NEGLIGENCE OR  OTHERWISE) ARISING IN  ANY WAY OUT OF THE  USE OF
 THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

 This software  consists of voluntary contributions made  by many individuals
 on  behalf of the Apache Software  Foundation and was  originally created by
 Michael Wechner <michi@apache.org>. For more information on the Apache Soft-
 ware Foundation, please see <http://www.apache.org/>.

 Lenya includes software developed by the Apache Software Foundation, W3C,
 DOM4J Project, BitfluxEditor, Xopus, and WebSHPINX.
</License>
*/
package org.apache.lenya.cms.rc;

import org.apache.lenya.util.XPSFileOutputStream;
import org.apache.lenya.xml.DOMParserFactory;
import org.apache.lenya.xml.DOMWriter;
import org.apache.lenya.xml.XPointerFactory;

import org.apache.log4j.Category;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

import java.util.Date;
import java.util.Vector;


/**
 * DOCUMENT ME!
 *
 * @author Michael Wechner
 * @author Marc Liyanage
 * @version 0.7.19
 */
public class RCML {
    static Category log = Category.getInstance(RCML.class);
    static short co = 0;
    static short ci = 1;
    private File rcmlFile;
    private Document document = null;
    private boolean dirty = false;
    int maximalNumberOfEntries = 5;

    /**
     * Creates a new RCML object.
     */
    public RCML() {
        maximalNumberOfEntries = new org.apache.lenya.xml.Configuration().maxNumberOfRollbacks;
        maximalNumberOfEntries = (2 * maximalNumberOfEntries) + 1;
    }

    /**
     * create a RCML-File if no one exists already
     *
     * @param rcmlDirectory DOCUMENT ME!
     * @param filename DOCUMENT ME!
     *
     * @throws Exception DOCUMENT ME!
     */
    public RCML(String rcmlDirectory, String filename, String rootDirectory)
        throws Exception {
        this();
        rcmlFile = new File(rcmlDirectory + "/" + filename + ".rcml");

        if (!rcmlFile.isFile()) {
            // The rcml file does not yet exist, so we create it now...
            //
            File dataFile = new File(rootDirectory + filename);
            long lastModified = 0;

            if (dataFile.isFile()) {
                lastModified = dataFile.lastModified();
            }

            initDocument();

            // Create a "fake" checkin entry so it looks like the
            // system checked the document in. We use the filesystem
            // modification date as checkin time.
            //
            checkOutIn(RCML.ci, RevisionController.systemUsername, lastModified);

            File parent = new File(rcmlFile.getParent());
            parent.mkdirs();

            write();
        } else {
            DOMParserFactory dpf = new DOMParserFactory();
            document = dpf.getDocument(rcmlFile.getAbsolutePath());
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param args DOCUMENT ME!
     */
    public static void main(String[] args) {
        if (args.length != 1) {
            log.info("Usage: java RCML rcmlDirectory datafilename rootDirectory");

            return;
        }

        try {
            RCML rcml = new RCML(args[0], args[1], args[2]);
            rcml.checkOutIn(RCML.co, "michi", new Date().getTime());

            new DOMWriter(new PrintWriter(System.out)).print(rcml.document);

            CheckOutEntry coe = rcml.getLatestCheckOutEntry();
            log.info("\n");

            if (coe == null) {
                log.info("Not checked out");
            } else {
                log.info("Checked out: " + coe.identity + " " + coe.time);
            }
        } catch (Exception e) {
            log.error(e);
        }
    }

    /**
     * initialise the RCML-document. Delete all entries
     */
    public void initDocument() {
        DOMParserFactory dpf = new DOMParserFactory();
        document = dpf.getDocument();

        Element root = dpf.newElementNode(document, "XPSRevisionControl");
        document.appendChild(root);
    }

    /**
     * Call the methode write, if the document is dirty
     *
     * @throws IOException DOCUMENT ME!
     * @throws Exception DOCUMENT ME!
     */
    public void finalize() throws IOException, Exception {
        if (this.isDirty()) {
            log.debug("RCML.finalize(): calling write()");
            write();
        }
    }

    /**
     * Write the xml RCML-document in the RCML-file. Limit the number of entries to the value
     * maximalNumberOfEntries (2maxNumberOfRollbacks(configured)+1)
     *
     * @throws IOException DOCUMENT ME!
     * @throws Exception DOCUMENT ME!
     */
    public void write() throws IOException, Exception {
        log.debug("RCML.write(): writing out file: " + rcmlFile.getAbsolutePath());
        pruneEntries(maximalNumberOfEntries);

        XPSFileOutputStream xpsfos = new XPSFileOutputStream(rcmlFile.getAbsolutePath());
        new DOMWriter(xpsfos).print(this.document);
        xpsfos.close();

        clearDirty();
    }

    /**
     * Write a new entry for a check out or a check in the RCML-File made by the user with identity
     * at time
     *
     * @param type co for a check out, ci for a check in
     * @param identity The identity of the user
     * @param time Time at which the check in/out is made
     *
     * @throws IOException DOCUMENT ME!
     * @throws Exception DOCUMENT ME!
     */
    public void checkOutIn(short type, String identity, long time)
        throws IOException, Exception {
        DOMParserFactory dpf = new DOMParserFactory();

        Element identityElement = dpf.newElementNode(document, "Identity");
        identityElement.appendChild(dpf.newTextNode(document, identity));

        Element timeElement = dpf.newElementNode(document, "Time");
        timeElement.appendChild(dpf.newTextNode(document, "" + time));

        Element checkOutElement = null;

        if (type == co) {
            checkOutElement = dpf.newElementNode(document, "CheckOut");
        } else if (type == ci) {
            checkOutElement = dpf.newElementNode(document, "CheckIn");
        } else {
            log.error("ERROR: " + this.getClass().getName() + ".checkOutIn(): No such type");

            return;
        }

        checkOutElement.appendChild(identityElement);
        checkOutElement.appendChild(timeElement);

        Element root = document.getDocumentElement();
        root.insertBefore(dpf.newTextNode(document, "\n"), root.getFirstChild());
        root.insertBefore(checkOutElement, root.getFirstChild());
        root.insertBefore(dpf.newTextNode(document, "\n"), root.getFirstChild());

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
     *
     * @return CheckOutEntry The entry of the check out
     *
     * @throws Exception DOCUMENT ME!
     */
    public CheckOutEntry getLatestCheckOutEntry() throws Exception {
        XPointerFactory xpf = new XPointerFactory();

        Vector firstCheckOut = xpf.select(document.getDocumentElement(),
                "xpointer(/XPSRevisionControl/CheckOut[1]/Identity)xpointer(/XPSRevisionControl/CheckOut[1]/Time)");

        if (firstCheckOut.size() == 0) {
            // No checkout at all
            //
            return null;
        }

        String[] fcoValues = xpf.getNodeValues(firstCheckOut);
        long fcoTime = new Long(fcoValues[1]).longValue();

        return new CheckOutEntry(fcoValues[0], fcoTime);
    }

    /**
     * get the latest check in
     *
     * @return CheckInEntry The entry of the check in
     *
     * @throws Exception DOCUMENT ME!
     */
    public CheckInEntry getLatestCheckInEntry() throws Exception {
        XPointerFactory xpf = new XPointerFactory();

        Vector firstCheckIn = xpf.select(document.getDocumentElement(),
                "xpointer(/XPSRevisionControl/CheckIn[1]/Identity)xpointer(/XPSRevisionControl/CheckIn[1]/Time)");

        if (firstCheckIn.size() == 0) {
            // No checkin at all
            //
            return null;
        }

        String[] fciValues = xpf.getNodeValues(firstCheckIn);
        long fciTime = new Long(fciValues[1]).longValue();

        return new CheckInEntry(fciValues[0], fciTime);
    }

    /**
     * get the latest entry (a check out or check in)
     *
     * @return RCMLEntry The entry of the check out/in
     *
     * @throws Exception DOCUMENT ME!
     */
    public RCMLEntry getLatestEntry() throws Exception {
        CheckInEntry cie = getLatestCheckInEntry();
        CheckOutEntry coe = getLatestCheckOutEntry();

        if ((cie != null) && (coe != null)) {
            if (cie.time > coe.time) {
                return cie;
            } else {
                return coe;
            }
        }

        if (cie != null) {
            return cie;
        } else {
            return coe;
        }
    }

    /**
     * get all check in and check out
     *
     * @return Vector of all check out and check in entries in this RCML-file
     *
     * @throws Exception DOCUMENT ME!
     */
    public Vector getEntries() throws Exception {
        XPointerFactory xpf = new XPointerFactory();

        Vector entries = xpf.select(document.getDocumentElement(),
                "xpointer(/XPSRevisionControl/CheckOut|/XPSRevisionControl/CheckIn)");
        Vector RCMLEntries = new Vector();

        for (int i = 0; i < entries.size(); i++) {
            Element elem = (Element) entries.get(i);
            String time = elem.getElementsByTagName("Time").item(0).getFirstChild().getNodeValue();
            String identity = elem.getElementsByTagName("Identity").item(0).getFirstChild()
                                  .getNodeValue();

            if (elem.getTagName().equals("CheckOut")) {
                RCMLEntries.add(new CheckOutEntry(identity, new Long(time).longValue()));
            } else {
                RCMLEntries.add(new CheckInEntry(identity, new Long(time).longValue()));
            }
        }

        return RCMLEntries;
    }

    /**
     * Prune the list of entries. Keep only entriesToKeep items at the front of the list
     *
     * @param entriesToKeep The number of entries to keep
     *
     * @throws Exception DOCUMENT ME!
     */
    public void pruneEntries(int entriesToKeep) throws Exception {
        XPointerFactory xpf = new XPointerFactory();

        Vector entries = xpf.select(document.getDocumentElement(),
                "xpointer(/XPSRevisionControl/CheckOut|/XPSRevisionControl/CheckIn)");

        Configuration conf = new Configuration();
        String backupDir = conf.backupDirectory;

        for (int i = entriesToKeep; i < entries.size(); i++) {
            Element current = (Element) entries.get(i);

            // remove the backup file associated with this entry
            String time = current.getElementsByTagName("Time").item(0).getFirstChild().getNodeValue();
            File backupFile = new File(backupDir + "/" + time + ".bak");
            backupFile.delete();

            // remove the entry from the list
            current.getParentNode().removeChild(current);
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     *
     * @throws Exception DOCUMENT ME!
     */
    public org.w3c.dom.Document getDOMDocumentClone() throws Exception {
        Document documentClone = new DOMParserFactory().getDocument();
        documentClone.appendChild(documentClone.importNode(document.getDocumentElement(), true));

        return documentClone;
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public boolean isDirty() {
        return dirty;
    }

    protected void setDirty() {
        dirty = true;
    }

    protected void clearDirty() {
        dirty = false;
    }

    /**
     * Delete the latest check in
     *
     * @throws Exception DOCUMENT ME!
     */
    public void deleteFirstCheckIn() throws Exception {
        XPointerFactory xpf = new XPointerFactory();
        Node root = document.getDocumentElement();
        Vector firstCheckIn = xpf.select(root, "xpointer(/XPSRevisionControl/CheckIn[1])");
        root.removeChild((Node) firstCheckIn.elementAt(0));
        root.removeChild(root.getFirstChild()); // remove EOL (end of line)
        setDirty();
    }
}
