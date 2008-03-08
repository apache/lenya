package org.apache.lenya.cms.content.flat;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.xml.parsers.ParserConfigurationException;
import org.apache.lenya.cms.content.Location;
import org.apache.lenya.cms.content.Resource;
import org.apache.lenya.cms.content.ResourceException;
import org.apache.lenya.cms.publication.Publication;
import org.apache.lenya.util.Globals;
import org.apache.lenya.xml.DocumentHelper;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
/**
 * 
 * @author solprovider
 * @since 1.3
 */
public class FlatResource implements Resource {
   private static final String FILENAME_RESOURCE = "resource.xml";
   private File contentDirectory;
   private File resourceDirectory;
   private Document resource;
   private Element root;
   private String unid = "";
   private String id = "";
   private String type = "";
   private String doctype = "";
   private String defaultLanguage = "en";
   private String defaultLanguageParameter = "";
   private String defaultRevision = "live";
   // private String extension = ""; //Moved to revision
   private String[] languages;
   boolean isChanged = false;
   public FlatResource(File directory, String unid, String language, String revision) {
      contentDirectory = directory;
      this.unid = unid;
      if(language.length() > 0)
         defaultLanguage = language;
      defaultRevision = revision;
      init(false);
   }
   public FlatResource(File directory, String unid, String language) {
      contentDirectory = directory;
      this.unid = unid;
      if(language.length() > 0)
         defaultLanguage = language;
      init(false);
   }
   public FlatResource(File directory, String unid) {
      contentDirectory = directory;
      this.unid = unid;
      init(true);
   }
   private void init(boolean useDefaultLanguage) {
      // System.out.println("FlatResource.init BEGIN UNID=" + unid);
      boolean workUseDefaultLanguage = useDefaultLanguage;
      Location location = new Location(unid);
      // Remove extra parts.
      unid = location.getUnid();
      // Revision
      String tmpString = location.getRevision();
      if(0 < tmpString.length()){
         defaultRevision = tmpString;
      }
      // Language
      tmpString = location.getLanguage();
      if(0 < tmpString.length()){
         defaultLanguage = tmpString;
         workUseDefaultLanguage = false;
      }else if(workUseDefaultLanguage){
         if(defaultLanguageParameter.length() > 0){
            defaultLanguage = defaultLanguageParameter;
         }
      }
      resourceDirectory = new File(contentDirectory, "resource" + File.separator + unid);
      Publication pub = Globals.getPublication();
      if(null != pub){
         // Use Publication's Default Language. Only possible during Request.
         if(1 > defaultLanguage.length()){
            defaultLanguage = pub.getDefaultLanguage();
         }
         // unid may be id during Request. If not valid unid, translate id to unid.
         // DESIGN: Can Content Structures be available outside a Publication?
         String workUnid = unid;
         int pos = unid.indexOf('/');
         if(!resourceDirectory.exists()){
            // Check structure named before first slash
            // DESIGN: Use String.split()?
            String structure = "live";
            if(pos > 0){
               structure = workUnid.substring(0, pos);
               workUnid = workUnid.substring(pos);
            }
            workUnid = pub.getContent().getUNID(structure, workUnid);
            if(0 != workUnid.length()){
               resourceDirectory = new File(contentDirectory, "resource" + File.separator + workUnid);
            }
         }
         if(!resourceDirectory.exists()){
            // Check live structure
            workUnid = pub.getContent().getUNID("live", unid);
            if(0 != workUnid.length()){
               resourceDirectory = new File(contentDirectory, "resource" + File.separator + workUnid);
            }
         }
         unid = workUnid;
      }
      // System.out.println("FlatResource.init USING UNID=" + unid);
      if(!resourceDirectory.exists()){
         return;
      }
      try{
         resource = DocumentHelper.readDocument(new File(resourceDirectory, FILENAME_RESOURCE));
         root = resource.getDocumentElement();
         if(root.hasAttribute("defaultlanguage")){
            defaultLanguageParameter = root.getAttribute("defaultlanguage");
         }
         if(root.hasAttribute("doctype"))
            doctype = root.getAttribute("doctype");
         if(root.hasAttribute("type"))
            type = root.getAttribute("type");
         if(root.hasAttribute("id"))
            id = root.getAttribute("id");
         String[] files = resourceDirectory.list();
         int length = files.length;
         List list = new ArrayList();
         for(int i = 0; i < length; i++){
            File file = new File(resourceDirectory, files[i]);
            if(file.isDirectory())
               list.add(files[i]);
         }
         languages = (String[]) list.toArray(new String[0]);
         Arrays.sort(languages);
      }catch(javax.xml.parsers.ParserConfigurationException pce){
         System.out.println("FlatResource.init ParserConfigurationException UNID=" + unid);
      }catch(org.xml.sax.SAXException saxe){
         System.out.println("FlatResource.init SAXException UNID=" + unid);
      }catch(java.io.IOException ioe){
         // Expected for Lenya-1.2 Publications
         // System.out.println("FlatResource.init IOException UNID=" + unid);
      }
      // Fix type
      String workType = doctype;
      String[] parts = doctype.split("/");
      if(parts[parts.length - 1].length() > 0){
         workType = parts[parts.length - 1];
      }else if((parts.length > 1) && (parts[parts.length - 2].length() > 0)){
         workType = parts[parts.length - 2];
      }else{
         parts = type.split("/");
         if(parts[parts.length - 1].length() > 0){
            workType = parts[parts.length - 1];
         }else if((parts.length > 1) && (parts[parts.length - 2].length() > 0)){
            workType = parts[parts.length - 2];
         }
      }
      doctype = workType;
      type = workType;
   }
   public String getURI() {
      return getTranslation(defaultLanguage).getURI();
   }
   public String getMetaURI() {
      return getTranslation(defaultLanguage).getMetaURI();
   }
   public String getNewURI() {
      return getTranslation(defaultLanguage).getNewURI();
   }
   public String getUNID() {
      return unid;
   }
   public String getID() {
      return id;
   }
   public String getType() {
      return type;
   }
   /**
    * @deprecated Use getType()
    */
   public String getDocumentType() {
      return doctype;
   }
   public String getLanguage() {
      return defaultLanguage;
   }
   // Maintain separately?
   public String getDefaultLanguage() {
      return defaultLanguage;
   }
   public String[] getLanguages() {
      return languages;
   }
   /**
    * Get Navigation Title from live revision of current language.
    */
   public String getTitle() {
      return getTitle(defaultLanguage, defaultRevision);
   }
   /**
    * Get Navigation Title from live revision of specified language.
    */
   public String getTitle(String language) {
      return getTitle(language, defaultRevision);
   }
   /**
    * Get Navigation Title from specified revision of specified language.
    */
   public String getTitle(String language, String revision) {
      try{
         return getTranslation(language).getRevision(revision).getTitle();
      }catch(java.lang.NullPointerException npe){
         System.out.println("FlatResource.getTitle NullPointerException UNID=" + unid);
         return "";
      }
   }
   public String getExtension() {
      return getTranslation().getExtension();
   }
   public String getHREF() {
      return getTranslation().getHREF();
   }
   /**
    * Get variable for Index Filters
    */
   // public String get(String property, String fullid, String fulltype, String fulldoctype) {
   public String get(String property, String fullid, String fulltype) {
      if(property.equalsIgnoreCase("type")){
         return fulltype;
         // }else if(property.equalsIgnoreCase("doctype")){
         // return fulldoctype;
      }
      return get(property, fullid);
   }
   /**
    * Get variable for Structured Index Filters
    */
   public String get(String property, String fullid) {
      if(property.equalsIgnoreCase("fullid")){
         return fullid;
      }
      return get(property);
   }
   /**
    * Get variable for Flat Index Includes
    */
   public String get(String property) {
      if(property.equalsIgnoreCase("unid")){
         return getUNID();
      }else if(property.equalsIgnoreCase("id")){
         return getID();
      }else if(property.equalsIgnoreCase("type")){
         return getType();
      }else if(property.equalsIgnoreCase("doctype")){
         return getDocumentType();
      }else if(property.equalsIgnoreCase("extension")){
         return getExtension();
      }else if(property.equalsIgnoreCase("title")){
         return getTitle();
      }else if(property.equalsIgnoreCase("language")){
         return getLanguage();
      }else if(property.equalsIgnoreCase("fullid")){
         return getID();
      }else if(property.equalsIgnoreCase("href")){
         return getHREF();
      }
      return getXPath(property);
   }
   private String getXPath(String property) {
      return "";
   }
   public FlatTranslation getTranslation() {
      return getTranslation(defaultLanguage, true);
   }
   public FlatTranslation getTranslation(String language) {
      return getTranslation(language, true);
   }
   public FlatTranslation getTranslation(String language, boolean allowDefaultLanguage) {
      if(allowDefaultLanguage){
         return new FlatTranslation(resourceDirectory, language, defaultRevision, defaultLanguage);
      }else
         return new FlatTranslation(resourceDirectory, language, defaultRevision);
   }
   public boolean hasRevision() {
      FlatTranslation translation = getTranslation(defaultLanguage, false);
      if(null == translation)
         return false;
      FlatRevision revision = translation.getRevision(defaultRevision);
      if(null == revision)
         return false;
      // DEV TEST
      // boolean exists = revision.exists();
      // if(getTitle().length() < 1){
      // System.out.println("Exists=" + exists + " U=" + unid + " L=" + defaultLanguage + " R=" + defaultRevision);
      // return false;
      // }
      try{
         return getTranslation(defaultLanguage, false).getRevision(defaultRevision).exists();
      }catch(java.lang.NullPointerException npe){
         return false;
      }
   }
   public Document getInfoDocument() {
      Document document;
      try{
         document = org.apache.lenya.xml.DocumentHelper.createDocument("", "resource", null);
      }catch(javax.xml.parsers.ParserConfigurationException pce){
         System.out.println("Resource InfoDocument: ParserConfigurationException");
         return (Document) null;
      }
      Element root = document.getDocumentElement();
      root.setAttribute("unid", unid);
      root.setAttribute("type", type);
      root.setAttribute("id", id);
      if(defaultLanguageParameter.length() > 0)
         root.setAttribute("defaultlanguage", defaultLanguageParameter);
      if(doctype.length() > 0)
         root.setAttribute("doctype", doctype);
      String tmp;
      if(null != languages){
         for(int l = 0; l < languages.length; l++){
            Element te = addElement(document, root, "translation");
            te.setAttribute("language", languages[l]);
            FlatTranslation translation = getTranslation(languages[l]);
            tmp = translation.getEdit();
            if(tmp.length() > 0)
               te.setAttribute("edit", tmp);
            tmp = translation.getLive();
            if(tmp.length() > 0)
               te.setAttribute("live", tmp);
            String[] revisions = translation.getRevisions();
            for(int r = 0; r < revisions.length; r++){
               Element re = addElement(document, te, "revision");
               re.setAttribute("revision", revisions[r]);
               FlatRevision revision = translation.getRevision(revisions[r]);
               tmp = revision.getTitle();
               if(tmp.length() > 0)
                  re.setAttribute("title", tmp);
               tmp = revision.getExtension();
               if(tmp.length() > 0)
                  re.setAttribute("extension", tmp);
               tmp = revision.getHREF();
               if(tmp.length() > 0)
                  re.setAttribute("href", tmp);
            }
         }
      }
      return document;
   }
   /* Used by getInfoDocument() */
   private Element addElement(Document document, Element parent, String newElementName) {
      Element newElement = document.createElement(newElementName);
      parent.appendChild(newElement);
      return newElement;
   }
   public Document update(Document document) {
      // System.out.println("FlatResource.update - BEGIN");
      Element root = document.getDocumentElement();
      if(root.hasAttribute("id"))
         setID(root.getAttribute("id"));
      if(root.hasAttribute("defaultlanguage"))
         setDefaultLanguage(root.getAttribute("defaultlanguage"));
      save();
      NodeList translations = root.getElementsByTagName("translation");
      int translationslength = translations.getLength();
      FlatTranslation ft = null;
      for(int t = 0; t < translationslength; t++){
         Element translation = (Element) translations.item(t);
         String language = translation.getAttribute("language");
         boolean exist = true;
         if(translation.hasAttribute("action")){
            String action = translation.getAttribute("action");
            if(action.equalsIgnoreCase("delete")){
               // System.out.println("FlatResource.update - Delete Translation");
               deleteTranslation(language);
               exist = false;
            }
         }
         // System.out.println("FlatResource.update - Get translation");
         if(exist){
            ft = getTranslation(language, false);
            if(null == ft)
               exist = false;
         }
         // System.out.println("FlatResource.update - Update Translation");
         if(exist){
            if(translation.hasAttribute("live"))
               ft.setLive(translation.getAttribute("live"));
            if(translation.hasAttribute("edit"))
               ft.setEdit(translation.getAttribute("edit"));
            NodeList revisions = translation.getElementsByTagName("revision");
            int revisionslength = revisions.getLength();
            for(int r = 0; r < revisionslength; r++){
               // System.out.println("FlatResource.update - BEFORE");
               Element revision = (Element) revisions.item(r);
               // System.out.println("FlatResource.update - AFTER");
               if(revision.hasAttribute("action")){
                  String action = revision.getAttribute("action");
                  if(action.equalsIgnoreCase("delete")){
                     // System.out.println("FlatResource.update - Delete Revision");
                     String revisionid = revision.getAttribute("revision");
                     ft.deleteRevision(revisionid);
                  }
               }
            }
            ft.save();
         }
      }
      // Update Indexes
      ((FlatContent) Globals.getPublication().getContent()).updateIndexes();
      // System.out.println("FlatResource.update - RETURN");
      return document;
   }
   private void setID(String newid) {
      if(!id.equals(newid)){
         // id = newid;
         // TODO: Must update Structures
      }
      // isChanged = true;
   }
   private void setDefaultLanguage(String language) {
      defaultLanguageParameter = language;
      defaultLanguage = language;
      isChanged = true;
   }
   private void deleteTranslation(String language) {
      File translationDirectory = new File(resourceDirectory, language);
      // System.out.println("DELETE=" + translationDirectory.getAbsolutePath());
      deleteFile(translationDirectory);
   }
   // TODO: Backup before delete. Use renameTo() to move outside the resource directory.
   private void deleteFile(File file) {
      if(!file.exists())
         return;
      if(file.isDirectory()){
         File[] files = file.listFiles();
         for(int f = 0; f < files.length; f++)
            deleteFile(files[f]);
      }
      file.delete();
   }
   private void save() {
      if(isChanged){
         File file = new File(resourceDirectory, "resource.xml");
         Document doc;
         try{
            doc = DocumentHelper.readDocument(file);
         }catch(javax.xml.parsers.ParserConfigurationException pce){
            System.out.println("FlatResource.save - ParserConfigurationException: " + file.getAbsolutePath());
            return;
         }catch(org.xml.sax.SAXException saxe){
            System.out.println("FlatResource.save - SAXException: " + file.getAbsolutePath());
            return;
         }catch(java.io.IOException ioe){
            System.out.println("FlatResource.save - Could not read file: " + file.getAbsolutePath());
            return;
         }
         root = doc.getDocumentElement();
         root.setAttribute("defaultlanguage", defaultLanguageParameter);
         root.setAttribute("id", id);
         try{
            DocumentHelper.writeDocument(doc, file);
         }catch(javax.xml.transform.TransformerConfigurationException tce){
            System.out.println("FlatResource.save - TransformerConfigurationException: " + file.getAbsolutePath());
         }catch(javax.xml.transform.TransformerException te){
            System.out.println("FlatResource.save - TransformerException: " + file.getAbsolutePath());
         }catch(java.io.IOException ioe2){
            System.out.println("FlatResource.save - Could not write file: " + file.getAbsolutePath());
         }
       }
      isChanged = false;
   }
   // <?xml version="1.0" encoding="UTF-8"?>
   // <resource defaultlanguage="en" doctype="xhtml" id="doctypes" type="xml" unid="0002" xmlns=""/>
   // TODO: Working here.
   // TODO: Create specific Exception
   static public FlatResource create(String unid, String type, String id) throws ResourceException {
      String workUnid = unid;
      if(unid.length() < 1){
         workUnid = Globals.createUUID();
      }
      Publication publication = Globals.getPublication();
      File contentDirectory = publication.getContentDirectory();
      File resourceDirectory = new File(contentDirectory, "resource" + File.separator + workUnid);
      if(resourceDirectory.exists()){
         System.out.println("FlatResource.create: Resource creation failed because the Resource '" + workUnid + "' already exists." + resourceDirectory.getAbsolutePath());
         throw new ResourceException("Resource creation failed because the Resource '" + workUnid + "' already exists: " + resourceDirectory.getAbsolutePath());
      }
      resourceDirectory.mkdirs();
      File resource = new File(resourceDirectory, FILENAME_RESOURCE);
      Document xml;
      try{
         xml = DocumentHelper.createDocument("", "resource", null);
      }catch(DOMException e){
         System.out.println("CreateResource: DOMException");
         throw new ResourceException("CreateResource: DOMException", e);
      }catch(ParserConfigurationException e){
         System.out.println("CreateResource: ParserConfigurationException");
         throw new ResourceException("CreateResource: ParserConfigurationException", e);
      }
      Element root = xml.getDocumentElement();
      root.setAttribute("unid", workUnid);
      root.setAttribute("id", id);
      root.setAttribute("creator", Globals.getUser());
      root.setAttribute("when", Globals.getDateString());
      String workType = type;
      int pos = type.lastIndexOf('/');
      if(pos > 0)
         workType = type.substring(pos + 1);
      root.setAttribute("type", workType);
      root.setAttribute("doctype", workType);
      // TODO: Throw these Exceptions.
      try{
         DocumentHelper.writeDocument(xml, resource);
      }catch(javax.xml.transform.TransformerConfigurationException tce){
         System.out.println("CreateResource: TransformerConfigurationException");
         throw new ResourceException("CreateResource: TransformerConfigurationException", tce);
      }catch(javax.xml.transform.TransformerException te){
         System.out.println("CreateResource: TransformerException");
         throw new ResourceException("CreateResource: TransformerException", te);
      }catch(java.io.IOException ioe){
         System.out.println("CreateResource: IOException writing XML file.");
         throw new ResourceException("CreateResource: IOException writing XML file: " + resource.getAbsolutePath(), ioe);
      }
      // Update Indexes
      ((FlatContent) publication.getContent()).updateIndexes();
      // Return
      return new FlatResource(contentDirectory, workUnid);
   }
}
