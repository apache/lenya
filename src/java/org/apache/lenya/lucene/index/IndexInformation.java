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

/**
 *
 * @author  hrt
 */
public class IndexInformation {
    
    public IndexInformation(File dumpDirectory, FileFilter filter) {
        System.out.println(getClass().getName() + ": collecting index information ...");

        this.fileNumber = countFiles(dumpDirectory, filter);
        this.startTime = new GregorianCalendar();

        System.out.println(getClass().getName() + ": " + fileNumber + " files to index");
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

    private int fileNumber;

    public int getFileNumber() {
        return fileNumber;
    }

    private Calendar startTime;

    public Calendar getStartTime() {
        return startTime;
    }

    public String printProgress() {
        
        double percent = (double) currentFile / (double) fileNumber;
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
        long estimatedMillis = (long) (millisPerFile * fileNumber) - elapsedMillis;
        
        //System.out.println("elapsed: " + DateFormat.getTimeInstance().format(new Date(elapsedMillis)));
        
        GregorianCalendar estimatedCalendar = new GregorianCalendar();
        estimatedCalendar.setTimeInMillis(estimatedMillis);
        estimatedCalendar.roll(Calendar.HOUR, false);
        return estimatedCalendar;
    }

    protected int countFiles(File file, FileFilter filter) {
        if (file.isDirectory()) {
            File files[] = file.listFiles(filter);
            int count = 0;
            for (int i = 0; i < files.length; i++) {
                count += countFiles(files[i], filter);
            }
            return count;
        }
        else {
            addFile(file);
            return 1;
        }
    }

}
