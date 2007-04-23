/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 *  contributor license agreements.  See the NOTICE file distributed with
 *  this work for additional information regarding copyright ownership.
 *  The ASF licenses this file to You under the Apache License, Version 2.0
 *  (the "License"); you may not use this file except in compliance with
 *  the License.  You may obtain a copy of the License at
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

package org.apache.lenya.cms.rc;

import java.io.FileNotFoundException;
import java.io.IOException;

import org.apache.avalon.framework.service.ServiceException;
import org.apache.lenya.ac.AccessControlException;
import org.apache.lenya.ac.impl.AbstractAccessControlTest;
import org.apache.lenya.cms.publication.Document;
import org.apache.lenya.cms.publication.DocumentManager;
import org.apache.lenya.cms.repository.RepositoryException;

/**
 * Revision Controller test
 */
public class RevisionControllerTest extends AbstractAccessControlTest {

    /**
     * @see <a href="http://issues.apache.org/bugzilla/show_bug.cgi?id=41005">Bug 41005</a>
     * @throws AccessControlException
     * @throws RepositoryException
     * @throws ServiceException
     */
    public void testCheckIn() throws AccessControlException, RepositoryException, ServiceException {
        login("lenya");
        
        DocumentManager docMgr = null;
        try {
            docMgr = (DocumentManager) getManager().lookup(DocumentManager.ROLE);
            Document source = getPublication("test").getArea("authoring").getSite().getNode("/links").getLink("en").getDocument();
            Document target = docMgr.addVersion(source, "authoring", "es");
            target.delete();
        }
        finally {
            if (docMgr != null) {
                getManager().release(docMgr);
            }
        }
        
        getFactory().getSession().commit();
    }

    public void testRevisionController() {

        String[] args = { "", "", "", "" };

        // TestRunner.run(getSuite());

        if (args.length != 4) {
            System.out
                    .println("Usage: "
                            + RevisionController.class.getName()
                            + " username(user who checkout) source(filename without the rootDirectory of the document to checkout) username(user who checkin) destination(filename without the rootDirectory of document to checkin)");

            return;
        }

        Document doc1 = null;
        Document doc2 = null;

        String identityS = args[0];
        String source = args[1];
        String identityD = args[2];
        String destination = args[3];
        RevisionController rc = new RevisionController(getLogger());
        try {
            rc.reservedCheckOut(doc1.getRepositoryNode(), identityS);
        } catch (FileNotFoundException e) // No such source file
        {
            System.out.println(e.toString());
        } catch (FileReservedCheckOutException e) // Source has been checked
                                                    // out already
        {
            System.out.println(e.toString());
            // System.out.println(error(e.source + "is already check out by " +
            // e.checkOutUsername + " since " + e.checkOutDate));
            return;

        } catch (IOException e) { // Cannot create rcml file
            System.out.println(e.toString());
            return;

        } catch (Exception e) {
            System.out.println(e.toString());
            return;
        }

        try {
            rc.reservedCheckIn(doc2.getRepositoryNode(), identityD, true, true);
        } catch (FileReservedCheckInException e) {
            System.out.println(e.toString());
        } catch (Exception e) {
            System.out.println(e.toString());
        }
    }

}
