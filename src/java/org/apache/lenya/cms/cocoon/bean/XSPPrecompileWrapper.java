/*
 * Copyright 1999-2004 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.lenya.cms.cocoon.bean;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;

import org.apache.avalon.excalibur.component.ExcaliburComponentManager;
import org.apache.avalon.excalibur.logger.LogKitLoggerManager;
import org.apache.avalon.framework.component.ComponentException;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.avalon.framework.configuration.DefaultConfigurationBuilder;
import org.apache.avalon.framework.container.ContainerUtil;
import org.apache.avalon.framework.context.ContextException;
import org.apache.avalon.framework.context.DefaultContext;
import org.apache.avalon.framework.logger.LogKitLogger;
import org.apache.avalon.framework.logger.Logger;
import org.apache.cocoon.Cocoon;
import org.apache.cocoon.Constants;
import org.apache.cocoon.ProcessingException;
import org.apache.cocoon.components.CocoonComponentManager;
import org.apache.cocoon.components.language.generator.CompiledComponent;
import org.apache.cocoon.components.language.generator.ProgramGenerator;
import org.apache.cocoon.environment.Environment;
import org.apache.cocoon.environment.commandline.CommandLineContext;
import org.apache.cocoon.environment.commandline.FileSavingEnvironment;
import org.apache.cocoon.environment.commandline.LinkSamplingEnvironment;
import org.apache.cocoon.util.ClassUtils;
import org.apache.cocoon.util.IOUtils;
import org.apache.cocoon.util.NetUtils;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.PosixParser;
import org.apache.commons.lang.SystemUtils;
import org.apache.excalibur.source.Source;
import org.apache.excalibur.source.SourceResolver;
import org.apache.log.Hierarchy;
import org.apache.log.Priority;
import org.xml.sax.SAXException;


/**
 * This is simple Wrapper like CocoonWrapper and can only
 * precompile all XSP in the context-directory.
 */
public class XSPPrecompileWrapper {

	protected static final String DEFAULT_USER_AGENT = Constants.COMPLETE_NAME;

	protected static final String DEFAULT_ACCEPT = "text/html, */*";

	// User Supplied Parameters
	private String contextDir = Constants.DEFAULT_CONTEXT_DIR;

	private String configFile = null;

	private String workDir = Constants.DEFAULT_WORK_DIR;

	private String logKit = null;

	protected String logger = null;

	protected String logLevel = "ERROR";

	private String userAgent = DEFAULT_USER_AGENT;

	private String accept = DEFAULT_ACCEPT;

	private List classList = new ArrayList();

	// Objects used alongside User Supplied Parameters
	private File context;

	private File work;

	private File conf;

	// Internal Objects
	private CommandLineContext cliContext;

	private LogKitLoggerManager logManager;

	private Cocoon cocoon;

	protected Logger log;

	private Map attributes = new HashMap();

	private HashMap empty = new HashMap();

	private boolean initialized = false;

	private SourceResolver sourceResolver;

	private static Options options;

	protected static final String HELP_OPT = "h";

	protected static final String LOG_KIT_OPT = "k";

	protected static final String CONTEXT_DIR_OPT = "c";

	protected static final String WORK_DIR_OPT = "w";

	protected static final String CONFIG_FILE_OPT = "C";

	protected static final String LOG_KIT_LONG = "logKitconfig";

	protected static final String CONTEXT_DIR_LONG = "contextDir";

	protected static final String WORK_DIR_LONG = "workDir";

	protected static final String HELP_LONG = "help";

	protected static final String CONFIG_FILE_LONG = "configFile";

