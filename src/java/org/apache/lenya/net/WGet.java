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

/* $Id: WGet.java,v 1.32 2004/03/01 16:18:25 gregor Exp $  */

package org.apache.lenya.net;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Category;


/**
 * Similar to the UNIX wget
 */
public class WGet {
    static Category log = Category.getInstance(WGet.class);
    String directory_prefix = null;

    /**
     * Creates a new WGet object.
     */
    public WGet() {
        directory_prefix = System.getProperty("user.dir");
    }

    /**
     * DOCUMENT ME!
     *
     * @param args DOCUMENT ME!
     */
    public static void main(String[] args) {
        if (args.length == 0) {
            System.out.println("Usage: org.apache.lenya.net.WGet [URL] -P/home/lenya/download");

            return;
        }

        try {
            WGet wget = new WGet();

            for (int i = 0; i < args.length; i++) {
                if (args[i].indexOf("-P") == 0) {
                    wget.setDirectoryPrefix(args[i].substring(2)); // -P/home/lenya/download, 2: remove "-P"
                }
            }

            byte[] response = wget.download(new URL(args[0]), "s/\\/lenya\\/oscom//g", "");
        } catch (MalformedURLException e) {
            System.err.println(e);
        } catch (Exception e) {
            System.err.println(e);
        }
    }

    /**
     * -P
     *
     * @param directory_prefix DOCUMENT ME!
     */
    public void setDirectoryPrefix(String directory_prefix) {
        this.directory_prefix = directory_prefix;
    }

    /**
     * @param url The url of the resource to download
     * @param prefixSubstitute Regexp which shall be replaced
     * @param substituteReplacement Replacement of the regexp
     *
     * @return bytes of downloaded resource
     *
     * @throws IOException URL might not exist
     */
    public byte[] download(URL url, String prefixSubstitute, String substituteReplacement)
        throws IOException {
        log.debug(".download(): " + url + " " + prefixSubstitute + " " + substituteReplacement);

        return downloadUsingHttpClient(url, prefixSubstitute, substituteReplacement);
    }

    /**
     * DOCUMENT ME!
     *
     * @param url DOCUMENT ME!
     * @param prefixSubstitute DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public byte[] downloadUsingHttpClient(URL url, String prefixSubstitute,
        String substituteReplacement) {
        log.debug(".downloadUsingHttpClient(): " + url);

        byte[] sresponse = null;

        try {
            sresponse = getResource(url);

            File file = new File(createFileName(url, prefixSubstitute, substituteReplacement));

            saveToFile(file.getAbsolutePath(), sresponse);

            substitutePrefix(file.getAbsolutePath(), prefixSubstitute, substituteReplacement);
        } catch (MalformedURLException e) {
            log.error(".downloadUsingHttpClient(): ", e);
        } catch (FileNotFoundException e) {
            log.error(".downloadUsingHttpClient(): ", e);
        } catch (IOException e) {
            log.error(".downloadUsingHttpClient(): ", e);
        }

        List links = null;

        try {
            links = getLinks(url);
        } catch (IOException ioe) {
            log.error(".downloadUsingHttpClient(): ", ioe);
        }

        if (links != null) {
            Iterator iterator = links.iterator();

            while (iterator.hasNext()) {
                String link = (String) iterator.next();

                try {
                    URL child_url = new URL(org.apache.lenya.util.URLUtil.complete(url.toString(),
                                link));

                    byte[] child_sresponse = getResource(child_url);
                    saveToFile(createFileName(child_url, prefixSubstitute, substituteReplacement),
                        child_sresponse);
                } catch (Exception e) {
                    log.error(".downloadUsingHttpClient(): ", e);
                }
            }
        }

        return sresponse;
    }

    /**
     *
     */
    public byte[] getResource(URL url) throws IOException {
        log.debug(".getResource(): " + url);

        HttpURLConnection httpConnection = (HttpURLConnection) url.openConnection();
        InputStream in = httpConnection.getInputStream();
        byte[] buffer = new byte[1024];
        int bytes_read;
        ByteArrayOutputStream bufferOut = new ByteArrayOutputStream();

        while ((bytes_read = in.read(buffer)) != -1) {
            bufferOut.write(buffer, 0, bytes_read);
        }

        byte[] sresponse = bufferOut.toByteArray();
        httpConnection.disconnect();

        return sresponse;
    }

