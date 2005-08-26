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
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;

/**
 * Helper class to hold some indexing data during the indexing process
 */
public class IndexInformation {
    
    private static Logger log = Logger.getLogger(IndexInformation.class);
    private String index;
    private boolean creating;
    
    /**
     * Creates a new IndexInformation object.
     * @param _index The index
     * @param dumpDirectory The dump directory
     * @param filter The filter
     * @param create Whether this index is being created
     */
    public IndexInformation(String _index, File dumpDirectory, FileFilter filter, boolean create) {
        log.info("Collecting index information for index '" + _index + "'...");

        this.index = _index;
        this.creating = create;
        collectFiles(dumpDirectory, filter, _index);
        this.startTime = new GregorianCalendar();

        log.info(getFileNumber() + " files to index");
    }

    protected String getIndex() {
        return this.index;
    }

    protected boolean isCreating() {
        return this.creating;
    }

    private List files = new ArrayList();

    /**
     * Add a file to the file list
     * @param _file The file
     */
    protected void addFile(File _file) {
        this.files.add(_file);
    }

    /**
     * Get all files in the file list
     * @return The file list
     */
    public File[] getFiles() {
        Collections.sort(this.files);
        return (File[]) this.files.toArray(new File[this.files.size()]);
    }

    private int currentFile = 0;

    /**
     * Increase the file count
     */
    public void increase() {
        this.currentFile++;
    }

    /**
     * Get the number of the current file
     * @return The number of the current file
     */
    public int getCurrentFile() {
        return this.currentFile;
    }

    /**
     * Get number of files to index
     * @return number of files to index
     */
    public int getFileNumber() {
        return this.files.size();
    }

    private Calendar startTime;

    /**
     * Get the start time of the index
     * @return The start time
     */
    public Calendar getStartTime() {
        return this.startTime;
    }

    /**
     * Generate string which tells about the indexing progress
     * @return indexing progress
     */
    public String printProgress() {
        double percent = (double) this.currentFile / (double) getFileNumber();
        DateFormat format = new SimpleDateFormat("HH:mm:ss");

        return "added document " + getCurrentFile() + " of " + getFileNumber() + " (" +
        (int) (percent * 100) + "%" + ", remaining time: " +
        format.format(getEstimatedTime().getTime()) + ")";
    }

    /**
     * Get the estimated time duration for the indexing process
     * @return The estimated duration
     */
    protected Calendar getEstimatedTime() {
        long elapsedMillis = new Date().getTime() - getStartTime().getTime().getTime();

        double millisPerFile = (double) elapsedMillis / (double) this.currentFile;
        long estimatedMillis = (long) (millisPerFile * getFileNumber()) - elapsedMillis;

        GregorianCalendar estimatedCalendar = new GregorianCalendar();
        estimatedCalendar.setTimeInMillis(estimatedMillis);
        estimatedCalendar.roll(Calendar.HOUR, false);

        return estimatedCalendar;
    }

    /**
     * Collect files
     * @param _dumpDirectory The dump directory
     * @param _filter The filter
     * @param _index The index
     */
    protected void collectFiles(File _dumpDirectory, FileFilter _filter, String _index) {
        IndexIterator iterator = new IndexIterator(_index, _filter);
        IndexIteratorHandler handler;

        if (isCreating()) {
            handler = new CreateHandler();
        } else {
            handler = new UpdateHandler();
        }

        iterator.addHandler(handler);
        iterator.iterate(_dumpDirectory);
    }

    /**
     * Create Handler class
     */
    public class CreateHandler extends AbstractIndexIteratorHandler {
        /** Handles a file.
         * @param _reader The reader
         * @param _file The file
         */
        public void handleFile(IndexReader _reader, File _file) {
            IndexInformation.this.addFile(_file);
        }
    }

    /**
     * Update handler class
     */
    public class UpdateHandler extends AbstractIndexIteratorHandler {
        /** Handles a new document.
         * @param _reader The reader
         * @param _term The term
         * @param _file The file
         */
        public void handleNewDocument(IndexReader _reader, Term _term, File _file) {
            IndexInformation.this.addFile(_file);
        }
    }
}
