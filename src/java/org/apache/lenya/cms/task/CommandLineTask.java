package org.wyona.cms.task;

import org.apache.avalon.framework.parameters.ParameterException;
import org.apache.log4j.Category;

/**
 * @author Michael Wechner
 */
public class CommandLineTask extends AbstractTask{
  static Category log = Category.getInstance(CommandLineTask.class);
/**
 *
 */
  public CommandLineTask(){
    }
/** 
 * Execute the task. All parameters must have been set with init(). 
 */
  public void execute(String path) {
    String command=getParameters().getParameter("command","echo \"Exception: No command parameter\"");
    log.debug(".execute(): " + command);

    try{
      Process process=Runtime.getRuntime().exec(command);

      java.io.InputStream in=process.getInputStream();
      byte[] buffer=new byte[1024];
      int bytes_read=0;
      java.io.ByteArrayOutputStream baout=new java.io.ByteArrayOutputStream();
      while((bytes_read=in.read(buffer)) != -1){
        baout.write(buffer,0,bytes_read);
        }
      if(baout.toString().length() > 0){
        log.debug(".execute(): %%%InputStream:S"+baout.toString()+"END:InputStream%%%");
        }

      java.io.InputStream in_e=process.getErrorStream();
      java.io.ByteArrayOutputStream baout_e=new java.io.ByteArrayOutputStream();
      while((bytes_read=in_e.read(buffer)) != -1){
        baout_e.write(buffer,0,bytes_read);
        }
      if(baout_e.toString().length() > 0){
        log.error(".execute(): ###ErrorStream:START"+baout_e.toString()+"END:ErrorStream###");
        }
      }
    catch(java.io.IOException e){
      log.error(".execute(): "+e);
      }
    }
  }
