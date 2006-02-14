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

/* $Id$  */

package org.apache.lenya.cms.publication;

import junit.framework.Test;
import junit.framework.TestSuite;
import junit.textui.TestRunner;

/**
 * 
 * To change the template for this generated type comment go to Window>Preferences>Java>Code
 * Generation>Code and Comments
 */
public class DefaultDocumentBuilderTest extends DefaultDocumentTest {
    /**
     * Constructor.
     * 
     * @param test The test.
     */
    public DefaultDocumentBuilderTest(String test) {
        super(test);
    }

    /**
     * The main program. The parameters are set from the command line arguments.
     * 
     * @param args The command line arguments.
     */
    public static void main(String[] args) {
        TestRunner.run(getSuite());
    }

    /**
     * Returns the test suite.
     * 
     * @return A test suite.
     */
    public static Test getSuite() {
        return new TestSuite(DefaultDocumentBuilderTest.class);
    }

    /**
     * @throws PublicationException 
     * @see org.apache.lenya.cms.publication.DefaultDocumentTest#getDocument(DocumentTestSet)
     */
    protected Document getDocument(DocumentTestSet set) throws PublicationException {
        Publication pub = PublicationUtil.getPublication(getManager(), "test");
        return getIdentityMap().getFromURL("/" + pub.getId() + "/" + set.getArea() + set.getUrl());
    }
}