package org.wyona.lucene;

import org.apache.avalon.framework.configuration.Configurable;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.avalon.framework.configuration.DefaultConfigurationBuilder;
import org.apache.log4j.Category;

import java.io.File;

/**
 * @author Michael Wechner
 */
public class IndexEnvironment implements Configurable{
  static Category log=Category.getInstance(IndexEnvironment.class);

  private String configurationFilePath;

  private String update_index_type;
  private String index_dir;
  private String htdocs_dump_dir;
/**
 *
 */
  public static void main(String[] args){
    if(args.length != 1){
      System.err.println("Usage: org.wyona.lucene.IndexEnvironment lucene.xconf");
      return;
      }
    IndexEnvironment ie=new IndexEnvironment(args[0]);
    String parameter;

    parameter=ie.getUpdateIndexType();
    System.out.println(parameter);

    parameter=ie.getIndexDir();
    System.out.println(parameter);
    System.out.println(ie.resolvePath(parameter));

    parameter=ie.getHTDocsDumpDir();
    System.out.println(parameter);
    System.out.println(ie.resolvePath(parameter));
    }
/**
 *
 */
  public IndexEnvironment(String configurationFilePath){
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
    update_index_type=configuration.getChild("update-index").getAttribute("type");
    index_dir=configuration.getChild("index-dir").getAttribute("src");
    htdocs_dump_dir=configuration.getChild("htdocs-dump-dir").getAttribute("src");
    }
/**
 *
 */
  public String getUpdateIndexType(){
    log.debug(".getUpdateIndexType(): "+update_index_type);
    return update_index_type;
    }
/**
 *
 */
  public String getIndexDir(){
    log.debug(".getIndexDir(): "+index_dir);
    return index_dir;
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
    if(path.indexOf(File.separator)==0){
      return path;
      }
    return org.apache.avalon.excalibur.io.FileUtil.catPath(configurationFilePath,path);
    }
}
