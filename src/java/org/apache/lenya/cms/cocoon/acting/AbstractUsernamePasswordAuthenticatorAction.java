package org.wyona.cms.cocoon.acting;

import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.cocoon.environment.Request;
import org.apache.cocoon.environment.Session;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import java.io.File;
import java.net.URL;
import java.util.Map;

import org.wyona.cms.ac.Identity;
import org.wyona.cms.ac.Password;

/**
 * @author Michael Wechner
 * @created 1.12.29
 * @version 1.12.29
 */
public abstract class AbstractUsernamePasswordAuthenticatorAction extends AbstractAuthenticatorAction{
/**
 *
 */
/*
  public void configure(Configuration conf) throws ConfigurationException{
    super.configure(conf);
    }
*/
/**
 *
 */
  public boolean authenticate(Request request,Map map) throws Exception{
    String username=request.getParameter("username");
    String password=request.getParameter("password");
    if((username != null) && (password != null)){
      return authenticate(username,password,request,map);
      }
    return false;
    }
/**
 *
 */
  public abstract boolean authenticate(String username,String password,Request request,Map map) throws Exception;
  }
