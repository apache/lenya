package org.wyona.util;

import java.util.Date;

/**
 * @author Michael Wechner
 */
public class GetMillis{
/**
 *
 */
  public static void main(String[] args){
    if(args.length != 3){
      System.out.println("Usage: year month day");
      return;
      }
    System.out.println(new GetMillis().getMillis(args[0],args[1],args[2]));
    }
/**
 *
 */
 public String getMillis(String year,String month,String day){
   return ""+new Date(new Integer(year).intValue()-1900,new Integer(month).intValue(),new Integer(day).intValue()).getTime();
   }
  }
