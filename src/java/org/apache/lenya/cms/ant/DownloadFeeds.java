package org.apache.lenya.cms.ant;

import org.apache.tools.ant.Task;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.taskdefs.Get;
import org.xml.sax.*;
import org.xml.sax.helpers.DefaultHandler;
import javax.xml.parsers.SAXParserFactory; 
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import java.util.NoSuchElementException;
import java.util.StringTokenizer;
import java.net.URL;
import java.net.URLConnection;
import java.net.MalformedURLException;
import java.net.HttpURLConnection;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.IOException;
import java.lang.NullPointerException;

/**
 * Download (via HTTP) feed (RSS, Atom, ...) and verify well-formedness of XML
 */
public class DownloadFeeds extends Task {
    private boolean verbose = false;
    private boolean ignoreErrors = false;
    
    String rootDir;
    String feeds;

    /**
     * Get feeds from build.xml
     */
    public void setFeeds(String str) {
        feeds = str;
    }

    /**
     * Get root directory from build.xml
     */
    public void setRootdir(String str) {
        rootDir = str;
    }
    
    /**
     *
     */
    public void execute() {
        if (rootDir == null) {
            throw new BuildException("No rootdir specified");
        } else {
            log("Root directory: " + rootDir);
        }

        if (feeds == null) {
            throw new BuildException("No feeds specified");
        }
        
        StringTokenizer tok = new StringTokenizer(feeds, ",");
        
        if (tok.countTokens() == 0) {
            throw new BuildException("Illegal feeds string");
        }
        
        try {        
            String url;
            
            while ((url = tok.nextToken()) != null) {
                String fname = tok.nextToken();
                
                URL source = null;
                try {
                    source = new URL(url);
                    log("Feed: " + url);
                } catch (MalformedURLException e) {
                    log("bad url");
                    throw new BuildException(e, getLocation());
                }
                
                File dest = null;
                try {    
                    //log("file: " + fname);
                    dest = new File(fname);
                    if (!dest.isAbsolute()) {
                        //log("Is NOT absolute: " + dest);
                        dest = new File(rootDir, fname);
                    }
                    log("Destination: " + dest);
                } catch (NullPointerException e) {
                    log(e.toString());
                }

                try {    
                    URLConnection connection = source.openConnection();
                    
                    connection.connect();
                    HttpURLConnection httpConnection = (HttpURLConnection) connection;
                    
                    InputStream is = null;
                    for (int i = 0; i < 3; i++) {
                        try {
                            is = connection.getInputStream();
                            break;
                        } catch (IOException ex) {
                            log("Error opening connection " + ex);
                        }
                    }
                    if (is == null) {
                        log("Can't get " + source + " to " + dest);
                        if (ignoreErrors) {
                            return;
                        }
                        throw new BuildException("Can't get " + source + " to " + dest,
                                                 getLocation());
                    }

                    FileOutputStream fos = new FileOutputStream(dest);
                    boolean finished = false;
                    try {
                        byte[] buffer = new byte[100 * 1024];
                        int length;
                        int dots = 0;

                        while ((length = is.read(buffer)) >= 0) {
                            fos.write(buffer, 0, length);
                            if (verbose) {
                                System.out.print(".");
                                if (dots++ > 50) {
                                    System.out.flush();
                                    dots = 0;
                                }
                            }
                        }
                        if (verbose) {
                            System.out.println();
                        }
                        finished = true;
                    } finally {
                        if (fos != null) {
                            fos.close();
                        }
                        is.close();
                        // we have started to (over)write dest, but failed.
                        // Try to delete the garbage we'd otherwise leave
                        // behind.
                        if (!finished) {
                            dest.delete();
                        }
                    }                        
                } catch (IOException e) {
                    log("IOException: " + e.toString());
                    throw new BuildException(e, getLocation());
                }
                
                SAXParserFactory factory = SAXParserFactory.newInstance();
                try {
                    SAXParser saxParser = factory.newSAXParser();
                    saxParser.parse( dest, new DefaultHandler() );
                } catch (IOException e) {
                    e.toString();
                    throw new BuildException(e, getLocation());
                } catch (ParserConfigurationException e) {
                    e.toString();
                    throw new BuildException(e, getLocation());                
                } catch (IllegalArgumentException e) {
                    e.toString();
                    throw new BuildException(e, getLocation());
                } catch (SAXException e) {
                    log("XML: " + dest + " is NOT well-formed!!!");
                    throw new BuildException(e, getLocation());
                }

                log("XML file: " + dest + " seems to be well-formed :-)");
            }
        } catch (NoSuchElementException e) {
            return;    
        }                
    }
}
