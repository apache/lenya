package org.apache.lenya.cms.cocoon.components.source.impl;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.apache.avalon.framework.thread.ThreadSafe;
import org.apache.excalibur.source.Source;
import org.apache.excalibur.source.SourceFactory;
import org.apache.excalibur.source.URIAbsolutizer;
import org.apache.excalibur.source.impl.FileSource;
import org.apache.avalon.framework.component.ComponentManager;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.DefaultConfigurationBuilder;
import org.apache.avalon.framework.context.Context;
import org.apache.avalon.framework.context.ContextException;
import org.apache.avalon.framework.context.Contextualizable;
import org.apache.cocoon.components.CocoonComponentManager;
import org.apache.cocoon.components.ContextHelper;
import org.apache.excalibur.source.SourceUtil;
import org.apache.excalibur.source.SourceNotFoundException;
import org.apache.excalibur.source.SourceResolver;
import org.apache.lenya.cms.content.Content;
import org.apache.lenya.cms.publication.Modules;
import org.apache.lenya.cms.publication.PageEnvelope;
import org.apache.lenya.cms.publication.PageEnvelopeException;
import org.apache.lenya.cms.publication.PageEnvelopeFactory;
import org.apache.lenya.cms.publication.Publication;
import org.apache.lenya.cms.publication.PublicationFactory;

import org.apache.lenya.cms.content.Resource;
import org.w3c.dom.Document;


/**
 * Implements content: protocol.
 * This should call the Content API that calls a Content Impl.
 * 
 * == Content API ==
 * Source getResourceByUNID(String unid, String translation, String revision)
 * Source getResourceByID(String structure, String id, String translation, String revision)
 * 
 * == Content Impls ==
 * ContentHierarchical (Lenya 1.2)
 * ContentFlat (Lenya 1.3)
 */


