/*
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

import java.io.File;
import java.io.IOException;
import java.util.Vector;

import javax.xml.parsers.ParserConfigurationException;

import org.apache.lenya.xml.DocumentHelper;
import org.apache.lenya.xml.NamespaceHelper;
import org.apache.lenya.xml.XPointerFactory;
import org.apache.log4j.Category;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;


/**
 * Handle with the RCML file
 *
 * @author Michael Wechner
 * @author Marc Liyanage
 * @author Edith Chevrier
 * @version $Id: RCML.java,v 1.23 2004/02/10 11:37:36 andreas Exp $
 */
public class RCML {
    private static Category log = Category.getInstance(RCML.class);
    
    public static final short co = 0;
    public static final short ci = 1;
    
    private File rcmlFile;
    private Document document = null;
    private boolean dirty = false;
    private int maximalNumberOfEntries = 5;

    /**
     * Creates a new RCML object.
     */
    public RCML() {
        /*Deprecated
        maximalNumberOfEntries = new org.apache.lenya.xml.Configuration().maxNumberOfRollbacks;
        */
        maximalNumberOfEntries = 10;
        maximalNumberOfEntries = (2 * maximalNumberOfEntries) + 1;
    }

    /**
     * create a RCML-File if no one exists already
     *
     * @param rcmlDirectory The rcml directory.
     * @param filename The path of the file from the publication (e.g. for file with 
     * absolute path home/.../jakarta-tomcat-4.1.24/webapps/lenya/lenya/pubs/{publication id}/content/authoring/foo/bar.xml
     * the filename is content/authoring/foo/bar.xml)
     * @param rootDirectory The publication directory
     *
     * @throws Exception if an error occurs
     */
    public RCML(String rcmlDirectory, String filename, String rootDirectory)
        throws Exception {
        this();
        rcmlFile = new File(rcmlDirectory , filename + ".rcml");
     
        if (!rcmlFile.isFile()) {
            // The rcml file does not yet exist, so we create it now...
            //
            File dataFile = new File(rootDirectory , filename);
            long lastModified = 0;

            if (dataFile.isFile()) {
                lastModified = dataFile.lastModified();
            }

            initDocument();

			// Create a "fake" checkin entry so it looks like the
			// system checked the document in. We use the filesystem
			// modification date as checkin time.
			//
            checkOutIn(RCML.ci, RevisionController.systemUsername, lastModified, false);

            File parent = new File(rcmlFile.getParent());
            parent.mkdirs();

            write();
        } else {
            document = DocumentHelper.readDocument(rcmlFile);
        }
    }

    /**
     * initialise the RCML-document. Delete all entries
     */
    public void initDocument() throws ParserConfigurationException {
        document = DocumentHelper.createDocument(null, "XPSRevisionControl", null); 
    }

    /**
     * Call the methode write, if the document is dirty
     *
     * @throws IOException if an error occurs
     * @throws Exception if an error occurs
     */
    public void finalize() throws IOException, Exception {
        if (this.isDirty()) {
            log.debug("RCML.finalize(): calling write()");
            write();
        }
    }
    /**
     * Write the xml RCML-document in the RCML-file. 
     *
     * @throws IOException if an error occurs
     * @throws Exception if an error occurs
     */
    public void write() throws IOException, Exception {
        DocumentHelper.writeDocument(document, rcmlFile);
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
     * @throws IOException if an error occurs
     * @throws Exception if an error occurs
     */
    public void checkOutIn(short type, String identity, long time, boolean backup)
        throws IOException, Exception {
            
        NamespaceHelper helper = new NamespaceHelper(null, "", document);
            
        Element identityElement = helper.createElement("Identity", identity);
        Element timeElement = helper.createElement("Time", "" + time);

        Element checkOutElement = null;

        if (type == co) {
            checkOutElement = helper.createElement("CheckOut");
        } else if (type == ci) {
            checkOutElement = helper.createElement("CheckIn");
        } else {
            log.error("ERROR: " + this.getClass().getName() + ".checkOutIn(): No such type");

            return;
        }

        checkOutElement.appendChild(identityElement);
        checkOutElement.appendChild(timeElement);

		if (backup) {
			Element backupElement = helper.createElement("Backup");
			checkOutElement.appendChild(backupElement);
		}

        Element root = document.getDocumentElement();
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
     *
     * @return CheckOutEntry The entry of the check out
     *
     * @throws Exception if an error occurs
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
     * @throws Exception if an error occurs
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
     * @throws Exception if an error occurs
     */
    public RCMLEntry getLatestEntry() throws Exception {
        CheckInEntry cie = getLatestCheckInEntry();
        CheckOutEntry coe = getLatestCheckOutEntry();

        if ((cie != null) && (coe != null)) {
            if (cie.getTime() > coe.getTime()) {
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
     * @throws Exception if an error occurs
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
     * Prune the list of entries and delete the corresponding backups. Limit the number of entries to the value
     * maximalNumberOfEntries (2maxNumberOfRollbacks(configured)+1)
     *
     * @param backupDir The backup directory
     *
     * @throws Exception if an error occurs
     */
    public void pruneEntries(String backupDir) throws Exception {
        XPointerFactory xpf = new XPointerFactory();

        Vector entries = xpf.select(document.getDocumentElement(),
                "xpointer(/XPSRevisionControl/CheckOut|/XPSRevisionControl/CheckIn)");

        for (int i = maximalNumberOfEntries; i < entries.size(); i++) {
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
     * Get a clone document 
     *
     * @return org.w3c.dom.Document The clone document
     *
     * @throws Exception if an error occurs
     */
    public org.w3c.dom.Document getDOMDocumentClone() throws Exception {
        Document documentClone = DocumentHelper.createDocument(null, "dummy", null);
        documentClone.removeChild(documentClone.getDocumentElement());
        documentClone.appendChild(documentClone.importNode(document.getDocumentElement(), true));

        return documentClone;
    }

    /**
     * Check if the document is dirty
     *
     * @return boolean dirty 
     */
    public boolean isDirty() {
        return dirty;
    }

	/**
	 * Set the value dirty to true
	 */
    protected void setDirty() {
        dirty = true;
    }

	/**
	 * Set the value dirty to false
	 */
    protected void clearDirty() {
        dirty = false;
    }

    /**
     * Delete the latest check in
     *
     * @throws Exception if an error occurs
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
