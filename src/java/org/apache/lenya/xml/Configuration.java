package org.wyona.xml;

import java.net.URL;
import java.util.Properties;

import org.apache.log4j.Category;

/**
 * @author Michael Wechner
 * @version 1.8.10
 */
public class Configuration{
 static Category log=Category.getInstance(Configuration.class);

 public static final String DEFAULT_CONFIGURATION_FILE="org/wyona/xml/xpsconf.properties";
 public static final String DEFAULT_CONFIGURATION_KEY="xps.configuration";
 public static final String OVERRIDE_DEFAULT_CONFIGURATION_KEY="override.xps.configuration";

 public String cacheFolder=null;
 public boolean cacheHTTP=false;
 public String INCLUDE=null;
 public String JAVA_ZONE=null;
 public String proxyHost=null;
 public String proxyPort=null;
 public String servletZone=null;
 public String xslt=null;
 public String xslt_dir=null;
 public String xslt_urlspace=null;
 public String listeners=null;
 public String password=null;
 public String spaces=null;
 public String html2xml=null;
 public String xslt_structure=null;
 public String spellcheckProviderClass=null;
 public String spellcheckRootElement=null;
 public String editor=null;
 public String PublishConf=null;
 public String MDMLRoot=null;
 public String signalMasterStateFile=null;
 public String signalMasterConfFile=null;
 public String signalMasterMonitorXSLT=null;
 public String webserverHostname=null;
 public String WorkFlowConfiguration=null;
 public String DocumentTypeXPath=null;
 public String DefaultStatus=null;
 public String WFDocTypeDefaultXSLT=null;
 public String html2xmlTranslation=null;
 public String xpsgwsconf=null;
 public String xpsgwssimpleconf=null;
 public String xpsguiconf=null;
 public int maxNumberOfRollbacks;
 public int maxNumberOfEntries;
/**
 * http://www.artima.com/java/answers/Mar2001/messages/164.html
 * export CLASSPATH=/home/wyona/src/xps/build/properties:...
 * java -Doverride.xps.configuration=org/wyona/xps/altconf.properties org.wyona.xps.Configuration
 */
 public static void main(String[] args){
  Configuration conf=new Configuration();

  System.out.println("Caching directory: "+conf.cacheFolder);
  System.out.println("Cache xml from http connections: "+conf.cacheHTTP);
  if(conf.proxyHost != null && conf.proxyHost != null){
   System.out.println("Proxy set:");
   System.out.println(conf.proxyHost);
   System.out.println(conf.proxyPort);
   }
  else{
   System.out.println("No proxy set.");
   }
  System.out.println("XSLT: "+conf.xslt);
  System.out.println("XSLT (Directory): "+conf.xslt_dir);
  System.out.println("XSLT (URLSpace): "+conf.xslt_urlspace);
  System.out.println("Listeners: "+conf.listeners);
  System.out.println("Space Password: "+conf.password);
  System.out.println("Spaces: "+conf.spaces);
  System.out.println("html2xml: "+conf.html2xml);
  System.out.println("XSLT (View structure): "+conf.xslt_structure);
  System.out.println("Editor: "+conf.editor);
  System.out.println(conf.MDMLRoot);
  System.out.println(conf.signalMasterStateFile);
  System.out.println("signalMasterConfFile: "+conf.signalMasterConfFile);
  System.out.println("signalMasterMonitorXSLT: "+conf.signalMasterMonitorXSLT);
  System.out.println("Hostname and Port: "+conf.webserverHostname);
  System.out.println("Workflow: "+conf.WorkFlowConfiguration);
  System.out.println(conf.DocumentTypeXPath);
  System.out.println("Default Workflow Status: "+conf.DefaultStatus);
  //System.out.println("WFDocTypeDefaultXSLT (Etwas faul!): "+conf.WFDocTypeDefaultXSLT);
  System.out.println(conf.html2xmlTranslation);
  System.out.println(conf.xpsgwsconf);
  System.out.println(conf.xpsgwssimpleconf);
  System.out.println(conf.xpsguiconf);
  System.out.println(conf.maxNumberOfRollbacks);
  System.out.println(conf.maxNumberOfEntries);
  }
/**
 *
 */
 public Configuration(){
  getProperties(load());
  }
/**
 *
 */
 public static Properties load(){
  String resourcePathRelativeToClasspath=System.getProperty(OVERRIDE_DEFAULT_CONFIGURATION_KEY);
  if(resourcePathRelativeToClasspath == null){
   resourcePathRelativeToClasspath=System.getProperty(DEFAULT_CONFIGURATION_KEY,DEFAULT_CONFIGURATION_FILE);
   log.debug(DEFAULT_CONFIGURATION_KEY+"="+resourcePathRelativeToClasspath);
   }
  else{
   log.debug(OVERRIDE_DEFAULT_CONFIGURATION_KEY+"="+resourcePathRelativeToClasspath);
   }
  ClassLoader cl=ClassLoader.getSystemClassLoader();
  URL url=org.apache.log4j.helpers.Loader.getResource("hallo");
  //URL url=cl.getResource(resourcePathRelativeToClasspath);
  if(url == null){
   //log.fatal("Could not find resource on classpath: "+resourcePathRelativeToClasspath);
   //return null;
   }
  log.debug(url);
  Properties properties=new Properties();
  try{
   properties.load(Configuration.class.getResourceAsStream("xpsconf.properties"));
   //properties.load(url.openStream());
   }
  catch(Exception e){
   log.error(e);
   }
  return properties;
  }
/**
 *
 */
 public void getProperties(Properties properties){
  if(properties != null){
   cacheFolder=getProperty(properties,"org.wyona.xps.XLinkInterpreter.cacheFolder");
   cacheHTTP=false;
   INCLUDE=getProperty(properties,"Include");
   JAVA_ZONE=getProperty(properties,"JavaZone");
   proxyHost=null;
   proxyPort=null;
   servletZone=getProperty(properties,"servletZone");
   xslt=getProperty(properties,"xslt");
   xslt_dir=getProperty(properties,"xslt_dir");
   xslt_urlspace=getProperty(properties,"xslt_urlspace");
   listeners=getProperty(properties,"listeners");
   password=getProperty(properties,"password");
   spaces=getProperty(properties,"spaces");
   html2xml=getProperty(properties,"html2xml");
   xslt_structure=getProperty(properties,"xslt_structure");
   spellcheckProviderClass=getProperty(properties,"spellcheckProviderClass");
   spellcheckRootElement=getProperty(properties,"spellcheckRootElement");
   editor=getProperty(properties,"editor");
   PublishConf=getProperty(properties,"PublishConf");
   MDMLRoot=getProperty(properties,"MDMLRoot");
   signalMasterStateFile=getProperty(properties,"signalMasterStateFile");
   signalMasterConfFile=getProperty(properties,"signalMasterConfFile");
   signalMasterMonitorXSLT=getProperty(properties,"signalMasterMonitorXSLT");
   webserverHostname=getProperty(properties,"webserverHostname");
   WorkFlowConfiguration=getProperty(properties,"WorkFlowConfiguration");
   DocumentTypeXPath=getProperty(properties,"DocumentTypeXPath");
   DefaultStatus=getProperty(properties,"DefaultStatus");
   //WFDocTypeDefaultXSLT=getProperty(properties,"WFDocTypeDefaultXSLT");
   //WFDocTypeDefaultXSLT=getProperty(properties,"WFDocTypeDefault");
   html2xmlTranslation=getProperty(properties,"html2xmlTranslation");
   xpsgwsconf=getProperty(properties,"xpsgwsconf");
   xpsgwssimpleconf=getProperty(properties,"xpsgwssimpleconf");
   xpsguiconf=getProperty(properties,"xpsguiconf");
   maxNumberOfRollbacks=new Integer(getProperty(properties,"maxNumberOfRollbacks")).intValue();
   maxNumberOfEntries=new Integer(getProperty(properties,"maxNumberOfEntries")).intValue();
   }
  }
/**
 *
 */
 public String getProperty(Properties properties,String key){
  String value=properties.getProperty(key);
  if(value != null){
   log.debug(key+"="+value);
   return value;
   }
  else{
   log.error("No such property: "+key);
   }
  return null;
 }
/**
 *
 */
 public static void register(){
  }
 }
