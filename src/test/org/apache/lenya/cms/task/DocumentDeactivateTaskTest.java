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

/* $Id: DocumentDeactivateTaskTest.java,v 1.5 2004/03/20 11:46:20 gregor Exp $  */

package org.apache.lenya.cms.task;

import java.io.File;

import junit.framework.Test;
import junit.framework.TestSuite;
import junit.textui.TestRunner;

import org.apache.avalon.framework.parameters.Parameters;
import org.apache.lenya.cms.PublicationHelper;
import org.apache.lenya.cms.publication.Publication;
import org.apache.lenya.cms.publication.SiteTree;
import org.apache.lenya.cms.publication.SiteTreeNode;


/**
 * Class for testing the task to deactivate a document.
 * Extend the AntTask class.
 */
public class DocumentDeactivateTaskTest extends AntTaskTest {

	private long time = 0;

	/**
     * Creates a new DocumentDeactivateTaskTest object.
	 * @param test The test.
	 */
	public DocumentDeactivateTaskTest(String test) {
		super(test);
	}

	/** 
	 * Creates a test suite.
	 * @return Test 
	 **/
	public static Test getSuite() {
		return new TestSuite(DocumentDeactivateTaskTest.class);
	}

	/**
	 * The main program for the DocumentDeactivateTaskTest class
	 *
	 * @param args The command line arguments
	 */
	public static void main(String[] args) {
		AntTaskTest.initialize(args);
		TestRunner.run(getSuite());
	}

	public static final String DOCUMENT_ID = "/tutorial";
    public static final String LANGUAGE = "en";
	public static final String AUTHORING_PATH = "content/authoring".replace('/', File.separatorChar);
	public static final String LIVE_PATH = "content/live".replace('/', File.separatorChar);
	
	/**
	 * @see org.apache.lenya.cms.task.AntTaskTest#getTaskParameters()
	 **/
	protected Parameters getTaskParameters() {
		Parameters parameters = super.getTaskParameters();
		parameters.setParameter("properties.node.firstdocumentid", DOCUMENT_ID);
        parameters.setParameter("properties.node.language", LANGUAGE);
		return parameters;
	}
    
	/**
	 * Returns the target test.
	 * @return target.
	 */
	protected String getTarget() {
		return "deactivateDocument";
	}

	/** (non-Javadoc)
     * @see org.apache.lenya.cms.task.AntTaskTest#prepareTest()
     */
    protected void prepareTest() throws Exception {
		File publicationDirectory = PublicationHelper.getPublication().getDirectory();
        String publicationPath = publicationDirectory.getAbsolutePath()+ File.separator; 


		// TODO generate the resources  
}

	/** (non-Javadoc)
     * @see org.apache.lenya.cms.task.AntTaskTest#evaluateTest()
     */
    protected void evaluateTest() throws Exception {
		File publicationDirectory = PublicationHelper.getPublication().getDirectory();
		String publicationPath = publicationDirectory.getAbsolutePath(); 
        File authoringDirectory = new File(publicationPath, AUTHORING_PATH);
		File liveDirectory = new File(publicationPath, LIVE_PATH);
        
		String filepath = DOCUMENT_ID.substring(1) + File.separator + "index_en.xml";  

		File authoringDocumentFile = new File(authoringDirectory, filepath);
        System.out.println("Authoring document: " + authoringDocumentFile.getAbsolutePath());
        assertTrue(authoringDocumentFile.exists());
		File liveDocumentFile = new File(liveDirectory, filepath);
        System.out.println("Live document: " + liveDocumentFile.getAbsolutePath());
		assertFalse(liveDocumentFile.exists());

        //TODO evaluation of resources, meta, workflow
        
		SiteTree authoringSitetree = PublicationHelper.getPublication().getSiteTree(Publication.AUTHORING_AREA);
		SiteTreeNode node = authoringSitetree.getNode(DOCUMENT_ID);
		assertNotNull(node);
        System.out.println("Sitetree node with id ["+node.getId()+"] is always in authoring");
		SiteTree liveSitetree = PublicationHelper.getPublication().getSiteTree(Publication.LIVE_AREA);
		SiteTreeNode livenode = liveSitetree.getNode(DOCUMENT_ID);
		assertNull(livenode);
        System.out.println("Sitetree node for document id ["+DOCUMENT_ID+"] was deleted from the live tree");
	}
}
