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

/* $Id: PublisherTest.java,v 1.7 2004/03/04 15:41:10 egli Exp $  */

package org.apache.lenya.cms.task;

import java.io.File;
import java.io.IOException;

import junit.framework.Test;
import junit.framework.TestSuite;
import junit.textui.TestRunner;

import org.apache.avalon.framework.parameters.Parameters;
import org.apache.lenya.cms.PublicationHelper;
import org.apache.lenya.cms.publication.DocumentIdToPathMapper;
import org.apache.lenya.cms.publication.Publication;


public class PublisherTest extends AntTaskTest {

    /**
     * Create a test.
     * 
     * @param test the test
     */
    public PublisherTest(String test) {
        super(test);
    }

    /**
     * Creates a test suite.
     * 
     * @return a test
     */
    public static Test getSuite() {
        return new TestSuite(PublisherTest.class);
    }

    /**
     * The main program for the PublisherTest class
     *
     * @param args The command line arguments
     */
    public static void main(String[] args) {
        AntTaskTest.initialize(args);
        TestRunner.run(getSuite());
    }

    public static final String DOCUMENT_ID = "/tutorial";
    public static final String DOCUMENT_LANGUAGE = "en";

	/**
	 *  (non-Javadoc)
	 * @see org.apache.lenya.cms.task.AntTaskTest#evaluateTest()
	 */
    protected void evaluateTest() throws IOException {
        Publication pub = PublicationHelper.getPublication();
        DocumentIdToPathMapper mapper = pub.getPathMapper();
        File publishedFile = mapper.getFile(pub, Publication.LIVE_AREA, DOCUMENT_ID, DOCUMENT_LANGUAGE);
        System.out.println("Path of file to publish: " + publishedFile);
        assertTrue(publishedFile.exists());
        System.out.println("Published file exists: " + publishedFile.getCanonicalPath());
    }

	/**
	 *  (non-Javadoc)
	 * @see org.apache.lenya.cms.task.AntTaskTest#getTaskParameters()
	 */
    protected Parameters getTaskParameters() {
        Parameters parameters = super.getTaskParameters();
        parameters.setParameter("properties.publish.documentid", DOCUMENT_ID);
        parameters.setParameter("properties.publish.language", DOCUMENT_LANGUAGE);
        Publication pub = PublicationHelper.getPublication();
        DocumentIdToPathMapper mapper = pub.getPathMapper();
        parameters.setParameter("properties.publish.sources", mapper.getPath(DOCUMENT_ID, DOCUMENT_LANGUAGE));

        return parameters;
    }

	/**
	 *  (non-Javadoc)
	 * @see org.apache.lenya.cms.task.AntTaskTest#getTarget()
	 */
    protected String getTarget() {
        return "publish";
    }
}
