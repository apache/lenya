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

package org.apache.lenya.lucene.index;

import java.io.File;
import java.io.FileFilter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import org.apache.log4j.Category;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;

/**
 * Helper class to hold indexing information
 */
public class IndexInformation {
    
    private static Category log = Category.getInstance(IndexInformation.class);
    
    /**
     * Creates a new IndexInformation object.
     * @param index DOCUMENT ME!
     * @param dumpDirectory DOCUMENT ME!
     * @param filter DOCUMENT ME!
     * @param create DOCUMENT ME!
     */
    public IndexInformation(String index, File dumpDirectory, FileFilter filter, boolean create) {
        log.info("Collecting index information for index '" + index + "'...");

        this.creating = create;
        this.index = index;
        collectFiles(dumpDirectory, filter, index);
        this.startTime = new GregorianCalendar();

        log.info(this.length + " files to index");
        //log.info(getFileNumber() + " files to index");
    }

    private String index;

    protected String getIndex() {
        return this.index;
    }

    private boolean creating;

    protected boolean isCreating() {
        return this.creating;
    }

    //private List files = new ArrayList();
    private int length = 0;

    /**
     *
     */
    protected void addFile(File file) {
        //files.add(file);
	this.length++;
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
/*
    public File[] getFiles() {
        Collections.sort(files);

        return (File[]) files.toArray(new File[files.size()]);
    }
*/

    private int currentFile = 0;

    /**
     * DOCUMENT ME!
     */
    public void increase() {
        this.currentFile++;
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
     * Get number of files to index
     *
     * @return number of files to index
     */
/*
    public int getFileNumber() {
        return files.size();
    }
*/

    private Calendar startTime;

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public Calendar getStartTime() {
        return this.startTime;
    }

    /**
     * Generate string which tells about the indexing progress
     *
     * @return indexing progress
     */
    public String printProgress() {
        double percent = (double) this.currentFile / (double) this.length;
        //double percent = (double) currentFile / (double) getFileNumber();
        DateFormat format = new SimpleDateFormat("HH:mm:ss");

        //return "added document " + getCurrentFile() + " of " + getFileNumber() + " (" +
        return "added document " + getCurrentFile() + " of " + this.length + " (" +
        (int) (percent * 100) + "%" + ", remaining time: " +
        format.format(getEstimatedTime().getTime()) + ")";
    }

    /**
     *
     */
    protected Calendar getEstimatedTime() {
        long elapsedMillis = new Date().getTime() - getStartTime().getTime().getTime();

        double millisPerFile = (double) elapsedMillis / (double) this.currentFile;
        long estimatedMillis = (long) (millisPerFile * this.length) - elapsedMillis;
        //long estimatedMillis = (long) (millisPerFile * getFileNumber()) - elapsedMillis;

        GregorianCalendar estimatedCalendar = new GregorianCalendar();
        estimatedCalendar.setTimeInMillis(estimatedMillis);
        estimatedCalendar.roll(Calendar.HOUR, false);

        return estimatedCalendar;
    }

    /**
     * Collect files
     */
    protected void collectFiles(File dumpDirectory, FileFilter filter, String _index) {
        IndexIterator iterator = new IndexIterator(_index, filter);
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
