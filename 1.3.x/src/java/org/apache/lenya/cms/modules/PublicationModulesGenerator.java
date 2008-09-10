package org.apache.lenya.cms.modules;
import java.io.IOException;
import java.io.Serializable;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import org.apache.cocoon.ProcessingException;
import org.apache.cocoon.caching.CacheableProcessingComponent;
import org.apache.cocoon.generation.ServiceableGenerator;
import org.apache.excalibur.source.SourceValidity;
import org.apache.lenya.cms.content.flat.FlatContent;
import org.apache.lenya.cms.publication.Publication;
import org.apache.lenya.util.Globals;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;
public class PublicationModulesGenerator extends ServiceableGenerator implements CacheableProcessingComponent {
   private static final String PREFIX = "publication";
   private static final String URI = "http://apache.org/lenya/1.3/" + PREFIX;
   private static final String ELEMENT_TOP = "modules";
   private static final String ELEMENT_MODULE = "module";
   private static final String ATTR_MODULE_ID = "id";
   private static final String ATTR_MODULE_SOURCE_PUBLICATION = "publication";
   private static final String ATTR_MODULE_SOURCE_MODULE = "module";
   private static final String ELEMENT_RESOURCE = "resource";
   private static final String ATTR_RESOURCE = "resource";
   private static final String ATTR_MODULE_NAME = "name";
   private static final String ATTR_PUB_ID = "publication";
   private static final String ATTR_PUB_CONTENT = "content";
   private static final String ELEMENT_STRUCTURE = "structure";
   private static final String ATTR_STRUCTURE_ID = "id";
   private static final String ELEMENT_REVISION = "revision";
   private static final String ATTR_REVISION_ID = "id";
   public Serializable getKey() {
      // TODO Auto-generated method stub
      return null;
   }
   public SourceValidity getValidity() {
      // TODO Auto-generated method stub
      return null;
   }
   /**
    * Generate XML describing the resources used by the current publication
    */
   public void generate() throws IOException, SAXException, ProcessingException {
      // <modules publication="default13" content="flat">
      // Root node of XML with publication's Id and content type.
      Publication publication = Globals.getPublication();
      PublicationModules modules = publication.getModules();
      Map modulesMap = modules.getSourceModules();
      Map resources = modules.getResources();
      ContentHandler handler = this.contentHandler;
      handler.startDocument();
      handler.startPrefixMapping(PREFIX, URI);
      AttributesImpl attributes = new AttributesImpl();
      attributes.addAttribute(URI, ATTR_PUB_ID, ATTR_PUB_ID, "CDATA", publication.getId());
      attributes.addAttribute(URI, ATTR_PUB_CONTENT, ATTR_PUB_CONTENT, "CDATA", publication.getContentType());
      handler.startElement(URI, ELEMENT_TOP, ELEMENT_TOP, attributes);
      handleResources(handler, resources);
      handleModules(handler, modulesMap);
      if(publication.getContentType().equals(FlatContent.TYPE)){
         FlatContent flatContent = (FlatContent) publication.getContent();
         handleStructures(handler, flatContent.getStructures());
         handleRevisions(handler, flatContent.getRevisions());
      }
      handler.endElement(URI, ELEMENT_TOP, ELEMENT_TOP);
      handler.endDocument();
   }
   private void handleResources(ContentHandler handler, Map resources) throws SAXException {
      // <resource resource="/xml/xhtml" publication="" module="xhtml" name="XHTML"/>
      // ResourceType is last part of "resource" attribute.
      // publication and module attributes set base class. Inheritance follows from that module.
      Iterator moduleI = resources.entrySet().iterator();
      while(moduleI.hasNext()){
         Map.Entry entry = (Map.Entry) moduleI.next();
         String resource = (String) entry.getKey();
         Module module = (Module) entry.getValue();
         String publicationId = module.getPublicationId();
         String moduleId = module.getId();
         AttributesImpl attributes = new AttributesImpl();
         attributes.addAttribute(URI, ATTR_RESOURCE, ATTR_RESOURCE, "CDATA", resource);
         attributes.addAttribute(URI, ATTR_MODULE_SOURCE_PUBLICATION, ATTR_MODULE_SOURCE_PUBLICATION, "CDATA", publicationId);
         attributes.addAttribute(URI, ATTR_MODULE_SOURCE_MODULE, ATTR_MODULE_SOURCE_MODULE, "CDATA", moduleId);
         attributes.addAttribute(URI, ATTR_MODULE_NAME, ATTR_MODULE_NAME, "CDATA", module.getName());
         handler.startElement(URI, ELEMENT_RESOURCE, ELEMENT_RESOURCE, attributes);
         handler.endElement(URI, ELEMENT_RESOURCE, ELEMENT_RESOURCE);
      }
   }
   private void handleModules(ContentHandler handler, Map modules) throws SAXException {
      // <module id="live" publication="" module="live" name="Live"/>
      // For a given "id", publication and module attributes set base class. Inheritance follows from that module.
      Iterator moduleI = modules.entrySet().iterator();
      while(moduleI.hasNext()){
         Map.Entry entry = (Map.Entry) moduleI.next();
         String moduleKey = (String) entry.getKey();
         Module module = (Module) entry.getValue();
         String publicationId = module.getPublicationId();
         String moduleId = module.getId();
         AttributesImpl attributes = new AttributesImpl();
         attributes.addAttribute(URI, ATTR_MODULE_ID, ATTR_MODULE_ID, "CDATA", moduleKey);
         attributes.addAttribute(URI, ATTR_MODULE_SOURCE_PUBLICATION, ATTR_MODULE_SOURCE_PUBLICATION, "CDATA", publicationId);
         attributes.addAttribute(URI, ATTR_MODULE_SOURCE_MODULE, ATTR_MODULE_SOURCE_MODULE, "CDATA", moduleId);
         attributes.addAttribute(URI, ATTR_MODULE_NAME, ATTR_MODULE_NAME, "CDATA", module.getName());
         handler.startElement(URI, ELEMENT_MODULE, ELEMENT_MODULE, attributes);
         handler.endElement(URI, ELEMENT_MODULE, ELEMENT_MODULE);
      }
   }
   private void handleStructures(ContentHandler handler, Set structures) throws SAXException {
      // <structure id="live"/>
      // List structures used by this publication.
      Iterator moduleI = structures.iterator();
      while(moduleI.hasNext()){
         String structure = (String) moduleI.next();
         AttributesImpl attributes = new AttributesImpl();
         attributes.addAttribute(URI, ATTR_STRUCTURE_ID, ATTR_STRUCTURE_ID, "CDATA", structure);
         handler.startElement(URI, ELEMENT_STRUCTURE, ELEMENT_STRUCTURE, attributes);
         handler.endElement(URI, ELEMENT_STRUCTURE, ELEMENT_STRUCTURE);
      }
   }
   private void handleRevisions(ContentHandler handler, Set revisions) throws SAXException {
      // <revision id="live"/>
      // List revisions used by this publication.
      Iterator moduleI = revisions.iterator();
      while(moduleI.hasNext()){
         String revision = (String) moduleI.next();
         AttributesImpl attributes = new AttributesImpl();
         attributes.addAttribute(URI, ATTR_REVISION_ID, ATTR_REVISION_ID, "CDATA", revision);
         handler.startElement(URI, ELEMENT_REVISION, ELEMENT_REVISION, attributes);
         handler.endElement(URI, ELEMENT_REVISION, ELEMENT_REVISION);
      }
   }
}
