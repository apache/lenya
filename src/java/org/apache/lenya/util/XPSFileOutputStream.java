/*
 * $Id: XPSFileOutputStream.java,v 1.7 2003/03/06 20:45:52 gregor Exp $
 * <License>
 * The Apache Software License
 *
 * Copyright (c) 2002 lenya. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice, this
 *    list of conditions and the following disclaimer in the documentation and/or
 *    other materials provided with the distribution.
 *
 * 3. All advertising materials mentioning features or use of this software must
 *    display the following acknowledgment: "This product includes software developed
 *    by lenya (http://www.lenya.org)"
 *
 * 4. The name "lenya" must not be used to endorse or promote products derived from
 *    this software without prior written permission. For written permission, please
 *    contact contact@lenya.org
 *
 * 5. Products derived from this software may not be called "lenya" nor may "lenya"
 *    appear in their names without prior written permission of lenya.
 *
 * 6. Redistributions of any form whatsoever must retain the following acknowledgment:
 *    "This product includes software developed by lenya (http://www.lenya.org)"
 *
 * THIS SOFTWARE IS PROVIDED BY lenya "AS IS" WITHOUT ANY WARRANTY EXPRESS OR IMPLIED,
 * INCLUDING THE WARRANTY OF NON-INFRINGEMENT AND THE IMPLIED WARRANTIES OF MERCHANTI-
 * BILITY AND FITNESS FOR A PARTICULAR PURPOSE. lenya WILL NOT BE LIABLE FOR ANY DAMAGES
 * SUFFERED BY YOU AS A RESULT OF USING THIS SOFTWARE. IN NO EVENT WILL lenya BE LIABLE
 * FOR ANY SPECIAL, INDIRECT OR CONSEQUENTIAL DAMAGES OR LOST PROFITS EVEN IF lenya HAS
 * BEEN ADVISED OF THE POSSIBILITY OF THEIR OCCURRENCE. lenya WILL NOT BE LIABLE FOR ANY
 * THIRD PARTY CLAIMS AGAINST YOU.
 *
 * Lenya includes software developed by the Apache Software Foundation, W3C,
 * DOM4J Project, BitfluxEditor and Xopus.
 * </License>
 */
package org.lenya.util;

import org.apache.log4j.Category;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileOutputStream;
import java.io.IOException;


/**
 * DOCUMENT ME!
 *
 * @author Marc Liyanage
 * @version 1.0
 */
public class XPSFileOutputStream extends FileOutputStream {
    static Category log = Category.getInstance(XPSFileOutputStream.class);
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

    // FIXME: the hashCode() is probably not good enough
    //        We need to find a better source of a random
    //        string that is available to a static method.
    //
    protected static String getTempFilename(String realname) {
        return realname + XPSFileOutputStream.suffixBase + "." + Runtime.getRuntime().hashCode();
    }

    protected String getRealFilename() {
        return this.realFilename;
    }

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
        new File(getTempFilename(getRealFilename())).renameTo(new File(getRealFilename()));
        log.debug(".close(): mv " + getTempFilename(getRealFilename()) + " " + getRealFilename());

    }

    /**
     * DOCUMENT ME!
     */
    public void flush() {
        log.debug("flush() called");
    }
}
