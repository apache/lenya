/*
 <License>
 </License>
 */

package org.wyona.util;

import java.util.StringTokenizer;
import java.util.Vector;

/**
 * @author Michael Wechner (http://www.wyona.com)
 * @version 0.12.17
 *
 * WARNING: This class does not work properly!!!
 * NOTE: matchNew() should be working properly!!!
 */
public class WildcardFilter
     {
/**
 *
 */
     public static void main(String[] args)
          {
          Vector wildcards=new Vector();
          if(new WildcardFilter().matchNew("/nzz/online/daily/2000/12/08/al/page-article32.html","*nzz/online/daily/*/1*/*/*/page-article*.htm*",wildcards))
            {
            for(int i=0;i<wildcards.size();i++)
               {
               System.out.println((String)wildcards.elementAt(i));
               }
            System.out.println("String matched");
            }
          else
            {
            System.out.println("String did not match");
            }
/*
          if(new WildcardFilter().match("file:/home/wyona/xml/page-article32.xml","file:/home/wyona/xml/*article*.xml"))
            {
            System.out.println("String matched");
            }
          else
            {
            System.out.println("String did not match");
            }
          if(new WildcardFilter().match("file:/home/wyona/xml/page-article32.xml","file:/home/wyona/xml/*article*.xm"))
            {
            System.out.println("String matched");
            }
          else
            {
            System.out.println("String did not match");
            }
          if(new WildcardFilter().match("file:/home/wyona/xml/page-article32.xml","ile:/home/wyona/xml/*article*.xml"))
            {
            System.out.println("String matched");
            }
          else
            {
            System.out.println("String did not match");
            }
*/
          }
/**
 *
 */
     public boolean match(String stringToMatch,String stringWithWildcards)
          {
          StringTokenizer st=new StringTokenizer(stringWithWildcards,"*");
          int length=st.countTokens();
          String firstToken=st.nextToken();
          String lastToken="";
          for(int i=1;i<length;i++)
             {
             lastToken=st.nextToken();
             }
          if(stringToMatch.indexOf(firstToken) == 0 && stringToMatch.indexOf(lastToken) == stringToMatch.length()-lastToken.length())
            {
            return matchInBetween(stringToMatch,stringWithWildcards);
            }
          return false;
          }
/**
 *
 */
     public boolean matchInBetween(String stringToMatch,String stringWithWildcards)
          {
          //System.out.println(stringToMatch+" "+stringWithWildcards);
          StringTokenizer st=new StringTokenizer(stringWithWildcards,"*");
          int length=st.countTokens();
          if(length >= 2)
            {
            String beforeFirstWildcard=st.nextToken();
            String afterFirstWildcard=st.nextToken();
            for(int i=2;i<length;i++)
               {
               afterFirstWildcard=afterFirstWildcard+"*"+st.nextToken();
               }
            int index=stringToMatch.indexOf(beforeFirstWildcard);
            if(index >= 0)
              {
              if(matchInBetween(stringToMatch.substring(index+beforeFirstWildcard.length()),afterFirstWildcard))
                {
                return true;
                }
              }
            else
              {
              return false;
              }
            }
          else // The end
            {
            if(stringToMatch.indexOf(stringWithWildcards) >= 0)
              {
              return true;
              }
            }
          return false;
          }
/**
 *
 */
     public boolean matchNew(String stringToMatch,String stringWithWildcards,Vector wildcards)
          {
//System.out.println("");
//System.out.println(stringToMatch);
//System.out.println(stringWithWildcards);
          int index=stringWithWildcards.indexOf("*");
          if(index < 0)
            {
            if(stringToMatch.equals(stringWithWildcards))
              {
              return true;
              }
            else
              {
              return false;
              }
            }
          else if(index == 0)
            {
            String stringWithWildcardAtBeginningRemoved=stringWithWildcards.substring(1);
            int secondIndex=stringWithWildcardAtBeginningRemoved.indexOf("*");
            if(secondIndex < 0)
              {
              int indexAfter=stringToMatch.indexOf(stringWithWildcardAtBeginningRemoved);
              if((indexAfter >= 0) && (stringWithWildcardAtBeginningRemoved.equals(stringToMatch.substring(indexAfter))))
                {
//System.out.println(stringToMatch.substring(0,indexAfter));
                wildcards.addElement(stringToMatch.substring(0,indexAfter));
                return true;
                }
              else if(stringWithWildcards.length() == 1)
                {
//System.out.println(stringToMatch);
                wildcards.addElement(stringToMatch);
                return true;
                }
              else
                {
                return false;
                }
              }
            else
              {
              String before=stringWithWildcardAtBeginningRemoved.substring(0,secondIndex);
              String after=stringWithWildcardAtBeginningRemoved.substring(secondIndex);
              int indexBefore=stringToMatch.indexOf(before);
              if(indexBefore >= 0)
                {
//System.out.println(stringToMatch.substring(0,indexBefore));
                wildcards.addElement(stringToMatch.substring(0,indexBefore));
                return matchNew(stringToMatch.substring(indexBefore+before.length()),after,wildcards);
                }
              else
                {
                return false;
                }
              }
            }
          else
            {
            String before=stringWithWildcards.substring(0,index);
            String after=stringWithWildcards.substring(index);
            if(stringToMatch.indexOf(before) == 0)
              {
              return matchNew(stringToMatch.substring(before.length()),after,wildcards);
              }
            }
          return false;
          }
     }
