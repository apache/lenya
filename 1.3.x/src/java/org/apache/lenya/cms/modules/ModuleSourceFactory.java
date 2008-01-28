package org.apache.lenya.cms.modules;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Map;
import org.apache.avalon.framework.component.ComponentManager;
import org.apache.avalon.framework.context.ContextException;
import org.apache.avalon.framework.context.Contextualizable;
import org.apache.avalon.framework.thread.ThreadSafe;
import org.apache.cocoon.components.CocoonComponentManager;
import org.apache.cocoon.components.ContextHelper;
import org.apache.excalibur.source.Source;
import org.apache.excalibur.source.SourceFactory;
import org.apache.excalibur.source.SourceNotFoundException;
import org.apache.excalibur.source.SourceResolver;
import org.apache.excalibur.source.SourceUtil;
import org.apache.excalibur.source.URIAbsolutizer;
import org.apache.excalibur.source.impl.FileSource;
import org.apache.lenya.cms.publication.PageEnvelope;
import org.apache.lenya.cms.publication.PageEnvelopeFactory;
import org.apache.lenya.cms.publication.Publication;
/**
 * 2000125: Integrated with org.apache.lenya.cms.modules
 * 
 * @author solprovider
 * 
 */
public class ModuleSourceFactory implements SourceFactory, ThreadSafe, URIAbsolutizer, Contextualizable {
   protected org.apache.avalon.framework.context.Context context;
   public void contextualize(org.apache.avalon.framework.context.Context context) throws ContextException {
      this.context = context;
   }
   public Source getSource(String location, Map parameters) throws IOException, MalformedURLException {
      // System.out.println("MSF LOC=" + location);
      int pos;
      SourceResolver resolver = null;
      ComponentManager manager = CocoonComponentManager.getSitemapComponentManager();
      try{
         resolver = (SourceResolver) manager.lookup(SourceResolver.ROLE);
      }catch(org.apache.avalon.framework.component.ComponentException ce){
      }
      if(null == resolver){
         System.out.println("ModuleSourceFactory ComponentException");
         return new FileSource(location);
      }
      String uri = resolver.resolveURI("").getURI();
      String publication;
      Publication pub;
      try{
         PageEnvelope envelope = PageEnvelopeFactory.getInstance().getPageEnvelope(ContextHelper.getObjectModel(context));
         pub = envelope.getPublication();
         publication = pub.getId();
      }catch(org.apache.lenya.cms.publication.PageEnvelopeException pee){
         throw new MalformedURLException("ModuleSourceFactory PageEnvelopeException. Could not get Publication.");
      }
      // Reset moduleInheritance
      boolean needsReset = false;
      if(location.indexOf("::") != -1)
         needsReset = true;
      // Decide Usage
      pos = location.indexOf(":///");
      int endpos;
      String moduleId = getModuleID(uri);
      // BUG ALERT:
      // <map:mount> tries both module://module/filepath and module://module/
      // If filepath has a default, the second attempt will error.
      // Not having the default destroys Flow:
      // source = resolver.resolveURI("cocoon:/pipeline");
      // var is = new
      // Packages.org.xml.sax.InputSource(source.getInputStream());
      // This errors.
      // Every Module must have a module.xmap (even if it is empty)!
      String filepath = "module.xmap";
      if(pos != -1){ // module:/filepath/filename.ext
         // Get current Module ID
         filepath = location.substring(pos + 4);
      }else{
         pos = location.indexOf("://");
         if(pos != -1){ // module://modulename/filepath/filename.ext
            pos += 3;
            endpos = location.indexOf("/", pos + 1);
            if(endpos > 0){
               moduleId = location.substring(pos, endpos);
               filepath = location.substring(endpos + 1);
               // System.out.println("MSF MOD=" + module + " FIL=" + filepath);
            }else{
               moduleId = location.substring(pos);
               // System.out.println("MSF MOD=" + module + " POS=" + pos);
            }
         }else{
            pos = location.indexOf(":/");
            if(pos != -1){ // module:///publication/modulename/filepath/filename.ext
               pos += 2;
               endpos = location.indexOf("/", pos);
               if(endpos > 0){
                  publication = location.substring(pos, endpos);
                  pos = endpos + 1;
                  endpos = location.indexOf("/", pos);
                  if(endpos > 0){
                     moduleId = location.substring(pos, endpos);
                     filepath = location.substring(endpos + 1);
                  }else{
                     moduleId = location.substring(pos);
                  }
               }else{
                  publication = location.substring(pos);
               }
            }else{
               // /filepath/filename.ext (Default protocol)
               filepath = location;
            }
         }
      }
      // Verify
      if(publication.length() < 1)
         throw new MalformedURLException("No Publication ID found.");
      if(moduleId.length() < 1)
         moduleId = getModuleID(uri);
      // BUG ALERT: See description above about no default
      if(filepath.length() < 1)
         filepath = "module.xmap";
      // ## Get Module - BEGIN
      // System.out.println("GetFile P=" + publication + " M=" + moduleId + " R=" + filepath);
      String newlocation = pub.getModules().getFile(moduleId, filepath, needsReset);
      // System.out.println("GetModule F=" + newlocation);
      if(newlocation.length() < 1)
         throw new SourceNotFoundException("Not found: " + location + " -> " + newlocation);
      System.out.println("ModuleSource: " + newlocation);
      FileSource source = new FileSource("file://" + newlocation);
      System.out.println("FileSource: " + source.getURI());
      File file = source.getFile();
      System.out.println("File: " + file.getAbsolutePath());
      return source;
      // return new FileSource(newlocation);
   }
   public void release(Source source1) {
   }
   public String absolutize(String baseURI, String location) {
      return SourceUtil.absolutize(baseURI, location, false, false);
   }
   private String getModuleID(String uri) throws MalformedURLException {
      String module = "";
      int pos = uri.indexOf("modules/");
      if(pos > -1){
         pos += "modules/".length();
         int endpos = uri.indexOf("/", pos);
         if(endpos > -1){
            module = uri.substring(pos, endpos);
         }else
            module = uri.substring(pos);
      }
      return module;
   }
}