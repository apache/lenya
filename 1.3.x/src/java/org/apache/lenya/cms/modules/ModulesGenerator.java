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
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;
public class ModulesGenerator extends ServiceableGenerator implements CacheableProcessingComponent {
   private static final String PREFIX = "modules";
   private static final String URI = "http://apache.org/lenya/1.3/" + PREFIX;
   private static final String ATTR_MODULE_NAME = "name";
   private static final String ELEMENT_TOP = "modules";
   private static final String ELEMENT_MODULE = "module";
   private static final String ATTR_MODULE_ID = "id";
   private static final String ATTR_MODULE_TYPE = "type";
   private static final String ATTR_MODULE_PUBLICATION = "publication";
   private static final String ELEMENT_DESCRIPTION = "description";
   public Serializable getKey() {
      // TODO Auto-generated method stub
      return null;
   }
   public SourceValidity getValidity() {
      // TODO Auto-generated method stub
      return null;
   }
   public void generate() throws IOException, SAXException, ProcessingException {
      ContentHandler handler = this.contentHandler;
      handler.startDocument();
      handler.startPrefixMapping(PREFIX, URI);
      AttributesImpl attributes = new AttributesImpl();
      attributes.addAttribute("", "xmlns", "xmlns", "CDATA", URI);
      // attributes.addAttribute("xmlns", PREFIX, "xmlns:" + PREFIX, "CDATA", URI);
      handler.startElement(URI, ELEMENT_TOP, ELEMENT_TOP, attributes);
      handleModules(handler, Modules.modules);
      handler.endElement(URI, ELEMENT_TOP, ELEMENT_TOP);
      handler.endDocument();
   }
   private void handleModules(ContentHandler handler, Map modules) throws SAXException {
      Iterator moduleI = modules.entrySet().iterator();
      while(moduleI.hasNext()){
         Map.Entry entry = (Map.Entry) moduleI.next();
         String moduleKey = (String) entry.getKey();
         Object o = entry.getValue();
         String[] moduleKeyParts = moduleKey.split("\\.", 2);
         String publicationId = moduleKeyParts[0];
         String moduleId = "";
         AttributesImpl attributes = new AttributesImpl();
         if(moduleKeyParts.length > 1){
            moduleId = moduleKeyParts[1];
         }
         attributes.addAttribute(URI, ATTR_MODULE_PUBLICATION, ATTR_MODULE_PUBLICATION, "CDATA", publicationId);
         if(publicationId.length() < 1){
            attributes.addAttribute(URI, ATTR_MODULE_TYPE, ATTR_MODULE_TYPE, "CDATA", "global");
         }else{
            attributes.addAttribute(URI, ATTR_MODULE_TYPE, ATTR_MODULE_TYPE, "CDATA", "publication");
         }
         attributes.addAttribute(URI, ATTR_MODULE_ID, ATTR_MODULE_ID, "CDATA", moduleId);
         handler.startElement(URI, ELEMENT_MODULE, ELEMENT_MODULE, attributes);
         handleObject(handler, o);
         handler.endElement(URI, ELEMENT_MODULE, ELEMENT_MODULE);
      }
   }
   private void handleObject(ContentHandler handler, Object o) throws SAXException {
      if(Module.class.isAssignableFrom(o.getClass())){
         Module module = (Module) o;
         handleModule(handler, module);
      }else if(ModuleSet.class.isAssignableFrom(o.getClass())){
         ModuleSet moduleSet = (ModuleSet) o;
         // Care about default?
         Iterator moduleI = moduleSet.modules.values().iterator();
         while(moduleI.hasNext()){
            Module module = (Module) moduleI.next();
            handleModule(handler, module);
         }
      }else{
         System.out.println("ModuleGenerator: Found class " + o.getClass().toString());
      }
   }
   private void handleModule(ContentHandler handler, Module module) throws SAXException {
      AttributesImpl attributes = new AttributesImpl();
      attributes.addAttribute(URI, ATTR_MODULE_TYPE, ATTR_MODULE_TYPE, "CDATA", module.getContentType());
      attributes.addAttribute(URI, ATTR_MODULE_NAME, ATTR_MODULE_NAME, "CDATA", module.getName());
      String resource = module.getResource();
      if(resource.length() > 0){
         attributes.addAttribute(URI, ATTR_MODULE_TYPE, ATTR_MODULE_TYPE, "CDATA", resource);
      }
      handler.startElement(URI, ELEMENT_MODULE, ELEMENT_MODULE, attributes);
      String description = module.getDescription();
      int length = description.length();
      if(length > 0){
         handler.startElement(URI, ELEMENT_DESCRIPTION, ELEMENT_DESCRIPTION, new AttributesImpl());
         handler.characters(description.toCharArray(), 0, length);
         handler.endElement(URI, ELEMENT_DESCRIPTION, ELEMENT_DESCRIPTION);
      }
      handleList(handler, module.inheritList, "inherit");
      handleList(handler, module.requiredList, "required");
      handleList(handler, module.recommendedList, "recommended");
      handleList(handler, module.optionalList, "optional");
      handler.endElement(URI, ELEMENT_MODULE, ELEMENT_MODULE);
   }
   private void handleList(ContentHandler handler, Map map, String element) throws SAXException {
      Iterator iterator = map.entrySet().iterator();
      while(iterator.hasNext()){
         Map.Entry entry = (Map.Entry) iterator.next();
         String description = (String) entry.getValue();
         AttributesImpl attributes = new AttributesImpl();
         attributes.addAttribute(URI, ATTR_MODULE_ID, ATTR_MODULE_ID, "CDATA", (String) entry.getKey());
         handler.startElement(URI, element, element, attributes);
         handler.characters(description.toCharArray(), 0, description.length());
         handler.endElement(URI, element, element);
      }
   }
   private void handleList(ContentHandler handler, Set set, String element) throws SAXException {
      Iterator iterator = set.iterator();
      while(iterator.hasNext()){
         String entry = (String) iterator.next();
         AttributesImpl attributes = new AttributesImpl();
         attributes.addAttribute(URI, ATTR_MODULE_ID, ATTR_MODULE_ID, "CDATA", entry);
         handler.startElement(URI, element, element, attributes);
         handler.endElement(URI, element, element);
      }
   }
}
