package org.wyona.util;

/**
 * StringUtil.java
 * $Id: StringUtil.java,v 1.3 2002/08/23 07:07:01 michicms Exp $
 *
 * Created: Thu Jan 24 18:27:05 2002
 *
 * @author <a href="mailto:tinu@email.ch">Martin Lüthi</a>
 * @version
 *
 * Utilty Class for String handling
 */

public class StringUtil {

  /**
   * @param       s       Main string.
   * @param       find    The substring to find.
   * @param       rep     Replaces the find substring.
   * @return      The new string. 
   *
   * Replaces one substring with another within a main string.
   */
  public static String replace(String s, String find, String rep) {
    return replace(new StringBuffer(s), find, rep).toString();                                   
  }
  
  /**
   * Replaces one substring with another within a main string.
   *
   * @param       sb      Main string. (StringBuffer)
   * @param       find    The substring to find.
   * @param       rep     Replaces the find substring.
   * @return      The new string. (StringBuffer)
   */
  public static StringBuffer replace(StringBuffer sb, String find, String rep) {
    StringBuffer buf = new StringBuffer(sb.toString());
    int startIndex, endIndex;
    boolean done = false;
    
    startIndex = endIndex = 0;
    // Halts if they is not need to do any replacements
    if (!find.equals(rep)) {
      String s = buf.toString();
      // Continues while more substring(s) (find) exist
      while (!done) {
        // Grab the position of the substring (find)
        if ((startIndex = s.indexOf(find)) >= 0) {
          // Replace "find" with "rep"
          endIndex = startIndex + find.length();
          buf.delete(startIndex, endIndex);
          buf.insert(startIndex, rep);
          s = buf.toString();
        } else {
          done = true;
        }       
      }
    }
    return buf;                                     
  }
  
}// StringUtil
