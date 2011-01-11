package org.apache.lenya.cms.cluster.impl;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import org.apache.avalon.framework.logger.LogEnabled;
import org.apache.avalon.framework.logger.Logger;
import org.apache.avalon.framework.service.ServiceException;
import org.apache.avalon.framework.service.ServiceManager;
import org.apache.avalon.framework.service.Serviceable;
import org.apache.commons.lang.Validate;
import org.apache.commons.lang.text.StrBuilder;
import org.apache.lenya.cms.observation.DocumentEvent;
import org.apache.lenya.cms.observation.RepositoryEvent;
import org.apache.lenya.cms.publication.DocumentFactory;
import org.apache.lenya.cms.publication.DocumentUtil;
import org.apache.lenya.cms.publication.Publication;
import org.apache.lenya.cms.publication.PublicationException;
import org.apache.lenya.cms.publication.PublicationManager;

/**
 * Thread implementation for executing rsync to synchronize
 * clustered content.
 */
public class RsyncExecutionThread extends Thread implements Serviceable, LogEnabled {

    private Logger logger;
    private ServiceManager manager;
    private PublicationManager publicationManager;

    private File baseDir;
    private String command;
    private String options;
    private String[] targets;

    private RepositoryEvent event;

    /**
     * C'tor.
     * @param event Repository event.
     * @param command rsync command.
     * @param options rsync options.
     * @param targets Array of targets.
     * @param baseDir rsync base directory.
     */
    public RsyncExecutionThread(RepositoryEvent event,
            String command, String options, String[] targets,
            File baseDir)
    {
        Validate.notNull(event, "event must not be null");
        Validate.notNull(command, "command must not be null");
        Validate.notNull(options, "options must not be null");
        Validate.notNull(targets, "targets must not be null");
        Validate.notNull(baseDir, "baseDir must not be null");
        this.event = event;
        this.command = command;
        this.options = options;
        this.targets = targets;
        this.baseDir = baseDir;
    }

    @Override
    public void run() {
        Validate.notNull(logger, "logger not initialized");
        Validate.notNull(manager, "manager not initialized");
        Validate.notNull(publicationManager,
                "publicationManager not initialized");
        String[] sources = null;
        try {
            if (event instanceof DocumentEvent) {
                DocumentEvent docEvent = (DocumentEvent) event;
                if (Publication.LIVE_AREA.equals(docEvent.getArea())) {
                    // Get source files affected by repository event.
                    sources = getSources(docEvent);
                }
            }
        } catch (Exception e) {
            if (getLogger().isErrorEnabled())
                getLogger().error("Error getting sources for rsync", e);
        }
        if (sources != null) {
            // Build whitespace separated list of source files.
            StrBuilder sourceBuilder = new StrBuilder()
                    .appendWithSeparators(sources, " ");
            String source = sourceBuilder.toString();
            // Synchronize with each target.
            for (String target : targets) {
                synchronizeWithHost(source, target);
            }
        }
    }

    /**
     * Get modified sources from DocumentEvent.
     * @param event Document event.
     * @return Space separated list of modified sources.
     * @throws PublicationException If creating publication fails.
     */
    private String[] getSources(DocumentEvent event)
    throws PublicationException
    {
        ArrayList<String> sources = new ArrayList<String>();
        String baseDirPath = baseDir.getAbsolutePath();
        DocumentFactory documentFactory = DocumentUtil
                .createDocumentFactory(manager, event.getSession());
        Publication publication = publicationManager.getPublication(
                documentFactory, event.getPublicationId());
        File contentDir = publication.getContentDirectory(
                event.getArea());
        File docFile = new File(contentDir, event.getUuid());
        if (docFile.exists()) {
            String path = docFile.getAbsolutePath();
            if (path.startsWith(baseDirPath)) {
                // Make path relative to baseDir.
                path = path.substring(baseDirPath.length() + 1);
            }
            sources.add(path);
        } else {
            if (getLogger().isErrorEnabled()) {
                getLogger().error("File for rsync synchronization not found [" +
                        docFile.getAbsolutePath() + "]");
            }
        }
        // Sync site tree and associated files too.
        File siteTreeFile = new File(contentDir, "sitetree.xml");
        String path = siteTreeFile.getAbsolutePath() + "*";
        if (path.startsWith(baseDirPath)) {
            // Make path relative to baseDir
            path = path.substring(baseDirPath.length() + 1);
        }
        sources.add(path);
        return sources.toArray(new String[sources.size()]);
    }

    /**
     * Execute rsync to synchronize source with target.
     * @param source Space separated list of source files. May use
     *      wildcards like '*' for shell expansion.
     * @param target Target.
     */
    private void synchronizeWithHost(String source, String target) {
        Validate.notNull(source, "source must not be null");
        Validate.notNull(target, "target must not be null");
        String[] cmd = new String[] { command, options, source, target };
        StrBuilder sb = new StrBuilder().appendWithSeparators(cmd, " ");
        String[] shellCmd = new String[] {"/bin/sh",  "-c",  sb.toString()};
        // Log command.
        if (getLogger().isDebugEnabled()) {
            StrBuilder cmdStr = new StrBuilder()
                    .appendWithSeparators(shellCmd, " ");
            getLogger().debug("Executing rsync command [" +
                    cmdStr.toString() + "]");
        }
        // Buffers for processing output and error output.
        // Note: output stream must be processed because limited
        // buffer capability may hang process.
        StringBuffer error =
            new StringBuffer("Error executing 'rsync' command\n");
        StringBuffer output = new StringBuffer("rsync command executed:\n");
        try {
            // Execute rsync command.
            Process proc = Runtime.getRuntime().exec(shellCmd, null,
                    baseDir);
            // Process error output.
            InputStream stderr = proc.getErrorStream();
            InputStreamReader isr = new InputStreamReader(stderr);
            BufferedReader br = new BufferedReader(isr);
            String line = null;
            while ( (line = br.readLine()) != null)
                error.append(line).append("\n");
            // Process output.
            InputStream stdout = proc.getInputStream();
            isr = new InputStreamReader(stdout);
            br = new BufferedReader(isr);
            line = null;
            while ( (line = br.readLine()) != null)
                output.append(line).append("\n");
            int exitCode = proc.waitFor();
            // Log output or any error.
            if (exitCode != 0) {
                if(getLogger().isErrorEnabled())
                    getLogger().error(error.toString());
            } else {
                if (getLogger().isDebugEnabled())
                    getLogger().debug(output.toString());
            }
        } catch (Exception e) {
            if (getLogger().isErrorEnabled())
                getLogger().error("Error executing 'rsync' command", e);
        }
    }

    public Logger getLogger() {
        return logger;
    }

    @Override
    public void service(ServiceManager manager) throws ServiceException {
        this.manager = manager;
        this.publicationManager = (PublicationManager) manager.lookup(
                PublicationManager.ROLE); 
    }

    @Override
    public void enableLogging(Logger logger) {
        this.logger = logger;
    }
}