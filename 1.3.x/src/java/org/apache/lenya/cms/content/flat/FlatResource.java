package org.apache.lenya.cms.content.flat;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import javax.xml.parsers.ParserConfigurationException;
import org.apache.lenya.cms.content.Content;
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
   private static final String ATTR_DEFAULT_LANGUAGE = "defaultlanguage";
   private File contentDirectory;
   private File resourceDirectory;
   private Document resource;
   private Element root;
   private String unid = "";
   private String id = "";
   private String type = "";
   private String doctype = "";
   private String defaultLanguage = FlatContent.LANGUAGE_DEFAULT;
   private String defaultLanguageParameter = "";
   private String defaultRevision = Content.REVISION_DEFAULT;
   // private String extension = ""; //Moved to revision
   private String[] languages;
   boolean isChanged = false;
   public FlatResource(File directory, String unid, String language, String revision) {
      // System.out.println("FlatResource new unid=" + unid + "_" + language + "!" + revision + " " + directory.getAbsolutePath());
      contentDirectory = directory;
      this.unid = unid;
      if((null == language) || (0 < language.length())) defaultLanguage = language;
      defaultRevision = revision;
      init(false);
   }
   public FlatResource(File directory, String unid, String language) {
      // System.out.println("FlatResource new unid=" + unid + "_" + language + " " + directory.getAbsolutePath());
      contentDirectory = directory;
      this.unid = unid;
      if(language.length() > 0) defaultLanguage = language;
      init(false);
   }
   public FlatResource(File directory, String unid) {
      // System.out.println("FlatResource new unid=" + unid + " " + directory.getAbsolutePath());
      contentDirectory = directory;
      this.unid = unid;
      init(true);
   }
   private void init(boolean useDefaultLanguage) {
      // System.out.println("FlatResource.init unid=" + unid + " " + contentDirectory.getAbsolutePath());
      boolean workUseDefaultLanguage = useDefaultLanguage;
      Location location = new Location(unid);
      // Remove extra parts.
      unid = location.getUnid();
      // Revision
      // System.out.println("FlatResource.init REV1=" + defaultRevision);
      String tmpString = location.getRevision();
      if(0 < tmpString.length()){
         defaultRevision = tmpString;
         // System.out.println("FlatResource.init REV2=" + defaultRevision);
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
      resourceDirectory = new File(contentDirectory, unid);
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
            String structure = FlatContent.RELATIONS_DEFAULT;
            if(pos > 0){
               structure = workUnid.substring(0, pos);
               workUnid = workUnid.substring(pos);
            }
            // Do not test if no structure or when seeking itself.
            // if((0 < structure.length()) && !workUnid.endsWith(FlatContent.SEPARATOR_RELATIONS + structure)){
            if((0 < structure.length()) && !workUnid.endsWith(structure)){
               // System.out.println("FlatResource.init Check Structure=" + structure);
               workUnid = pub.getContent().getUNID(structure, workUnid);
               if(0 != workUnid.length()){
                  resourceDirectory = new File(contentDirectory, workUnid);
               }
            }
         }
         if(!resourceDirectory.exists()){
            // Check default structure without part before slash
            // Do not test default structure when seeking default Structure Design Resource
            // if(!workUnid.endsWith(FlatContent.SEPARATOR_RELATIONS + FlatContent.RELATIONS_DEFAULT)){
            if(!workUnid.endsWith(FlatContent.RELATIONS_DEFAULT)){
               // System.out.println("FlatResource.init Check default Structure partialUnid=" + workUnid);
               workUnid = pub.getContent().getUNID(FlatContent.RELATIONS_DEFAULT, unid);
               if(0 != workUnid.length()){
                  resourceDirectory = new File(contentDirectory, workUnid);
               }
            }
         }
         if(!resourceDirectory.exists()){
            // Check default structure using entire unid
            workUnid = unid;
            // Do not test default structure when seeking default Structure Design Resource
            // if(!workUnid.endsWith(FlatContent.SEPARATOR_RELATIONS + FlatContent.RELATIONS_DEFAULT)){
            if(!workUnid.endsWith(FlatContent.RELATIONS_DEFAULT)){
               // System.out.println("FlatResource.init Check Default Structure with unid=" + workUnid);
               workUnid = pub.getContent().getUNID(FlatContent.RELATIONS_DEFAULT, unid);
               if(0 != workUnid.length()){
                  resourceDirectory = new File(contentDirectory, workUnid);
               }
            }
         }
         if(resourceDirectory.exists()){
            unid = workUnid;
         }
      }
      // System.out.println("FlatResource.init USING UNID=" + unid);
      if(!resourceDirectory.exists()){
         // System.out.println("FlatResource.init Could not find unid '" + unid + "' at " + resourceDirectory.getAbsolutePath());
         return;
      }
      // System.out.println("FlatResource.init Loading");
      try{
         resource = DocumentHelper.readDocument(new File(resourceDirectory, FILENAME_RESOURCE));
         root = resource.getDocumentElement();
         if(root.hasAttribute(ATTR_DEFAULT_LANGUAGE)){
            defaultLanguageParameter = root.getAttribute(ATTR_DEFAULT_LANGUAGE);
         }
         if(root.hasAttribute("doctype")) doctype = root.getAttribute("doctype");
         if(root.hasAttribute("type")) type = root.getAttribute("type");
         if(root.hasAttribute("id")) id = root.getAttribute("id");
         String[] files = resourceDirectory.list();
         int length = files.length;
         List list = new ArrayList();
         for(int i = 0; i < length; i++){
            File file = new File(resourceDirectory, files[i]);
            if(file.isDirectory()) list.add(files[i]);
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
      if(property.equalsIgnoreCase("type")){ return fulltype;
      // }else if(property.equalsIgnoreCase("doctype")){
      // return fulldoctype;
      }
      return get(property, fullid);
   }
   /**
    * Get variable for Structured Index Filters
    */
   public String get(String property, String fullid) {
      if(property.equalsIgnoreCase("fullid")){ return fullid; }
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
      }else if(property.equalsIgnoreCase("href")){ return getHREF(); }
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
      if(allowDefaultLanguage){ return new FlatTranslation(resourceDirectory, language, defaultRevision, defaultLanguage); }
      return new FlatTranslation(resourceDirectory, language, defaultRevision);
   }
   public boolean hasRevision() {
      FlatTranslation translation = getTranslation(defaultLanguage, false);
      if(null == translation) return false;
      FlatRevision revision = translation.getRevision(defaultRevision);
      if(null == revision) return false;
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
      if(defaultLanguageParameter.length() > 0) root.setAttribute(ATTR_DEFAULT_LANGUAGE, defaultLanguageParameter);
      if(doctype.length() > 0) root.setAttribute("doctype", doctype);
      String tmp;
      if(null != languages){
         for(int l = 0; l < languages.length; l++){
            Element te = addElement(document, root, "translation");
            te.setAttribute("language", languages[l]);
            FlatTranslation translation = getTranslation(languages[l]);
            // TODO: Use NamedRevisions - Content.getRevisions()
            tmp = translation.getNamedRevision("edit");
            if(tmp.length() > 0) te.setAttribute("edit", tmp);
            tmp = translation.getNamedRevision(Content.REVISION_DEFAULT);
            if(tmp.length() > 0) te.setAttribute(Content.REVISION_DEFAULT, tmp);
            String[] revisions = translation.getRevisions();
            for(int r = 0; r < revisions.length; r++){
               Element re = addElement(document, te, "revision");
               re.setAttribute("revision", revisions[r]);
               FlatRevision revision = translation.getRevision(revisions[r]);
               tmp = revision.getTitle();
               if(tmp.length() > 0) re.setAttribute("title", tmp);
               tmp = revision.getExtension();
               if(tmp.length() > 0) re.setAttribute("extension", tmp);
               tmp = revision.getHREF();
               if(tmp.length() > 0) re.setAttribute("href", tmp);
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
      if(root.hasAttribute("id")) setID(root.getAttribute("id"));
      if(root.hasAttribute(ATTR_DEFAULT_LANGUAGE)) setDefaultLanguage(root.getAttribute(ATTR_DEFAULT_LANGUAGE));
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
            if(null == ft) exist = false;
         }
         // System.out.println("FlatResource.update - Update Translation");
         if(exist){
            // Use Content's NamedRevisions
            Iterator iterator = ((FlatContent) Globals.getPublication().getContent()).getRevisions().iterator();
            while(iterator.hasNext()){
               String revisionName = (String) iterator.next();
               if(translation.hasAttribute(revisionName)) ft.setNamedRevision(revisionName, translation.getAttribute(revisionName));
            }
            NodeList revisions = translation.getElementsByTagName("revision");
            int revisionslength = revisions.getLength();
            for(int r = 0; r < revisionslength; r++){
               Element revision = (Element) revisions.item(r);
               if(revision.hasAttribute("action")){
                  String action = revision.getAttribute("action");
                  if(action.equalsIgnoreCase("delete")){
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
   // TODO: Backup before delete. Use renameTo() to move outside the resource directory. See 13TODO.txt for design of transactions.
   private void deleteFile(File file) {
      if(!file.exists()) return;
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
         root.setAttribute(ATTR_DEFAULT_LANGUAGE, defaultLanguageParameter);
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
   static public FlatResource createContent(String unid, String type, String id) throws ResourceException {
      // System.out.println("FlatResource.createContent");
      File contentDirectory = new File(Globals.getPublication().getContentDirectory(), FlatContent.DIRECTORY_CONTENT);
      FlatResource resource = create(unid, type, id, contentDirectory, false);
      // Only update "ALL" Index because cannot Resoruce cannot appear in other Indexes until a Revision is created.
      ((FlatContent) Globals.getPublication().getContent()).indexer.updateAllIndex();
      return resource;
   }
   static public FlatResource createDesign(String unid, String type, String id) throws ResourceException {
      // System.out.println("FlatResource.createDesign");
      FlatResource resource = create(FlatDesign.getDesignUnid(unid), type, id, Globals.getPublication().getDesign().getDirectory(), true);
      return resource;
   }
   static public FlatResource createStructure(String unid, String type, String id) throws ResourceException {
      System.out.println("FlatResource.createStructure");
      FlatDesign design = Globals.getPublication().getDesign();
      // FlatResource resource = create(FlatDesign.getStructureUnid(unid), type, id, Globals.getPublication().getDesign().getDirectory(), true);
      FlatResource resource = create(design.getStructureUnid(unid), type, id, Globals.getPublication().getDesign().getDirectory(), true);
      return resource;
   }
   static private FlatResource create(String unid, String type, String id, File contentDirectory, boolean isDesign) throws ResourceException {
      // System.out.println("FlatResource.create DIR=" + contentDirectory.getAbsolutePath());
      String workUnid = unid;
      // TODO: Make unid safe. See ModuleSourceFactory.safe().
      if(unid.length() < 1){
         workUnid = Globals.createUUID();
      }
      File resourceDirectory = new File(contentDirectory, workUnid);
      if(resourceDirectory.exists()){
         System.out.println("FlatResource.create: Resource creation failed because the Resource '" + workUnid + "' already exists." + resourceDirectory.getAbsolutePath());
         throw new ResourceException("Resource creation failed because the Resource '" + workUnid + "' already exists: " + resourceDirectory.getAbsolutePath());
      }
      resourceDirectory.mkdirs();
      File resource = new File(resourceDirectory, FILENAME_RESOURCE);
      Document xml;
      try{
         xml = DocumentHelper.createDocument("", FlatContent.DIRECTORY_CONTENT, null);
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
      if(pos > 0) workType = type.substring(pos + 1);
      root.setAttribute("type", workType);
      root.setAttribute("doctype", workType);
      if(isDesign) root.setAttribute(ATTR_DEFAULT_LANGUAGE, FlatDesign.DESIGN_LANGUAGE);
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
      ((FlatContent) Globals.getPublication().getContent()).updateIndexes();
      // Return
      return new FlatResource(contentDirectory, workUnid);
   }
   public boolean exists() {
      return getTranslation().exists();
   }
}
