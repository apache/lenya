/*
$Id: XPSFileOutputStream.java,v 1.11 2003/10/01 09:28:40 edith Exp $
<License>

 ============================================================================
                   The Apache Software License, Version 1.1
 ============================================================================

 Copyright (C) 1999-2003 The Apache Software Foundation. All rights reserved.

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

 Lenya includes software developed by the Apache Software Foundation, W3C,
 DOM4J Project, BitfluxEditor, Xopus, and WebSHPINX.
</License>
*/
package org.apache.lenya.util;

import org.apache.log4j.Category;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileOutputStream;
import java.io.IOException;

import org.apache.avalon.excalibur.io.FileUtil;


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
        FileUtil.copyFile(temp, file);
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
