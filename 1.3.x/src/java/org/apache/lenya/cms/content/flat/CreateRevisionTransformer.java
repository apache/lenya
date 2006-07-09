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

//For userid
import org.apache.cocoon.environment.Session;
import org.apache.lenya.ac.Identity;
import org.apache.lenya.ac.User;

/**
 * @cocoon.sitemap.component.documentation
 * This transformer creates a new Revision.
 * 
 * It takes the UNID as the src parameter, uses the current language, and create a new Revision
 * It will be enhanced to take "/structure/path/docid" as the src.  That will be easy.  If there is a slash, then convert using Content.getUNID().
 * 
 * @author <a href="mailto:solprovider@apache.org">Paul Ercolino</a>
 */
public class CreateRevisionTransformer extends AbstractDOMTransformer{
   private static final String SERIALIZER_NAME = "xml";
   public static final String UPLOADASSET_PARAM_PREFIX = "dc.";
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
      createRevision(request, this.source, doc, false);
      return doc;
   }

   static public org.w3c.dom.Document transformDocument(Request request, String unid, org.w3c.dom.Document doc, boolean setLive){
      createRevision(request, unid, doc, setLive);
      return doc;
   }

   /**
    *
    * @param request The request
    * @param doc The data to be inserted.
    */
   static private void createRevision(Request request, String unid, org.w3c.dom.Document doc, boolean setLive)
//         throws SAXException, IOException, ProcessingException 
{
      if (doc == null){
System.out.println("CreateRevision: Document is required.");
//         throw new ProcessingException("CreateRevision: document is required.");
      }
      PageEnvelope envelope = (PageEnvelope) request.getAttribute(PageEnvelope.class.getName());
      Publication pub = envelope.getPublication();
      Content content = pub.getContent();
      String language = envelope.getDocument().getLanguage();
      String newFilename = content.getNewURI(unid, language);
      if (newFilename == null){
System.out.println("CreateRevision: Could not get new filename.");
//         throw new ProcessingException("CreateRevision: Could not get new filename.");
      }
      File file = new File(newFilename);
      if(file.exists()){
System.out.println("Revision '"+newFilename+"' already exists.");
//         throw new ProcessingException("Revision '" + newFilename + "' already exists.");
      }
      String filenameNoExtension = newFilename;
      int pos = newFilename.lastIndexOf(".");
      if(pos > 0) filenameNoExtension = newFilename.substring(0, pos);
      String revision = (new File(filenameNoExtension)).getName();

      File assetFile;
      // determine if the upload is an asset or a content upload
      Map dublinCoreParams = getDublinCoreParameters(request);
      // upload the file to the uploadDir
      String extension = "";
//      Document metadoc = (Document) null;
      Element root = doc.getDocumentElement();
      if(root.hasAttribute("filefield")){
         Part part = (Part) request.get(root.getAttribute("filefield"));
         if(null != part){
            String filename = part.getFileName();
            pos = filename.lastIndexOf(".");
            if(pos > 0) extension = filename.substring(pos + 1);
//System.out.println("Upload: EXT=" + extension);
            String mimeType = part.getMimeType();
            dublinCoreParams.put("format", mimeType);
            int fileSize = part.getSize();
            dublinCoreParams.put("extent", Integer.toString(fileSize));
            assetFile = new File(filenameNoExtension + "." + extension);
            try{
               saveFileFromPart(assetFile, part);
            }catch(java.lang.Exception e){
System.out.println("CreateRevision: Exception saving upload.");
            }
            //Add Meta to doc
            addMeta(doc, dublinCoreParams);
         }  // END - part not null
      }  // END - doc has "file" attribute.
      //ASSUME: Upload is saved.
      //Store creator, when, revision and extension.
      root.setAttribute("revision", revision);
      String userid = "anonymous";
      Session session = request.getSession();
      if(session != null){
         Identity identity = (Identity) session.getAttribute(Identity.class.getName());
         if(identity != null){
            User user = identity.getUser();
            if(user != null) userid = user.getId();
         }
      } 
      root.setAttribute("creator", userid);
      root.setAttribute("when", getDateString());
      if(extension.length() > 0) root.setAttribute("extension", extension);
      try{
         DocumentHelper.writeDocument(doc, file);
      }catch(javax.xml.transform.TransformerConfigurationException tce){
System.out.println("CreateRevision: TransformerConfigurationException");
      }catch(javax.xml.transform.TransformerException te){
System.out.println("CreateRevision: TransformerException");
      }catch(java.io.IOException ioe){
         System.out.println("CreateRevision: IOException writing XML file.");
      }
      //Update Translation
      FlatResource resource = (FlatResource) ((FlatContent)content).getResource(unid, language, "edit");
      FlatTranslation translation = resource.getTranslation();
      translation.setEdit(revision);
      if(setLive) translation.setLive(revision);
      translation.save();
   }

    /**
     * Saves the asset to a file.
     * 
     * @param assetFile The asset file.
     * @param part The part of the multipart request.
     * @throws Exception if an error occurs.
     */
    static private void saveFileFromPart(File assetFile, Part part) throws Exception {
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
    static private Map getDublinCoreParameters(Request request) {
        HashMap dublinCoreParams = new HashMap();
        for (int i = 0; i < DUBLIN_CORE_PARAMETERS.length; i++) {
            String paramName = DUBLIN_CORE_PARAMETERS[i];
            String paramValue = request.getParameter(UPLOADASSET_PARAM_PREFIX + paramName);
            if (paramValue == null)  paramValue = "";
            dublinCoreParams.put(paramName, paramValue);
        }
        return dublinCoreParams;
    }

   static private void addMeta(Document doc, Map dublinCoreParams){
      NamespaceHelper helper = new NamespaceHelper("http://purl.org/dc/elements/1.1/", "dc", doc);
      Element root = doc.getDocumentElement();
// DESIGN CHANGE: No metadata element.
//      org.w3c.dom.Node meta = root.appendChild(helper.createElement("metadata"));
      Iterator iter = dublinCoreParams.keySet().iterator();
      while (iter.hasNext()) {
         String tagName = (String) iter.next();
         String tagValue = (String) dublinCoreParams.get(tagName);
//         meta.appendChild(helper.createElement(tagName, tagValue));
         root.appendChild(helper.createElement(tagName, tagValue));
      }
   }
   static private String getDateString(){
      return Long.toString(new java.util.Date().getTime());
   }
}
