package org.wyona.cms.cocoon.transformation;

import org.apache.avalon.framework.configuration.Configurable;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;

import org.apache.cocoon.Constants;
import org.apache.cocoon.environment.Request;
import org.apache.cocoon.transformation.AbstractDOMTransformer;

import org.apache.log4j.Category;

import org.w3c.dom.Document;

/**
 * @author Michael Wechner
 * @version 2002.5.30
 */
public class IncludeTransformer extends AbstractDOMTransformer implements Configurable{
  static Category log=Category.getInstance(IncludeTransformer.class);

  private String domain="127.0.0.1";
  private String port=null;
  private String context=null;
  private String publication=null;
/**
 *
 */
  public void configure(Configuration conf) throws ConfigurationException{
    if(conf != null){
      publication=conf.getChild("publication").getAttribute("type");
      getLogger().debug("PUBLICATION TYPE: "+publication);
      }
    else{
      getLogger().error("Configuration is null");
      }
    }
/**
 *
 */
  protected Document transform(Document doc){
    try{
      org.apache.cocoon.environment.Source input_source=this.resolver.resolve("");
      String sitemapPath=input_source.getSystemId();
      getLogger().debug("Absolute SITEMAP Directory: " + sitemapPath);

/*
      String[] params=this.parameters.getNames();
      String names="";
      for(int i=0;i<params.length;i++){
        names=names+" "+params[i];
        }
      getLogger().debug("Parameter Names: " + names);
*/

      String href=this.parameters.getParameter("href",null);
      if(href != null){
        getLogger().debug("Parameter href = " + href);
        }
      else{
        getLogger().debug("No Parameter");
        }

      Request request=(Request)this.objectModel.get(Constants.REQUEST_OBJECT);

      String request_uri=request.getRequestURI();
      String sitemap_uri=request.getSitemapURI();
      getLogger().debug("REQUEST URI: "+request_uri);
      getLogger().debug("SITEMAP URI: "+sitemap_uri);

      context=request.getContextPath();
      String context_publication=context+"/"+publication;
      int port=request.getServerPort();
      String cocoon_base_request="http://"+domain+":"+port+context_publication;
      log.debug("COCOON_BASE_REQUEST: "+cocoon_base_request);
      getLogger().debug("COCOON_BASE_REQUEST: "+cocoon_base_request);

      if(href != null){
        return new org.wyona.xml.XPSAssembler().assemble(doc,sitemapPath+href,cocoon_base_request);
        }
      else{
        return new org.wyona.xml.XPSAssembler().assemble(doc,sitemapPath+sitemap_uri,cocoon_base_request);
        }
      }
    catch(Exception e){
      log.error(e);
      getLogger().error(".transform(): "+e);
      }

    return doc;
    }
/**
 *
 */
/*
  protected Document transform(Document doc){
    return doc;
    }
*/
  }
