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
/**
 *
 */
     public static String getMonthName(int i)
          {
          String name=null;
          switch(i){
            case 1:  name="January";break;
            case 2:  name="February";break;
            case 3:  name="March";break;
            case 4:  name="April";break;
            case 5:  name="May";break;
            case 6:  name="June";break;
            case 7:  name="July";break;
            case 8:  name="August";break;
            case 9:  name="September";break;
            case 10: name="October";break;
            case 11: name="November";break;
            case 12: name="December";break;
            default: name="Hey, that's not a valid month";break;
            }
          return name;
          }
/**
 *
 */
     public static String getDayName(int i)
          {
          String name=null;
          switch(i){
            case 1:  name="Sunday";break;
            case 2:  name="Monday";break;
            case 3:  name="Tuesday";break;
            case 4:  name="Wednesday";break;
            case 5:  name="Thursday";break;
            case 6:  name="Friday";break;
            case 7:  name="Saturday";break;
            default: name="Hey, that's not a valid day";break;
            }
          return name;
          }
     }
