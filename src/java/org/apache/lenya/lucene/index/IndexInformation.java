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

/* $Id: IndexInformation.java,v 1.9 2004/03/01 16:18:16 gregor Exp $  */

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
