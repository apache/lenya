package org.wyona.cms.cocoon.acting;

import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.avalon.framework.thread.ThreadSafe;
import org.apache.cocoon.Constants;
import org.apache.cocoon.acting.AbstractComplementaryConfigurableAction;
import org.apache.cocoon.acting.ValidatorActionHelper;
import org.apache.cocoon.environment.Redirector;
import org.apache.cocoon.environment.Request;
import org.apache.cocoon.environment.Session;
import org.apache.cocoon.environment.SourceResolver;
import org.apache.cocoon.util.Tokenizer;
import org.apache.xpath.XPathAPI;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

import org.w3c.dom.Document;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.wyona.cms.ac.Identity;
import org.wyona.cms.ac.Policy;

/**
 * @author Michael Wechner
 * @version 2.1.6
 */
public class PMLAuthorizerAction extends AbstractAuthorizerAction implements ThreadSafe {
    private String authenticator_type=null;
    private String domain=null;
    private String port=null;
    private String context=null;
    private String policies=null;
    /**
     *
     */
    public void configure(Configuration conf) throws ConfigurationException{
        super.configure(conf);

        Configuration authenticatorConf=conf.getChild("authenticator");
        authenticator_type=authenticatorConf.getAttribute("type");
        if (getLogger().isDebugEnabled()) {
            getLogger().debug("CONFIGURATION: authenticator type="+authenticator_type);
        }
        /*
          if(authenticator_type == null){
          throw new ConfigurationException("No authenticator type set");
          }
        */

        Configuration domainConf=conf.getChild("domain");
        domain=domainConf.getValue("127.0.0.1");
        if (getLogger().isDebugEnabled()) {
            getLogger().debug("CONFIGURATION: domain="+domain);
        }

        Configuration portConf=conf.getChild("port");
        port=portConf.getValue(null);
        if (getLogger().isDebugEnabled()) {
            getLogger().debug("CONFIGURATION: port="+port);
        }

        Configuration contextConf=conf.getChild("context");
        context=contextConf.getValue(null);
        if (getLogger().isDebugEnabled()) {
            getLogger().debug("CONFIGURATION: context="+context);
        }

        Configuration policiesConf=conf.getChild("policies");
        policies=policiesConf.getValue(null);
        if (getLogger().isDebugEnabled()) {
            getLogger().debug("CONFIGURATION: policies="+policies);   
        }
    }
    /**
     *
     */
    public boolean authorize(Request request,Map map) throws Exception{
        String remoteAddress=request.getRemoteAddr();

        // Permit ?Identity? and Policy requests for localhost
        String sitemap_uri=request.getRequestURI();
        if(getLogger().isDebugEnabled()){
          getLogger().debug("POLICIES: "+sitemap_uri+" "+policies);
          }
        if(remoteAddress.equals("127.0.0.1") && (sitemap_uri.indexOf(policies) >= 0)){
            return true;
        }
    
        // Get policy
        Document policyDoc=null;
        try{
            policyDoc=getPolicyDoc(request);
        }
        catch(Exception e){
            getLogger().error(".authorize(): No policy could be retrieved ("+e+"). Access denied (return false).");
            return false;
        }
        Policy policy=new Policy(policyDoc,getLogger());

        // Read action (read, write, publish, etc.)
        String action=XPathAPI.selectSingleNode(policyDoc,"/ac/request/action/@name").getNodeValue(); //"read";
        if (getLogger().isDebugEnabled()) {
            getLogger().debug("action: "+action);
        }

        // Check permissions
        if(policy.authorizeWorld(action)){
            return true;
        }

        if(policy.authorizeMachine(action,remoteAddress)){
            return true;
        }

        Session session=request.getSession(true);
        if(session == null){
            getLogger().error("No session object");
            return false;
        }

        // If there are more than one authenticator enabled, then check corresponding type
        String authenticator_type=(String)session.getAttribute("org.wyona.cms.cocoon.acting.IMLAuthenticator.type");
        if(!this.authenticator_type.equals(authenticator_type)){
        //if(this.authenticator_type != authenticator_type){
            getLogger().warn(".authorize(): Bad authenticator: "+authenticator_type+" (Authorizer's authenticator: "+this.authenticator_type+")");
            return false;
        }

        Identity identity=(Identity)session.getAttribute("org.wyona.cms.ac.Identity");
        if(identity != null){
            if(policy.authorizeUser(action,identity.getUsername())){
                return true;
            }

            String[] groupname=identity.getGroupnames();
            for(int i=0;i<groupname.length;i++){
                if(policy.authorizeGroup(action,groupname[i])){
                    return true;
                }
            }
        }

        getLogger().warn(".authorize(): Permission denied");
        return false;
    }
    /**
     *
     */
    private Document getPolicyDoc(Request request) throws Exception{
        String context=request.getContextPath();
        int port=request.getServerPort();
        String sitemap_uri=request.getSitemapURI();
        String pmlURLString="http://"+domain;
        if(this.port != null){
          getLogger().debug(".getPolicyDoc(): Port set by Configuration: "+this.port+" (request-port: "+port+")");
            pmlURLString=pmlURLString+":"+this.port;
        }
        else{
          getLogger().debug(".getPolicyDoc(): Port set equals to request port: "+port);
            pmlURLString=pmlURLString+":"+port;
        }
        if(this.context != null){
            pmlURLString=pmlURLString+this.context;
        }
        else{
            pmlURLString=pmlURLString+context;
        }
        pmlURLString=pmlURLString+"/"+policies+sitemap_uri+".acml";
        //pmlURLString=pmlURLString+"/"+policies+sitemap_uri+".pml";
        if(getLogger().isDebugEnabled()){
          getLogger().debug(".getPolicyDoc(): "+pmlURLString);
          }
        DocumentBuilderFactory dbf=DocumentBuilderFactory.newInstance();
        DocumentBuilder db=dbf.newDocumentBuilder();
        return db.parse(new URL(pmlURLString).openStream());
    }
    /**
     *
     */
    /*
      private void parse() throws Exception{
      javax.xml.parsers.SAXParserFactory spf=javax.xml.parsers.SAXParserFactory.newInstance();
      javax.xml.parsers.SAXParser sp=spf.newSAXParser();
      }
    */
}
