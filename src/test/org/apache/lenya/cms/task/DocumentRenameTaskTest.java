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
package org.apache.lenya.cms.task;

import java.io.File;
import java.util.StringTokenizer;

import junit.framework.Test;
import junit.framework.TestSuite;
import junit.textui.TestRunner;

import org.apache.avalon.framework.parameters.Parameters;
import org.apache.lenya.cms.PublicationHelper;
import org.apache.lenya.cms.publication.Publication;
import org.apache.lenya.cms.publication.SiteTree;
import org.apache.lenya.cms.publication.SiteTreeNode;
import org.apache.lenya.cms.rc.RevisionController;


/**
 * Class for testing the task to rename a document.
 * @author edith
 */
public class DocumentRenameTaskTest extends AntTaskTest {
	private long time = 0;

	/**
     * Creates a new DocumentRenameTaskTest object.
	 * @param test the test
	 */
	public DocumentRenameTaskTest(String test) {
		super(test);
	}

	/** 
	 * Creates a test suite.
	 * @return Test 
	 **/
	public static Test getSuite() {
		return new TestSuite(DocumentRenameTaskTest.class);
	}

	/**
	 * The main program for the DocumentRenameTaskTest class
	 *
	 * @param args The command line arguments
	 */
	public static void main(String[] args) {
		AntTaskTest.initialize(args);
		TestRunner.run(getSuite());
	}

	public static final String FIRST_DOCUMENT_ID = "/doctypes/simple-document";
	public static final String SEC_DOCUMENT_ID = "newname";
	public static final String FIRST_AREA = "authoring";
	public static final String SEC_AREA = "authoring";
	public static final String AUTHORING_PATH = "content/authoring".replace('/', File.separatorChar);
	public static final String AUTHORING_RESOURCE = "resources/authoring";
	public static final String RCML_DIR = "content/rcml";
	public static final String RCBAK_DIR = "content/rcbak";
	
	/**
	 * @see org.apache.lenya.cms.task.AntTaskTest#getTaskParameters()
	 **/
	protected Parameters getTaskParameters() {
		Parameters parameters = super.getTaskParameters();
		parameters.setParameter("properties.node.firstdocumentid", FIRST_DOCUMENT_ID);
		parameters.setParameter("properties.node.secdocumentid", SEC_DOCUMENT_ID);
		parameters.setParameter("properties.firstarea", FIRST_AREA);
		parameters.setParameter("properties.secarea", SEC_AREA);
		return parameters;
	}
    
	/**
	 * Returns the target test.
	 * @return target.
	 */
	protected String getTarget() {
		return "renameDocument";
	}

	/**
	 * prepare the test
     * 
     * @throws Exception if an error occurs
	 */
	protected void prepareTest() throws Exception {
		File publicationDirectory = PublicationHelper.getPublication().getDirectory();
        String publicationPath = publicationDirectory.getAbsolutePath()+ File.separator; 
		File authoringDirectory = new File(publicationPath, AUTHORING_PATH);

		String filename = AUTHORING_PATH +FIRST_DOCUMENT_ID + File.separator + "index_de.xml";
		String filepath = authoringDirectory + FIRST_DOCUMENT_ID + File.separator + "index_de.xml";  
		String en_filepath = authoringDirectory + FIRST_DOCUMENT_ID + File.separator + "index_en.xml";  

		// TODO generate the resources  

		// generate the rcml and rcbak files
		File rcmlDirectory = new File(publicationPath , RCML_DIR);
		File rcbakDirectory = new File(publicationPath , RCBAK_DIR);
		RevisionController rc = new RevisionController(rcmlDirectory.getAbsolutePath(), rcbakDirectory.getAbsolutePath(), publicationPath);
		rc.reservedCheckOut(filename, "lenya");   
		time = rc.reservedCheckIn(filename, "lenya", true);   

		// TODO generate the workflow, meta  
	}

