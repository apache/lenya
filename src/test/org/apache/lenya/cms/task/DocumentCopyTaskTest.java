/*
 * Copyright  1999-2004 The Apache Software Foundation
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
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

/* $Id: DocumentCopyTaskTest.java,v 1.7 2004/03/04 15:41:10 egli Exp $  */

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


/**
 * Class for testing the task to copy a document.
 */
public class DocumentCopyTaskTest extends AntTaskTest {

	/**
     * Creates a new DocumentCopyTaskTest object.
	 * @param test the test
	 */
	public DocumentCopyTaskTest(String test) {
		super(test);
	}

	/** 
	 * Creates a test suite.
	 * @return Test 
	 **/
	public static Test getSuite() {
		return new TestSuite(DocumentCopyTaskTest.class);
	}

	/**
	 * The main program for the DocumentCopyTaskTest class
	 *
	 * @param args The command line arguments
	 */
	public static void main(String[] args) {
		AntTaskTest.initialize(args);
		TestRunner.run(getSuite());
	}

	public static final String FIRST_DOCUMENT_ID = "/tutorial";
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
		return "copyDocument";
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

		// TODO generate the resources  
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

        SiteTree sitetree = PublicationHelper.getPublication().getSiteTree(Publication.AUTHORING_AREA);
        SiteTreeNode node = sitetree.getNode(secdocumentid);
        assertNotNull(node);
        System.out.println("Sitetree node with id " + node.getId() +
            " was created as child of node with id: " + node.getAbsoluteParentId());
    }
}
