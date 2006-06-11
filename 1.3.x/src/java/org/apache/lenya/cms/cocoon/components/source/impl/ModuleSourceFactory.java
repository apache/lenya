package org.apache.lenya.cms.cocoon.components.source.impl;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

import org.apache.cocoon.components.CocoonComponentManager;
import org.apache.cocoon.components.ContextHelper;

import org.apache.avalon.framework.component.ComponentManager;
import org.apache.avalon.framework.context.Context;
import org.apache.avalon.framework.context.ContextException;
import org.apache.avalon.framework.context.Contextualizable;
import org.apache.avalon.framework.thread.ThreadSafe;

import org.apache.excalibur.source.impl.FileSource;
import org.apache.excalibur.source.Source;
import org.apache.excalibur.source.SourceFactory;
import org.apache.excalibur.source.SourceNotFoundException;
import org.apache.excalibur.source.SourceResolver;
import org.apache.excalibur.source.SourceUtil;
import org.apache.excalibur.source.URIAbsolutizer;

import org.apache.lenya.cms.publication.Modules;
import org.apache.lenya.cms.publication.PageEnvelope;
import org.apache.lenya.cms.publication.PageEnvelopeException;
import org.apache.lenya.cms.publication.PageEnvelopeFactory;
import org.apache.lenya.cms.publication.Publication;
import org.apache.lenya.cms.publication.PublicationFactory;