	/**
	 * evaluate the test
     * 
     * @throws Exception if an error occurs
	 */
	protected void evaluateTest() throws Exception {
		File publicationDirectory = PublicationHelper.getPublication().getDirectory();
		String publicationPath = publicationDirectory.getAbsolutePath(); 
        File authoringDirectory = new File(publicationPath, AUTHORING_PATH);
        
		StringTokenizer st = new StringTokenizer(FIRST_DOCUMENT_ID , "/");
		int l = st.countTokens();
        String secdocumentid = "";
		for (int i=1; i<l; i++) {
		  secdocumentid= secdocumentid + "/" + st.nextToken();
		}
		secdocumentid = secdocumentid + "/" + SEC_DOCUMENT_ID;      
		System.out.println("the second document id" + secdocumentid);

		String filepath = secdocumentid + File.separator + "index_de.xml";  
		String en_filepath = secdocumentid + File.separator + "index_en.xml";  
		String firstfilepath = FIRST_DOCUMENT_ID + File.separator + "index_de.xml";  
		String firsten_filepath = FIRST_DOCUMENT_ID + File.separator + "index_en.xml";  

        //evaluate the file
		File documentFile = new File(authoringDirectory, filepath);
		assertTrue(documentFile.exists());
		System.out.println("Document was copied: " + documentFile.getAbsolutePath());
		File en_documentFile = new File(authoringDirectory, en_filepath);
		assertTrue(en_documentFile.exists());
		System.out.println("Document was copied: " + en_documentFile.getAbsolutePath());
		File firstdocumentFile = new File(authoringDirectory, firstfilepath);
		assertFalse(firstdocumentFile.exists());
		System.out.println("Document was deleted: " + firstdocumentFile.getAbsolutePath());
		File firsten_documentFile = new File(authoringDirectory, firsten_filepath);
		assertFalse(firsten_documentFile.exists());
		System.out.println("Document was deleted: " + firsten_documentFile.getAbsolutePath());

        //evaluate the rcml 
		File rcmlDirectory = new File(publicationPath , RCML_DIR);
		String rcmlFilePath = filepath+".rcml";
		File rcmlFile = new File(rcmlDirectory , AUTHORING_PATH + rcmlFilePath);
		assertTrue(rcmlFile.exists());
		System.out.println("rcml file was copied: " + rcmlFile.getAbsolutePath());

		String firstRcmlFilePath = firstfilepath+".rcml";
		File firstRcmlFile = new File(rcmlDirectory , AUTHORING_PATH + firstRcmlFilePath);
		assertFalse(firstRcmlFile.exists());
		System.out.println("rcml file was deleted: " + firstRcmlFile.getAbsolutePath());

        //evaluate the backup
		File rcbakDirectory = new File(publicationPath , RCBAK_DIR);
		String rcbakFilePath= filepath +".bak." +time ;
		File rcbakFile = new File(rcbakDirectory, AUTHORING_PATH + rcbakFilePath);
		assertTrue(rcbakFile.exists());
		System.out.println("Backup was copied: " + rcbakFile.getAbsolutePath());

		String firstRcbakFilePath= firstfilepath +".bak." +time ;
		File firstRcbakFile = new File(rcbakDirectory, AUTHORING_PATH + firstRcbakFilePath);
		assertFalse(firstRcbakFile.exists());
		System.out.println("Backup was deleted: " + firstRcbakFile.getAbsolutePath());

		//TODO evaluation of meta, workflow
        
        //evaluate the node
		SiteTree sitetree = PublicationHelper.getPublication().getSiteTree(Publication.AUTHORING_AREA);
		SiteTreeNode node = sitetree.getNode(secdocumentid);
		assertNotNull(node);
		System.out.println("Sitetree node with id "+node.getId()+" was created as child of node with id: " + node.getAbsoluteParentId());
		SiteTreeNode firstnode = sitetree.getNode(FIRST_DOCUMENT_ID);
		assertNull(firstnode);
		System.out.println("Sitetree node for document id "+FIRST_DOCUMENT_ID+" was deleted");

    }
}
