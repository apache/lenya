package org.lenya.cms.cocoon.acting;

import org.apache.avalon.framework.parameters.Parameters;
import org.apache.avalon.framework.thread.ThreadSafe;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;

import org.apache.cocoon.environment.ObjectModelHelper;
import org.apache.cocoon.environment.Redirector;
import org.apache.cocoon.environment.Request;
import org.apache.cocoon.environment.Session;
import org.apache.cocoon.environment.SourceResolver;
import org.apache.cocoon.acting.ConfigurableComposerAction;

import org.apache.excalibur.source.Source;
import org.apache.cocoon.xml.AbstractXMLConsumer;

import org.xml.sax.Attributes;

import java.util.HashMap;
import java.util.Map;
import java.util.Iterator;

import org.apache.log4j.Category;

public class URIParametrizerAction extends ConfigurableComposerAction  {
    static Category log = Category.getInstance(URIParametrizerAction.class);

    private Map parameters;

    public class URIParametrizerConsumer extends AbstractXMLConsumer {

	boolean inParamElement = false;
	String parameterValue = null;

	public void startElement(String uri, String loc, String raw, Attributes a) {
	    if (loc.equals("parameter")) {
		log.error("start Element " + uri + ":"+ loc + ":" + raw);
		inParamElement = true;
	    }
	}

	public void endElement(String uri, String loc, String raw, Attributes a) {
	    if (loc.equals("parameter")) {
		log.error("stop Element " + uri + ":"+ loc + ":" + raw);
		inParamElement = false;
	    }
            log.error("processing Element " + uri + ":"+ loc + ":" + raw);
	}

	public void characters(char[] ch, int start, int len) {
	    if (inParamElement) {
		parameterValue = new String(ch, start, len);
		log.error("grab Element " + parameterValue);
	    }
	}
	public String getParameter() {
	    return parameterValue;
	}
    }

    /**
     * Describe <code>configure</code> method here.
     *
     * @param conf a <code>Configuration</code> value
     *
     * @exception ConfigurationException if an error occurs
     */
    public void configure(Configuration conf) throws ConfigurationException {
        super.configure(conf);
	
	Configuration[] parameterConfigs = null;
	this.parameters = new HashMap();

	parameterConfigs = conf.getChildren("parameter");
	for (int i = 0; i < parameterConfigs.length; i++) {
	    parameters.put(parameterConfigs[i].getAttribute("name"),
			   parameterConfigs[i].getAttribute("src"));
	}
    }


    public Map act (Redirector redirector, SourceResolver resolver,
		    Map objectModel, String src, Parameters par) throws Exception {
	Source inputSource = null;
	URIParametrizerConsumer xmlConsumer = new URIParametrizerConsumer();

	Map map = new HashMap();
	
	if (this.getLogger().isDebugEnabled()) {
	    this.getLogger().debug("processing file " + src);
	    this.getLogger().debug("file resolved to " + inputSource.getURI());
	}

	Iterator parameterMappings = this.parameters.keySet().iterator();
	while (parameterMappings.hasNext()) {
	    String parameterName = (String)parameterMappings.next();
	    String parameterSrc = (String)parameters.get(parameterName);
		
	    inputSource = resolver.resolveURI(parameterSrc);

	    resolver.toSAX(inputSource, xmlConsumer);
	
	    map.put(parameterName, xmlConsumer.getParameter());
	}
	return map;
    }
}