	/**
	 * INITIALISATION METHOD
	 * @throws IOException
	 */
	public void initialize() throws IOException {
		// @todo@ these should log then throw exceptions back to the caller, not
		// use system.exit()

		// Create a new hierarchy. This is needed when CocoonBean is called from
		// within a CocoonServlet call, in order not to mix logs
		final Hierarchy hierarchy = new Hierarchy();

		final Priority priority = Priority.getPriorityForName(this.logLevel);
		hierarchy.setDefaultPriority(priority);

		// Install a temporary logger so that getDir() can log if needed
		this.log = new LogKitLogger(hierarchy.getLoggerFor(""));

		try {
            // First of all, initialize the logging system

            // Setup the application context with context-dir and work-dir that
            // can be used in logkit.xconf
            this.context = getDir(this.contextDir, "context");
            this.work = getDir(this.workDir, "working");
            DefaultContext appContext = new DefaultContext();
            appContext.put(Constants.CONTEXT_WORK_DIR, this.work);

            this.logManager = new LogKitLoggerManager(hierarchy);
            this.logManager.enableLogging(this.log);

            if (this.logKit != null) {
            	final FileInputStream fis = new FileInputStream(this.logKit);
            	final DefaultConfigurationBuilder builder = new DefaultConfigurationBuilder();
            	final Configuration logKitConf = builder.build(fis);
            	final DefaultContext subcontext = new DefaultContext(appContext);
            	subcontext.put("context-root", this.contextDir);
            	subcontext.put("context-work", this.workDir);
            	this.logManager.contextualize(subcontext);
            	this.logManager.configure(logKitConf);
            	if (this.logger != null) {
            		this.log = this.logManager.getLoggerForCategory(this.logger);
            	} else {
            		this.log = this.logManager.getLoggerForCategory("cocoon");
            	}
            }

            this.conf = getConfigurationFile(this.context, this.configFile);

            this.cliContext = new CommandLineContext(this.contextDir);
            this.cliContext.enableLogging(this.log);

            appContext.put(Constants.CONTEXT_ENVIRONMENT_CONTEXT, this.cliContext);
            appContext.put(Constants.CONTEXT_CLASS_LOADER, org.apache.cocoon.bean.CocoonWrapper.class
            		.getClassLoader());
            appContext.put(Constants.CONTEXT_CLASSPATH,
            		getClassPath(this.contextDir));
            appContext.put(Constants.CONTEXT_UPLOAD_DIR, this.contextDir
            		+ "upload-dir");
            File cacheDir = getDir(this.workDir + File.separator + "cache-dir",
            		"cache");
            appContext.put(Constants.CONTEXT_CACHE_DIR, cacheDir);
            appContext.put(Constants.CONTEXT_CONFIG_URL, this.conf.toURL());
            appContext.put(Constants.CONTEXT_DEFAULT_ENCODING, "ISO-8859-1");

            loadClasses(this.classList);

            this.cocoon = new Cocoon();
            ContainerUtil.enableLogging(this.cocoon, this.log);
            ContainerUtil.contextualize(this.cocoon, appContext);
            this.cocoon.setLoggerManager(this.logManager);
            ContainerUtil.initialize(this.cocoon);

            this.sourceResolver = (SourceResolver) getComponentManager().lookup(
            		SourceResolver.ROLE);
        } catch (final IllegalStateException e) {
			this.log.fatalError("Exception caught", e);
			throw new IOException(e.toString());
        } catch (final FileNotFoundException e) {
			this.log.fatalError("Exception caught", e);
			throw new IOException(e.toString());
        } catch (final ConfigurationException e) {
			this.log.fatalError("Exception caught", e);
			throw new IOException(e.toString());
        } catch (final ContextException e) {
			this.log.fatalError("Exception caught", e);
			throw new IOException(e.toString());
        } catch (final MalformedURLException e) {
			this.log.fatalError("Exception caught", e);
			throw new IOException(e.toString());
        } catch (final ComponentException e) {
			this.log.fatalError("Exception caught", e);
			throw new IOException(e.toString());
        } catch (final IOException e) {
			this.log.fatalError("Exception caught", e);
			throw new IOException(e.toString());
        } catch (final SAXException e) {
			this.log.fatalError("Exception caught", e);
			throw new IOException(e.toString());
        } catch (final Exception e) {
			this.log.fatalError("Exception caught", e);
			throw new IOException(e.toString());
        }


		this.initialized = true;
	}

	protected ExcaliburComponentManager getComponentManager() {
		return this.cocoon.getComponentManager();
	}

	/**
	 * Look around for the configuration file.
	 * 
	 * @param dir
	 *            a <code>File</code> where to look for configuration files
	 * @param _configFile The config file
	 * @return a <code>File</code> representing the configuration
	 * @exception IOException
	 *                if an error occurs
	 */
	private File getConfigurationFile(File dir, String _configFile)
			throws IOException {
		File _conf;
		if (_configFile == null) {
			_conf = tryConfigurationFile(dir + File.separator
					+ Constants.DEFAULT_CONF_FILE);
			if (_conf == null) {
				_conf = tryConfigurationFile(dir + File.separator + "WEB-INF"
						+ File.separator + Constants.DEFAULT_CONF_FILE);
			}
			if (_conf == null) {
				_conf = tryConfigurationFile(SystemUtils.USER_DIR
						+ File.separator + Constants.DEFAULT_CONF_FILE);
			}
			if (_conf == null) {
				_conf = tryConfigurationFile("/usr/local/etc/"
						+ Constants.DEFAULT_CONF_FILE);
			}
		} else {
			_conf = new File(_configFile);
			if (!_conf.exists()) {
				_conf = new File(dir, _configFile);
			}
		}
		if (_conf == null) {
			this.log.error("Could not find the configuration file.");
			throw new FileNotFoundException(
					"The configuration file could not be found.");
		}
		return _conf;
	}

