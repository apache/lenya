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
package org.lenya.cms.task;

import org.lenya.cms.task.ExecutionException;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.Iterator;

import java.util.Map;
import org.apache.avalon.framework.parameters.ParameterException;
import org.apache.avalon.framework.parameters.Parameters;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.ProjectHelper;
import org.apache.tools.ant.XmlLogger;
import org.lenya.cms.publishing.PublishingEnvironment;
//import org.lenya.util.NamespaceMap;


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
    public void executeAntTarget(File buildFile, String target, Map arguments,
        Map properties) throws ExecutionException {
            
        Project project = new Project();
        project.setCoreLoader(getClass().getClassLoader());
        
        Throwable error = null;
        
        try {
            File logFile = new File(buildFile.getParentFile(), "ant-log.xml");
            project.setUserProperty("XmlLogger.file", logFile.getAbsolutePath());
            XmlLogger logger = new XmlLogger();
            project.addBuildListener(logger);
            project.fireBuildStarted();
            /*
            try {
                File logFile = new File(buildFile.getParentFile(), "ant-log.xml");
                logFile.createNewFile();
                logger.setOutputPrintStream(new PrintStream(new FileOutputStream(logFile)));
            }
            catch (Exception e) {
                throw new ExecutionException("Could not create log file: " + e);
            }
             */
            
            project.init();
            project.setBaseDir(buildFile.getParentFile());
            ProjectHelper helper = ProjectHelper.getProjectHelper();
            helper.parse(project, buildFile);
            
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
    
    public static final String BUILDFILE = "buildfile";
    public static final String TARGET = "target";
    public static final String ANT_PREFIX = "ant";
    public static final String PROPERTIES_PREFIX = "properties";
    public static final String DEFAULT_BUILDFILE = "config/tasks.xml";

    /**
     * Execute the task. All parameters must have been set with parameterize().
     *
     * @param servletContextPath The servlet context path.
     *
     * @throws ExecutionException when the execution of the task failed.
     */
    public void execute(String servletContextPath) throws ExecutionException {
        
        File buildFile;
        String target;
        Map arguments;
        Map properties;
        
        try {
            String publicationId = getParameters().getParameter(PARAMETER_PUBLICATION_ID);

            String relativeBuildFilePath = getParameters().getParameter("buildfile", DEFAULT_BUILDFILE);
            relativeBuildFilePath = relativeBuildFilePath.replace('/', File.separatorChar);

            PublishingEnvironment environment
                = new PublishingEnvironment(servletContextPath, publicationId);
            File publicationDirectory = environment.getPublicationDirectory();
            buildFile = new File(publicationDirectory, relativeBuildFilePath);

            target = getParameters().getParameter(TARGET, null);

            Map parametersMap = Parameters.toProperties(getParameters());

            //NamespaceMap antMap = new NamespaceMap(parametersMap, ANT_PREFIX);
            arguments = null; //antMap.getMap();
            //NamespaceMap propertiesMap = new NamespaceMap(parametersMap, PROPERTIES_PREFIX);
            properties = null; //propertiesMap.getMap();
        }
        catch (ParameterException e) {
            throw new ExecutionException(e);
        }
        
        executeAntTarget(buildFile, target, arguments, properties);
        
    }
}
