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

import org.apache.lenya.util.CommandLineLogger;

import org.apache.lucene.document.DateField;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.index.TermEnum;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;


/**
 * @author Andreas Hartmann
 * @version $Id: IndexIterator.java,v 1.6 2003/12/09 21:32:49 stefano Exp $
 */
public class IndexIterator {
    /** Creates a new instance of IndexItertor */
    public IndexIterator(String index, FileFilter filter) {
        this.filter = filter;
        this.index = index;
    }

    private CommandLineLogger logger = new CommandLineLogger(getClass());

    protected CommandLineLogger getLogger() {
        return logger;
    }

    private String index;

    protected String getIndex() {
        return index;
    }

    private FileFilter filter;

    /**
     * @return FileFilter
     */
    protected FileFilter getFilter() {
        return filter;
    }

    private List handlers = new ArrayList();

    /**
     * DOCUMENT ME!
     *
     * @param handler DOCUMENT ME!
     */
    public void addHandler(IndexIteratorHandler handler) {
        if (!handlers.contains(handler)) {
            handlers.add(handler);
        }
    }

    protected void handleFile(File file) {
        for (Iterator i = handlers.iterator(); i.hasNext();) {
            IndexIteratorHandler handler = (IndexIteratorHandler) i.next();
            handler.handleFile(getReader(), file);
        }
    }

    protected void handleStaleDocument(Term term) {
        for (Iterator i = handlers.iterator(); i.hasNext();) {
            IndexIteratorHandler handler = (IndexIteratorHandler) i.next();
            handler.handleStaleDocument(getReader(), term);
        }
    }

    protected void handleUnmodifiedDocument(Term term, File file) {
        for (Iterator i = handlers.iterator(); i.hasNext();) {
            IndexIteratorHandler handler = (IndexIteratorHandler) i.next();
            handler.handleUnmodifiedDocument(getReader(), term, file);
        }
    }

    protected void handleNewDocument(Term term, File file) {
        for (Iterator i = handlers.iterator(); i.hasNext();) {
            IndexIteratorHandler handler = (IndexIteratorHandler) i.next();
            handler.handleNewDocument(getReader(), term, file);
        }
    }

    private IndexReader reader;

    protected IndexReader getReader() {
        return reader;
    }

    /**
     * DOCUMENT ME!
     *
     * @param dumpDirectory DOCUMENT ME!
     */
    public void iterate(File dumpDirectory) {
        getLogger().debug("Iterating files for index " + getIndex());

        try {
            reader = IndexReader.open(getIndex());

            TermEnum iterator = enumerateUIDs(getReader());

            File[] files = getFiles(dumpDirectory);

            for (int i = 0; i < files.length; i++) {
                iterateFiles(iterator, files[i], dumpDirectory);
            }

            // iterate the rest of stale documents
            while ((iterator.term() != null) && iterator.term().field().equals("uid")) {
                handleStaleDocument(iterator.term());
                iterator.next();
            }

            iterator.close();
            reader.close();
        } catch (IOException e) {
            getLogger().log(e);
        }
    }

    protected void iterateFiles(TermEnum iterator, File file, File dumpDirectory)
        throws IOException {
        String uid = createUID(file, dumpDirectory);
        getLogger().debug("-----------------------------------------------------");
        getLogger().debug("[file]  file uid: " + uid2url(uid));

        handleFile(file);

        // handle all terms with a smaller uid than the modified file and delete their documents
        while (isStale(iterator.term(), uid)) {
            getLogger().debug("[stale] term uid: " + uid2url(iterator.term().text()));
            handleStaleDocument(iterator.term());
            iterator.next();
        }

        // handle un-modified file
        if (hasEqualUID(iterator.term(), uid)) {
            getLogger().debug("[unmod] term uid: " + uid2url(iterator.term().text()));
            handleUnmodifiedDocument(iterator.term(), file);
            iterator.next();
        }
        // handle new file
        else {
            if (iterator.term() != null) {
                getLogger().debug("[new]   term uid: " + uid2url(iterator.term().text()));
                handleNewDocument(iterator.term(), file);
            }
        }
    }

    /**
     * Returns an term enumerator beginning with the first term that represents a UID field.
     */
    protected TermEnum enumerateUIDs(IndexReader reader) {
        TermEnum enum = null;

        try {
            enum = reader.terms(new Term("uid", ""));
        } catch (IOException e) {
            getLogger().log("Term enumeration failed: ", e);
        }
            
        return enum;
    }

    /**
     * Returns if the term is not null and decribes a UID field.
     */
    protected static boolean isUIDTerm(Term term) {
        return (term != null) && term.field().equals("uid");
    }

    /**
     * Returns <code>true</code> if the file described by uid has a bigger UID than the
     * file described by the existing UID term.
     */
    protected static boolean isStale(Term term, String uid) {
        return isUIDTerm(term) && (term.text().compareTo(uid) < 0);
    }

    /**
     * Returns <code>true</code> if the file described by uid has the same UID as the
     * file described by the existing UID term.
     */
    protected static boolean hasEqualUID(Term term, String uid) {
        return isUIDTerm(term) && term.text().equals(uid);
    }

    /**
     * Append path and date into a string in such a way that lexicographic sorting gives the same
     * results as a walk of the file hierarchy.  Thus null (\u0000) is used both to separate
     * directory components and to separate the path from the date.
     *
     * @param file DOCUMENT ME!
     * @param htdocsDumpDir DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public static String createUID(File file, File htdocsDumpDir) {
        String requestURI = file.getPath().substring(htdocsDumpDir.getPath().length());
        String uid = requestURI.replace(File.separatorChar, '\u0000') + "\u0000" +
            DateField.timeToString(file.lastModified());

        return uid;
    }

    /**
     * Converts a UID to a URL string.
     */
    public static String uid2url(String uid) {
        String url = uid.replace('\u0000', '/'); // replace nulls with slashes
        String timeString = uid.substring(uid.lastIndexOf(" ") + 1);
        Date date = DateField.stringToDate(timeString);
        DateFormat format = new SimpleDateFormat("yyyy.MM.dd  HH:mm:ss");

        return url.substring(0, url.lastIndexOf('/')) + " " + format.format(date);
    }

    //-------------------------------------------------------------
    // Files
    //-------------------------------------------------------------
    public File[] getFiles(File dumpDirectory) {
        List files = new ArrayList();
        collectFiles(dumpDirectory, files);
        Collections.sort(files);

        Map uidToFile = new HashMap();

        String[] uids = new String[files.size()];

        for (int i = 0; i < uids.length; i++) {
            uids[i] = createUID((File) files.get(i), dumpDirectory);
            uidToFile.put(uids[i], files.get(i));
        }

        Arrays.sort(uids);

        File[] fileArray = new File[uids.length];

        for (int i = 0; i < uids.length; i++) {
            fileArray[i] = (File) uidToFile.get(uids[i]);
        }

        return fileArray;
    }

    protected void collectFiles(File file, List files) {
        if (file.isDirectory()) {
            File[] fileArray = file.listFiles(getFilter());

            for (int i = 0; i < fileArray.length; i++) {
                collectFiles(fileArray[i], files);
            }
        } else {
            files.add(file);
        }
    }
}
