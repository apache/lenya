package org.wyona.net;

import java.net.URL;
import java.util.Properties;

import org.apache.log4j.Category;

/**
 * @author Michael Wechner
 * @version 1.8.10
 */
public class Configuration{
 static Category log=Category.getInstance(Configuration.class);

 public static final String DEFAULT_CONFIGURATION_FILE="org/wyona/net/conf.properties";
 public static final String DEFAULT_CONFIGURATION_KEY="wyona.configuration";
 public static final String OVERRIDE_DEFAULT_CONFIGURATION_KEY="override.wyona.configuration";

 public String configurationPath=null;
 public String smtpHost=null;
 public String smtpPort=null;
 public String smtpDomain=null;
/**
 * http://www.artima.com/java/answers/Mar2001/messages/164.html
 * export CLASSPATH=/home/wyona/src/xps/build/properties:...
 * java -Doverride.wyona.configuration=org/wyona/altconf.properties org.wyona.net.Configuration
 */
 public static void main(String[] args){
  Configuration conf=new Configuration();

  System.out.println("Proxy Manager Configuration Path: "+conf.configurationPath);
  System.out.println("SMTP Host: "+conf.smtpHost);
  System.out.println("SMTP Port: "+conf.smtpPort);
  System.out.println("SMTP Domain: "+conf.smtpDomain);
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
  URL url=cl.getResource(resourcePathRelativeToClasspath);
  if(url == null){
   log.error("Could not find resource on classpath: "+resourcePathRelativeToClasspath);
   //return null;
   }
  log.debug(url);
  Properties properties=new Properties();
  try{
   properties.load(Configuration.class.getResourceAsStream("conf.properties"));
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
   configurationPath=getProperty(properties,"org.wyona.net.ProxyManager.configurationPath");
   smtpHost=getProperty(properties,"org.wyona.net.SMTP.host");
   smtpPort=getProperty(properties,"org.wyona.net.SMTP.port");
   smtpDomain=getProperty(properties,"org.wyona.net.SMTP.domain");
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
