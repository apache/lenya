package org.apache.lenya.cms.content.flat;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import org.apache.cocoon.ProcessingException;
import org.apache.cocoon.transformation.AbstractDOMTransformer;
import org.apache.cocoon.environment.ObjectModelHelper;
import org.apache.cocoon.environment.Request;
import org.apache.cocoon.servlet.multipart.Part;
import org.apache.lenya.cms.content.Content;
import org.apache.lenya.cms.publication.PageEnvelope;
import org.apache.lenya.cms.publication.PageEnvelopeFactory;
import org.apache.lenya.cms.publication.Publication;
//import org.apache.lenya.cms.publication.ResourcesManager;
import org.apache.lenya.xml.DocumentHelper;
import org.apache.lenya.xml.NamespaceHelper;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

/**
 * @cocoon.sitemap.component.documentation
 * This transformer creates a new Revision.
 * 
 * It takes the UNID as the src parameter, uses the current language, and create a new Revision
 * It will be enhanced to take "/structure/path/docid" as the src.  That will be easy.  If there is a slash, then convert using Content.getUNID().
 * 
 * Much of the code was modified from org.apache.lenya.cms.cocoon.acting.UploadAction
 * 
 * @author <a href="mailto:solprovider@apache.org">Paul Ercolino</a>
 */
public class CreateRevisionTransformer extends AbstractDOMTransformer{
    private static final String SERIALIZER_NAME = "xml";
    public static final String UPLOADASSET_PARAM_NAME = "properties.asset.data";
    public static final String UPLOADASSET_PARAM_PREFIX = "properties.asset.";
    public static final String UPLOADASSET_RETURN_FILESIZE = "file-size";
    public static final String UPLOADASSET_RETURN_MIMETYPE = "mime-type";
    public static final String CONTENT_PREFIX = "content";
    public static final String FILE_NAME_REGEXP = "[-a-zA-Z0-9_. ]+";
    // optional parameters for meta data according to dublin core
    public static final String[] DUBLIN_CORE_PARAMETERS = { "title", "creator", "subject",
            "description", "publisher", "contributor", "date", "type", "format", "identifier",
            "source", "language", "relation", "coverage", "rights" };

    protected org.w3c.dom.Document transform(org.w3c.dom.Document doc){
       Request request = ObjectModelHelper.getRequest(super.objectModel);
       PageEnvelope envelope = (PageEnvelope) request.getAttribute(PageEnvelope.class.getName());
       Publication pub = envelope.getPublication();
       Content content = pub.getContent();
       String unid = this.source;
       String language = envelope.getDocument().getLanguage();
       String sourceName = content.getNewURI(unid, language);
       try{
           save(sourceName, doc);
       }catch(java.io.IOException ioe){
           System.out.println("CreateRevision: IOException");
       }catch(org.apache.cocoon.ProcessingException pe){
           System.out.println("CreateRevision: ProcessingException");
       }catch(org.xml.sax.SAXException saxe){
           System.out.println("CreateRevision: SAXException");
       }
       return doc;
    }

