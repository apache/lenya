/*
 * AntTaskTestCase.java
 *
 * Created on 28. April 2003, 12:28
 */

package org.apache.lenya.cms.task;

import java.io.File;
import java.net.URI;
import java.net.URL;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import junit.textui.TestRunner;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.DefaultConfigurationBuilder;
import org.apache.avalon.framework.parameters.Parameters;
import org.apache.lenya.cms.task.AntTask;
import org.apache.lenya.cms.task.Task;
import org.apache.lenya.cms.task.TaskManager;

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
        TestRunner.run(getSuite());
    }

    /**
     *
     */
    public static Test getSuite() {
        TestSuite suite = new TestSuite();
        suite.addTest(new AntTaskTest("test"));
        return suite;
    }

    /**
     *
     */
    public AntTaskTest(String test) {
        super(test);
        try {
            
            String taskId = "ant-test";
             
            URL tasksConfigurationResource
                = getClass().getClassLoader().getResource("org/apache/lenya/cms/task/test-tasks.xconf");
            DefaultConfigurationBuilder configurationBuilder = new DefaultConfigurationBuilder();
            Configuration configuration
                = configurationBuilder.build(tasksConfigurationResource.toString());
             
            TaskManager manager = new TaskManager();
            manager.configure(configuration);
            AntTask task = (AntTask) manager.getTask(taskId);
            
            Parameters parameters = new Parameters();
            
            URL buildfileResource
                = getClass().getClassLoader().getResource("org/apache/lenya/cms/task/tasks.xml");
            File buildfile = new File(new URI(buildfileResource.toString()));
            parameters.setParameter(AntTask.BUILDFILE, buildfile.getAbsolutePath());
            task.parameterize(parameters);
            
            String servletContextPath = task.getParameters().getParameter(Task.PARAMETER_SERVLET_CONTEXT);
            task.execute(servletContextPath);
             
        } catch (Exception e) {
            e.printStackTrace(System.err);
        }
    }
    
    public void test() {
    }

}
