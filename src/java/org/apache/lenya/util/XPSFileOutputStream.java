package org.wyona.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileDescriptor;
import java.io.IOException;
import java.io.FileNotFoundException;

//import org.wyona.xps.publish.Publisher;

import org.apache.log4j.Category;

/**
 * @author Marc Liyanage
 * @version 1.0
 */
public class XPSFileOutputStream extends FileOutputStream{
  static Category log=Category.getInstance(XPSFileOutputStream.class);


	protected String realFilename = null;
	protected String suffix = null;

	private static final String suffixBase = ".xpstemp";

	// FIXME: the hashCode() is probably not good enough
	//        We need to find a better source of a random
	//        string that is available to a static method.
	//
	protected static String getTempFilename(String realname) {
		return realname + XPSFileOutputStream.suffixBase + "." + Runtime.getRuntime().hashCode();
	}


	public XPSFileOutputStream(String name) throws IOException {
		super(getTempFilename(name));
		setRealFilename(name);
	}


	public XPSFileOutputStream(File file) throws IOException {
		super(getTempFilename(file.getAbsolutePath()));
		setRealFilename(file.getAbsolutePath());
	}



	public XPSFileOutputStream(String filename, boolean append) throws IOException {
		super(getTempFilename(filename), append);
		setRealFilename(filename);
	}


	/**
	 * We cannot support this version of the constructer because we
	 * need to play tricks with the filename.
	 * There is no filename available when starting with a FileDescriptor.
	 *
	 */
	public XPSFileOutputStream(FileDescriptor fdObj) throws IOException {
		super(fdObj);
		throw new IOException("Constructing an XPSFileOutputStream using a FileDescriptor is not suported because we depend on a filename");
	}


	

	protected String getRealFilename() {
		return this.realFilename;
	}



	protected void setRealFilename(String filename) {
		this.realFilename = filename;
	}
/**
 *
 */
  public void close() throws IOException {
    super.close();
    new File(getTempFilename(getRealFilename())).renameTo(new File(getRealFilename()));
    log.debug(".close(): mv "+getTempFilename(getRealFilename())+" "+getRealFilename());
/*
    try{
      new Publisher(new File("/"),"replication").publish(getRealFilename());
      }
    catch(Exception e){
      log.error(".close(): "+e);
      }
*/
    }
/**
 *
 */
  public void flush() {
    log.debug("flush() called");
    }	
  }
