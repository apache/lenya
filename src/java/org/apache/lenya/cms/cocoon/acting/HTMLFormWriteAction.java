/*
 * $Id: HTMLFormWriteAction.java,v 1.3 2003/02/07 12:14:08 ah Exp $
 * <License>
 * The Apache Software License
 *
 * Copyright (c) 2002 wyona. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice, this
 *    list of conditions and the following disclaimer in the documentation and/or
 *    other materials provided with the distribution.
 *
 * 3. All advertising materials mentioning features or use of this software must
 *    display the following acknowledgment: "This product includes software developed
 *    by wyona (http://www.wyona.org)"
 *
 * 4. The name "wyona" must not be used to endorse or promote products derived from
 *    this software without prior written permission. For written permission, please
 *    contact contact@wyona.org
 *
 * 5. Products derived from this software may not be called "wyona" nor may "wyona"
 *    appear in their names without prior written permission of wyona.
 *
 * 6. Redistributions of any form whatsoever must retain the following acknowledgment:
 *    "This product includes software developed by wyona (http://www.wyona.org)"
 *
 * THIS SOFTWARE IS PROVIDED BY wyona "AS IS" WITHOUT ANY WARRANTY EXPRESS OR IMPLIED,
 * INCLUDING THE WARRANTY OF NON-INFRINGEMENT AND THE IMPLIED WARRANTIES OF MERCHANTI-
 * BILITY AND FITNESS FOR A PARTICULAR PURPOSE. wyona WILL NOT BE LIABLE FOR ANY DAMAGES
 * SUFFERED BY YOU AS A RESULT OF USING THIS SOFTWARE. IN NO EVENT WILL wyona BE LIABLE
 * FOR ANY SPECIAL, INDIRECT OR CONSEQUENTIAL DAMAGES OR LOST PROFITS EVEN IF wyona HAS
 * BEEN ADVISED OF THE POSSIBILITY OF THEIR OCCURRENCE. wyona WILL NOT BE LIABLE FOR ANY
 * THIRD PARTY CLAIMS AGAINST YOU.
 *
 * Wyona includes software developed by the Apache Software Foundation, W3C,
 * DOM4J Project, BitfluxEditor and Xopus.
 * </License>
 */
package org.wyona.cms.cocoon.acting;

import org.apache.avalon.framework.configuration.Configurable;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.avalon.framework.parameters.Parameters;
import org.apache.avalon.framework.thread.ThreadSafe;

import org.apache.cocoon.Constants;
import org.apache.cocoon.acting.AbstractComplementaryConfigurableAction;
import org.apache.cocoon.acting.ValidatorActionHelper;
import org.apache.cocoon.environment.Redirector;
import org.apache.cocoon.environment.Request;
import org.apache.cocoon.environment.Session;
import org.apache.cocoon.environment.SourceResolver;
import org.apache.cocoon.util.Tokenizer;

import org.wyona.util.Stack;

import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;


/**
 * DOCUMENT ME!
 *
 * @author Michael Wechner
 * @version 2001.12.28
 */
public class HTMLFormWriteAction extends AbstractComplementaryConfigurableAction
    implements Configurable {
        
    //  String[] public_hrefs=null;
    //  boolean logRequests=false;

    /**
     * DOCUMENT ME!
     *
     * @param conf DOCUMENT ME!
     *
     * @throws ConfigurationException DOCUMENT ME!
     */
    public void configure(Configuration conf) throws ConfigurationException {
        super.configure(conf);

        /*
            Configuration[] publics=conf.getChildren("public");
            public_hrefs=new String[publics.length];
            for(int i=0;i<publics.length;i++){
              public_hrefs[i]=publics[i].getAttribute("href");
              getLogger().debug("CONFIGURATION: public: "+public_hrefs[i]);
              }
            Configuration log=conf.getChild("log");
            if(log.getValue("off").equals("on")){
              logRequests=true;
              }
            if(logRequests){
              getLogger().debug("CONFIGURATION: log requests: on");
              }
            else{
              getLogger().debug("CONFIGURATION: log requests: off");
              }
        */
    }

    /**
     * DOCUMENT ME!
     *
     * @param redirector DOCUMENT ME!
     * @param resolver DOCUMENT ME!
     * @param objectModel DOCUMENT ME!
     * @param src DOCUMENT ME!
     * @param parameters DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     *
     * @throws Exception DOCUMENT ME!
     */
    public Map act(Redirector redirector, SourceResolver resolver, Map objectModel, String src,
        Parameters parameters) throws Exception {
        if (23 > 2) {
            HashMap actionMap = new HashMap();

            return actionMap;
        }

        return null;

        /*
            // Get request object
            Request req=(Request)objectModel.get(Constants.REQUEST_OBJECT);
            if(req == null){
              getLogger().error ("No request object");
              return null;
              }
            Session session=req.getSession(true);
            if(session == null){
              getLogger().error("No session object");
              return null;
              }

            // Get uri
            String request_uri=req.getRequestURI();
            String sitemap_uri=req.getSitemapURI();
            getLogger().error("request-uri="+request_uri);
            getLogger().error("sitemap-uri="+sitemap_uri);

            // Set history
            Stack history=(Stack)session.getAttribute("org.wyona.cms.cocoon.acting.History");
            if(history == null){
              history=new Stack(10);
              session.setAttribute("org.wyona.cms.cocoon.acting.History",history);
              }
            history.push(sitemap_uri);

            // Check public uris from configuration above. Should only be used during development before the implementation of a concrete authorizer.
            for(int i=0;i<public_hrefs.length;i++){
              if(sitemap_uri.equals(public_hrefs[i])){
                getLogger().error("Permission granted: "+request_uri);
                HashMap actionMap=new HashMap();
                return actionMap;
                }
              }

            session.setAttribute("protected_destination",request_uri);

            HashMap actionMap=new HashMap();
            if(authorize(req,actionMap)){
              getLogger().error("Permission granted: "+request_uri);
              return actionMap;
              }
            getLogger().error("Permission denied: "+request_uri);
            return null;
        */
    }
}