	/**
	 * Try loading the configuration file from a single location
	 * @param filename The configuration filename
	 * @return The configuration file
	 */
	private File tryConfigurationFile(String filename) {
		if (this.log.isDebugEnabled()) {
			this.log.debug("Trying configuration file at: " + filename);
		}
		File _conf = new File(filename);
		if (_conf.canRead()) {
			return _conf;
		}
		return null;
	}

	/**
	 * Get a <code>File</code> representing a directory.
	 * @param dir
	 *            a <code>String</code> with a directory name
	 * @param type
	 *            a <code>String</code> describing the type of directory
	 * @return a <code>File</code> value
	 * @exception IOException
	 *                if an error occurs
	 */
	private File getDir(String dir, String type) throws IOException {
		if (this.log.isDebugEnabled()) {
			this.log.debug("Getting handle to " + type + " directory '" + dir + "'");
		}
		File d = new File(dir);

		if (!d.exists()) {
			if (!d.mkdirs()) {
				throw new IOException("Error creating " + type + " directory '"
						+ d + "'");
			}
		}

		if (!d.isDirectory()) {
			throw new IOException("'" + d + "' is not a directory.");
		}

		if (!d.canRead()) {
			throw new IOException("Directory '" + d + "' is not readable");
		}

		if ("working".equals(type) && !d.canWrite()) {
			throw new IOException("Directory '" + d + "' is not writable");
		}

		return d;
	}

	protected void finalize() throws Throwable {
		dispose();
		super.finalize();
	}

	protected void loadClasses(List _classList) {
		if (_classList != null) {
			for (Iterator i = _classList.iterator(); i.hasNext();) {
				String className = (String) i.next();
				try {
                    if (this.log.isDebugEnabled()) {
                    	this.log.debug("Trying to load class: " + className);
                    }
                    ClassUtils.loadClass(className).newInstance();
                } catch (final InstantiationException e) {
					if (this.log.isWarnEnabled()) {
						this.log.warn("Could not force-load class: " + className, e);
					}
					// Do not throw an exception, because it is not a fatal
					// error.
                } catch (final IllegalAccessException e) {
					if (this.log.isWarnEnabled()) {
						this.log.warn("Could not force-load class: " + className, e);
					}
					// Do not throw an exception, because it is not a fatal
					// error.
                } catch (final ClassNotFoundException e) {
					if (this.log.isWarnEnabled()) {
						this.log.warn("Could not force-load class: " + className, e);
					}
					// Do not throw an exception, because it is not a fatal
					// error.
                }
			}
		}
	}

	//
	// GETTERS AND SETTERS FOR CONFIGURATION PROPERTIES
	//

	/**
	 * Set LogKit configuration file name
	 * @param _logKit
	 *            LogKit configuration file
	 */
	public void setLogKit(String _logKit) {
		this.logKit = _logKit;
	}

	/**
	 * Set log level. Default is DEBUG.
	 * @param _logLevel
	 *            log level
	 */
	public void setLogLevel(String _logLevel) {
		this.logLevel = _logLevel;
	}

	/**
	 * Set logger category as default logger for the Cocoon engine
	 * 
	 * @param _logger
	 *            logger category
	 */
	public void setLogger(String _logger) {
		this.logger = _logger;
	}

	/**
	 * Return the name of the logger
	 * @return The name of the logger
	 */
	public String getLoggerName() {
		return this.logger;
	}

	/**
	 * Set context directory
	 * 
	 * @param _contextDir
	 *            context directory
	 */
	public void setContextDir(String _contextDir) {
		this.contextDir = _contextDir;
	}

	/**
	 * Set working directory
	 * 
	 * @param wDir
	 *            working directory
	 */
	public void setWorkDir(String wDir) {
		this.workDir = wDir;
	}

	/**
	 * Set the configuration file
	 * @param cFile The configuration file
	 */
	public void setConfigFile(String cFile) {
		this.configFile = cFile;
	}

