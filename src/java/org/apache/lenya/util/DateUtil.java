package org.wyona.util;

/**
 * @author Edith Chevrier 
 * @version 2002.7.8
 */
public class DateUtil
     {
     public String year=null;
     public String month=null;
     public String day=null;
/**
 *
 */
     public static void main(String[] args)
          {
          }
/**
 *
 */
     public DateUtil()
          {
          }
/**
 *
 */
     public DateUtil(String year,String month,String day)
          {
          this.year=year;
          this.month=month;
          this.day=day;
          }
/**
 *
 */
     public String toString()
          {
          return year+"."+month+"."+day;
          }
/**
 *
 */
     public static String oneToTwoDigits(String oneDigit)
          {
          if(oneDigit.length() == 1)
            {
            return "0"+oneDigit;
            }
          return oneDigit;
          }
/**
 *
 */
     public static String twoToOneDigit(String twoDigit)
          {
          if((twoDigit.charAt(0) == '0') && (twoDigit.length() == 2))
            {
            return ""+twoDigit.charAt(1);
            }
          return twoDigit;
          }
     }
