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

/* $Id: AntTaskTest.java,v 1.19 2004/03/04 15:41:10 egli Exp $  */

package org.apache.lenya.cms.task;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.GregorianCalendar;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import junit.textui.TestRunner;

import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.avalon.framework.parameters.ParameterException;
import org.apache.avalon.framework.parameters.Parameters;
import org.apache.lenya.cms.PublicationHelper;
import org.apache.lenya.cms.publication.Publication;
import org.xml.sax.SAXException;


/**
 * Class for testing AntTasks.
 * Extend this class to test your own AntTask.
 * The target can be passed as a command-line argument.
 * If the argument is omitted, the target is "test".
 * Override {@link #getTarget()} to provide a hard-coded target.
 * Override {@link #evaluateTest()} to add your evaluation code.
 */
public class AntTaskTest extends TestCase {
    private static String target = "test";

    /**
     * Creates a new AntTaskTest object.
     * 
     * @param test the test
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
    public static void initialize(String[] args) {
        args = PublicationHelper.extractPublicationArguments(args);

        if (args.length > 0) {
            target = args[0];
        }
    }

    /**
     * Creates a test suite.
     * 
     * @return the test suite
     */
    public static Test getSuite() {
        return new TestSuite(AntTaskTest.class);
    }

    /**
     * Tests the AntTask class.
     * Template method, please override {@link #evaluateTest()} and {@link #getTarget()} and {@link #prepareTest()}.
     * 
     * @throws Exception if an error occurs
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
     * @param target the target of the task to test.
     * 
     * @throws ExecutionException if an error occurs
     * @throws IOException if an error occurs
     * @throws ParameterException if an error occurs
     * @throws SAXException if an error occurs
     * @throws ConfigurationException if an error occurs
     */
    protected void doTest(String target) throws ExecutionException, ParameterException, ConfigurationException, SAXException, IOException {
        System.out.println("Testing target [" + target + "]:");

        Publication publication = PublicationHelper.getPublication();

        TaskManager manager = new TaskManager(publication.getDirectory().getCanonicalPath());
        AntTask task = (AntTask) manager.getTask(TaskManager.ANT_TASK);

        Parameters parameters = getTaskParameters();
        parameters.setParameter(AntTask.PARAMETER_PUBLICATION_ID, publication.getId());
        parameters.setParameter(AntTask.PARAMETER_CONTEXT_PREFIX, "/lenya");
        parameters.setParameter(AntTask.PARAMETER_SERVLET_CONTEXT,
            publication.getServletContext().getCanonicalPath());
        parameters.setParameter(AntTask.TARGET, getTarget());
        task.parameterize(parameters);

        final GregorianCalendar beforeExecution = new GregorianCalendar();

        task.execute(publication.getServletContext().getCanonicalPath());

        File logDirectory = new File(publication.getDirectory(), AntTask.LOG_PATH);
        File[] logFiles = logDirectory.listFiles(new FileFilter() {
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
    * @throws Exception if an error occurs
     */
    protected void prepareTest() throws Exception {
        System.out.println("prepare");
    }

    /**
     * Override this method to add your test evaluation code.
     * @throws Exception if an error occurs
     */
    protected void evaluateTest() throws Exception {
        System.out.println("evaluate");
    }

    /** (non-Javadoc)
     * @see junit.framework.TestCase#setUp()
     */
    protected void setUp() throws Exception {
        if (PublicationHelper.getPublication() == null) {
            String[] args = {
                "/home/egli/build/jakarta-tomcat-4.1.21-LE-jdk14/webapps/lenya", "test"
            };
            PublicationHelper.extractPublicationArguments(args);
        }
    }
}
