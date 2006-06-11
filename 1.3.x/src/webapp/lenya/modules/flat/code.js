var PublicationDirectory;
var ContentDirectoryNew;
var ResourcesDirectory;
var RelationsDirectory;

function init(){
//Inconsistent directories.
//Java assumes lenya installation root.
//JS assumes module directory, which changes whether in Publication or global.
//Use absolute paths for everything except module code (Use module: protocol).
   var publication = cocoon.parameters.publication;
   var module = cocoon.parameters.module;
   var file = new Packages.java.io.File("");
   PublicationDirectory = file.getAbsolutePath() + "/build/lenya/webapp/lenya/pubs/" + publication + "/";
   ContentDirectoryNew = PublicationDirectory + "content/";
   ResourcesDirectory = ContentDirectoryNew + "resource/";
   RelationsDirectory = ContentDirectoryNew + "relation/";
}

var incremental = false;

function migrate() {
   init();
   var resource;
   var resourcedoc;
   var resourceroot;
   var translations;
   var translationslength;
   var translation;
   var translationdoc;
   var translationroot;
   var files;
   var fileslength;
   var file;
   var filedoc;
   var fileroot;
   var oldfilename;
   var indexes;
   var indexeslength;
   var index;
   var indexdoc;
   var indexroot;
   var fullid;
   var pos;  //temporary variable for String manipulation
   var id;
   var unid;
   var parentid;
   var parentunid;
   var position;
   var resourcetabledoc;
   var resourcetableroot;
   var resourcetableentry;
   var type;
   var extension;
   var id2unid = new Packages.java.util.HashMap();
   var homeunid = "0001";
   var nextunid = 1;
   var unidformatter = new Packages.java.text.DecimalFormat("0000");
   var dom = loadDocument("cocoon:/all");
   var root = dom.getDocumentElement();
   var resources = root.getElementsByTagName("resource");
   var resourceslength = resources.getLength();
   if(0 < resourceslength){
      resource = resources.item(0);
      homeunid = resource.getAttribute("unid");
   }
   // Load Relations - BEGIN
   var testfile = new Packages.java.io.File(ContentDirectoryNew + "relations.xml");
   if(testfile.exists()) incremental = true;
   resourcetabledoc = createDocument("relations", ContentDirectoryNew + "relations.xml");
   resourcetableroot = resourcetabledoc.getDocumentElement();
   if(resourcetableroot.hasAttribute("home")) homeunid = resourcetableroot.getAttribute("home");
   if(resourcetableroot.hasAttribute("next")) 
         nextunid = Packages.java.lang.Integer.parseInt(resourcetableroot.getAttribute("next"));
   var resourcetableentries = resourcetableroot.getElementsByTagName("resource");
   var resourcetablelength = resourcetableentries.getLength();
   for(var x = 0; x < resourcetablelength; x++){
      resourcetableentry = resourcetableentries.item(x);
      unid = resourcetableentry.getAttribute("unid");
      fullid = resourcetableentry.getAttribute("full");
      id2unid.put(fullid, unid);
   }
   // Load Relations - END
   // Reset Relations XML
   resourcetabledoc = Packages.org.apache.lenya.xml.DocumentHelper.createDocument("", "relations", null);
   resourcetableroot = resourcetabledoc.getDocumentElement();
   addComment(resourcetabledoc, resourcetableroot, " ### This is a temporary file created by the migration process for Lenya 1.2 publications to Lenya 1.3 flat storage.  Do not use this file for any functions.  It may be deleted after the conversion has been tested. ### ");
print("*** Resources ***");

// ### Resources ###
   for(var r = 0; r < resourceslength; r++){
      // Resource
      resource = resources.item(r);
      fullid = resource.getAttribute("id");
      if(incremental){
         unid = id2unid.get(fullid);
         if(unid == null || unid.length() < 1){
            unid = unidformatter.format(nextunid);
            nextunid++;
         }
      }else{
         if(resource.hasAttribute("unid")){
            unid = resource.getAttribute("unid");
            id2unid.put(fullid, unid);
         }else{
            unid = unidformatter.format(nextunid);
            nextunid++;
            incremental = true;
         }
      }
      resourcedoc = createDocument("resource", ResourcesDirectory + unid + resource.getAttribute("filename"));
      resourceroot = resourcedoc.getDocumentElement();

      resourceroot.setAttribute("unid", unid);
      position = resource.getAttribute("position");
      pos = fullid.lastIndexOf("/");
      id = fullid.substring(pos + 1);
      // Extension - BEGIN
      extension = "";
      pos = id.lastIndexOf(".");
      if(pos > 0) extension = id.substring(pos + 1);
// WORKAROUND: BUG with JS String.length.  If length > 0 returns function code rather than number.
      var hasExtension = (extension.length == 0 ? false : true);
      // Extension - END
      pos = fullid.lastIndexOf("/" + id);
      parentid = fullid.substring(0, pos);
      //Translate ID to UNID.
      parentunid = id2unid.get(parentid);
      if(parentunid == null || parentunid.length() < 1) parentunid = "";
      resourceroot.setAttribute("id", id);
      type = resource.getAttribute("type")
      resourceroot.setAttribute("type", type);
      resourceroot.setAttribute("defaultlanguage", "en");
      if(resource.hasAttribute("doctype")) resourceroot.setAttribute("doctype", resource.getAttribute("doctype"));
      //Indexes
      indexes = resource.getElementsByTagName("index");
      indexeslength = indexes.getLength();
      for(var i = 0; i < indexeslength; i++){
         //Index
         index = indexes.item(i);
         resourcetableentry = addElement(resourcetabledoc, resourcetableroot, "resource");
         resourcetableentry.setAttribute("structure", index.getAttribute("name"));
         resourcetableentry.setAttribute("parent", parentunid);
         resourcetableentry.setAttribute("unid", unid);
         resourcetableentry.setAttribute("position", index.getAttribute("position"));
         resourcetableentry.setAttribute("full", fullid);
         resourcetableentry.setAttribute("id", id);
      }
//Translations
      translations = resource.getElementsByTagName("translation");
      translationslength = translations.getLength();
      for(var t = 0; t < translationslength; t++){
//Translation
         translation = translations.item(t);
         translationdoc = createDocument("translation", ResourcesDirectory + unid + translation.getAttribute("filename"));
         translationroot = translationdoc.getDocumentElement();
         translationroot.setAttribute("language", translation.getAttribute("language"));
         translationroot.setAttribute("edit", translation.getAttribute("edit"));
         translationroot.setAttribute("live", translation.getAttribute("live"));
//DESIGN: Added <Translation language="xx">.  Repeats information from directory structure (extra maintenance).
//         var element = addElement(resourcedoc, resourceroot, "translation");
//         element.setAttribute("language", translation.getAttribute("language"));
//Files
         files = translation.getElementsByTagName("file");
         fileslength = files.getLength();
         for(var f = 0; f < fileslength; f++){
//File
            file = files.item(f);
            oldfilename = file.getAttribute("oldfilename");
            if(oldfilename.length() > 0){
               filedoc = loadDocument(PublicationDirectory + oldfilename);
            }else{
                filedoc = createDocument(resource.getAttribute("type"), 
                      ResourcesDirectory + unid + file.getAttribute("filename"));
            }
            fileroot = filedoc.getDocumentElement();
            if(file.hasAttribute("navtitle")){
               fileroot.setAttribute("title", file.getAttribute("navtitle"));
            }else{
               var titles = fileroot.getElementsByTagName("title");
               if(titles.getLength() > 0){
                  fileroot.setAttribute("title", getText(titles.item(0)));
               }else{
                  titles = fileroot.getElementsByTagNameNS("*", "title");
                  if(titles.getLength() > 0){
                     fileroot.setAttribute("title", getText(titles.item(0)));
                  }else{
                    fileroot.setAttribute("title", id);
                  }
               }
            }
            if(file.hasAttribute("href")) fileroot.setAttribute("href", file.getAttribute("href"));
            if(hasExtension) fileroot.setAttribute("extension", extension);
            writeDocument(filedoc, ResourcesDirectory + unid + file.getAttribute("filename"));
            if(file.hasAttribute("binaryfilenameold")){
               //Copy binaryfilenameold to binaryfilenamenew
               copyfile(PublicationDirectory + file.getAttribute("binaryfilenameold"),
                     ResourcesDirectory + unid + file.getAttribute("binaryfilenamenew"));
            }
         }
         writeDocument(translationdoc, ResourcesDirectory + unid + translation.getAttribute("filename"));
      }
      writeDocument(resourcedoc, ResourcesDirectory + unid + resource.getAttribute("filename"));
      if(!incremental){
         nextunid = 1 + Packages.java.lang.Integer.parseInt(unid);
      }
   }
   resourcetableroot.setAttribute("home", homeunid);
   resourcetableroot.setAttribute("next", nextunid);
   writeDocument(resourcetabledoc, ContentDirectoryNew + "relations.xml");
   //### SPLIT RELATIONS BY STRUCTURE ###
   var dom = loadDocument("cocoon:/structures");
   var root = dom.getDocumentElement();
   var structures = root.getElementsByTagName("structure");
   var structurelength = structures.getLength();
   // Use module: protocol to get XSLT
   var resolver = cocoon.getComponent(Packages.org.apache.cocoon.environment.SourceResolver.ROLE);
   var xslsource = resolver.resolveURI("module:///structure2.xsl");
   var stylesheet = new Packages.javax.xml.transform.stream.StreamSource(xslsource.getURI());
   for(var i = 0; i < structurelength; i++){
      var structure = structures.item(i);
      var structureName = structure.getAttribute("name");
      var newDoc = Packages.org.apache.lenya.xml.DocumentHelper.createDocument("", "resources", null);
      var newRoot = newDoc.getDocumentElement();
      newRoot.setAttribute("name", structureName);
      var resources = structure.getElementsByTagName("resource");
      var resourceslength = resources.getLength();
      for(var r = 0; r < resourceslength; r++){
         var resource = resources.item(r);
            var newResource = addElement(newDoc, newRoot, "resource");
            copyAttributes(resource, newResource);
      }
      // Transform
      var factory = Packages.javax.xml.transform.TransformerFactory.newInstance();
      var transformer = factory.newTransformer(stylesheet);
      var source = new Packages.javax.xml.transform.dom.DOMSource(newDoc);
      var newDoc2 = Packages.org.apache.lenya.xml.DocumentHelper.createDocument("", "resources", null);
      var resultFile = new Packages.java.io.File(RelationsDirectory + structureName + ".xml");
      createNewFile(resultFile);
      var result = new Packages.javax.xml.transform.stream.StreamResult(resultFile);
      transformer.transform(source, result);
   }
   //### Copy Indexes
   var isource = resolver.resolveURI("module:///index");
   var oldindexdirectoryURI = new Packages.java.net.URI(isource.getURI());
   var oldindexdirectory = new Packages.java.io.File(oldindexdirectoryURI);
   var newindexdirectory = new Packages.java.io.File(ContentDirectoryNew, "index");
   if(!newindexdirectory.exists()){
      newindexdirectory.mkdirs();
   }
   var indexfilenames = oldindexdirectory.list();
   var indexfilenameslength = indexfilenames.length;
   for(var f = 0; f < indexfilenameslength; f++){
      var filename = indexfilenames[f];
      var newfile = new Packages.java.io.File(newindexdirectory, filename);
      if(!newfile.exists()){
         var oldfile = new Packages.java.io.File(oldindexdirectory, filename);
         copyfile(oldfile.getAbsolutePath(), newfile.getAbsolutePath());
      }
   }
   //### Return
   cocoon.sendPage("finish");
}

