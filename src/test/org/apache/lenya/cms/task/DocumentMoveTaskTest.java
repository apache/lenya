/*
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
package org.apache.lenya.cms.task;

import java.io.File;
import java.util.StringTokenizer;

import junit.framework.Test;
import junit.framework.TestSuite;
import junit.textui.TestRunner;

import org.apache.avalon.framework.parameters.Parameters;
import org.apache.lenya.cms.PublicationHelper;
import org.apache.lenya.cms.publication.DefaultSiteTree;
import org.apache.lenya.cms.publication.SiteTree;
import org.apache.lenya.cms.publication.SiteTreeNode;
import org.apache.lenya.cms.rc.RevisionController;


/**
 * Class for testing the task to copy a document.
 * Extend the AntTask class.
 * @author edith
 */
public class DocumentMoveTaskTest extends AntTaskTest {

	private long time = 0;

	/**
     * Creates a new DocumentMoveTaskTest object.
	 * @param test
	 */
	public DocumentMoveTaskTest(String test) {
		super(test);
	}

	/** 
	 * Creates a test suite.
	 * @return Test 
	 **/
	public static Test getSuite() {
		return new TestSuite(DocumentMoveTaskTest.class);
	}

	/**
	 * The main program for the DocumentMoveTaskTest class
	 *
	 * @param args The command line arguments
	 */
	public static void main(String[] args) {
		AntTaskTest.initialize(args);
		TestRunner.run(getSuite());
	}

	public static final String FIRST_DOCUMENT_ID = "/concepts";
	public static final String SEC_DOCUMENT_ID = "/features";
	public static final String AUTHORING_PATH = "content/authoring".replace('/', File.separatorChar);
	public static final String TREE_FILE = "sitetree.xml";
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
		return parameters;
	}
    
	/**
	 * Returns the target test.
	 * @return target.
	 */
	protected String getTarget() {
		return "moveDocument";
	}

	/**
	 * prepare the test
	 */
	protected void prepareTest() throws Exception {
		File publicationDirectory = PublicationHelper.getPublication().getDirectory();
        String publicationPath = publicationDirectory.getAbsolutePath()+ File.separator; 
		File authoringDirectory = new File(publicationPath, AUTHORING_PATH);

		// TODO generate the resources  

		// generate the rcml and rcbak files
		File rcmlDirectory = new File(publicationPath , RCML_DIR);
		File rcbakDirectory = new File(publicationPath , RCBAK_DIR);
        RevisionController rc = new RevisionController(rcmlDirectory.getAbsolutePath(), rcbakDirectory.getAbsolutePath(), publicationPath);
		String filename = AUTHORING_PATH +FIRST_DOCUMENT_ID + File.separator + "index.xml";
		rc.reservedCheckOut(filename, "lenya");   
		time = rc.reservedCheckIn(filename, "lenya", true);   
	}

	/**
	 * evaluate the test
	 */
	protected void evaluateTest() throws Exception {
		File publicationDirectory = PublicationHelper.getPublication().getDirectory();
		String publicationPath = publicationDirectory.getAbsolutePath(); 
        File authoringDirectory = new File(publicationPath, AUTHORING_PATH);
        
		StringTokenizer st = new StringTokenizer(FIRST_DOCUMENT_ID , "/", true);
		int l = st.countTokens();
		for (int i=1; i<l; i++) {
		  st.nextToken();
		}
		String secdocumentid = SEC_DOCUMENT_ID+"/"+st.nextToken();      
		String filepath = secdocumentid + File.separator + "index.xml";  

		File documentFile = new File(authoringDirectory, filepath);
        assertTrue(documentFile.exists());
		System.out.println("Document was copied: " + documentFile.getAbsolutePath());
		String firstfilepath = FIRST_DOCUMENT_ID + File.separator + "index.xml";  
		File firstdocumentFile = new File(authoringDirectory, firstfilepath);
		assertFalse(firstdocumentFile.exists());
		System.out.println("Document was deleted: " + firstdocumentFile.getAbsolutePath());

		File rcmlDirectory = new File(publicationPath , RCML_DIR);
        String rcmlFilePath = filepath+".rcml";
		File rcmlFile = new File(rcmlDirectory , AUTHORING_PATH + rcmlFilePath);
        assertTrue(rcmlFile.exists());
		System.out.println("rcml file was copied: " + rcmlFile.getAbsolutePath());

		String firstRcmlFilePath = firstfilepath+".rcml";
		File firstRcmlFile = new File(rcmlDirectory , AUTHORING_PATH + firstRcmlFilePath);
		assertFalse(firstRcmlFile.exists());
		System.out.println("rcml file was deleted: " + firstRcmlFile.getAbsolutePath());

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
        
        File sitetreeFile = new File(authoringDirectory, TREE_FILE);
        
		SiteTree sitetree = new DefaultSiteTree(sitetreeFile);
		SiteTreeNode node = sitetree.getNode(secdocumentid);
		assertNotNull(node);
		System.out.println("Sitetree node with id "+node.getId()+" was created as child of node with id: " + node.getAbsoluteParentId());
		SiteTreeNode firstnode = sitetree.getNode(FIRST_DOCUMENT_ID);
		assertNull(firstnode);
		System.out.println("Sitetree node for document id "+FIRST_DOCUMENT_ID+" was deleted");
	}
}
