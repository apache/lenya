package org.wyona.cms.ac;

import org.w3c.tools.crypt.Md5;

/**
 * @author Michael Wechner
 * @created 1.12.26
 * @created 1.12.28
 */
public class Password{
/**
 *
 */
  public static void main(String[] args){
    if(args.length != 1){
      System.out.println("Usage: plain-text-password");
      return;
      }
    try{
      System.out.println(Password.encrypt(args[0]));
      }
    catch(Exception e){
      System.err.println(e);
      }
    }
/**
 *
 */
  public static String encrypt(String plain) throws Exception{
    Md5 md5=new Md5(plain);
    byte[] b=md5.processString();
    return md5.getStringDigest();
    }
  }
