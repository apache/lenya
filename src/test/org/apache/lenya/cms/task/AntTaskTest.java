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
import java.io.FileFilter;
import java.io.IOException;
import java.util.GregorianCalendar;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import junit.textui.TestRunner;

import org.apache.avalon.framework.parameters.ParameterException;
import org.apache.avalon.framework.parameters.Parameters;
import org.apache.lenya.cms.PublicationHelper;
import org.apache.lenya.cms.publication.Publication;

/**
 * Class for testing AntTasks.
 * Extend this class to test your own AntTask.
 * The target can be passed as a command-line argument.
 * If the argument is omitted, the target is "test".
 * Override {@link #getTarget()} to provide a hard-coded target.
 * Override {@link #evaluateTest()} to add your evaluation code.
 * @author  andreas
 */
public class AntTaskTest extends TestCase {

    private static String target = "test";

    /**
     * Creates a new AntTaskTest object.
     */
    public AntTaskTest(String test) {
        super(test);
    }

    /**
     * The main program for the AntTaskTest class
     *
     * @param args The command line arguments
     */
    public static void main(String[] args) {
        initialize(args);
        TestRunner.run(getSuite());
    }
    
    /**
     * Initializes the parameters. Call this method from your subclass
     * {@link main(String[])} method.
     * @param args The command-line arguments.
     */
    public static void initialize(String args[]) {
        args = PublicationHelper.extractPublicationArguments(args);
        if (args.length > 0) {
            target = args[0];
        }
    }
    
    /**
     * Creates a test suite.
     */
    public static Test getSuite() {
        return new TestSuite(AntTaskTest.class);
    }

    /**
     * Tests the AntTask class.
     * Template method, please override {@link #evaluateTest()} and {@link #getTarget()} and {@link #prepareTest()}.
     */
    public final void testAntTask() throws Exception {
        prepareTest();
        doTest(getTarget());
        evaluateTest();
    }

    /**
     * Returns the target to test.
     * @return The target.
     */
    protected String getTarget() {
        return target;
    }

    /**
     * Tests an AntTask.
     * @param taskId The ID of the task to test.
     * @throws ExecutionException
     * @throws IOException
     * @throws ParameterException
     */
    protected void doTest(String target)
        throws ExecutionException, IOException, ParameterException {
            
        System.out.println("Testing target [" + target + "]:");
        
        Publication publication = PublicationHelper.getPublication();

        TaskManager manager = new TaskManager(publication.getDirectory().getCanonicalPath());
        AntTask task = (AntTask) manager.getTask(TaskManager.ANT_TASK);

        Parameters parameters = getTaskParameters();
        parameters.setParameter(AntTask.PARAMETER_PUBLICATION_ID, publication.getId());
        parameters.setParameter(AntTask.PARAMETER_SERVLET_CONTEXT,
            publication.getServletContext().getCanonicalPath());
        parameters.setParameter(AntTask.TARGET, getTarget());
        task.parameterize(parameters);

        final GregorianCalendar beforeExecution = new GregorianCalendar();

        task.execute(publication.getServletContext().getCanonicalPath());

        File logDirectory = new File(publication.getDirectory(), AntTask.LOG_PATH);
        File logFiles[] = logDirectory.listFiles(new FileFilter() {
            public boolean accept(File file) {
                return file.lastModified() > beforeExecution.getTimeInMillis();
            }
        });

        assertTrue(logFiles.length == 1);
        File logFile = logFiles[0];

    }
    
    /**
     * Returns the task parameters.
     * You don't need to specify the publication-id.
     * @return The task parameters.
     */
    protected Parameters getTaskParameters() {
        return new Parameters();
    }

	/**
	 * Override this method to prepare your test.
     * @throws Exception
	 */
	protected void prepareTest() throws Exception {
		System.out.println("prepare");
	}

    /**
     * Override this method to add your test evaluation code.
     * @throws Exception
     */
    protected void evaluateTest() throws Exception {
        System.out.println("evaluate");
    }

    /* (non-Javadoc)
     * @see junit.framework.TestCase#setUp()
     */
    protected void setUp() throws Exception {
        if (PublicationHelper.getPublication() == null) {
            String args[] = {
                "/home/edith/build/jakarta-tomcat-4.1.21-LE-jdk14/webapps/lenya",
                "test"
            };
            PublicationHelper.extractPublicationArguments(args);
        }
    }

}
