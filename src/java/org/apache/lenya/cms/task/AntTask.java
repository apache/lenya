/*
 * $Id: AntTask.java
<License>
 * =======================================================================
 * Copyright (c) 2000 wyona. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. All advertising materials mentioning features or use of this
 *    software must display the following acknowledgment:
 *    "This product includes software developed by wyona (http://www.wyona.org)"
 *
 * 4. The name "wyona" must not be used to endorse or promote products
 *    derived from this software without prior written permission.
 *    For written permission , please contact contact@wyona.org
 *
 * 5. Products derived from this software may not be called "wyona"
 *    nor may "wyona" appear in their names without prior written
 *    permission of wyona.
 *
 * 6. Redistributions of any form whatsoever must retain the following
 *    acknowledgment:
 *    "This product includes software developed by wyona (http://www.wyona.org)"
 *
 * THIS SOFTWARE IS PROVIDED BY wyona "AS IS" WITHOUT ANY WARRANTY
 * EXPRESS OR IMPLIED, INCLUDING THE WARRANTY OF NON-INFRINGEMENT AND
 * THE IMPLIED WARRANTIES OF MERCHANTIBILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE. wyona WILL NOT BE LIABLE FOR ANY DAMAGES SUFFERED BY YOU AS
 * A RESULT OF USING THIS SOFTWARE. IN NO EVENT WILL wyona BE LIABLE FOR
 * ANY SPECIAL, INDIRECT OR CONSEQUENTIAL DAMAGES OR LOST PROFITS EVEN
 * IF wyona HAS BEEN ADVISED OF THE POSSIBILITY OF THEIR OCCURRENCE.
 * wyona WILL NOT BE LIABLE FOR ANY THIRD PARTY CLAIMS AGAINST YOU.
 * =======================================================================
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
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.ProjectHelper;
import org.apache.tools.ant.XmlLogger;
import org.apache.lenya.cms.publishing.PublishingEnvironment;
import org.apache.lenya.util.NamespaceMap;


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
public class AntTask
        extends AbstractTask {
    
    /**
     * Executes an Ant target.
     *
     * @param buildFile The build XML file.
     * @param target The name of the target to execute.
     * @param arguments A map mapping the command-line arguments to their values.
     * @param properties A map mapping the project properties to their values.
     */
    public void executeAntTarget(
            String servletContextPath,
            String publicationId,
            File publicationDirectory,
            File buildFile,
            String target,
            Map arguments,
            Map properties,
            File logFile) throws ExecutionException {
            
        Project project = new Project();
        project.setCoreLoader(getClass().getClassLoader());
        
        Throwable error = null;
        
        try {

            // create task log directory if it doesn't exist
            File logDirectory = logFile.getParentFile();
            if (!logDirectory.exists()) {
                logDirectory.mkdirs();
            }
            
            project.setUserProperty("XmlLogger.file", logFile.getAbsolutePath());
            XmlLogger logger = new XmlLogger();
            project.addBuildListener(logger);
            project.fireBuildStarted();
            
            project.init();
            project.setBaseDir(publicationDirectory);
            ProjectHelper helper = ProjectHelper.getProjectHelper();
            helper.parse(project, buildFile);
            
            project.setUserProperty(PUBLICATION_DIRECTORY, publicationDirectory.getAbsolutePath());
            project.setUserProperty(PUBLICATION_ID, publicationId);
            project.setUserProperty(SERVLET_CONTEXT_PATH, servletContextPath);
            
            for (Iterator keys = properties.keySet().iterator(); keys.hasNext(); ) {
                String key = (String) keys.next();
                project.setUserProperty(key, (String) properties.get(key));
            }
            
            if (target == null) {
                target = project.getDefaultTarget();
            }
            
            project.executeTarget(target);
            
        }
        catch (BuildException e) {
            error = e;
            throw new ExecutionException(e);
        }
        finally {
            project.fireBuildFinished(error);
        }
    }
    
    /**
     * Returns the filename of the logfile to write.
     */
    protected File getDefaultLogFile(File publicationDirectory) {
        Calendar now = new GregorianCalendar();
        return new File(publicationDirectory, LOG_PATH + dateFormat.format(now.getTime()) + ".xml");
    }
    
    
    public static final DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss-SSS");
    public static final String PUBLICATION_DIRECTORY = "pub.dir";
    public static final String PUBLICATION_ID = "pub.id";
    public static final String SERVLET_CONTEXT_PATH = "servlet.context";
    public static final String BUILDFILE = "buildfile";
    public static final String TARGET = "target";
    public static final String ANT_PREFIX = "ant";
    public static final String PROPERTIES_PREFIX = "properties";
    public static final String DEFAULT_BUILDFILE = "config/tasks/targets.xml";
    public static final String LOG_PATH = "logs/tasks/".replace('/', File.separatorChar);
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
        File buildFile;
        String target;
        Map arguments;
        Map properties;
        File logFile;
        
        try {

            String buildFileName
                = getParameters().getParameter("buildfile", DEFAULT_BUILDFILE)
                .replace('/', File.separatorChar);

            publicationId = getParameters().getParameter(PARAMETER_PUBLICATION_ID);
            
            if (publicationId.equals("")) {
                publicationDirectory = new File(".");
                buildFile = new File(buildFileName);
            }
            else {
                PublishingEnvironment environment
                    = new PublishingEnvironment(servletContextPath, publicationId);
                publicationDirectory = environment.getPublicationDirectory();
                buildFile = new File(publicationDirectory, buildFileName);
            }
            
            target = getParameters().getParameter(TARGET, null);

            Map parametersMap = Parameters.toProperties(getParameters());

            NamespaceMap antMap = new NamespaceMap(parametersMap, ANT_PREFIX);
            arguments = antMap.getMap();
            NamespaceMap propertiesMap = new NamespaceMap(parametersMap, PROPERTIES_PREFIX);
            properties = propertiesMap.getMap();

            // set logfile
            String logFilename = getParameters().getParameter(
                PARAMETER_LOGFILE, getDefaultLogFile(publicationDirectory).getAbsolutePath());
            logFile = new File(logFilename);
            
        }
        catch (ParameterException e) {
            throw new ExecutionException(e);
        }
        
        executeAntTarget(servletContextPath, publicationId, publicationDirectory, buildFile, target, arguments, properties, logFile);
        
    }
}
