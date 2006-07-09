var manager = Packages.org.apache.cocoon.components.CocoonComponentManager.getSitemapComponentManager();

function update(){
   var publication = cocoon.parameters.publication;
   var module = cocoon.parameters.module;
   var unid = cocoon.parameters.unid;

   var flowHelper = new Packages.org.apache.lenya.cms.cocoon.flow.FlowHelper();
   var documentHelper = flowHelper.getDocumentHelper(cocoon);
   var pageEnvelope = flowHelper.getPageEnvelope(cocoon);
   var pub = pageEnvelope.getPublication();
   var content = pub.getContent();
   var resource = content.getResource(unid);
   //Input
print("####### FILE UPDATE BEGIN #######");
   var dom = loadDocument("cocoon:/" + module + "/savedata/" + unid);
   var root = dom.getDocumentElement();
print("ROOT=" + root.getTagName());
   var child = root.getFirstChild();
   while(child != null){
      var childtag = child.getNodeName();
      if(childtag.equalsIgnoreCase("resource")){
         var doc = convertNodeToDocument(child);
         Packages.org.apache.lenya.cms.content.ResourceTransformer.transformDocument(cocoon.request, unid, doc);
      }
      if(childtag.equalsIgnoreCase("revision")){
         var language = child.getAttribute("language");
         var setLive = false;
         if(child.hasAttribute("live")){
            if(child.getAttribute("live").equalsIgnoreCase("true")) setLive = true;
         }
         var revision = child.getFirstChild();
         var doc = convertNodeToDocument(revision);
         Packages.org.apache.lenya.cms.content.flat.CreateRevisionTransformer.transformDocument(cocoon.request, unid + "_" + language, doc, setLive);
      }
      child = child.getNextSibling();
   }
   cocoon.sendPage("edit/" + unid);
}


function convertNodeToDocument(node){
   var factory = Packages.javax.xml.parsers.DocumentBuilderFactory.newInstance();
   var builder = factory.newDocumentBuilder();
   var doc = builder.newDocument();
   doc.appendChild(doc.importNode(node, true));
   return doc;
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
      var stream = source.getInputStream();
      var is = new Packages.org.xml.sax.InputSource(stream);
      is.setSystemId(source.getURI());
      return parser.parseDocument(is);
  } finally {
      if (source != null) resolver.release(source);
      if (parser != null) cocoon.releaseComponent(parser);
      if (resolver != null) cocoon.releaseComponent(resolver);
  }
}
