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
package org.apache.lenya.cms.workflow;

import org.apache.lenya.cms.ac.FileUser;
import org.apache.lenya.cms.ac.Group;
import org.apache.lenya.cms.ac.Role;
import org.apache.lenya.cms.ac.User;
import org.apache.lenya.cms.publication.DefaultDocument;
import org.apache.lenya.cms.publication.Document;
import org.apache.lenya.cms.publication.DocumentType;
import org.apache.lenya.cms.publication.DocumentTypeBuildException;
import org.apache.lenya.cms.publication.DocumentTypeBuilder;
import org.apache.lenya.cms.publication.Publication;
import org.apache.lenya.workflow.Situation;
import org.apache.lenya.workflow.Transition;
import org.apache.lenya.workflow.WorkflowBuildException;
import org.apache.lenya.workflow.WorkflowFactory;
import org.apache.lenya.workflow.WorkflowInstance;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import junit.textui.TestRunner;

/**
 * @author andreas
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class WorkflowTest extends TestCase {

    /**
	 * @param arg0
	 */
	public WorkflowTest(String test) {
		super(test);
	}

	/**
     * The main program for the WorkflowTest class
     *
     * @param args The command line arguments
     */
    public static void main(String[] args) {

        String publicationId = args[0];
        String servletContextPath = args[1];
        String documentTypeName = args[2];
        WorkflowTest.init(servletContextPath, publicationId, documentTypeName);

        TestRunner.run(getSuite());
    }

    public static void init(
        String servletContextPath,
        String publicationId,
        String documentTypeName) {

        Publication publication = new Publication(publicationId, servletContextPath);

        DocumentTypeBuilder builder = new DocumentTypeBuilder();
        DocumentType documentType = null;
        Exception exception = null;
        try {
            documentType = builder.buildDocumentType(documentTypeName, publication);
        } catch (DocumentTypeBuildException e) {
            exception = e;
            e.printStackTrace(System.err);
        }
        assertNull(exception);
        
        Situation situation = null;
        WorkflowInstance instance = null;
        Document document = new DefaultDocument(publication,  "index");
        
        User user = new FileUser("test-user");
        Role role = new Role("editor");
        
        Group group = new Group();
        group.addRole(role);
        
        user.addGroup(group);
        
        WorkflowFactory factory = WorkflowFactoryImpl.newInstance(document, user);

        try {
            instance = factory.buildInstance();
            situation = factory.buildSituation();
        } catch (WorkflowBuildException e) {
            e.printStackTrace(System.err);
            exception = e;
        }
        assertNull(exception);
        
        setSituation(situation);
        setInstance(instance);

    }

    /**
     *
     */
    public static Test getSuite() {
        return new TestSuite(WorkflowTest.class);
    }

    private static WorkflowInstance instance;
    private static Situation situation;

    public void testWorkflow() {

        assertNotNull(getSituation());
        assertNotNull(getInstance());

        Transition transitions[] = getInstance().getExecutableTransitions(situation);

        System.out.println("Transitions:");
        for (int i = 0; i < transitions.length; i++) {
            System.out.println(" - " + transitions[i]);
        }
        System.out.println();

        CommandFilter filter = new CommandFilterImpl();
        String commands[] = filter.getExecutableCommands(getInstance(), getSituation());

        System.out.println("Commands:");
        for (int i = 0; i < commands.length; i++) {
            System.out.println(" - " + commands[i]);
        }
        System.out.println();

        System.out.println("Test completed.");

    }

    /**
     * @return
     */
    public static Situation getSituation() {
        return situation;
    }

    /**
     * @param situation
     */
    public static void setSituation(Situation sit) {
        situation = sit;
    }

    /**
     * @return
     */
    public static WorkflowInstance getInstance() {
        return instance;
    }

    /**
     * @param instance
     */
    public static void setInstance(WorkflowInstance ins) {
        instance = ins;
    }

    /* (non-Javadoc)
     * @see junit.framework.TestCase#setUp()
     */
    protected void setUp() throws Exception {
        if (getInstance() == null) {
            init("D:\\Development\\build\\tomcat-4.1.24\\webapps\\lenya", "default", "simple");
        }
    }

}
