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
          System.out.println(conf.rcmlDirectory);
          System.out.println(conf.backupDirectory);
          //System.out.println(conf.documentRootDirectory);
          System.out.println(conf.mountPoints);
          System.out.println(conf.password);
          System.out.println(conf.servlet);

          System.out.println(conf.xslt);
          System.out.println(conf.xslt_rcmlrollback);
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
