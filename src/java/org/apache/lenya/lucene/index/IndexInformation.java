/*
 * IndexInformation.java
 *
 * Created on 19. März 2003, 14:56
 */

package org.lenya.lucene.index;

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
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.lenya.util.CommandLineLogger;

/**
 *
 * @author  hrt
 */
public class IndexInformation {
    
    public IndexInformation(String index, File dumpDirectory, FileFilter filter, boolean create) {
        
        CommandLineLogger logger = new CommandLineLogger(getClass());
        logger.log("collecting index information for index '" + index + "'...");

        creating = create;
        collectFiles(dumpDirectory, filter, index);
        this.startTime = new GregorianCalendar();

        logger.log(getFileNumber() + " files to index");
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
    
    public File[] getFiles() {
        Collections.sort(files);
        return (File[]) files.toArray(new File[files.size()]);
    }

    private int currentFile = 0;

    public void increase() {
        currentFile++;
    }

    public int getCurrentFile() {
        return currentFile;
    }

    public int getFileNumber() {
        return files.size();
    }

    private Calendar startTime;

    public Calendar getStartTime() {
        return startTime;
    }

    public String printProgress() {
        
        double percent = (double) currentFile / (double) getFileNumber();
        DateFormat format = new SimpleDateFormat("HH:mm:ss");
        
        return "added document " + getCurrentFile() + " of " + getFileNumber()
                + " ("
                + (int) (percent * 100) + "%"
                + ", remaining time: " + format.format(getEstimatedTime().getTime())
                + ")";
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
        }
        else {
            handler = new UpdateHandler();
        }
        iterator.addHandler(handler);
        iterator.iterate(dumpDirectory);
    }
    
    public class CreateHandler
        extends AbstractIndexIteratorHandler {
        
        /** Handles a file.
         *
         */
        public void handleFile(IndexReader reader, File file) {
            IndexInformation.this.addFile(file);
        }

    }

    public class UpdateHandler
        extends AbstractIndexIteratorHandler {
        
        /** Handles a new document.
         *
         */
        public void handleNewDocument(IndexReader reader, Term term, File file) {
            IndexInformation.this.addFile(file);
        }

    }

}
