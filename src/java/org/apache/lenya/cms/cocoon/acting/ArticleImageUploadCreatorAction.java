package org.wyona.cms.cocoon.acting;

import java.io.*;
import java.util.*;

import org.apache.avalon.framework.configuration.Configurable;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.avalon.framework.parameters.Parameters;
import org.apache.avalon.framework.thread.ThreadSafe;

import org.apache.cocoon.Constants;
import org.apache.cocoon.acting.AbstractConfigurableAction;
import org.apache.cocoon.components.request.multipart.*;
import org.apache.cocoon.environment.Context;
import org.apache.cocoon.environment.Redirector;
import org.apache.cocoon.environment.Request;
import org.apache.cocoon.environment.Source;
import org.apache.cocoon.environment.SourceResolver;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;
import org.dom4j.io.OutputFormat;

public class ArticleImageUploadCreatorAction
    extends AbstractConfigurableAction implements ThreadSafe  {

    Properties default_properties = null;
    protected String uploadDirName = null;
    protected String metaDirName = null;

    final String UPLOADFILE_PARAM_NAME = "uploadFile";
    final String IMAGEXPATH_PARAM_NAME = "xpath";
    final String DOCUMENTID_PARAM_NAME = "documentid";
    final String REFERER_PARAM_NAME    = "referer";

    // optional parameters for meta data according to dublin core
    final String[] DUBLIN_CORE_PARAMETERS
	= {"title", "creator",
	   "subject", "description",
	   "publisher", "contributor",
	   "date", "type",
	   "format", "identifier",
	   "source", "language",
	   "relation", "coverage", "rights"};

    /**
     * Describe <code>configure</code> method here.
     *
     * @param conf a <code>Configuration</code> value
     * @exception ConfigurationException if an error occurs
     */
    public void configure(Configuration conf) throws ConfigurationException {
	super.configure(conf);
	// The name of the uploaddir is specified as a parameter of
	// the action. The parameter is a child of the configuration.
	Configuration[] parameters = conf.getChildren();
	for (int i = 0; i<parameters.length; i++) {
	    if (parameters[i].getAttribute("name").equals("images-dir")) {
		uploadDirName = parameters[i].getAttribute("value");
	    } else if (parameters[i].getAttribute("name").equals("meta-dir")) {
		metaDirName = parameters[i].getAttribute("value");
	    }
	}
	getLogger().debug("uploadDirName:" + uploadDirName);
	getLogger().debug("metaDirName:" + metaDirName);
    }

    /**
     * Describe <code>act</code> method here.
     *
     * @param redirector a <code>Redirector</code> value
     * @param resolver a <code>SourceResolver</code> value
     * @param objectModel a <code>Map</code> value
     * @param source a <code>String</code> value
     * @param parameters a <code>Parameters</code> value
     * @return a <code>Map</code> value
     * @exception Exception if an error occurs
     */
    public Map act(Redirector redirector, SourceResolver resolver,
		   Map objectModel, String source, Parameters parameters)
	throws Exception {

	HashMap results = new HashMap();
	Request request = (Request)objectModel.get(Constants.REQUEST_OBJECT);
	Context context = (Context)objectModel.get(Constants.CONTEXT_OBJECT);
	
	// find the absolute path (so we know where to put images and
	// meta data)
	Source  inputSource = resolver.resolve("");
	String  sitemapPath = inputSource.getSystemId();
	sitemapPath = sitemapPath.substring(5); // Remove "file:" protocol

	String absoluteUploadDirName = sitemapPath + File.separator + uploadDirName;
	String absoluteMetaDirName = sitemapPath + File.separator + metaDirName;
	
	Properties properties = new Properties(default_properties);
	byte[] buf = new byte[4096];

	getLogger().debug("request: " + request);
	getLogger().debug("context: " + context);
	getLogger().debug("properties: " + properties);

	// pass the referer back to the sitemap so that it can do a
	// redirect back to it.
	String referer = request.getParameter(REFERER_PARAM_NAME);
	results.put(REFERER_PARAM_NAME, referer);
	getLogger().debug(REFERER_PARAM_NAME + ": " + referer);

	String imageXPath = request.getParameter(IMAGEXPATH_PARAM_NAME);
	String requestingDocumentName = sitemapPath +
	    request.getParameter(DOCUMENTID_PARAM_NAME);
	String uploadFile = request.getParameter(UPLOADFILE_PARAM_NAME);

	getLogger().debug("imageXPath: " + imageXPath);
	getLogger().debug("requestingDocumentName: " + requestingDocumentName);
	getLogger().debug("uploadFile: " + uploadFile);

	// optional parameters for the meta file which contains dublin
	// core information.
	HashMap dublinCoreParams = new HashMap();
	for (int i = 0; i < DUBLIN_CORE_PARAMETERS.length; i++) {
	    String paramName = DUBLIN_CORE_PARAMETERS[i];
	    String paramValue = request.getParameter(paramName);
	    if (paramValue == null) {
		paramValue = "";
	    }
	    dublinCoreParams.put(paramName, paramValue);
        }

	Iterator iter = dublinCoreParams.keySet().iterator();
	while(iter.hasNext()) {
	    String paramName = (String)iter.next();
	    getLogger().debug(paramName + ": "+ dublinCoreParams.get(paramName));
	}

	// if we can't find the uploadFile simply return, i.e. don't
	// do anything.
	if (uploadFile == null) {
	    return Collections.unmodifiableMap(results);
	}

	Object obj = request.get(UPLOADFILE_PARAM_NAME);
	getLogger().debug(obj.getClass().getName());

	// upload the file to the uploadDir
	if (obj instanceof FilePart) {
	    getLogger().debug("Uploading file: " +
			      ((FilePart)obj).getFileName());
	    
	    // due to some requirement we want the file extension of
	    // the original file and want to add it to the filename
	    // that the user provided through the "identifier"
	    // parameter.
	    String originalFileName = ((FilePart)obj).getFileName();
	    String extension = originalFileName.substring(originalFileName.lastIndexOf("."));

	    String fileName = (String)dublinCoreParams.get("identifier") +
		extension;
	
	    // grab the mime type and add it to the dublin core meta
	    // data as "format" 
	    //	    String mimeType = ((FilePart)obj).getMimeType();
	    String mimeType = "FIXME:";
	    if (mimeType != null) {
		dublinCoreParams.put("format", mimeType);
	    }
		
	    String imagePathName = absoluteUploadDirName + File.separator +
		fileName;

	    getLogger().debug("fileName: " + fileName);
	    getLogger().debug("absoluteUploadDirName: " + absoluteUploadDirName);
	    getLogger().debug("sitemapPath: " + sitemapPath);
	    
	    File dir = new File(absoluteUploadDirName);
	    if (!dir.exists())
		dir.mkdir();

	    if (obj instanceof FilePartFile) {
		((FilePartFile)obj).getFile().renameTo(new File(imagePathName));
	    } else {	
		FileOutputStream out = new FileOutputStream(imagePathName);
		InputStream in = ((FilePart)obj).getInputStream();
		int read = in.read(buf);
		while(read > 0) {
		    out.write(buf, 0, read);
		    read = in.read(buf);
		}
		out.close();
	    }
	    
	    // create an extra file containing the meta description for
	    // the image. 
	    createMetaData(absoluteMetaDirName + File.separator + fileName +
			   ".meta", dublinCoreParams);
	    
	    // insert <media> tags at the location sepecified by the
	    // cpath in the original document (the referer)
	    insertMediaTag(requestingDocumentName, imageXPath,
			   fileName, dublinCoreParams);
	    
	} else if (obj instanceof String) {
	    getLogger().debug("Skipping parameter: " + (String)obj);
	}			

	return Collections.unmodifiableMap(results);
    }

    /**
     * Describe <code>createMetaData</code> method here.
     *
     * @param metaDataFilePathName a <code>String</code> value
     * @param dublinCoreParams a <code>HashMap</code> value
     * @exception IOException if an error occurs
     */
    protected void createMetaData(String metaDataFilePathName,
				  HashMap dublinCoreParams)
	throws IOException {

	getLogger().debug("metaDataFilePathName:" + metaDataFilePathName);
	
	Document document = DocumentHelper.createDocument();
        Element root = document.addElement("dc:metadata");

	Iterator iter = dublinCoreParams.keySet().iterator();
	while(iter.hasNext()) {
	    String tagName = (String)iter.next();
	    String tagValue = (String)dublinCoreParams.get(tagName);
	    root.addElement(tagName).addText(tagValue);
	}

	OutputStream out =
	    new BufferedOutputStream(new FileOutputStream(metaDataFilePathName));
	XMLWriter writer = new XMLWriter(out, OutputFormat.createPrettyPrint());
	writer.write(document);
	writer.close();
    }

    /**
     * Insert <media> tags at the location specified by the xpath in
     * the original document (the requestingDocumentName) 
     *
     * @param requestingDocumentName the xml document from where the
     * image upload request originated.
     * @param imageXPath the xpath after which the image is to be
     * inserted.
     * @param imagePathName path name of the uploaded image
     * @param dublinCoreParams a HashMap of additional values
     * according to Dublin Core.
     *
     * @exception DocumentException if an error occurs
     * @exception IOException if an error occurs
     */
    protected void insertMediaTag(String requestingDocumentName,
				  String imageXPath,
				  String imagePathName,
				  HashMap dublinCoreParams)
	throws DocumentException, IOException {

	// insert <media> tags at the location specified by the xpath
	// in the original document (the referer)
	
	// read the document
	SAXReader reader = new SAXReader();
	
	Document document = reader.read(requestingDocumentName);
	getLogger().debug("insertMediaTag:" + requestingDocumentName);

	// create the media element
	Element mediaTag = DocumentHelper.createElement("media");
	mediaTag.addAttribute("media-type", "image");

	mediaTag.addElement("media-reference")
	    .addAttribute("mime-type", (String)dublinCoreParams.get("format"))
	    .addAttribute("source", imagePathName)
	    .addAttribute("alternate-text",
			  (String)dublinCoreParams.get("title"))
	    .addAttribute("copyright", (String)dublinCoreParams.get("rights"));

	mediaTag.addElement("media-caption").addText(""); // FIXME:
	mediaTag.addElement("authorline")
	    .addText((String)dublinCoreParams.get("creator"));
	     
	// find the node where the figure tag has to be inserted
	Node node = document.selectSingleNode(imageXPath);
	
	Element parent = node.getParent();
	List list = parent.content();
	
	// insert the tag before the imageXPath
	list.add(parent.indexOf(node), mediaTag);

	// write it back to the file
	OutputStream out =
	    new BufferedOutputStream(new FileOutputStream(requestingDocumentName));
	XMLWriter writer = new XMLWriter(out, OutputFormat.createPrettyPrint());
	writer.write(document);
	writer.close();
    }
}

