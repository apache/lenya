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
import org.apache.cocoon.environment.SourceResolver;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.io.SAXReader;

import org.apache.log4j.Category;

public class ArticleImageUploadCreatorAction
    extends AbstractConfigurableAction implements ThreadSafe  {

    private Category log=Category.getInstance(ArticleImageUploadCreatorAction.class);
    
    Properties default_properties = null;
    protected String uploadDirName = null;
    protected String metaDirName = null;

    final String UPLOADFILE_PARAM_NAME = "uploadFile";
    final String IMAGEXPATH_PARAM_NAME = "imageXPath";
    final String REFERER_PARAM_NAME = "referer";

    // optional parameters for meta data according to dublin core
    final String[] DUBLIN_CORE_PARAMETERS
	= {"title", "creator",
	   "subject", "description",
	   "publisher", "contributor",
	   "date", "type",
	   "format", "identifier",
	   "source", "language",
	   "relation", "coverage", "rights"};

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
        log.debug(".act(): "+"uploadDirName:" + uploadDirName);
        log.debug(".act(): "+"metaDirName:" + metaDirName);
	getLogger().debug("uploadDirName:" + uploadDirName);
	getLogger().debug("metaDirName:" + metaDirName);
    }
/**
 *
 */
    public Map act(Redirector redirector, SourceResolver resolver,
		   Map objectModel, String source, Parameters parameters)
	throws Exception {

    org.apache.cocoon.environment.Source input_source=resolver.resolve("");
    String sitemapPath=input_source.getSystemId();
    sitemapPath=sitemapPath.substring(5); // Remove "file:" protocol
    log.debug("Absolute SITEMAP Directory: " + sitemapPath);

    String absoluteUploadDirName=sitemapPath+"/"+uploadDirName;
    String absoluteMetaDirName=sitemapPath+"/"+metaDirName;
    log.debug(".act(): "+"absoluteUploadDirName:" + absoluteUploadDirName);
    log.debug(".act(): "+"abssoluteMetaDirName:" + absoluteMetaDirName);
        

        if(true){
          log.debug(".act(): RETURN");
          return null;
          }
	
	HashMap results = new HashMap();
	Request request = (Request)objectModel.get(Constants.REQUEST_OBJECT);
	Context context = (Context)objectModel.get(Constants.CONTEXT_OBJECT);
	Properties properties = new Properties(default_properties);
	byte[] buf = new byte[4096];

	getLogger().debug("request: " + request);
	getLogger().debug("context: " + context);
	getLogger().debug("properties: " + properties);

	String imageXPath = request.getParameter(IMAGEXPATH_PARAM_NAME);
	String requestingDocumentName = request.getParameter(REFERER_PARAM_NAME);
	String uploadFile = request.getParameter(UPLOADFILE_PARAM_NAME);

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

	getLogger().debug("imageXPath: " + imageXPath);
	getLogger().debug("requestingDocumentName: " + requestingDocumentName);
	getLogger().debug("uploadFile: " + uploadFile);

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
	    
	    String fileName = ((FilePart)obj).getFileName();
	    
	    String realPath = context.getRealPath("/wyona/cms/pubs/unipublic");
	    String realUploadDirName = null;
	    
	    if (realPath != null)
		realUploadDirName = realPath + File.separator + uploadDirName;

	    String imagePathName = realUploadDirName + File.separator + fileName;
	    
	    getLogger().debug("fileName: " + fileName);
	    getLogger().debug("realUploadDirName: " + uploadDirName);
	    getLogger().debug("realPath: " + realPath);
	    System.out.println("==fileName:" + fileName);
	    System.out.println("==:realUploadDirName" + realUploadDirName);
	    System.out.println("==realPath:" + realPath);
	    
	    
	    File dir = new File(realUploadDirName);
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
	    createMetaData(imagePathName + ".meta", dublinCoreParams);
	    
	    // insert <figure> tags at the location sepecified by the
	    // cpath in the original document (the referer)
	    insertFigureTag(requestingDocumentName, imageXPath,
			    imagePathName);
	    
	} else if (obj instanceof String) {
	    getLogger().debug("Skipping parameter: " + (String)obj);
	}			

	return Collections.unmodifiableMap(results);
    }

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

	FileWriter out = new FileWriter(metaDataFilePathName);
	document.write(out);
	out.close();
    }

    protected void insertFigureTag(String requestingDocumentName,
				   String imageXPath,
				   String imagePathName)
	throws DocumentException, IOException {
	// insert <figure> tags at the location specified by the xpath
	// in the original document (the referer)
	
	// read the document
	SAXReader reader = new SAXReader();
	
	Document document = reader.read(requestingDocumentName);
	
	// create the figure element
	Element figureTag = DocumentHelper.createElement("figure");
	figureTag.addAttribute("src", imagePathName);
	
	// find the node where the figure tag has to be inserted
	Node node = document.selectSingleNode(imageXPath);
	
	Element parent = node.getParent();
	List list = parent.content();
	
	// insert the tag before the imageXPath
	list.add(parent.indexOf(node), figureTag);

	// write it back to the file
	FileWriter out = new FileWriter(requestingDocumentName);
	document.write(out);
	out.close();
    }
}

