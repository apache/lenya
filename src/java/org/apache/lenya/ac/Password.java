/*
 * Copyright  1999-2004 The Apache Software Foundation
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 */

/* $Id: Password.java,v 1.3 2004/03/03 12:56:31 gregor Exp $  */

package org.apache.lenya.ac;

import java.security.MessageDigest;

import org.apache.log4j.Category;

/**
 * Encrypt plain text password
 * Example: "message digest" becomes "f96b697d7cb7938d525a2f31aaf161d0" (hexadecimal notation (32 characters))
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
     * @return encrypted password
     *
     */
    public static String encrypt(String plain) {
        return getMD5(plain);
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