    /**
     *
     * @param systemID The name of the revision xml file.
     * @param doc The data to be inserted.
     */
    private void save(String systemID, org.w3c.dom.Document doc)
    throws SAXException, IOException, ProcessingException {
        // test parameters
        if (systemID == null) throw new ProcessingException("createFile: systemID is required.");
        if (doc == null) throw new ProcessingException("createFile: document is required.");
        File file = new File(systemID);
        if(file.exists()){
System.out.println("Revision '"+systemID+"' already exists.");
           throw new ProcessingException("Revision '"+systemID+"' already exists.");
        }
        String filenameNoExtension = systemID;
        int pos = systemID.lastIndexOf(".");
        if(pos > 0) filenameNoExtension = systemID.substring(0, pos);
//System.out.println("Upload: FNE=" + filenameNoExtension);
        //Check for file upload from request, Save fileupload.
        Request request = ObjectModelHelper.getRequest(super.objectModel);
        File assetFile;
        // determine if the upload is an asset or a content upload
        Map dublinCoreParams = getDublinCoreParameters(request);
        // upload the file to the uploadDir
        Part part = (Part) request.get(UPLOADASSET_PARAM_NAME);
        String extension = "";
        Document metadoc = (Document) null;
        if(null != part){
           String filename = part.getFileName();
           pos = filename.lastIndexOf(".");
           if(pos > 0) extension = filename.substring(pos);
System.out.println("Upload: EXT=" + extension);
           String mimeType = part.getMimeType();
           dublinCoreParams.put("format", mimeType);
           int fileSize = part.getSize();
           dublinCoreParams.put("extent", Integer.toString(fileSize));
           assetFile = new File(filenameNoExtension + "." + extension);
           try{
              saveFileFromPart(assetFile, part);
           }catch(java.lang.Exception e){
System.out.println("CreateRevision: Exception saving upload.");
               throw new ProcessingException("CreateRevision: Exception saving upload.");
           }
           try{
              metadoc = createMetaDocument(dublinCoreParams);
           }catch(javax.xml.transform.TransformerConfigurationException tce){
System.out.println("CreateRevision: TransformerConfigurationException creating DC Meta Document.");
           }catch(javax.xml.transform.TransformerException te){
System.out.println("CreateRevision: TransformerException creating DC Meta Document.");
           }catch(javax.xml.parsers.ParserConfigurationException pce){
System.out.println("CreateRevision: ParserConfigurationException creating DC Meta Document.");

           }
        }
//Upload is saved.
//doc contains input.
//metadoc contains DublinCore Document.
//WORK: Merge documents and store extension.
        if(null != metadoc){
            File metaDataFile = new File(filenameNoExtension + ".meta");
            try{
               DocumentHelper.writeDocument(metadoc, metaDataFile);
            }catch(javax.xml.transform.TransformerConfigurationException tce){
System.out.println("CreateRevision: TransformerConfigurationException saving DC Meta Document.");
            }catch(javax.xml.transform.TransformerException te){
System.out.println("CreateRevision: TransformerException saving DC Meta Document.");
            }
        }
            try{
               DocumentHelper.writeDocument(doc, file);
            }catch(javax.xml.transform.TransformerConfigurationException tce){
System.out.println("CreateRevision: TransformerConfigurationException");
               throw new ProcessingException("CreateRevision: TransformerConfigurationException");
            }catch(javax.xml.transform.TransformerException te){
System.out.println("CreateRevision: TransformerException");
               throw new ProcessingException("CreateRevision: TransformerException");
            }
    }

    /**
     * Saves the asset to a file.
     * 
     * @param assetFile The asset file.
     * @param part The part of the multipart request.
     * @throws Exception if an error occurs.
     */
    protected void saveFileFromPart(File assetFile, Part part) throws Exception {
        if (!assetFile.exists()) {
            boolean created = assetFile.createNewFile();
            if (!created) {
                throw new RuntimeException("The file [" + assetFile + "] could not be created.");
            }
        }
        byte[] buf = new byte[4096];
        FileOutputStream out = new FileOutputStream(assetFile);
        try {
            InputStream in = part.getInputStream();
            int read = in.read(buf);
            while (read > 0) {
                out.write(buf, 0, read);
                read = in.read(buf);
            }
        } finally {
            out.close();
        }
    }
    /**
     * Retrieves optional parameters for the meta file which contains dublin core information from
     * the request.
     * @param request The request.
     * @return A map.
     */
    protected Map getDublinCoreParameters(Request request) {
        HashMap dublinCoreParams = new HashMap();
        for (int i = 0; i < DUBLIN_CORE_PARAMETERS.length; i++) {
            String paramName = DUBLIN_CORE_PARAMETERS[i];
            String paramValue = request.getParameter(UPLOADASSET_PARAM_PREFIX + paramName);
            if (paramValue == null)  paramValue = "";
            dublinCoreParams.put(paramName, paramValue);
        }
        Iterator iter = dublinCoreParams.keySet().iterator();
        while (iter.hasNext()) {
            String paramName = (String) iter.next();
            getLogger().debug(paramName + ": " + dublinCoreParams.get(paramName));
        }
        return dublinCoreParams;
    }

    /**
     * Create the meta data file given the dublin core parameters.
     * 
     * @param dublinCoreParams a <code>Map</code> containing the dublin core values
     * @throws TransformerConfigurationException if an error occurs.
     * @throws TransformerException if an error occurs.
     * @throws ParserConfigurationException if an error occurs.
     */
    protected org.w3c.dom.Document createMetaDocument(Map dublinCoreParams)
            throws TransformerConfigurationException, TransformerException, ParserConfigurationException {
        NamespaceHelper helper = new NamespaceHelper("http://purl.org/dc/elements/1.1/", "dc", "metadata");
        Element root = helper.getDocument().getDocumentElement();
        Iterator iter = dublinCoreParams.keySet().iterator();
        while (iter.hasNext()) {
            String tagName = (String) iter.next();
            String tagValue = (String) dublinCoreParams.get(tagName);
            root.appendChild(helper.createElement(tagName, tagValue));
        }
        return helper.getDocument();
    }
}