function createNewFile(file){
   file.getParentFile().mkdirs();
   file.createNewFile();
}

function copyAttributes(srcElement, dstElement){
   var attrList = srcElement.getAttributes();
   for(var a = 0; a < attrList.getLength(); a++){
      var attributeName = attrList.item(a).getNodeName();
      dstElement.setAttribute(attributeName, srcElement.getAttribute(attributeName));
   }
}

function copyfile(srcFilename, dstFilename){
   var src = new Packages.java.io.File(srcFilename);
   var dst = new Packages.java.io.File(dstFilename);
   var sin = new Packages.java.io.BufferedInputStream(new Packages.java.io.FileInputStream(src));
   var sout = new Packages.java.io.BufferedOutputStream(new Packages.java.io.FileOutputStream(dst));
   var sbuf = sin.read();
   while (sbuf != -1){
     sout.write(sbuf);
     sbuf = sin.read();
   }
   sin.close();
   sout.close();
}

function printElement(element){
   print("ELEMENT: " + element.getLocalName());
   var attributes = element.getAttributes();
   var alen = attributes.getLength();
   for(var a = 0; a < alen; a++){
      print(attributes.item(a).getNodeName() + ": " + attributes.item(a).getNodeValue());
   }
}

function getText(node){
   var text = "";
   if(node.getFirstChild() != null) text = node.getFirstChild().getNodeValue();
   return text;
}

