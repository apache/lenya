package org.wyona.cms.rc;

import java.io.FileReader;
import java.io.FileNotFoundException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.io.OutputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.FileInputStream;
import java.util.Vector;
import java.util.Date;

//import org.wyona.xps.signalling.StatusChangeSignalHandler;
import org.wyona.util.XPSFileOutputStream;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import org.apache.log4j.Category;

/**
 * @author Michael Wechner (http://www.wyona.com)
 * @version 1.5.1
 */
public class RevisionController
     {
     static Category log=Category.getInstance(RevisionController.class);
     String rcmlDirectory=null;
     String rootDir=null;
     String backupDir=null;
     
	// System username. This is used for 
	// - creating dummy checkin events in a new RCML file
	//   when it is created on-the-fly
	// - system override on checkin, i.e. you can force
	//   a checkin into the repository if you use this
	//   username as identity parameter to reservedCheckIn()
	//
	public static final String systemUsername = "System";


/**
 *
 */
     public static void main(String[] args)
          {
          if(args.length != 4)
            {
            log.info("Usage: "+new RevisionController().getClass().getName()+" username(user who checkout) source(document to checkout) username(user who checkin) destination(document to checkin)");
            return;
            }
          String identityS=args[0];
          String source=args[1];
          String identityD=args[2];
          String destination=args[3];
          RevisionController rc=new RevisionController();
          File in=null;
          try
            {
            in=rc.reservedCheckOut(source,identityS);
            }
          catch(FileNotFoundException e) // No such source file
            {
            log.error(e);
            }
          catch(FileReservedCheckOutException e) // Source has been checked out already
            {
            log.error(e);
            log.error(e.source +"is already check out by "+e.checkOutUsername+" since "+e.checkOutDate);
            return;
            }
          catch(IOException e) // Cannot create rcml file
            {
            log.error(e);
            return;
            }
          catch(Exception e)
            {
            log.error(e);
            return;
            }
/*
          BufferedReader buffer=new BufferedReader(in);
          String line=null;
          try
            {
            while((line=buffer.readLine()) != null)
                 {
                 log.info(line);
                 }
            }
          catch(IOException e)
            {
            log.error(e);
            }
*/

          try
            {
            rc.reservedCheckIn(destination,identityD,true);
            }
          catch(FileReservedCheckInException e)
            {
            log.error(e);
            }
          catch(Exception e)
            {
            log.error(e);
            }

/*
          if(args.length != 2)
            {
            log.info("Usage: "+new RevisionController().getClass().getName()+" time destination");
            return;
            }
          long time=new Long(args[0]).longValue();
          String destination=args[1];
          RevisionController rc=new RevisionController();
          try
            {
            rc.undoCheckIn(time,destination);
            }
          catch(Exception e)
            {
            log.error(e);
            }
*/
          }
/**
 *
 */
	public RevisionController() {
		Configuration conf = new Configuration();
		rcmlDirectory = conf.rcmlDirectory;
		backupDir = conf.backupDirectory;
		rootDir = "";
	}
/**
 *
 */
	public RevisionController(String rcmlDirectory,String backupDirectory) {
		this.rcmlDirectory = rcmlDirectory;
		this.backupDir = backupDirectory;
		rootDir = "";
	}
/**
 *
 */
     public RevisionController(String rootDir)
          {
          this();
          this.rootDir=rootDir;
          }
/**
 * Shows Configuration
 */
  public String toString(){
    return "rcmlDir="+rcmlDirectory+" , rcbakDir="+backupDir;
    }
/** Get the RCML File for the file source
 * @param source The filename of a document. 
 * @return RCML The corresponding RCML file. 
 */
	public RCML getRCML(String source) 
			throws FileNotFoundException, IOException, Exception {
		
		File file = new File(rootDir + source);
		if(!file.isFile()) {
			throw new FileNotFoundException(file.getAbsolutePath());
		}

		return new RCML(rcmlDirectory, rootDir + source);

	}


/**Try to make a reserved check out of the file source for a user with identity
 * @param source The filename of the file to check out
 * @param identity The identity of the user
 * @return File File to check out
 * @exception FileReservedCheckOutException if the document is already checked out by another user
 * @exception FileNotFoundException if the file couldn't be found
 * @exception Exception  if another problem occurs 
 */
     public File reservedCheckOut(String source, String identity) 
          throws FileNotFoundException, FileReservedCheckOutException, IOException, Exception
          {
          File file=new File(rootDir+source);
          if(!file.isFile())
            {
            throw new FileNotFoundException(file.getAbsolutePath());
            }
          RCML rcml = new RCML(rcmlDirectory, rootDir + source);

//          CheckOutEntry coe = rcml.getLatestCheckOutEntry();
          RCMLEntry entry = rcml.getLatestEntry();

          // The same user is allowed to check out repeatedly without
          // having to check back in first.
          //
          
          log.debug("entry: " + entry);
          log.debug("entry.type:" + entry.type);
          log.debug("entry.identity" + entry.identity);
          
          if (entry != null && entry.type != RCML.ci && !entry.identity.equals(identity)) {
            throw new FileReservedCheckOutException(source, rcml);
          }
          
          rcml.checkOutIn(RCML.co, identity, new Date().getTime());
          
		  return file;
          }
/**Try to make a reserved check in of the file destination for a user with identity. A backup copy can be made.
 * @param destination The file we want to check in 
 * @param identity The identity of the user
 * @param backup if true, a backup will be created, else no backup will be made.
 * @exception FileReservedCheckInException if the document couldn't be checked in (for instance because it is already checked out by someone other ...)
 * @exception Exception if other problems occur
 */

	public long reservedCheckIn(String destination, String identity, boolean backup)
			throws FileReservedCheckInException, Exception {
		
		RCML rcml = new RCML(rcmlDirectory, rootDir+"/"+destination);
		
		CheckOutEntry coe = rcml.getLatestCheckOutEntry();
		CheckInEntry  cie = rcml.getLatestCheckInEntry();


		// If there has never been a checkout for this object
		// *or* if the user attempting the checkin right now
		// is the system itself, we will skip any checks and proceed
		// right away to the actual checkin.
		// In all other cases we enforce the revision control
		// rules inside this if clause:
		//
		if( ! (coe == null || identity.equals(RevisionController.systemUsername))) {

			
			/*
			 * Possible cases and rules:
			 *
			 * 1.) we were able to read the latest checkin and it is later than latest checkout
			 *     (i.e. there is no open checkout to match this checkin, an unusual case)
			 *     1.1.) identity of latest checkin is equal to current user
			 *           -> checkin allowed, same user may check in repeatedly
			 *     1.2.) identity of latest checkin is not equal to current user
			 *           -> checkin rejected, may not overwrite the revision which
			 *              another user checked in previously
			 * 2.) there was no checkin or the latest checkout is later than latest checkin
			 *     (i.e. there is an open checkout)
			 *     2.1.) identity of latest checkout is equal to current user
			 *           -> checkin allowed, user checked out and may check in again
			 *              (the most common case)
			 *     2.2.) identity of latest checkout is not equal to current user
			 *           -> checkin rejected, may not check in while another
			 *              user is working on this document
			 *
			 */
			if (cie != null && cie.time > coe.time) {
				
				// We have case 1

				if (!cie.identity.equals(identity)) {
					
					// Case 1.2., abort...
					//
					throw new FileReservedCheckInException(destination, rcml);
									
				}
				
			} else {
			
				// Case 2
				
				if (!coe.identity.equals(identity)) {
					
					// Case 2.2., abort...
					//
					throw new FileReservedCheckInException(destination, rcml);
									
				}
			}
		}
		
		
		File originalFile=new File(rootDir+destination);
		long time=new Date().getTime();
		if(backup && originalFile.isFile()) {
			File backupFile=new File(backupDir+"/"+destination+".bak."+time);
			File parent = new File(backupFile.getParent());
			if (!parent.isDirectory()){
                                parent.mkdirs();
			}
			log.info("Backup: copy "+originalFile.getAbsolutePath()+" to "+backupFile.getAbsolutePath());
			InputStream in=new FileInputStream(originalFile.getAbsolutePath());
			//OutputStream out=new FileOutputStream(backupFile.getAbsolutePath());
			OutputStream out=new XPSFileOutputStream(backupFile.getAbsolutePath());
			byte[] buffer=new byte[512];
			int length;
			while((length=in.read(buffer)) != -1) {
				out.write(buffer,0,length);
			}
			out.close();
		}

		rcml.checkOutIn(RCML.ci, identity, time);
		rcml.write();

		try{
//		  log.debug(this.getClass().getName()+": Send Signal: "+originalFile.getAbsolutePath());
//		  StatusChangeSignalHandler.emitSignal("file:"+originalFile.getAbsolutePath(),"reservedCheckIn");
		  }
        catch(Exception e){
		  log.error(this.getClass().getName()+".reservedCheckIn(): "+e);
		  }

		return time;
	}


	/*
	public long reservedCheckIn(String destination, String identity, boolean backup)
			throws FileReservedCheckInException, Exception {
		
		RCML rcml = new RCML(rcmlDirectory, rootDir+"/"+destination);
		
		CheckOutEntry coe=rcml.getLatestCheckOutEntry();

		if(coe != null) {

			String rcmlIdentity = coe.identity;
			if(!rcmlIdentity.equals(identity)) {
				throw new FileReservedCheckInException(destination, rcml);
			}


		}
		
		
		
		
		File originalFile=new File(rootDir+destination);
		long time=new Date().getTime();
		if(backup && originalFile.isFile()) {
			File backupFile=new File(backupDir+"/"+time+".bak");
			log.info("Backup: copy "+originalFile.getAbsolutePath()+" to "+backupFile.getAbsolutePath());
			InputStream in=new FileInputStream(originalFile.getAbsolutePath());
			//OutputStream out=new FileOutputStream(backupFile.getAbsolutePath());
			OutputStream out=new XPSFileOutputStream(backupFile.getAbsolutePath());
			byte[] buffer=new byte[512];
			int length;
			while((length=in.read(buffer)) != -1) {
				out.write(buffer,0,length);
			}
			out.close();
		}

		rcml.checkOutIn(RCML.ci, identity, time);
		rcml.write();
		return time;
	}
	*/

/*
	public String getBackupFilename(long time) {
	
        	File backupFile=new File(backupDir+"/"+destination+".bak."+time);
		return backup.getAbsolutePath();

	}
*/
/**Rolls back to the given point in time 
 * @param destination File which will be rolled back
 * @param identity The identity of the user
 * @param backupFlag If true, a backup of the current version will be made before the rollback
 * @param time The time point of the desired version
 * @exception FileReservedCheckOutException if the current version couldn't be checked out
 * @exception FileReservedCheckInException if the current version couldn't be checked in again
 * @exception FileNotFoundException if a file couldn't be found
 * @exception Exception if another problem occurs
 */
	public long rollback(String destination, String identity, boolean backupFlag, long time)
			throws FileReservedCheckInException, FileReservedCheckOutException, FileNotFoundException, Exception {


		// Make sure the old version exists
		//
        	File backup=new File(backupDir+"/"+destination+".bak."+time);
		File current = new File(rootDir + destination);
		
		if(!backup.isFile()) {
			throw new FileNotFoundException(backup.getAbsolutePath());
		}
		
		if(!current.isFile()) {
			throw new FileNotFoundException(current.getAbsolutePath());
		}



		// Try to check out current version
		//
		reservedCheckOut(destination, identity);

		// Try to check back in, this might cause
		// a backup of the current version to be created if
		// desired by the user.
		//
		long newtime = reservedCheckIn(destination, identity, backupFlag);
		
		
		
		// Now roll back to the old state
		//
		FileInputStream in = new FileInputStream(backup.getAbsolutePath());
		//FileOutputStream out = new FileOutputStream(current.getAbsolutePath());
		XPSFileOutputStream out = new XPSFileOutputStream(current.getAbsolutePath());
		byte[] buffer=new byte[512];
		int length;
		while((length=in.read(buffer)) != -1) {
			out.write(buffer,0,length);
		}
		out.close();
		
		return newtime;
		
	}
/** Delete the check in and roll back the file to the backup at time
 * @param time The time point of the back version we want to retrieve
 * @param destination The File for which we want undo the check in
 * @exception exception FileNotFoundException if the back  version or the current version couldn't be found
 */
     public void undoCheckIn(long time,String destination)
          throws Exception
          {
       	  File backup=new File(backupDir+"/"+destination+".bak."+time);
          File current=new File(rootDir+destination);

          RCML rcml = new RCML(rcmlDirectory, current.getAbsolutePath());
		  
          if(!backup.isFile())
            {
            throw new FileNotFoundException(backup.getAbsolutePath());
            }
          if(!current.isFile())
            {
            throw new FileNotFoundException(current.getAbsolutePath());
            }
          FileInputStream in=new FileInputStream(backup.getAbsolutePath());
          //FileOutputStream out=new FileOutputStream(current.getAbsolutePath());
          XPSFileOutputStream out=new XPSFileOutputStream(current.getAbsolutePath());
          byte[] buffer=new byte[512];
          int length;
          while((length=in.read(buffer)) != -1)
               {
               out.write(buffer,0,length);
               }
          log.info("Undo: copy "+backup.getAbsolutePath()+" "+current.getAbsolutePath());
          
          rcml.deleteFirstCheckIn();
          out.close();
          
          }

     }
