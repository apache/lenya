package org.apache.lenya.cms.cocoon.acting;

import org.apache.avalon.framework.parameters.Parameters;

import org.apache.cocoon.environment.Redirector;
import org.apache.cocoon.environment.SourceResolver;
import org.apache.cocoon.acting.ConfigurableComposerAction;

import org.apache.cocoon.components.source.SourceUtil;

import org.apache.excalibur.source.Source;
import org.apache.cocoon.xml.AbstractXMLConsumer;

import org.xml.sax.Attributes;

import java.util.HashMap;
import java.util.Map;
import org.apache.log4j.Category;

public class URIParametrizerAction extends ConfigurableComposerAction  {
    static Category log = Category.getInstance(URIParametrizerAction.class);

    public class URIParametrizerConsumer extends AbstractXMLConsumer {

	boolean inParamElement = false;
	String parameterValue = null;

	public void startElement(String uri, String loc, String raw, Attributes a) {
	    if (loc.equals("parameter")) {
		log.debug("start Element " + uri + ":"+ loc + ":" + raw);
		inParamElement = true;
	    }
	}

	public void endElement(String uri, String loc, String raw, Attributes a) {
	    if (loc.equals("parameter")) {
		log.debug("stop Element " + uri + ":"+ loc + ":" + raw);
		inParamElement = false;
	    }
            log.debug("processing Element " + uri + ":"+ loc + ":" + raw);
	}

	public void characters(char[] ch, int start, int len) {
	    if (inParamElement) {
		parameterValue = new String(ch, start, len);
		log.debug("grab Element " + parameterValue);
	    }
	}
	public String getParameter() {
	    return parameterValue;
	}
    }

    public Map act (Redirector redirector, SourceResolver resolver,
		    Map objectModel, String src, Parameters parameters) throws Exception {
	Source inputSource = null;
	URIParametrizerConsumer xmlConsumer = new URIParametrizerConsumer();

	Map map = new HashMap();
	
        /*
	if (this.getLogger().isDebugEnabled()) {
	    this.getLogger().debug("processing file " + src);
	}

        Request request = ObjectModelHelper.getRequest(objectModel);
        
        String requestUri = request.getRequestURI();
        
        if (getLogger().isDebugEnabled()) {
            getLogger().debug("request URI (not processed): " + requestUri);
        }
        
        String servletPath = request.getServletPath();
        if (requestUri.startsWith(servletPath)) {
            requestUri = requestUri.substring(0, servletPath.length());
        }
        
        if (requestUri.startsWith("/")) {
            requestUri = requestUri.substring(0, 1);
        }
        
        String publicationId = requestUri.substring(0, requestUri.indexOf("/"));
        requestUri = requestUri.substring(0, publicationId.length());
        
        if (getLogger().isDebugEnabled()) {
            getLogger().debug("request URI (processed): " + requestUri);
        }
        */
        
        String parameterNames[] = parameters.getNames();
        for (int i = 0; i < parameterNames.length; i++) {
	    String parameterSrc = parameters.getParameter(parameterNames[i]) + "/" + src;
	    inputSource = resolver.resolveURI(parameterSrc);
	    
	    if (this.getLogger().isDebugEnabled()) {
		this.getLogger().debug("file resolved to " + inputSource.getURI());
	    }
	    SourceUtil.toSAX(inputSource, xmlConsumer);
	    map.put(parameterNames[i], xmlConsumer.getParameter());
        }

	return map;
    }
}



