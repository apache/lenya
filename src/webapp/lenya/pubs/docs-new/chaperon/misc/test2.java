/*
 *  Copyright (C) Chaperon. All rights reserved.                               
 *  ------------------------------------------------------------------------- 
 *  This software is published under the terms of the Apache Software License 
 *  version 1.1, a copy of which has been included  with this distribution in 
 *  the LICENSE file.                                                         
 */

package net.sourceforge.chaperon.helpers;

/**
 * The class maskes special characters
 *
 * @author Stephan Michels
 * @version %version%
 */
public class Decoder
{

  /**
   * Mask special characters
   *
   * @param string String
   *
   * @return Mask string
   */
  public static String decode(String string)
  {
    StringBuffer buffer = new StringBuffer();

    for (int i = 0; i < string.length(); i++)
    {
      if (string.charAt(i) == '\n')
        buffer.append("\\n");
      else if (string.charAt(i) == '\t')
        buffer.append("\\t");
      else if (string.charAt(i) == '\r')
        buffer.append("\\r");
      else if (string.charAt(i) == '\"')
        buffer.append("\"");
      else if (string.charAt(i) == '\\')
        buffer.append("\\");
      else if ((string.charAt(i) >= '!') && (string.charAt(i) <= '~'))
        buffer.append(string.charAt(i));
      else
      {
        String hexstring = Integer.toHexString((int) string.charAt(i));
        String zeros = "0000";

        buffer.append("\\u" + zeros.substring(4 - hexstring.length())
                      + hexstring);
      }
    }
    return buffer.toString();
  }
}
