/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 *  contributor license agreements.  See the NOTICE file distributed with
 *  this work for additional information regarding copyright ownership.
 *  The ASF licenses this file to You under the Apache License, Version 2.0
 *  (the "License"); you may not use this file except in compliance with
 *  the License.  You may obtain a copy of the License at
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

/* $Id$  */

package org.apache.lenya.ac;

import java.security.MessageDigest;

/**
 * Encrypt plain text password
 * Example: "message digest" becomes "f96b697d7cb7938d525a2f31aaf161d0" (hexadecimal notation (32 characters))
 */
public class Password {

    /**
     * Encrypt plain text password
     *
     * @param plain plain text password
     * @return encrypted password
     */
    public static String encrypt(String plain) {
        return getMD5(plain);
    }

    /**
     * Returns the MD5 representation of a string.
     * @param plain The plain string.
     * @return A string.
     */
    public static String getMD5(String plain) {
        MessageDigest md = null;
        try {
            md = MessageDigest.getInstance("MD5");
        } catch (java.security.NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
        return stringify(md.digest(plain.getBytes()));
    }

    /**
     * Converts a byte buffer to a string.
     * @param buf The buffer.
     * @return A string.
     */
    private static String stringify(byte[] buf) {
        StringBuffer sb = new StringBuffer(2 * buf.length);

        for (int i = 0; i < buf.length; i++) {
            int h = (buf[i] & 0xf0) >> 4;
            int l = (buf[i] & 0x0f);
            sb.append(Character.valueOf((char) ((h > 9) ? (('a' + h) - 10) : ('0' + h))));
            sb.append(Character.valueOf((char) ((l > 9) ? (('a' + l) - 10) : ('0' + l))));
        }

        return sb.toString();
    }
}
