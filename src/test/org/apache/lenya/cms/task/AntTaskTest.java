/*
$Id
<License>

 ============================================================================
                   The Apache Software License, Version 1.1
 ============================================================================

 Copyright (C) 1999-2003 The Apache Software Foundation. All rights reserved.

 Redistribution and use in source and binary forms, with or without modifica-
 tion, are permitted provided that the following conditions are met:

 1. Redistributions of  source code must  retain the above copyright  notice,
    this list of conditions and the following disclaimer.

 2. Redistributions in binary form must reproduce the above copyright notice,
    this list of conditions and the following disclaimer in the documentation
    and/or other materials provided with the distribution.

 3. The end-user documentation included with the redistribution, if any, must
    include  the following  acknowledgment:  "This product includes  software
    developed  by the  Apache Software Foundation  (http://www.apache.org/)."
    Alternately, this  acknowledgment may  appear in the software itself,  if
    and wherever such third-party acknowledgments normally appear.

 4. The names "Apache Lenya" and  "Apache Software Foundation"  must  not  be
    used to  endorse or promote  products derived from  this software without
    prior written permission. For written permission, please contact
    apache@apache.org.

 5. Products  derived from this software may not  be called "Apache", nor may
    "Apache" appear  in their name,  without prior written permission  of the
    Apache Software Foundation.

 THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED WARRANTIES,
 INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 FITNESS  FOR A PARTICULAR  PURPOSE ARE  DISCLAIMED.  IN NO  EVENT SHALL  THE
 APACHE SOFTWARE  FOUNDATION  OR ITS CONTRIBUTORS  BE LIABLE FOR  ANY DIRECT,
 INDIRECT, INCIDENTAL, SPECIAL,  EXEMPLARY, OR CONSEQUENTIAL  DAMAGES (INCLU-
 DING, BUT NOT LIMITED TO, PROCUREMENT  OF SUBSTITUTE GOODS OR SERVICES; LOSS
 OF USE, DATA, OR  PROFITS; OR BUSINESS  INTERRUPTION)  HOWEVER CAUSED AND ON
 ANY  THEORY OF LIABILITY,  WHETHER  IN CONTRACT,  STRICT LIABILITY,  OR TORT
 (INCLUDING  NEGLIGENCE OR  OTHERWISE) ARISING IN  ANY WAY OUT OF THE  USE OF
 THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

 This software  consists of voluntary contributions made  by many individuals
 on  behalf of the Apache Software  Foundation and was  originally created by
 Michael Wechner <michi@apache.org>. For more information on the Apache Soft-
 ware Foundation, please see <http://www.apache.org/>.

 Lenya includes software developed by the Apache Software Foundation, W3C,
 DOM4J Project, BitfluxEditor, Xopus, and WebSHPINX.
</License>
*/
package org.apache.lenya.cms.task;

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

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;

import java.util.GregorianCalendar;


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
