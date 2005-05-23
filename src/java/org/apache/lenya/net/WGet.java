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

/* $Id$  */

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

import org.apache.lenya.util.SED;
import org.apache.log4j.Logger;


/**
 * This class retrieves resources over HTTP, similar to the UNIX wget
 */
public class WGet {
    static Logger log = Logger.getLogger(WGet.class);
    String directory_prefix = null;

    /**
     * Creates a new WGet object.
     */
    public WGet() {
        this.directory_prefix = System.getProperty("user.dir");
    }

    /**
     * Command line interface
     * @param args Command line args
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

            wget.download(new URL(args[0]), "s/\\/lenya\\/default//g", "");
        } catch (final MalformedURLException e) {
            System.err.println(e);
        } catch (final IOException e) {
            System.err.println(e);
        }
    }

    /**
     * Set the directory prefix (-P on the command line)
     * @param _directory_prefix The prefix
     */
    public void setDirectoryPrefix(String _directory_prefix) {
        this.directory_prefix = _directory_prefix;
    }

    /**
     * Downloads the specified resources and performs replacements.
     * @param url The url of the resource to download
     * @param prefixSubstitute Regexp which shall be replaced
     * @param substituteReplacement Replacement of the regexp
     * @return bytes of downloaded resource
     * @throws IOException URL might not exist
     */
    public byte[] download(URL url, String prefixSubstitute, String substituteReplacement)
        throws IOException {
        log.debug(".download(): " + url + " " + prefixSubstitute + " " + substituteReplacement);
        return downloadUsingHttpClient(url, prefixSubstitute, substituteReplacement);
    }

    /**
     * Downloads the specified resources and performs replacements.
     * @param url The url of the resource to download
     * @param prefixSubstitute Regexp which shall be replaced
     * @param substituteReplacement Replacement of the regexp
     * @return bytes of downloaded resource
     * @throws IOException
     */
    public byte[] downloadUsingHttpClient(URL url, String prefixSubstitute,
        String substituteReplacement) throws IOException {
        log.debug(".downloadUsingHttpClient(): " + url);

        byte[] sresponse = null;

        try {
            sresponse = getResource(url);

            File file = new File(createFileName(url, prefixSubstitute, substituteReplacement));
            saveToFile(file.getAbsolutePath(), sresponse);

            substitutePrefix(file.getAbsolutePath(), prefixSubstitute, substituteReplacement);
        } catch (final MalformedURLException e) {
            log.error(".downloadUsingHttpClient(): ", e);
            throw new IOException();
        } catch (final FileNotFoundException e) {
            log.error(".downloadUsingHttpClient(): ", e);
            throw new IOException();
        } catch (final IOException e) {
            log.error(".downloadUsingHttpClient(): ", e);
            throw new IOException();
        }

        List links = null;

        try {
            links = getLinks(url);
        } catch (final IOException ioe) {
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
                } catch (final MalformedURLException e) {
                    log.error(".downloadUsingHttpClient(): ", e);
                } catch (final FileNotFoundException e) {
                    log.error(".downloadUsingHttpClient(): ", e);
                } catch (final IOException e) {
                    log.error(".downloadUsingHttpClient(): ", e);
                }
            }
        }

        return sresponse;
    }

    /**
     * Get the specified resource over HTTP
     * @param url The resource to get
     * @return The resource
     * @throws IOException if an error occurs
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
     * Returns the links in the document represented by a URL
     * @param url The URL
     * @return The list of Links
     * @throws IOException if an error occurs
     */
    public List getLinks(URL url) throws IOException {
        log.debug(".getLinks(): Get links from " + url);

        List links = null;

        org.apache.lenya.util.HTML html = new org.apache.lenya.util.HTML(url.toString());
        links = html.getImageSrcs(false);
        if (links != null) {
	        links.addAll(html.getLinkHRefs(false));
            log.debug(".getLinks(): Number of links found: " + links.size());
        }

        return links;
    }

    /**
     * Substitute prefix, e.g. "/lenya/blog/live/" by "/"
     * @param filename Filename
     * @param prefixSubstitute Prefix which shall be replaced
     * @param substituteReplacement Prefix which is going to replace the original
     * @throws IOException if an error occurs
     */
    public void substitutePrefix(String filename, String prefixSubstitute, String substituteReplacement) throws IOException {
        log.debug("Replace " + prefixSubstitute + " by " + substituteReplacement);

        SED.replaceAll(new File(filename), escapeSlashes(prefixSubstitute), escapeSlashes(substituteReplacement));
    }

    /**
     * Escape slashes
     * @param string The string to escape
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
     * Returns the directory prefix
     * @return The directory prefix
     */
    public String toString() {
        return "-P: " + this.directory_prefix;
    }

    /**
     * Saves the specified byte array into a file
     * @param filename The filename to use
     * @param bytes The byte stream
     * @throws FileNotFoundException if the file could not be found
     * @throws IOException if an IO error occurs
     */
    public void saveToFile(String filename, byte[] bytes) throws IOException {
        FileOutputStream out = null;

		try {
	        File file = new File(filename);
	        File parent = new File(file.getParent());

	        if (!parent.exists()) {
	            log.info(".saveToFile(): Directory will be created: " + parent.getAbsolutePath());
	            parent.mkdirs();
	        }

	        out = new FileOutputStream(file.getAbsolutePath());
			out.write(bytes);
		} catch (final FileNotFoundException e) {
			log.error("file not found." + e.toString());
			throw new IOException(e.toString());
		} catch (final IOException e) {
			log.error("IO error." + e.toString());
			throw new IOException(e.toString());
		} finally {
			if (out != null)
				out.close();
		}
    }

    /**
     * Create the file name given a URL
     * @param url URL of resource, which has been downloaded and shall be saved
     * @param prefixSubstitute The prefix to be replaced
     * @param substituteReplacement The replacement
     * @return Absolute substituted filename
     */
    public String createFileName(URL url, String prefixSubstitute, String substituteReplacement) {
        File file = new File(this.directory_prefix + File.separator + url.getFile());
        return file.getAbsolutePath().replaceAll(prefixSubstitute, substituteReplacement);
    }

    /**
     * Run the WGet process
     * @param command The command to run
     * @return The byte stream
     * @throws Exception if an error occurs
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