	/**
	 * Set user agent options
	 * @param _userAgent The user agent
	 */
	public void setAgentOptions(String _userAgent) {
		this.userAgent = _userAgent;
	}

	/**
	 * Set the accept header
	 * @param _accept The accept header
	 */
	public void setAcceptOptions(String _accept) {
		this.accept = _accept;
	}

	/**
	 * Set the class name
	 * @param _className The class name
	 */
	public void addLoadedClass(String _className) {
		this.classList.add(_className);
	}

	/**
	 * Add the loaded classes
	 * @param cList The list of classes to load
	 */
	public void addLoadedClasses(List cList) {
		this.classList.addAll(cList);
	}

	/**
	 * Process single URI into given output stream.
	 * 
	 * @param uri
	 *            to process
	 * @param outputStream
	 *            to write generated contents into
	 * @throws Exception
	 */
	public void processURI(String uri, OutputStream outputStream)
			throws Exception {

		if (!this.initialized) {
			initialize();
		}
		this.log.info("Processing URI: " + uri);

		// Get parameters, deparameterized URI and path from URI
		final TreeMap parameters = new TreeMap();
		final String deparameterizedURI = NetUtils.deparameterize(uri,
				parameters);
		parameters.put("user-agent", this.userAgent);
		parameters.put("accept", this.accept);

		int status = getPage(deparameterizedURI, 0L, parameters, null, null,
				outputStream);

		if (status >= 400) {
			throw new ProcessingException("Resource not found: " + status);
		}
	}

	/**
	 * Disposal method
	 */
	public void dispose() {
		if (this.initialized) {
			this.initialized = false;
			ContainerUtil.dispose(this.cocoon);
			this.cocoon = null;
			this.logManager.dispose();
			if (this.log.isDebugEnabled()) {
				this.log.debug("Disposed");
			}
		}
	}

	/**
	 * Samples an URI for its links.
	 * 
	 * @param deparameterizedURI
	 *            a <code>String</code> value of an URI to start sampling from
	 * @param parameters
	 *            a <code>Map</code> value containing request parameters
	 * @return a <code>Collection</code> of links
	 * @exception Exception
	 *                if an error occurs
	 */
	protected Collection getLinks(String deparameterizedURI, Map parameters)
			throws Exception {

		parameters.put("user-agent", this.userAgent);
		parameters.put("accept", this.accept);

		LinkSamplingEnvironment env = new LinkSamplingEnvironment(
				deparameterizedURI, this.context, this.attributes, parameters,
				this.cliContext, this.log);
		processLenient(env);
		return env.getLinks();
	}

	/**
	 * Processes an URI for its content.
	 * 
	 * @param deparameterizedURI
	 *            a <code>String</code> value of an URI to start sampling from
	 * @param lastModified The last modified date
	 * @param parameters
	 *            a <code>Map</code> value containing request parameters
	 * @param links
	 *            a <code>Map</code> value
	 * @param gatheredLinks
	 * @param stream
	 *            an <code>OutputStream</code> to write the content to
	 * @return a <code>String</code> value for the content
	 * @exception Exception
	 *                if an error occurs
	 */
	protected int getPage(String deparameterizedURI, long lastModified,
			Map parameters, Map links, List gatheredLinks, OutputStream stream)
			throws Exception {

		parameters.put("user-agent", this.userAgent);
		parameters.put("accept", this.accept);

		FileSavingEnvironment env = new FileSavingEnvironment(
				deparameterizedURI, lastModified, this.context, this.attributes,
				parameters, links, gatheredLinks, this.cliContext, stream, this.log);

		// Here Cocoon can throw an exception if there are errors in processing
		// the page
		this.cocoon.process(env);

		// if we get here, the page was created :-)
		int status = env.getStatus();
		if (!env.isModified()) {
			status = -1;
		}
		return status;
	}

	/** Class <code>NullOutputStream</code> here. */
	static class NullOutputStream extends OutputStream {
		/** 
		 * @see java.io.OutputStream#write(int)
		 */
		public void write(int b) throws IOException {
		    // do nothing
		}

		/**
		 * @see java.io.OutputStream#write(byte[])
		 */
		public void write(byte b[]) throws IOException {
		    // do nothing
		}

		/**
		 * @see java.io.OutputStream#write(byte[], int, int)
		 */
		public void write(byte b[], int off, int len) throws IOException {
		    // do nothing
		}
	}

