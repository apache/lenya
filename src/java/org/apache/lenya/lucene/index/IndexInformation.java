/*
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
package org.apache.lenya.lucene.index;

import java.io.File;
import java.io.FileFilter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import org.apache.log4j.Category;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;


/**
 * 
 * @author Andreas Hartmann
 * @version $Id: IndexInformation.java,v 1.8 2004/02/19 13:18:45 michi Exp $
 */
public class IndexInformation {
    
    private static Category log = Category.getInstance(IndexInformation.class);
    
    /**
     * Creates a new IndexInformation object.
     *
     * @param index DOCUMENT ME!
     * @param dumpDirectory DOCUMENT ME!
     * @param filter DOCUMENT ME!
     * @param create DOCUMENT ME!
     */
    public IndexInformation(String index, File dumpDirectory, FileFilter filter, boolean create) {
        log.debug("collecting index information for index '" + index + "'...");

        creating = create;
        collectFiles(dumpDirectory, filter, index);
        this.startTime = new GregorianCalendar();

        log.debug(getFileNumber() + " files to index");
    }

    private String index;

    protected String getIndex() {
        return index;
    }

    private boolean creating;

    protected boolean isCreating() {
        return creating;
    }

    private List files = new ArrayList();

    protected void addFile(File file) {
        files.add(file);
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public File[] getFiles() {
        Collections.sort(files);

        return (File[]) files.toArray(new File[files.size()]);
    }

    private int currentFile = 0;

    /**
     * DOCUMENT ME!
     */
    public void increase() {
        currentFile++;
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public int getCurrentFile() {
        return currentFile;
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public int getFileNumber() {
        return files.size();
    }

    private Calendar startTime;

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public Calendar getStartTime() {
        return startTime;
    }

    /**
     * Generate string which tells about the indexing progress
     *
     * @return indexing progress
     */
    public String printProgress() {
        double percent = (double) currentFile / (double) getFileNumber();
        DateFormat format = new SimpleDateFormat("HH:mm:ss");

        return "added document " + getCurrentFile() + " of " + getFileNumber() + " (" +
        (int) (percent * 100) + "%" + ", remaining time: " +
        format.format(getEstimatedTime().getTime()) + ")";
    }

    protected Calendar getEstimatedTime() {
        long elapsedMillis = new Date().getTime() - getStartTime().getTime().getTime();

        double millisPerFile = (double) elapsedMillis / (double) currentFile;
        long estimatedMillis = (long) (millisPerFile * getFileNumber()) - elapsedMillis;

        GregorianCalendar estimatedCalendar = new GregorianCalendar();
        estimatedCalendar.setTimeInMillis(estimatedMillis);
        estimatedCalendar.roll(Calendar.HOUR, false);

        return estimatedCalendar;
    }

    protected void collectFiles(File dumpDirectory, FileFilter filter, String index) {
        IndexIterator iterator = new IndexIterator(index, filter);
        IndexIteratorHandler handler;

        if (isCreating()) {
            handler = new CreateHandler();
        } else {
            handler = new UpdateHandler();
        }

        iterator.addHandler(handler);
        iterator.iterate(dumpDirectory);
    }

    /**
     * DOCUMENT ME!
     *
     * @author $author$
     * @version $Revision: 1.8 $
     */
    public class CreateHandler extends AbstractIndexIteratorHandler {
        /** Handles a file.
         *
         */
        public void handleFile(IndexReader reader, File file) {
            IndexInformation.this.addFile(file);
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @author $author$
     * @version $Revision: 1.8 $
     */
    public class UpdateHandler extends AbstractIndexIteratorHandler {
        /** Handles a new document.
         *
         */
        public void handleNewDocument(IndexReader reader, Term term, File file) {
            IndexInformation.this.addFile(file);
        }
    }
}