public class ModuleSourceFactory
    implements SourceFactory, ThreadSafe, URIAbsolutizer, Contextualizable {

    protected org.apache.avalon.framework.context.Context context;
    private String servletContextPath;
    String pubsPrefix;
    String globalPrefix;
    private Set publications = new HashSet();   // Publications checked
    static private Map moduleInheritance = new HashMap();   // Key={publication, module} Value = Next publication


    public void contextualize(org.apache.avalon.framework.context.Context context)
    throws ContextException {
        this.context = context;
    }

    public Source getSource(String location, Map parameters) throws IOException, MalformedURLException {
       int pos;
       Map contextmap = ContextHelper.getObjectModel(context);
       org.apache.cocoon.environment.http.HttpContext httpcontext = 
             (org.apache.cocoon.environment.http.HttpContext) contextmap.get("context");
       servletContextPath = httpcontext.getRealPath("");
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
       StringTokenizer tokens = new StringTokenizer(uri, "/\\:", true);
       StringBuffer buffer = new StringBuffer();
       boolean done = false;
       while(tokens.hasMoreTokens() & !done){
          String token = tokens.nextToken();
          if(token.equalsIgnoreCase("pubs") | token.equalsIgnoreCase("modules")){
             done = true;
          }else buffer.append(token);
       }
       String tmpPrefix = buffer.toString();
       globalPrefix = tmpPrefix + "modules" + File.separator;
       pubsPrefix = tmpPrefix + "pubs" + File.separator;

       String publication;
       Modules modules;
       publications.clear();
       try{
            PageEnvelope envelope = 
                  PageEnvelopeFactory.getInstance().getPageEnvelope(ContextHelper.getObjectModel(context));
            Publication pub = envelope.getPublication();
            publication = pub.getId();
		modules = pub.getModules();
        }catch(org.apache.lenya.cms.publication.PageEnvelopeException pee){
            throw new MalformedURLException("ModuleSourceFactory PageEnvelopeException. Could not get Publication.");
        }

       // Reset moduleInheritance
       pos = location.indexOf("::");
       if(pos != -1) moduleInheritance.clear();
       // Decide Usage
       pos = location.indexOf(":///");
       int endpos;
       String module = getModuleID(uri);
       String filepath = "module.xmap";
       if(pos != -1){
          // module:/filepath/filename.ext
          //Get current Module ID
          filepath = location.substring(pos + 4);
       }else{
          pos = location.indexOf("://");
	    if(pos != -1){
             // module://modulename/filepath/filename.ext
             pos += 3;
             endpos = location.indexOf("/", pos);
             if(endpos > 0){
                module = location.substring(pos, endpos);
                filepath = location.substring(endpos + 1);
             }else{
                module = location.substring(pos);
             }
          }else{
             pos = location.indexOf(":/");
             if(pos != -1){
                // module:///publication/modulename/filepath/filename.ext
                pos += 2;
                endpos = location.indexOf("/", pos);
                if(endpos > 0){
                   publication = location.substring(pos, endpos);
                   pos = endpos + 1;
                   endpos = location.indexOf("/", pos);
                   if(endpos > 0){
                      module = location.substring(pos, endpos);
                      filepath = location.substring(endpos + 1);
                   }else{
                      module = location.substring(pos);
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
       if(publication.length() < 1) throw new MalformedURLException("No Publication ID found.");
       if(module.length() < 1) module = getModuleID(uri);
       if(filepath.length() < 1) filepath = "module.xmap";
       //Check current publication
       if(!modules.isAllowed(module)) 
             throw new SourceNotFoundException("Not allowed: " + publication + "/" + module + "/" + filepath);
       /********** Get Source *************/
       //String newpath;
       String newlocation = pubsPrefix  + publication + File.separator + "modules" + File.separator + module + File.separator + filepath;
       // Check if exists locally.  Yes = done.
       try{
          Source source = resolver.resolveURI(newlocation);
          if(source.exists()){
            if (resolver != null) manager.release(resolver);
             return source;
          }
       }catch(java.net.MalformedURLException mue2){
       }catch(java.io.IOException ioe1){
       }
       publications.add(publication);

       //Check inherited publication(s)
       if(null != modules){
          Source ret = getInheritedSource(publication, module, filepath, modules.getTemplates(module), parameters, resolver);
          if(null != ret){
            if (resolver != null) manager.release(resolver);
            return ret;
          }
       }

       // Check global
       newlocation = globalPrefix + module + File.separator + filepath;
       try{
          Source source = resolver.resolveURI(newlocation);
          if(source.exists()){
             if (resolver != null) manager.release(resolver);
             return source;
          }
       }catch(java.net.MalformedURLException mue2){
       }catch(java.io.IOException ioe1){
       }
       if (resolver != null) manager.release(resolver);
       throw new SourceNotFoundException("Not found: " + publication + "/" + module + "/" + filepath);
    }

    public void release(Source source1) {
    }

    public String absolutize(String baseURI, String location) {
        return SourceUtil.absolutize(baseURI, location, false, false);
    }
   private String getModuleID(String uri) throws MalformedURLException{
        String module = "";
        int pos = uri.indexOf("modules/");
        if(pos > -1){
             pos += "modules/".length();
             int endpos = uri.indexOf("/", pos);
             if(endpos > -1){
               module = uri.substring(pos, endpos);
             }else module = uri.substring(pos);
        }
        return module;
   }
   private Source getInheritedSource(String publication, String modulex, String filepath, String[] templates, Map parameters, SourceResolver resolver){
       String module = modulex;
       int i = 0;
       boolean found = false;
       Modules modules = (Modules) null;
       String key = publication + "~" + module;
       String newpublication = "";
       if(moduleInheritance.containsKey(key)){
          newpublication = (String) moduleInheritance.get(key);
          Publication pub = getPublication(newpublication);
          modules = pub.getModules();
          found = true;
          publications.add(templates[i]);
       }else{
          while(!found & (i < templates.length)){
             newpublication = templates[i];
             // Do not repeat publication
             if(!publications.contains(newpublication)){
                modules = (Modules) null;
                Publication pub = getPublication(newpublication);
                if(null != pub){
                   modules = pub.getModules();
                   if(modules.isAllowed(module)) found = true;
                }
                publications.add(newpublication);
             }
             i++;
          }
       }
       if(found){
          moduleInheritance.put(key , newpublication);
          String newlocation = pubsPrefix + newpublication + File.separator + "modules" + File.separator+ module + File.separator + filepath;
          try{
             Source source = resolver.resolveURI(newlocation);
             if(source.exists()){
                return source;
             }
          }catch(java.net.MalformedURLException mue2){
          }catch(java.io.IOException ioe1){
          }
          if(null != modules){
             //First check if module name was overridden
             Source ret = getInheritedSource(newpublication, modules.getInheritedModule(module), 
                   filepath, modules.getTemplates(module), parameters, resolver);
             if(null != ret) return ret;
             return getInheritedSource(newpublication, module, filepath, modules.getTemplates(module), parameters, resolver);
          }
       }
       return (Source) null;
   }
   private Publication getPublication(String publication){
      try{
         return PublicationFactory.getPublication(publication, servletContextPath);
      }catch(org.apache.lenya.cms.publication.PublicationException pe){
         return (Publication) null;
      }
   }
}