	/**
	 * Analyze the type of content for an URI.
	 * @param deparameterizedURI
	 *            a <code>String</code> value to analyze
	 * @param parameters
	 *            a <code>Map</code> value for the request
	 * @return a <code>String</code> value denoting the type of content
	 * @exception Exception
	 *                if an error occurs
	 */
	protected String getType(String deparameterizedURI, Map parameters)
			throws Exception {

		parameters.put("user-agent", this.userAgent);
		parameters.put("accept", this.accept);

		FileSavingEnvironment env = new FileSavingEnvironment(
				deparameterizedURI, this.context, this.attributes, parameters, this.empty,
				null, this.cliContext, new NullOutputStream(), this.log);
		processLenient(env);
		return env.getContentType();
	}

	/**
	 * Try to process something but don't throw a ProcessingException.
	 * @param env
	 *            the <code>Environment</code> to process
	 * @return boolean true if no error were cast, false otherwise
	 * @exception Exception
	 *                if an error occurs, except RNFE
	 */
	private boolean processLenient(Environment env) throws Exception {
		try {
			this.cocoon.process(env);
		} catch (ProcessingException pe) {
			return false;
		}
		return true;
	}

	/**
	 * This builds the important ClassPath used by this class. It does so in a
	 * neutral way. It iterates in alphabetical order through every file in the
	 * lib directory and adds it to the classpath.
	 * Also, we add the files to the ClassLoader for the Cocoon system. In order
	 * to protect ourselves from skitzofrantic classloaders, we need to work
	 * with a known one.
	 * @param _context
	 *            The context path
	 * @return a <code>String</code> value
	 */
	protected String getClassPath(final String _context) {
		StringBuffer buildClassPath = new StringBuffer();

		String classDir = _context + "/WEB-INF/classes";
		buildClassPath.append(classDir);

		File root = new File(_context + "/WEB-INF/lib");
		if (root.isDirectory()) {
			File[] libraries = root.listFiles();
			Arrays.sort(libraries);
			for (int i = 0; i < libraries.length; i++) {
				if (libraries[i].getAbsolutePath().endsWith(".jar")) {
					buildClassPath.append(File.pathSeparatorChar).append(
							IOUtils.getFullFilename(libraries[i]));
				}
			}
		}

		buildClassPath.append(File.pathSeparatorChar).append(
				SystemUtils.JAVA_CLASS_PATH);

		// Extra class path is necessary for non-classloader-aware java
		// compilers to compile XSPs
		// buildClassPath.append(File.pathSeparatorChar)
		// .append(getExtraClassPath(context));

		if (this.log.isDebugEnabled()) {
			this.log.debug("Context classpath: " + buildClassPath);
		}
		return buildClassPath.toString();
	}

	/**
	 * Allow subclasses to recursively precompile XSPs.
	 */
	protected void precompile() {
		recursivelyPrecompile(this.context, this.context);
	}

	/**
	 * Recurse the directory hierarchy and process the XSP's.
	 * @param _contextDir
	 *            a <code>File</code> value for the context directory
	 * @param file
	 *            a <code>File</code> value for a single XSP file or a
	 *            directory to scan recursively
	 */
	private void recursivelyPrecompile(File _contextDir, File file) {
		if (file.isDirectory()) {
			String entries[] = file.list();
			for (int i = 0; i < entries.length; i++) {
				recursivelyPrecompile(_contextDir, new File(file, entries[i]));
			}
		} else if (file.getName().toLowerCase(Locale.ENGLISH).endsWith(".xmap")) {
			try {
				// necessary?
				this.processXMAP(IOUtils.getContextFilePath(_contextDir
						.getCanonicalPath(), file.getCanonicalPath()));
			} catch (Exception e) {
				System.err.println(e.toString());
			}
		} else if (file.getName().toLowerCase(Locale.ENGLISH).endsWith(".xsp")) {
			try {
				this.processXSP(IOUtils.getContextFilePath(_contextDir
						.getCanonicalPath(), file.getCanonicalPath()));
			} catch (Exception e) {
				System.err.println(e.toString());
			}
		}
	}

	/**
	 * Process a single XSP file
	 * @param uri
	 *            a <code>String</code> pointing to an xsp URI
	 * @exception Exception
	 *                if an error occurs
	 */
	protected void processXSP(String uri) throws Exception {

		String markupLanguage = "xsp";
		String programmingLanguage = "java";
		Environment env = new LinkSamplingEnvironment("/", this.context, this.attributes,
				null, this.cliContext, this.log);
		precompile(uri, env, markupLanguage, programmingLanguage);
	
	}

