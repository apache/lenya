/*
 * AntTaskTestCase.java
 *
 * Created on 28. April 2003, 12:28
 */

package org.apache.lenya.cms.task;

import java.io.File;
import java.io.FileFilter;
import java.util.GregorianCalendar;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import junit.textui.TestRunner;

import org.apache.avalon.framework.parameters.Parameters;
import org.apache.lenya.cms.publication.Publication;

/**
 *
 * @author  andreas
 */
public class AntTaskTest
    extends TestCase {
    
    /**
     * The main program for the IdentityTestCase class
     *
     * @param args The command line arguments
     */
    public static void main(String[] args) {
        setServletContextPath(args[0]);
        TestRunner.run(getSuite());
    }
    
    private static String servletContextPath;

    /**
     *
     */
    public static Test getSuite() {
        return new TestSuite(AntTaskTest.class);
    }

    /**
     * Tests the AntTask class.
     */
    public void testAntTask() {
        try {
            
            String publicationId = "default";
            String taskId = "ant-test";
            
            Publication publication = new Publication(publicationId, getServletContextPath());
            TaskManager manager = new TaskManager(publication.getDirectory().getCanonicalPath());
            AntTask task = (AntTask) manager.getTask(taskId);
            
            Parameters parameters = new Parameters();
            parameters.setParameter(Task.PARAMETER_PUBLICATION_ID, publicationId);
            task.parameterize(parameters);
            
            final GregorianCalendar beforeExecution = new GregorianCalendar();
            
            task.execute(getServletContextPath());
            
            File logDirectory = new File(publication.getDirectory(), AntTask.LOG_PATH);
            File logFiles[] = logDirectory.listFiles(new FileFilter() {
                public boolean accept(File file) {
                    return file.lastModified() > beforeExecution.getTimeInMillis();
                }
            });
            
            assertTrue(logFiles.length == 1);
            File logFile = logFiles[0];
            
        } catch (Exception e) {
            e.printStackTrace(System.err);
        }
    }
    

    /**
     * @return
     */
    public static String getServletContextPath() {
        return servletContextPath;
    }

    /**
     * @param string
     */
    public static void setServletContextPath(String string) {
        servletContextPath = string;
    }

}
