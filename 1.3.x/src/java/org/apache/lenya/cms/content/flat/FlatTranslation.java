package org.apache.lenya.cms.content.flat;
import java.io.File;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;
import org.apache.lenya.cms.content.Content;
import org.apache.lenya.util.Globals;
import org.apache.lenya.xml.DocumentHelper;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
/**
 * 
 * @author solprovider
 * @since 1.3
 */
public class FlatTranslation {
   private File resourceDirectory;
   private File translationDirectory;
   private String language = "en";
   private String defaultLanguage = "en";
   private String revision = Content.REVISION_DEFAULT;
   private Document document;
   private Element root;
   private FlatRevision flatRevision;
   private boolean initRevisions = false;
   private SortedSet revisions = new TreeSet(); // list of FlatRevisions' filenames without extension
   private Map namedRevisions = new HashMap();
   private boolean isChanged = false;
   public FlatTranslation(File directory, String language, String revision, String defaultLanguage) {
      // System.out.println("FlatTranslation new L=" + language + " DL=" + defaultLanguage + " REV=" + revision + " DIR=" + directory.getAbsolutePath());
      resourceDirectory = directory;
      this.language = language;
      this.defaultLanguage = defaultLanguage;
      this.revision = revision;
      init();
   }
   public FlatTranslation(File directory, String language, String revision) {
      // System.out.println("FlatTranslation new L=" + language + " REV=" + revision + " DIR=" + directory.getAbsolutePath());
      resourceDirectory = directory;
      this.language = language;
      this.revision = revision;
      this.defaultLanguage = language; // Set to publication's default
      init();
   }
   public FlatTranslation(File directory, String language) {
      // System.out.println("FlatTranslation new L=" + language + " DIR=" + directory.getAbsolutePath());
      resourceDirectory = directory;
      this.language = language;
      this.defaultLanguage = language; // Set to publication's default
      init();
   }
   private void init() {
      translationDirectory = new File(resourceDirectory, language);
      if(!translationDirectory.exists()) translationDirectory = new File(resourceDirectory, defaultLanguage);
      if(!translationDirectory.exists()) return;
      File translationFile = new File(translationDirectory, "translation.xml");
      if(translationFile.exists()){
         try{
            document = DocumentHelper.readDocument(translationFile);
            root = document.getDocumentElement();
            if(root.hasAttribute(Content.REVISION_DEFAULT)) setNamedRevision(Content.REVISION_DEFAULT, root.getAttribute(Content.REVISION_DEFAULT));
            if(root.hasAttribute("edit")) setNamedRevision("edit", root.getAttribute("edit"));
         }catch(javax.xml.parsers.ParserConfigurationException pce){
            System.out.println("FlatTranslation - ParserConfigurationException: " + translationFile.getAbsolutePath());
         }catch(org.xml.sax.SAXException saxe){
            System.out.println("FlatTranslation - SAXException: " + translationFile.getAbsolutePath());
         }catch(java.io.IOException ioe){
            System.out.println("FlatTranslation - IOException: " + translationFile.getAbsolutePath());
         }
      }else{
         try{
            document = DocumentHelper.createDocument("", "translation", null);
         }catch(javax.xml.parsers.ParserConfigurationException pce){
            System.out.println("New FlatTranslation - ParserConfigurationException:" + translationFile.getAbsolutePath());
            return;
         }
         root = document.getDocumentElement();
         root.setAttribute("language", language);
         isChanged = true;
      }
   }
   public String[] getRevisions() {
      if(!initRevisions){
         String[] filelist = translationDirectory.list();
         if(null == filelist){
            // System.out.println("FlatTranslatioOn.getRevisions NULL=" + translationDirectory.getAbsolutePath());
         }else{
            for(int f = 0; f < filelist.length; f++){
               String filename = filelist[f];
               int pos = filename.lastIndexOf(".");
               if(pos > 0) filename = filename.substring(0, pos);
               if(!filename.equalsIgnoreCase("translation")) revisions.add(filename);
            }
            initRevisions = true;
         }
      }
      return (String[]) revisions.toArray(new String[0]);
   }
   public String getURI() {
      if(null == flatRevision) flatRevision = getRevision();
      if(null == flatRevision){
         // System.out.println("FlatTranslation.getURI: No revision.");
         return "";
      }
      return flatRevision.getURI();
   }
   public String getMetaURI() {
      if(null == flatRevision) flatRevision = getRevision();
      if(null == flatRevision) return "";
      return flatRevision.getMetaURI();
   }
   public String getNewURI() {
      String newRevision = Globals.getDateString();
      // WORK: Change Edit to newRevision
      return (new File(translationDirectory, newRevision + ".xml")).getPath();
   }
   public String getExtension() {
      if(null == flatRevision) flatRevision = getRevision();
      if(null == flatRevision) return "";
      return flatRevision.getExtension();
   }
   public String getHREF() {
      if(null == flatRevision) flatRevision = getRevision();
      if(null == flatRevision) return "";
      return flatRevision.getHREF();
   }
   public FlatRevision getRevision() {
      return getRevision(revision);
   }
   public FlatRevision getRevision(String revision) {
      // System.out.println("FlatTranslation.getRevision " + revision);
      String rev = revision;
      // Verify revisions is initialized.
      getRevisions();
      if(revisions.isEmpty()){
         rev = Globals.getDateString();
         initRevisions = false;
      }
      if(rev.equalsIgnoreCase("edit")) rev = getNamedRevision("edit");
      if(rev.equalsIgnoreCase(Content.REVISION_DEFAULT)) rev = getNamedRevision(Content.REVISION_DEFAULT);
      try{
         if(rev.equalsIgnoreCase("last")) rev = (String) revisions.last();
         if(rev.equalsIgnoreCase("first")) rev = (String) revisions.first();
      }catch(java.util.NoSuchElementException nsee){
         return null;
      }
      // Defaults
      // System.out.println("FlatTranslation.getRevision rev1=" + rev);
      if(1 > rev.length()) rev = getNamedRevision(Content.REVISION_DEFAULT);
      try{
         if(1 > rev.length()) rev = (String) revisions.last();
      }catch(java.util.NoSuchElementException nsee){
         return null;
      }
      if((null == rev) || (1 > rev.length())){
         // System.out.println("FlatTranslation.getRevision rev=null");
         return null;
      }
      // System.out.println("FlatTranslation.getRevision revZ=" + rev);
      return new FlatRevision(translationDirectory, rev);
   }
   void setNamedRevision(String name, String revision) {
      namedRevisions.put(name, revision);
      isChanged = true;
   }
   String getNamedRevision(String name) {
      String revision = "";
      if(namedRevisions.containsKey(name)){
         revision = (String) namedRevisions.get(name);
      }
      if((1 > revision.length()) && namedRevisions.containsKey(Content.REVISION_DEFAULT)){
         revision = (String) namedRevisions.get(Content.REVISION_DEFAULT);
      }
      if((1 > revision.length()) && namedRevisions.containsKey("last")) revision = (String) namedRevisions.get("last");
      return revision;
   }
   /**
    * @deprecated Use getNamedRevision(Content.REVISION_DEFAULT)
    * @return
    */
   public String getLive() {
      return getNamedRevision(Content.REVISION_DEFAULT);
   }
   /**
    * @deprecated Use getNamedRevision("edit")
    * @return
    */
   public String getEdit() {
      return getNamedRevision("edit");
   }
   /**
    * @deprecated Use setNamedRevision(Content.REVISION_DEFAULT, revision)
    * @param revision
    */
   void setLive(String revision) {
      setNamedRevision(Content.REVISION_DEFAULT, revision);
      isChanged = true;
   }
   /**
    * @deprecated Use setNamedRevision("edit", revision)
    * @param revision
    */
   void setEdit(String revision) {
      setNamedRevision("edit", revision);
      isChanged = true;
   }
   void save() {
      if(isChanged){
         FlatContent content = (FlatContent) Globals.getPublication().getContent();
         File file = new File(translationDirectory, "translation.xml");
         // Used NamedRevisions
         Iterator revisionNames = content.getRevisions().iterator();
         while(revisionNames.hasNext()){
            String revisionName = (String) revisionNames.next();
            // TODO: NPE on setAttribute
            // System.out.println("FlatTranslation.save - RevName:" + revisionName + " Rev:" + getNamedRevision(revisionName));
            String revision = getNamedRevision(revisionName);
            // if(0 < revision.length())
            // Zero-length revision is valid for retracting (unpublishing).
            root.setAttribute(revisionName, revision);
         }
         try{
            // DocumentHelper.writeDocument(doc, file);
            DocumentHelper.writeDocument(document, file);
         }catch(javax.xml.transform.TransformerConfigurationException tce){
            System.out.println("FlatTranslation.save - TransformerConfigurationException:" + file.getAbsolutePath());
         }catch(javax.xml.transform.TransformerException te){
            System.out.println("FlatTranslation.save - TransformerException:" + file.getAbsolutePath());
         }catch(java.io.IOException ioe2){
            System.out.println("FlatTranslation.save - Could not write file:" + file.getAbsolutePath());
         }
         // TODO: Update indexes for just this language.
         content.updateIndexes();
      }
      isChanged = false;
   }
   void deleteRevision(String revision) {
      FlatRevision fr = getRevision(revision);
      String extension = fr.getExtension();
      // TODO: Backup before delete. Use renameTo() to move outside the resource directory.
      File file = new File(translationDirectory, revision + ".xml");
      if(file.exists()){
         file.delete();
         if(extension.length() > 0){
            file = new File(translationDirectory, revision + "." + extension);
            if(file.exists()) file.delete();
         }
      }
   }
   public boolean exists() {
      return getRevision().exists();
   }
}