	/**
	 * Process a single XMAP file
	 * @param uri
	 *            a <code>String</code> pointing to an xmap URI
	 * @exception Exception
	 *                if an error occurs
	 */
	protected void processXMAP(String uri) throws Exception {

		String markupLanguage = "sitemap";
		String programmingLanguage = "java";
		Environment env = new LinkSamplingEnvironment("/", this.context, this.attributes,
				null, this.cliContext, this.log);
		precompile(uri, env, markupLanguage, programmingLanguage);
	}

	/**
	 * Process the given <code>Environment</code> to generate Java code for
	 * specified XSP files.
	 * @param fileName
	 *            a <code>String</code> value
	 * @param environment
	 *            an <code>Environment</code> value
	 * @param markupLanguage The markup language
	 * @param programmingLanguage The programming language
	 * @exception Exception
	 *                if an error occurs
	 */
	public void precompile(String fileName, Environment environment,
			String markupLanguage, String programmingLanguage) throws Exception {

		ProgramGenerator programGenerator = null;
		Source source = null;
		Object key = CocoonComponentManager.startProcessing(environment);
		CocoonComponentManager.enterEnvironment(environment,
				getComponentManager(), this.cocoon);
		try {
			if (this.log.isDebugEnabled()) {
				this.log.debug("XSP generation begin:" + fileName);
			}

			programGenerator = (ProgramGenerator) getComponentManager().lookup(
					ProgramGenerator.ROLE);
			source = this.sourceResolver.resolveURI(fileName);
			CompiledComponent xsp = programGenerator.load(
					getComponentManager(), source, markupLanguage,
					programmingLanguage, environment);
			System.out.println("[XSP generated] " + xsp);
			if (this.log.isDebugEnabled()) {
				this.log.debug("XSP generation complete:" + xsp);

			}
		} finally {
			this.sourceResolver.release(source);
			getComponentManager().release(programGenerator);

			CocoonComponentManager.leaveEnvironment();
			CocoonComponentManager.endProcessing(environment, key);
		}
	}

	/**
	 * To invoke the wrapper from the command line
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {

		XSPPrecompileWrapper.setOptions();
		CommandLine line = new PosixParser().parse(options, args);
		XSPPrecompileWrapper wrapper = new XSPPrecompileWrapper();
		if (line.hasOption(HELP_OPT)) {
			printUsage();
		}

		if (line.hasOption(WORK_DIR_OPT)) {
			String workDir = line.getOptionValue(WORK_DIR_OPT);
			if (workDir.equals("")) {
				System.exit(1);
			} else {
				wrapper.setWorkDir(line.getOptionValue(WORK_DIR_OPT));
			}
		}

		if (line.hasOption(CONTEXT_DIR_OPT)) {
			String contextDir = line.getOptionValue(CONTEXT_DIR_OPT);
			if (contextDir.equals("")) {

				System.exit(1);
			} else {
				wrapper.setContextDir(contextDir);
			}
		}
		if (line.hasOption(LOG_KIT_OPT)) {
			wrapper.setLogKit(line.getOptionValue(LOG_KIT_OPT));
		}

		if (line.hasOption(CONFIG_FILE_OPT)) {
			wrapper.setConfigFile(line.getOptionValue(CONFIG_FILE_OPT));
		}
		wrapper.initialize();
		wrapper.precompile();
		wrapper.dispose();
		System.exit(0);
	}

	private static void setOptions() {
		options = new Options();

		options.addOption(new Option(LOG_KIT_OPT, LOG_KIT_LONG, true,
				"use given file for LogKit Management configuration"));

		options.addOption(new Option(CONTEXT_DIR_OPT, CONTEXT_DIR_LONG, true,
				"use given dir as context"));
		options.addOption(new Option(WORK_DIR_OPT, WORK_DIR_LONG, true,
				"use given dir as working directory"));

		options.addOption(new Option(HELP_OPT, HELP_LONG, false,
				"print this message and exit"));

		options.addOption(new Option(CONFIG_FILE_OPT, CONFIG_FILE_LONG, true,
				"specify alternate location of the configuration"
						+ " file (default is ${contextDir}/cocoon.xconf)"));

	}

	/**
	 * Print the usage message and exit
	 */
	private static void printUsage() {
		HelpFormatter formatter = new HelpFormatter();

		formatter.printHelp(
				"java org.apache.cocoon.bean.XSPPrecompileWrapper [options] ",

				options);
	}

}
