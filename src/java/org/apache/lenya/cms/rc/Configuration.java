package org.wyona.cms.rc;

import java.util.Properties;
import org.apache.log4j.Category;

/**
 * @author Michael Wechner (http://www.wyona.com)
 * @version 0.10.5
 */
public class Configuration
     {
     static Category log=Category.getInstance(Configuration.class);
     public String rcmlDirectory=null;
     public String backupDirectory=null;
     //public String documentRootDirectory=null;
     public String mountPoints=null;
     public String password=null;
     public String servlet=null;

     public String xslt=null;
     public String xslt_rcmlrollback=null;
/**
 *
 */
     public static void main(String[] args)
          {
          Configuration conf=new Configuration();
          log.debug("rcmlDirectory: "+conf.rcmlDirectory);
          log.debug("backupDirectory: "+conf.backupDirectory);
          //log.debug("documentRootDirectory :"+conf.documentRootDirectory);
          log.debug("mountPoints :"+conf.mountPoints);
          log.debug("password :"+conf.password);
          log.debug("servlet :"+conf.servlet);

          log.debug("xslt :"+conf.xslt);
          log.debug("xslt_rcmlrollback :"+conf.xslt_rcmlrollback);
          }
/**
 *
 */
     public Configuration()
          {
          String propertiesFileName="conf.properties";
          Properties properties=new Properties();
          try
            {
            properties.load(Configuration.class.getResourceAsStream(propertiesFileName));
            }
          catch(Exception e)
            {
            log.fatal(": Failed to load properties from resource: "+propertiesFileName);
            log.fatal("System.exit(0)");
            System.exit(0);
            }
          rcmlDirectory=properties.getProperty("rcmlDirectory");
          backupDirectory=properties.getProperty("backupDirectory");
          //documentRootDirectory=properties.getProperty("documentRootDirectory");
          mountPoints=properties.getProperty("mountPoints");
          password=properties.getProperty("password");
          servlet=properties.getProperty("servlet");

          xslt=properties.getProperty("xslt");
          xslt_rcmlrollback=properties.getProperty("xslt_rcmlrollback");
          }
     }
