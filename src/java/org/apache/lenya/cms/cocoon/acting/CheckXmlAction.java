package org.wyona.cms.cocoon.acting;

import org.apache.avalon.framework.parameters.Parameters;
import org.apache.cocoon.acting.AbstractAction;
import org.apache.cocoon.Constants;
import java.util.Map;
import java.util.HashMap;
import org.apache.cocoon.environment.Request;
import org.xml.sax.EntityResolver;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.apache.avalon.framework.configuration.Configurable;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.avalon.framework.parameters.Parameters;
import org.apache.avalon.framework.thread.ThreadSafe;
import org.apache.cocoon.Constants;
import org.apache.cocoon.acting.AbstractComplementaryConfigurableAction;
import org.apache.cocoon.acting.ValidatorActionHelper;
import org.apache.cocoon.environment.Context;
import org.apache.cocoon.environment.Redirector;
import org.apache.cocoon.environment.Request;
import org.apache.cocoon.environment.Session;
import org.apache.cocoon.environment.Source;
import org.apache.cocoon.environment.SourceResolver;
import org.apache.cocoon.util.Tokenizer;
import org.w3c.dom.Document;
import org.wyona.util.Stack;

/**
 * @author Memo Birgi
 * @created 2002.02.21
 * @version 0.1
 */
public abstract class CheckXmlAction extends AbstractAction {
  public java.util.Map act (Redirector redirector, 
                  SourceResolver resolver, 
                  Map objectModel, 
                  String source, 
                  Parameters params) {
    Map sitemapParams = new HashMap();
    sitemapParams.put("world", "hello");

    Request request = (Request) objectModel.get(Constants.REQUEST_OBJECT);

    request.setAttribute("hello", "world");

    return sitemapParams;
  }
}