    /**
     *
     */
    public List getLinks(URL url) throws IOException {
        log.debug(".getLinks(): Get links from " + url);

        List links = null;

        try {
            org.apache.lenya.util.HTML html = new org.apache.lenya.util.HTML(url.toString());
            links = html.getImageSrcs(false);
            links.addAll(html.getLinkHRefs(false));
        } catch (Exception e) {
            log.error(".getLinks() Exception 423432: ", e);
        }

        if (links != null) {
            log.debug(".getLinks(): Number of links found: " + links.size());
        }

        return links;
    }

    /**
     * Substitute prefix, e.g. "/lenya/blog/live/" by "/"
     *
     * @param filename Filename
     * @param prefixSubstitute Prefix which shall be replaced
     * @param substituteReplacement Prefix which is going to replace the original
     *
     * @throws IOException DOCUMENT ME!
     */
    public void substitutePrefix(String filename, String prefixSubstitute, String substituteReplacement) throws IOException {
        log.debug("Replace " + prefixSubstitute + " by " + substituteReplacement);

	org.apache.lenya.util.SED.replaceAll(new File(filename), escapeSlashes(prefixSubstitute), escapeSlashes(substituteReplacement));
    }

    /**
     * Escape slashes
     *
     * @return String with escaped slashes
     */
    public String escapeSlashes(String string) {
        StringBuffer buffer = new StringBuffer("");

        for (int i = 0; i < string.length(); i++) {
            if (string.charAt(i) == '/') {
                buffer.append("\\/");
            } else {
                buffer.append(string.charAt(i));
            }
        }

        return buffer.toString();
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public String toString() {
        return "-P: " + directory_prefix;
    }

    /**
     *
     */
    public void saveToFile(String filename, byte[] bytes)
        throws FileNotFoundException, IOException {
        File file = new File(filename);
        File parent = new File(file.getParent());

        if (!parent.exists()) {
            log.warn(".saveToFile(): Directory will be created: " + parent.getAbsolutePath());
            parent.mkdirs();
        }

        FileOutputStream out = new FileOutputStream(file.getAbsolutePath());
        out.write(bytes);
        out.close();
    }

    /**
     * @param url URL of resource, which has been downloaded and shall be saved
     * @return Absolute substituted filename
     */
    public String createFileName(URL url, String prefixSubstitute, String substituteReplacement) {
        File file = new File(directory_prefix + File.separator + url.getFile());

        return file.getAbsolutePath().replaceAll(prefixSubstitute, substituteReplacement);
    }

    /**
     *
     */
    public byte[] runProcess(String command) throws Exception {
        Process process = Runtime.getRuntime().exec(command);

        java.io.InputStream in = process.getInputStream();
        byte[] buffer = new byte[1024];
        int bytes_read = 0;
        java.io.ByteArrayOutputStream baout = new java.io.ByteArrayOutputStream();

        while ((bytes_read = in.read(buffer)) != -1) {
            baout.write(buffer, 0, bytes_read);
        }

        if (baout.toString().length() > 0) {
            log.debug(".runProcess(): %%%InputStream:START" + baout.toString() +
                "END:InputStream%%%");
        }

        java.io.InputStream in_e = process.getErrorStream();
        java.io.ByteArrayOutputStream baout_e = new java.io.ByteArrayOutputStream();

        while ((bytes_read = in_e.read(buffer)) != -1) {
            baout_e.write(buffer, 0, bytes_read);
        }

        if (baout_e.toString().length() > 0) {
            log.error(".runProcess(): ###ErrorStream:START" + baout_e.toString() +
                "END:ErrorStream###");
        }

        return baout.toByteArray();
    }
}