function getFieldText(node, fieldname){
   var text = "";
   if(node.getElementsByTagName(fieldname).item(0)) text = getText(node.getElementsByTagName(fieldname).item(0));
   return text;
}

function addElement(document, parent, newElementName){
   var newElement = document.createElement(newElementName);
   parent.appendChild(newElement);
   return newElement;
}
function addComment(document, parent, comment){
   var newComment = document.createComment(comment);
   parent.appendChild(newComment);
   return newComment;
}

function writeDocument(document, filename){
   var file = new Packages.java.io.File(filename);
   Packages.org.apache.lenya.xml.DocumentHelper.writeDocument(document, file);
}

function createDocument(rootnode, filename) {
   if(incremental){
      var testfile = new Packages.java.io.File(filename);
      if(testfile.exists()) if(testfile.canRead()){
         return loadDocument(filename);
      }
   }
   return Packages.org.apache.lenya.xml.DocumentHelper.createDocument("", rootnode, null);
}

/**
 * loadDocument() reads in an XML file and returns a DOM Document.
**/
function loadDocument(uri) {
  var parser = null;
  var source = null;
  var resolver = null;
  try {
      parser = cocoon.getComponent(Packages.org.apache.excalibur.xml.dom.DOMParser.ROLE);
      resolver = cocoon.getComponent(Packages.org.apache.cocoon.environment.SourceResolver.ROLE);
      source = resolver.resolveURI(uri);
      var is = new Packages.org.xml.sax.InputSource(source.getInputStream());
      is.setSystemId(source.getURI());
      return parser.parseDocument(is);
  } finally {
      if (source != null) resolver.release(source);
      if (parser != null) cocoon.releaseComponent(parser);
      if (resolver != null) cocoon.releaseComponent(resolver);
  }
}


function createfile(filename, content) {
   var fileOutputStream = new Packages.java.io.FileOutputStream(ContentDirectoryNew + filename, true);
   //## ADD DATA TO FILE
   toFile(fileOutputStream, content);
   //## CLOSE FILE
   fileOutputStream.close();
}

function toFile( fileOutputStream, pString){
   if(pString != null){
      var tmpString = Packages.java.lang.String(pString);   
      var bs = tmpString.getBytes("UTF-8");
      var bc = 0;
      var ch = bs[bc];
      while((bc < bs.length)){
         fileOutputStream.write(ch);
         bc += 1;
         ch = bs[bc];
      }
   }
}
