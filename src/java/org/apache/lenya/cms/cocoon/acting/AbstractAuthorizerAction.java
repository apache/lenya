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
import org.apache.cocoon.sitemap.PatternException;
import org.apache.cocoon.util.Tokenizer;

import org.apache.regexp.RE;
import org.apache.regexp.RECompiler;
import org.apache.regexp.REProgram;
import org.apache.regexp.RESyntaxException;

import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

import org.wyona.util.Stack;

/**
 * @author Michael Wechner
 * @created 2001.11.18
 * @version $Id: AbstractAuthorizerAction.java,v 1.3 2002/06/21 23:40:01 michicms Exp $
 */
public abstract class AbstractAuthorizerAction extends AbstractComplementaryConfigurableAction implements Configurable {
    REProgram [] public_matchers;
    boolean logRequests=false;

    /**
     *
     */
    public void configure(Configuration conf) throws ConfigurationException {
        super.configure(conf);
        Configuration[] publics = conf.getChildren("public");
        public_matchers = new REProgram[publics.length];
        for (int i = 0; i < publics.length; i++) {
            String public_href = publics[i].getValue(null);
            try {
                public_matchers[i] = preparePattern(public_href);
            } catch (PatternException pe) {
                throw new ConfigurationException("invalid pattern for public hrefs", pe);
            }
            if (getLogger().isDebugEnabled()) {
                getLogger().debug("CONFIGURATION: public: " + public_href);
            }
        }
        Configuration log=conf.getChild("log");
        if(log.getValue("off").equals("on")){
            logRequests=true;
        }
        if(logRequests){
            if (getLogger().isDebugEnabled()) {
                getLogger().debug("CONFIGURATION: log requests: on");
            }
        }
        else{
            if (getLogger().isDebugEnabled()) {
                getLogger().debug("CONFIGURATION: log requests: off");
            }
        }
    }
    /**
     *
     */
    public Map act(Redirector redirector,SourceResolver resolver,Map objectModel,String src,Parameters parameters) throws Exception {
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
        if (getLogger().isDebugEnabled()) {
            getLogger().debug("request-uri=" + request_uri);
            getLogger().debug("sitemap-uri=" + sitemap_uri);
        }

        // Set history
        Stack history=(Stack)session.getAttribute("org.wyona.cms.cocoon.acting.History");
        if(history == null){
            history=new Stack(10);
            session.setAttribute("org.wyona.cms.cocoon.acting.History",history);
        }
        history.push(sitemap_uri);

        // Check public uris from configuration above. Should only be used during development before the implementation of a concrete authorizer.
        for (int i = 0; i < public_matchers.length; i++) {
            if (preparedMatch(public_matchers[i], sitemap_uri)) {
                if (getLogger().isDebugEnabled()) {
                    getLogger().debug("Permission granted for free: "+request_uri);
                }
                HashMap actionMap=new HashMap();
                return actionMap;
            }
        }

        String query_string=req.getQueryString();
        if(query_string != null){
          session.setAttribute("protected_destination",request_uri+"?"+req.getQueryString());
          }
        else{
          session.setAttribute("protected_destination",request_uri);
          }

        HashMap actionMap=new HashMap();
        if(authorize(req,actionMap)){
            if (getLogger().isDebugEnabled()) {
                getLogger().debug("Permission granted dues to authorisation: "+request_uri);
            }
            return actionMap;
        }
        if (getLogger().isDebugEnabled()) {
            getLogger().debug("Permission denied: "+request_uri);
        }
        return null;
    }

    /**
     * Compile the pattern in a <code>org.apache.regexp.REProgram</code>.
     */
    protected REProgram preparePattern(String pattern) throws PatternException {
        if (pattern == null) {
            throw new PatternException("null passed as a pattern", null);
        }

        if (pattern.length() == 0) {
            pattern = "^$";
            if (getLogger().isWarnEnabled()) {
                getLogger().warn("The empty pattern string was rewritten to '^$'"
                                 + " to match for empty strings.  If you intended"
                                 + " to match all strings, please change your"
                                 + " pattern to '.*'");
            }
        }

        try {
            RECompiler compiler = new RECompiler();
            REProgram program = compiler.compile(pattern);
            return program;

        } catch (RESyntaxException rse) {
            getLogger().debug("Failed to compile the pattern '" + pattern + "'", rse);
            throw new PatternException(rse.getMessage(), rse);
        }
    }

    /**
     * 
     */
    protected boolean preparedMatch(REProgram preparedPattern, String match) {

        RE re = new RE(preparedPattern);

        if (match == null) {
            return false;
        }

        return re.match(match);
    }

    /**
     * Should be implemented by a concrete authorizer
     */
    public abstract boolean authorize(Request request,Map map) throws Exception;
}
