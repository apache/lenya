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

/**
 * @author Michael Wechner (http://www.wyona.com)
 * @version 1.5.1
 */
public class RevisionController
     {
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
            System.err.println("Usage: "+new RevisionController().getClass().getName()+" username source username destination");
            return;
            }
          String identityS=args[0];
          String source=args[1];
          String identityD=args[2];
          String destination=args[3];
          RevisionController rc=new RevisionController();
            System.err.println("rcmlDirectory "+rc.rcmlDirectory);
            System.err.println("rootDir "+rc.rootDir);
            System.err.println("backupDir "+rc.backupDir);
          File in=null;
          try
            {
            in=rc.reservedCheckOut(source,identityS);
            }
          catch(FileNotFoundException e) // No such source file
            {
            System.err.println(e);
            }
          catch(FileReservedCheckOutException e) // Source has been checked out already
            {
            System.err.println(e);
            System.err.println(e.source +"is already check out by "+e.checkOutUsername+" since "+e.checkOutDate);
            return;
            }
          catch(IOException e) // Cannot create rcml file
            {
            System.err.println(e);
            return;
            }
          catch(Exception e)
            {
            System.err.println(e);
            return;
            }
/*
          BufferedReader buffer=new BufferedReader(in);
          String line=null;
          try
            {
            while((line=buffer.readLine()) != null)
                 {
                 System.out.println(line);
                 }
            }
          catch(IOException e)
            {
            System.err.println(e);
            }
*/

          try
            {
            rc.reservedCheckIn(destination,identityD,true);
            }
          catch(FileReservedCheckInException e)
            {
            System.err.println(e);
            }
          catch(Exception e)
            {
            System.err.println(e);
            }

/*
          if(args.length != 2)
            {
            System.err.println("Usage: "+new RevisionController().getClass().getName()+" time destination");
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
            System.err.println(e);
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
     public RevisionController(String rootDir)
          {
          this();
          this.rootDir=rootDir;
          }




	public RCML getRCML(String source) 
			throws FileNotFoundException, IOException, Exception {
		
		File file = new File(rootDir + source);
		if(!file.isFile()) {
			throw new FileNotFoundException(file.getAbsolutePath());
		}

		return new RCML(rcmlDirectory, rootDir + source);

	}


/**
 *
 */
     public File reservedCheckOut(String source, String identity) 
          throws FileNotFoundException, FileReservedCheckOutException, IOException, Exception
          {
          File file=new File(rootDir+source);
          if(!file.isFile())
            {
            throw new FileNotFoundException(file.getAbsolutePath());
            }
          System.err.println("Instance de RCML avec: " + rcmlDirectory +" et "+rootDir + source);
          RCML rcml = new RCML(rcmlDirectory, rootDir + source);

//          CheckOutEntry coe = rcml.getLatestCheckOutEntry();
          RCMLEntry entry = rcml.getLatestEntry();

          // The same user is allowed to check out repeatedly without
          // having to check back in first.
          //
          
          System.err.println("entry: " + entry);
          System.err.println("entry.type:" + entry.type);
          System.err.println("entry.identity" + entry.identity);
          
          if (entry != null && entry.type != RCML.ci && !entry.identity.equals(identity)) {
            throw new FileReservedCheckOutException(source, rcml);
          }
          
          rcml.checkOutIn(RCML.co, identity, new Date().getTime());
          
		  return file;
          }
/**
 * backup=true: A backup will be created
 * backup=false: No backup will be made
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
			File backupFile=new File(backupDir+"/"+time+".bak");
			System.err.println("Backup: copy "+originalFile.getAbsolutePath()+" "+backupFile.getAbsolutePath());
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
		  System.err.println(this.getClass().getName()+": Send Signal: "+originalFile.getAbsolutePath());
//		  StatusChangeSignalHandler.emitSignal("file:"+originalFile.getAbsolutePath(),"reservedCheckIn");
		  }
        catch(Exception e){
		  System.err.println(this.getClass().getName()+".reservedCheckIn(): "+e);
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
			System.err.println("Backup: copy "+originalFile.getAbsolutePath()+" "+backupFile.getAbsolutePath());
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

	public String getBackupFilename(long time) {
	
		File backup = new File(backupDir + "/" + time + ".bak");
		return backup.getAbsolutePath();

	}

/**
 * Rolls back to the given point in time
 *
 */
	public long rollback(String destination, String identity, boolean backupFlag, long time)
			throws FileReservedCheckInException, FileReservedCheckOutException, FileNotFoundException, Exception {


		// Make sure the old version exists
		//
		File backup = new File(backupDir + "/" + time + ".bak");
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
/**
 *
 */
     public void undoCheckIn(long time,String destination)
          throws Exception
          {
          File backup=new File(backupDir+"/"+time+".bak");
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
          System.out.println("Undo: copy "+backup.getAbsolutePath()+" "+current.getAbsolutePath());
          
          rcml.deleteFirstCheckIn();
          out.close();
          
          }

     }
