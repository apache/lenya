/*
 * $Id
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
import java.io.IOException;

import junit.framework.Test;
import junit.framework.TestSuite;
import junit.textui.TestRunner;

import org.apache.avalon.framework.parameters.Parameters;
import org.apache.lenya.cms.PublicationHelper;

/**
 * @author andreas
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class PublisherTest extends AntTaskTest {

    /**
     * @param test
     */
    public PublisherTest(String test) {
        super(test);
    }
    
    /**
     * Creates a test suite.
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

    public static final String DOCUMENT_ID = "tutorial";
    public static final String LIVE_PATH = "content/live/".replace('/', File.separatorChar);
    
    protected void evaluateTest() throws IOException {
        File publicationDirectory = PublicationHelper.getPublication().getDirectory();
        String livePath = LIVE_PATH + DOCUMENT_ID + File.separator + "index.xml"; 
        File publishedFile = new File(publicationDirectory, livePath);
        System.out.println("Path of file to publish: " + livePath);
        assertTrue(publishedFile.exists());
        System.out.println("Published file exists: " + publishedFile.getCanonicalPath());
    }
    
    protected Parameters getTaskParameters() {
        Parameters parameters = super.getTaskParameters();
        parameters.setParameter("properties.publish.documentid", DOCUMENT_ID);
        parameters.setParameter("properties.publish.sources", DOCUMENT_ID + File.separator + "index.xml");
        return parameters;
    }
    
    protected String getTarget() {
        return "publish";
    }

}
