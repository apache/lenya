/*
<License>

 ============================================================================
                   The Apache Software License, Version 1.1
 ============================================================================

 Copyright (C) 1999-2004 The Apache Software Foundation. All rights reserved.

 Redistribution and use in source and binary forms, with or without modifica-
 tion, are permitted provided that the following conditions are met:

 1. Redistributions of  source code must  retain the above copyright  notice,
    this list of conditions and the following disclaimer.

 2. Redistributions in binary form must reproduce the above copyright notice,
    this list of conditions and the following disclaimer in the documentation
    and/or other materials provided with the distribution.

 3. The end-user documentation included with the redistribution, if any, must
    include  the following  acknowledgment:  "This product includes  software
    developed  by the  Apache Software Foundation  (http://www.apache.org/)."
    Alternately, this  acknowledgment may  appear in the software itself,  if
    and wherever such third-party acknowledgments normally appear.

 4. The names "Apache Lenya" and  "Apache Software Foundation"  must  not  be
    used to  endorse or promote  products derived from  this software without
    prior written permission. For written permission, please contact
    apache@apache.org.

 5. Products  derived from this software may not  be called "Apache", nor may
    "Apache" appear  in their name,  without prior written permission  of the
    Apache Software Foundation.

 THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED WARRANTIES,
 INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 FITNESS  FOR A PARTICULAR  PURPOSE ARE  DISCLAIMED.  IN NO  EVENT SHALL  THE
 APACHE SOFTWARE  FOUNDATION  OR ITS CONTRIBUTORS  BE LIABLE FOR  ANY DIRECT,
 INDIRECT, INCIDENTAL, SPECIAL,  EXEMPLARY, OR CONSEQUENTIAL  DAMAGES (INCLU-
 DING, BUT NOT LIMITED TO, PROCUREMENT  OF SUBSTITUTE GOODS OR SERVICES; LOSS
 OF USE, DATA, OR  PROFITS; OR BUSINESS  INTERRUPTION)  HOWEVER CAUSED AND ON
 ANY  THEORY OF LIABILITY,  WHETHER  IN CONTRACT,  STRICT LIABILITY,  OR TORT
 (INCLUDING  NEGLIGENCE OR  OTHERWISE) ARISING IN  ANY WAY OUT OF THE  USE OF
 THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

 This software  consists of voluntary contributions made  by many individuals
 on  behalf of the Apache Software  Foundation and was  originally created by
 Michael Wechner <michi@apache.org>. For more information on the Apache Soft-
 ware Foundation, please see <http://www.apache.org/>.
</License>
*/
package org.apache.lenya.ac;

import java.security.MessageDigest;

import org.apache.log4j.Category;

/**
 * Encrypt plain text password
 * Example: "message digest" becomes "f96b697d7cb7938d525a2f31aaf161d0" (hexadecimal notation (32 characters))
 *
 * @author Michael Wechner
 * @version $Id: Password.java,v 1.2 2004/01/25 23:08:19 michi Exp $
 */
public class Password {
    private static Category log = Category.getInstance(Password.class);

    /**
     * CLI
     *
     * @param args plain text password
     */
    public static void main(String[] args) {
        if (args.length != 1) {
            System.out.println("Usage: plain-text-password");

            return;
        }

        try {
            System.out.println(Password.encrypt(args[0]));
        } catch (Exception e) {
            System.err.println(e);
        }
    }

    /**
     * Encrypt plain text password
     *
     * @param plain plain text password
     *
     * @return enrcypted password
     *
     */
    public static String encrypt(String plain) {
        return getMD5(plain);
/*
        org.w3c.tools.crypt.Md5 md5 = new org.w3c.tools.crypt.Md5(plain);
        byte[] b = md5.processString();

        return md5.getStringDigest();
*/
    }

    /**
     *
     */
    public static String getMD5(String plain) {
        MessageDigest md = null;
        try {
            md = MessageDigest.getInstance("MD5");
        } catch (java.security.NoSuchAlgorithmException e) {
            log.error(e);
        }
        return stringify(md.digest(plain.getBytes()));
    }

    /**
     *
     */
    private static String stringify(byte[] buf) {
        StringBuffer sb = new StringBuffer(2 * buf.length);

        for (int i = 0; i < buf.length; i++) {
            int h = (buf[i] & 0xf0) >> 4;
            int l = (buf[i] & 0x0f);
            sb.append(new Character((char) ((h > 9) ? (('a' + h) - 10) : ('0' + h))));
            sb.append(new Character((char) ((l > 9) ? (('a' + l) - 10) : ('0' + l))));
        }

        return sb.toString();
    }
}
