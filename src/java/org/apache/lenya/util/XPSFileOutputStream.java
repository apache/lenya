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

package org.apache.lenya.util;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileOutputStream;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;

/**
 * DOCUMENT ME!
 */
public class XPSFileOutputStream extends FileOutputStream {
    static Logger log = Logger.getLogger(XPSFileOutputStream.class);
    private static final String suffixBase = ".xpstemp";
    protected String realFilename = null;
    protected String suffix = null;

    /**
     * Creates a new XPSFileOutputStream object.
     *
     * @param name DOCUMENT ME!
     *
     * @throws IOException DOCUMENT ME!
     */
    public XPSFileOutputStream(String name) throws IOException {
        super(getTempFilename(name));
        setRealFilename(name);
    }

    /**
     * Creates a new XPSFileOutputStream object.
     *
     * @param file DOCUMENT ME!
     *
     * @throws IOException DOCUMENT ME!
     */
    public XPSFileOutputStream(File file) throws IOException {
        super(getTempFilename(file.getAbsolutePath()));
        setRealFilename(file.getAbsolutePath());
    }

    /**
     * Creates a new XPSFileOutputStream object.
     *
     * @param filename DOCUMENT ME!
     * @param append DOCUMENT ME!
     *
     * @throws IOException DOCUMENT ME!
     */
    public XPSFileOutputStream(String filename, boolean append)
        throws IOException {
        super(getTempFilename(filename), append);
        setRealFilename(filename);
    }

    /**
     * We cannot support this version of the constructer because we need to play tricks with the
     * filename. There is no filename available when starting with a FileDescriptor.
     *
     * @param fdObj DOCUMENT ME!
     *
     * @throws IOException DOCUMENT ME!
     */
    public XPSFileOutputStream(FileDescriptor fdObj) throws IOException {
        super(fdObj);
        throw new IOException(
            "Constructing an XPSFileOutputStream using a FileDescriptor is not suported because we depend on a filename");
    }

    /**
	 * @param realname DOCUMENT ME!
	 * @return DOCUMENT ME!
	 */
	// FIXME: the hashCode() is probably not good enough
    //        We need to find a better source of a random
    //        string that is available to a static method.
    //
    protected static String getTempFilename(String realname) {
        return realname + XPSFileOutputStream.suffixBase + "." + Runtime.getRuntime().hashCode();
    }

    /**
	 * @return DOCUMENT ME!
	 */
	protected String getRealFilename() {
        return this.realFilename;
    }

    /**
	 * @param filename DOCUMENT ME!
	 */
	protected void setRealFilename(String filename) {
        this.realFilename = filename;
    }

    /**
     * DOCUMENT ME!
     *
     * @throws IOException DOCUMENT ME!
     */
    public void close() throws IOException {
        super.close();
        File temp =  new File(getTempFilename(getRealFilename()));
		File file =  new File(getRealFilename());
        FileUtils.copyFile(temp, file);
        boolean deleted = temp.delete();
        if (deleted) {
        	log.debug("The temporary file "+temp.getAbsolutePath() +"is deleted");
        } else {
			log.debug("The temporary file "+temp.getAbsolutePath() +" couldn't be deleted");
        }
        log.debug(".close(): mv " + getTempFilename(getRealFilename()) + " " + getRealFilename());
    }

    /**
     * DOCUMENT ME!
     */
    public void flush() {
        log.debug("flush() called");
    }
}
