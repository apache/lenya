package org.wyona.search.crawler;

import org.apache.avalon.framework.configuration.Configurable;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.avalon.framework.configuration.DefaultConfigurationBuilder;
import org.apache.log4j.Category;

import java.io.File;

/**
 * @author Michael Wechner
 */
public class CrawlerEnvironment implements Configurable{
  static Category log=Category.getInstance(CrawlerEnvironment.class);

  private String configurationFilePath;

  private String base_url;
  private String scope_url;
  private String uri_list;
  private String htdocs_dump_dir;
/**
 *
 */
  public static void main(String[] args){
    if(args.length == 0){
      System.err.println("Usage: org.wyona.search.crawler.CrawlerEnvironment crawler.xconf [-name <name>]");
      return;
      }
    CrawlerEnvironment ce=new CrawlerEnvironment(args[0]);
    String parameter;

    String name=null;
    for(int i=0;i<args.length;i++){
      if(args[i].equals("-name")){
        if(i+1 < args.length) name=args[i+1];
        }
      }

    if(name != null){
      if(name.equals("htdocs-dump-dir")){
        parameter=ce.getHTDocsDumpDir();
        System.out.println(ce.resolvePath(parameter));
        }
      else{
        System.out.println("No such element: "+name);
        }
      }    
    else{
      parameter=ce.getBaseURL();
      System.out.println(parameter);

      parameter=ce.getScopeURL();
      System.out.println(parameter);

      parameter=ce.getURIList();
      System.out.println(parameter);
      System.out.println(ce.resolvePath(parameter));

      parameter=ce.getHTDocsDumpDir();
      System.out.println(parameter);
      System.out.println(ce.resolvePath(parameter));
      }
    }
/**
 *
 */
  public CrawlerEnvironment(String configurationFilePath){
    this.configurationFilePath=configurationFilePath;

    File configurationFile = new File(configurationFilePath);
    try{
      DefaultConfigurationBuilder builder=new DefaultConfigurationBuilder();
      Configuration configuration = builder.buildFromFile(configurationFile);
      configure(configuration);
      }
    catch(Exception e){
      log.error("Cannot load publishing configuration! ",e);
      }
    }
/**
 *
 */
  public void configure(Configuration configuration) throws ConfigurationException{
    base_url=configuration.getChild("base-url").getAttribute("href");
    scope_url=configuration.getChild("scope-url").getAttribute("href");
    uri_list=configuration.getChild("uri-list").getAttribute("src");
    htdocs_dump_dir=configuration.getChild("htdocs-dump-dir").getAttribute("src");
    }
/**
 *
 */
  public String getBaseURL(){
    log.debug(".getBaseURL(): "+base_url);
    return base_url;
    }
/**
 *
 */
  public String getScopeURL(){
    log.debug(".getScopeURL(): "+scope_url);
    return scope_url;
    }
/**
 *
 */
  public String getURIList(){
    log.debug(".getURIList(): "+uri_list);
    return uri_list;
    }
/**
 *
 */
  public String getHTDocsDumpDir(){
    log.debug(".getHTDocsDumpDir(): "+htdocs_dump_dir);
    return htdocs_dump_dir;
    }
/**
 *
 */
  public String resolvePath(String path){
    return org.apache.avalon.excalibur.io.FileUtil.catPath(configurationFilePath,path);
    }
}