public class ContentSourceFactory
    implements SourceFactory, ThreadSafe, URIAbsolutizer, Contextualizable {

    private static final int REQUEST_DATA = 0;
    private static final int REQUEST_META = 1;
    private static final int REQUEST_INFO = 2;

    protected org.apache.avalon.framework.context.Context context;
    private String servletContextPath;
    String pubsPrefix;
    DefaultConfigurationBuilder builder = new DefaultConfigurationBuilder();
    SourceResolver resolver = null;

    public void contextualize(org.apache.avalon.framework.context.Context context)
    throws ContextException {
        this.context = context;
    }

    public Source getSource(String plocation, Map parameters) throws IOException, MalformedURLException {
       String location = plocation;
       int pos;
       Map contextmap = ContextHelper.getObjectModel(context);
       org.apache.cocoon.environment.http.HttpContext httpcontext = 
             (org.apache.cocoon.environment.http.HttpContext) contextmap.get("context");
       servletContextPath = httpcontext.getRealPath("");
//TODO: Move resolver, pubsPrefix and other init out of getSource().  Make static?
        ComponentManager manager = CocoonComponentManager.getSitemapComponentManager();
        try{
           resolver = (SourceResolver) manager.lookup(SourceResolver.ROLE);
        }catch(org.apache.avalon.framework.component.ComponentException ce){
        }
        if(null == resolver){
           throw new SourceNotFoundException("No Resolver: " + plocation);
        }
        String uri = resolver.resolveURI("").getURI();
        pos = uri.indexOf("/pubs/");
        if(pos > 0){
           pubsPrefix = uri.substring(0, pos + 6);
        }else{
           pos = uri.indexOf("/modules/");
           if(pos > 0){
              pubsPrefix = uri.substring(0, pos) + "/pubs/";
           }
        }
       String publication;
       String contentpath;
       Publication pub;
       Content content;
       try{
            PageEnvelope envelope = 
                  PageEnvelopeFactory.getInstance().getPageEnvelope(ContextHelper.getObjectModel(context));
            pub = envelope.getPublication();
            publication = pub.getId();
            content = pub.getContent();
            contentpath = pub.getContentDirectory().getAbsolutePath() + File.separator;
        }catch(org.apache.lenya.cms.publication.PageEnvelopeException pee){
            throw new MalformedURLException("Could not get Publication ID.");
        }
       // Decide Usage
       // Content or Meta
       int requestType = REQUEST_DATA;
       pos = location.indexOf(":::");
       if(pos != -1){
          requestType = REQUEST_INFO;
       }else{
          pos = location.indexOf("::");
          if(pos != -1) requestType = REQUEST_META;
       }
       //Revision
       String revision = "live";
       pos = location.lastIndexOf("!");
       if(pos != -1){
          revision = location.substring(pos + 1);
          location = location.substring(0, pos);
       }
       //Language
       String language = "";
       pos = location.lastIndexOf("_");
       if(pos != -1){
          language = location.substring(pos + 1);
          location = location.substring(0, pos);
       }
//TODO: Set language to document or publication's default if not specified.
       int endpos;
       String structure = "";
       String unid = "";
       String fullid = "";
       pos = location.indexOf(":///");
       if(pos != -1){
          // content:///parents/resourceID
          //Guess structure?
          fullid = location.substring(pos + 4);
       }else{
          pos = location.indexOf("://");
	    if(pos != -1){
          // content://structure/parents/resourceID
             pos += 3;
             endpos = location.indexOf("/", pos);
             if(endpos > 0){
                structure = location.substring(pos, endpos);
                fullid = location.substring(endpos + 1);
//System.out.println("CSF SF S=" + structure + "  F=" + fullid);
             }else{
                structure = location.substring(pos);
             }
          }else{
             //Use UNID
             // content:/resourceUNID
             pos = location.indexOf(":/");
             if(pos != -1){
                // module:/unid
                pos += 2;
                unid = location.substring(pos);
             }else{
                // (Default protocol)
                pos = location.indexOf("/");
                if(pos != -1){
                   fullid = location;
                }else{
                   unid = location;
                }
             }
          }
       }
       if(unid.length() < 1){
          unid = content.getUNID(structure, "/" + fullid);
       }
       if(language.length() < 1){
          Resource resource = content.getResource(unid);
          if(resource != null) language = resource.getDefaultLanguage();
       }
       if(language.length() < 1) language = pub.getDefaultLanguage();

       /********** Get Source (uses Content) *************/
       Source source;
       if(REQUEST_INFO == requestType){
//TODO: Catch errors
          Resource resource = content.getResource(unid);
if(resource == null) System.out.println("NO RESOURCE");
          Document doc = resource.getInfoDocument();
if(doc == null) System.out.println("NO DOC");
          source = new StringSource(manager, doc);
if(source == null) System.out.println("NO SOURCE");
          return source;
       }
       if(REQUEST_META == requestType){
          source = resolver.resolveURI(content.getMetaURI(unid, language, revision));
          if(source.exists()){
             if (resolver != null) manager.release(resolver);
             return source;
          }
       }
//System.out.println("CSF UNID=" + unid + " LANG=" + language + "  REV=" + revision);
      
       String curi = content.getURI(unid, language, revision);
//System.out.println("CSF CURI=" + curi);
       source = resolver.resolveURI(curi);
       if(source.exists()){
          if (resolver != null) manager.release(resolver);
          return source;
       }
       if (resolver != null) manager.release(resolver);
       throw new SourceNotFoundException("Not found: " + plocation + " (" + curi + ")");
    }
    public void release(Source source1) {
    }
    public String absolutize(String baseURI, String location) {
        return SourceUtil.absolutize(baseURI, location, false, false);
    }
    private Publication getPublication(String publication){
      try{
         return PublicationFactory.getPublication(publication, servletContextPath);
      }catch(org.apache.lenya.cms.publication.PublicationException pe){
         return (Publication) null;
      }
    }
}
