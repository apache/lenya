/*
$Id: AntTask.java,v 1.18 2004/02/02 02:50:39 stefano Exp $
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

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.Map;

import org.apache.avalon.framework.parameters.ParameterException;
import org.apache.avalon.framework.parameters.Parameters;
import org.apache.lenya.cms.publishing.PublishingEnvironment;
import org.apache.lenya.util.NamespaceMap;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.ProjectHelper;
import org.apache.tools.ant.XmlLogger;

/**
 * An object of this class is used to execute Ant tasks.
 * The task parameters are:
 *
 * <ul>
 *   <li><code><strong>publication-id</strong></code>: the publication ID</li>
 *   <li><code><strong>buildfile</strong> (optional)</code>: the location of the build file
 *       relative to the publication directory. If this parameter is
 *       not provided, the file is loaded from {@link #DEFAULT_BUILDFILE}.</li>
 *   <li><code><strong>target</strong> (optional)</code>: the build target. If this parameter
         is not provided, the default target is executed.</li>
 *   <li><code><strong>ant.*</strong></code>: the command-line parameters for Ant <strong>(not implemented yet!)</strong></li>
 *   <li><code><strong>properties.*</strong></code>: the project properties</li>
 * </ul>
 *
 * @author <a href="mailto:ah@wyona.org">Andreas Hartmann</a>
 */
public class AntTask extends AbstractTask {
    /**
     * Executes an Ant target.
     *
     * @param buildFile The build XML file.
     * @param target The name of the target to execute.
     * @param arguments A map mapping the command-line arguments to their values.
     * @param properties A map mapping the project properties to their values.
     * @param servletContextPath The context-path of the servlet
     * @param contextPrefix The context-prefix of the servlet 
     * @param publicationId The publication-id
     * @param publicationDirectory The directory of the publication
     * @param logFile The file where the log should go to
     * 
     * @throws ExecutionException if the execution failed
     */
    public void executeAntTarget(
        String servletContextPath,
        String contextPrefix,
        String publicationId,
        File publicationDirectory,
        File buildFile,
        String target,
        Map arguments,
        Map properties,
        File logFile)
        throws ExecutionException {
        Project project = new Project();
        project.setCoreLoader(getClass().getClassLoader());

        Throwable error = null;

        try {
            // create task log directory if it doesn't exist
            File logDirectory = logFile.getParentFile();

            if (!logDirectory.exists()) {
                logDirectory.mkdirs();
            }

            project.setUserProperty(
                "XmlLogger.file",
                logFile.getAbsolutePath());

            XmlLogger logger = new XmlLogger();
            project.addBuildListener(logger);
            project.fireBuildStarted();

            project.init();
            project.setBaseDir(publicationDirectory);

            ProjectHelper helper = ProjectHelper.getProjectHelper();
            helper.parse(project, buildFile);

            project.setUserProperty(
                PUBLICATION_DIRECTORY,
                publicationDirectory.getAbsolutePath());
            project.setUserProperty(PUBLICATION_ID, publicationId);
            project.setUserProperty(SERVLET_CONTEXT_PATH, servletContextPath);
			project.setUserProperty(CONTEXT_PREFIX, contextPrefix);

            for (Iterator keys = properties.keySet().iterator();
                keys.hasNext();
                ) {
                String key = (String)keys.next();
                project.setUserProperty(key, (String)properties.get(key));
            }

            if (target == null) {
                target = project.getDefaultTarget();
            }

            project.executeTarget(target);
        } catch (BuildException e) {
            error = e;
            throw new ExecutionException(e);
        } finally {
            project.fireBuildFinished(error);
        }
    }

    /**
     * Returns the filename of the logfile to write.
     * 
     * @param publicationDirectory for which publication the 
     * filename of the logfile is requested
     * 
     * @return the file path for the log file
     */
    protected File getDefaultLogFile(File publicationDirectory) {
        Calendar now = new GregorianCalendar();

        return new File(
            publicationDirectory,
            LOG_PATH + dateFormat.format(now.getTime()) + ".xml");
    }

    public static final DateFormat dateFormat =
        new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss-SSS");
    public static final String PUBLICATION_DIRECTORY = "pub.dir";
    public static final String PUBLICATION_ID = "pub.id";
    public static final String SERVLET_CONTEXT_PATH = "servlet.context";
    public static final String CONTEXT_PREFIX = "context.prefix";
    public static final String BUILDFILE = "buildfile";
    public static final String TARGET = "target";
    public static final String ANT_PREFIX = "ant";
    public static final String PROPERTIES_PREFIX = "properties";
    public static final String DEFAULT_BUILDFILE = "config/tasks/targets.xml";
    public static final String LOG_PATH =
        "logs/tasks/".replace('/', File.separatorChar);
    public static final String PARAMETER_LOGFILE = "logfile";

    /**
     * Execute the task. All parameters must have been set with parameterize().
     *
     * @param servletContextPath The servlet context path.
     *
     * @throws ExecutionException when the execution of the task failed.
     */
    public void execute(String servletContextPath) throws ExecutionException {
        String publicationId;
        File publicationDirectory;
        String contextPrefix;
        File buildFile;
        String target;
        Map arguments;
        Map properties;
        File logFile;

        try {
            String buildFileName =
                getParameters().getParameter(
                    "buildfile",
                    DEFAULT_BUILDFILE).replace(
                    '/',
                    File.separatorChar);

            publicationId =
                getParameters().getParameter(PARAMETER_PUBLICATION_ID);
            contextPrefix = getParameters().getParameter(PARAMETER_CONTEXT_PREFIX);
            

            if (publicationId.equals("")) {
                publicationDirectory = new File(".");
                buildFile = new File(buildFileName);
            } else {
                PublishingEnvironment environment =
                    new PublishingEnvironment(
                        servletContextPath,
                        publicationId);
                publicationDirectory = environment.getPublicationDirectory();
                buildFile = new File(publicationDirectory, buildFileName);
            }

            target = getParameters().getParameter(TARGET, null);

            Map parametersMap = Parameters.toProperties(getParameters());

            NamespaceMap antMap = new NamespaceMap(parametersMap, ANT_PREFIX);
            arguments = antMap.getMap();

            NamespaceMap propertiesMap =
                new NamespaceMap(parametersMap, PROPERTIES_PREFIX);
            properties = propertiesMap.getMap();

            // set logfile
            String logFilename =
                getParameters().getParameter(
                    PARAMETER_LOGFILE,
                    getDefaultLogFile(publicationDirectory).getAbsolutePath());
            logFile = new File(logFilename);
        } catch (ParameterException e) {
            throw new ExecutionException(e);
        }

        executeAntTarget(
            servletContextPath,
            contextPrefix,
            publicationId,
            publicationDirectory,
            buildFile,
            target,
            arguments,
            properties,
            logFile);
    }
}
