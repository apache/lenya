package org.wyona.cms.rc;

import java.io.FileReader;
import java.io.FileNotFoundException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.io.OutputStream;
import java.util.Vector;
import java.util.Date;

import org.wyona.xml.DOMParserFactory;
import org.wyona.xml.DOMWriter;
import org.wyona.xml.XPointerFactory;
import org.wyona.util.XPSFileOutputStream;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import java.util.AbstractList;

/**
 * @author Michael Wechner
 * @author Marc Liyanage
 * @version 0.7.19
 */
public class RCML{
  private File rcmlFile;
  private Document document = null;
  private boolean dirty = false;
  static short co=0;
  static short ci=1;
  int maximalNumberOfEntries = 5;
/**
 *
 */
     public static void main(String[] args)
          {
          if(args.length != 1)
            {
            System.err.println("Usage: java RCML rcmlDirectory datafilename");
            return;
            }
          try
            {
            //RCML rcml=new RCML(args[0]);
            RCML rcml=new RCML(args[0], args[1]);
            rcml.checkOutIn(RCML.co,"michi",new Date().getTime());
            //rcml.checkOutIn(RCML.ci,"michi",new Date().getTime());
            //rcml.checkOutIn(RCML.ci,"michi",new Date().getTime());
            new DOMWriter(new PrintWriter(System.out)).print(rcml.document);
            CheckOutEntry coe=rcml.getLatestCheckOutEntry();
            System.out.print("\n");
            if(coe == null)
              {
              System.out.println("Not checked out");
              }
            else
              {
              System.out.println("Checked out: "+coe.identity+" "+coe.time);
              }
            }
          catch(Exception e)
            {
            System.err.println(e);
            }
          }
/**
 *
 */

	public void initDocument() {
		DOMParserFactory dpf=new DOMParserFactory();
		document=dpf.getDocument();
		Element root=dpf.newElementNode(document,"XPSRevisionControl");
		document.appendChild(root);
	}

	

	public void finalize() throws IOException, Exception {
	
		if (this.isDirty()) {
			System.err.println("RCML.finalize(): calling write()");
			write();
		}
		
	}
		

	public void write() throws IOException, Exception {
	
		System.err.println("RCML.write(): writing out file: " + rcmlFile.getAbsolutePath());

//		new DOMWriter(new PrintWriter(new FileWriter(rcmlFile.getAbsolutePath()))).print(document);

		pruneEntries(maximalNumberOfEntries);
		XPSFileOutputStream xpsfos = new XPSFileOutputStream(rcmlFile.getAbsolutePath());
		new DOMWriter(xpsfos).print(this.document);
		xpsfos.close();

		clearDirty();

	}
/**
 *
 */
  public RCML(){
    maximalNumberOfEntries=new org.wyona.xml.Configuration().maxNumberOfRollbacks;
    maximalNumberOfEntries=2*maximalNumberOfEntries+1;
    //maximalNumberOfEntries=5;
    }
/**
 *
 */
  public RCML(String rcmlDirectory, String filename) throws Exception{
    this();
    rcmlFile = new File(rcmlDirectory + filename + ".rcml");
    if(!rcmlFile.isFile()){
			// The rcml file does not yet exist, so we create it now...
			//
			File dataFile = new File(filename);
			long lastModified = 0;
			if (dataFile.isFile()) {
				lastModified = dataFile.lastModified();
			}

			initDocument();
			
			// Create a "fake" checkin entry so it looks like the
			// system checked the document in. We use the filesystem
			// modification date as checkin time.
			//
			checkOutIn(RCML.ci, RevisionController.systemUsername, lastModified);

			File parent = new File(rcmlFile.getParent());
			parent.mkdirs();

			write();

		} else {
		
			DOMParserFactory dpf = new DOMParserFactory();
			document = dpf.getDocument(rcmlFile.getAbsolutePath());
		
		}

	}
/**
 *
 */
     public void checkOutIn(short type, String identity, long time) throws IOException, Exception
          {
          DOMParserFactory dpf=new DOMParserFactory();

          Element identityElement=dpf.newElementNode(document,"Identity");
          identityElement.appendChild(dpf.newTextNode(document,identity));
          Element timeElement=dpf.newElementNode(document,"Time");
          timeElement.appendChild(dpf.newTextNode(document,""+time));

          Element checkOutElement=null;
          if(type == co)
            {
            checkOutElement=dpf.newElementNode(document,"CheckOut");
            }
          else if(type == ci)
            {
            checkOutElement=dpf.newElementNode(document,"CheckIn");
            }
          else
            {
            System.err.println("ERROR: "+this.getClass().getName()+".checkOutIn(): No such type");
            return;
            }
          checkOutElement.appendChild(identityElement);
          checkOutElement.appendChild(timeElement);

          Element root=document.getDocumentElement();
          root.insertBefore(dpf.newTextNode(document,"\n"),root.getFirstChild());
          root.insertBefore(checkOutElement,root.getFirstChild());
          root.insertBefore(dpf.newTextNode(document,"\n"),root.getFirstChild());

          setDirty();

          // If this is a checkout, we write back the changed state
          // to the file immediately because otherwise another
          // process might read the file and think there is no open
          // checkout (as it is only visible in our private DOM tree
          // at this time).
          //
          // If, however, this is a checkin, we do not yet write it
          // out because then another process might again check it
          // out immediately and manipulate the file contents
          // *before* our caller has finished writing back the
          // changed data to the destination file. We therefore rely
          // on either our caller invoking the write() method when
          // finished or the garbage collector calling the finalize()
          // method.
          //
          if (type == co) {
            write();
          }
                    
     }
     
     
/**
 *
 */
	public CheckOutEntry getLatestCheckOutEntry() throws Exception {

		XPointerFactory xpf = new XPointerFactory();
		
		Vector firstCheckOut = xpf.select(document.getDocumentElement(),"xpointer(/XPSRevisionControl/CheckOut[1]/Identity)xpointer(/XPSRevisionControl/CheckOut[1]/Time)");
		
		if(firstCheckOut.size() == 0) {

			// No checkout at all
			//
			return null;
		}
		
		String[] fcoValues = xpf.getNodeValues(firstCheckOut);
		long fcoTime = new Long(fcoValues[1]).longValue();
		
		return new CheckOutEntry(fcoValues[0], fcoTime);

	}


	public CheckInEntry getLatestCheckInEntry() throws Exception {

		XPointerFactory xpf = new XPointerFactory();
		
		Vector firstCheckIn = xpf.select(document.getDocumentElement(), "xpointer(/XPSRevisionControl/CheckIn[1]/Identity)xpointer(/XPSRevisionControl/CheckIn[1]/Time)");
		
		if(firstCheckIn.size() == 0) {

			// No checkin at all
			//
			return null;
		}
		
		String[] fciValues = xpf.getNodeValues(firstCheckIn);
		long fciTime = new Long(fciValues[1]).longValue();
		
		return new CheckInEntry(fciValues[0], fciTime);

	}


	public RCMLEntry getLatestEntry() throws Exception {

		CheckInEntry cie = getLatestCheckInEntry();
		CheckOutEntry coe = getLatestCheckOutEntry();
		
		if (cie != null && coe != null) {
			
			if (cie.time > coe.time) {
				return cie;
			} else {
				return coe;
			}
		
		}
		
		if (cie != null) {
			return cie;
		} else {
			return coe;
		}	

	}


          
    /*      
	public CheckOutEntry getFirstEntry() throws Exception {

		XPointerFactory xpf = new XPointerFactory();
		
		Vector firstCheckOut = xpf.select(document.getDocumentElement(),"xpointer(/XPSRevisionControl/CheckOut[1]/Identity)xpointer(/XPSRevisionControl/CheckOut[1]/Time)");
		Vector firstCheckIn  = xpf.select(document.getDocumentElement(),"xpointer(/XPSRevisionControl/CheckIn[1]/Identity)xpointer(/XPSRevisionControl/CheckIn[1]/Time)");
		
		// No checkout at all
		//
		if(firstCheckOut.size() == 0) {
			return null;
		}
		
		String[] fcoValues = xpf.getNodeValues(firstCheckOut);
		long fcoTime = new Long(fcoValues[1]).longValue();
		

		if(firstCheckIn.size() == 2) {
			String[] fciValues = xpf.getNodeValues(firstCheckIn);
			long fciTime = new Long(fciValues[1]).longValue();
			if(fciTime > fcoTime) {
				return null;
			}
		}
		
		return new CheckOutEntry(fcoValues[0], fcoTime);

	}
          
	*/          
          

          
	public Vector getEntries() throws Exception {

		XPointerFactory xpf=new XPointerFactory();

		Vector entries = xpf.select(document.getDocumentElement(),"xpointer(/XPSRevisionControl/CheckOut|/XPSRevisionControl/CheckIn)");
		Vector RCMLEntries = new Vector();


		for (int i = 0; i < entries.size(); i++) {
		
			Element elem = (Element) entries.get(i);
			String time = elem.getElementsByTagName("Time").item(0).getFirstChild().getNodeValue();
			String identity = elem.getElementsByTagName("Identity").item(0).getFirstChild().getNodeValue();
		
			if (elem.getTagName().equals("CheckOut")) {
				RCMLEntries.add(new CheckOutEntry(identity, new Long(time).longValue()));
			} else {
				RCMLEntries.add(new CheckInEntry(identity, new Long(time).longValue()));
			}
					
		}

		return RCMLEntries;
          
	}
          
          

	// Prune the list of entries. Keep <entriesToKeep> items
	// at the front of the list.
	//
 	public void pruneEntries(int entriesToKeep) throws Exception {

		XPointerFactory xpf = new XPointerFactory();

		Vector entries = xpf.select(document.getDocumentElement(),"xpointer(/XPSRevisionControl/CheckOut|/XPSRevisionControl/CheckIn)");
		
		Configuration conf = new Configuration();
		String backupDir = conf.backupDirectory;
		
		for (int i = entriesToKeep; i < entries.size(); i++) {
			
			Element current = (Element) entries.get(i);
			
			// remove the backup file associated with this entry
			String time = current.getElementsByTagName("Time").item(0).getFirstChild().getNodeValue();
			File backupFile = new File(backupDir + "/" + time + ".bak");
			backupFile.delete();
			
			// remove the entry from the list
			current.getParentNode().removeChild(current);
			
		}
          
	}
          
  
   
   
            
	public org.w3c.dom.Document getDOMDocumentClone() throws Exception {

		Document documentClone = new DOMParserFactory().getDocument();
		documentClone.appendChild(documentClone.importNode(document.getDocumentElement(), true));

		return documentClone;
          
	}
          
          
    public boolean isDirty() {
    	return dirty;
    }
    
	protected void setDirty () {    	
		dirty = true;
	}
          
	protected void clearDirty () {    	
		dirty = false;
	}
         
          
          
/**
 *
 */
     public void deleteFirstCheckIn() throws Exception
          {
          XPointerFactory xpf=new XPointerFactory();
          Node root=document.getDocumentElement();
          Vector firstCheckIn=xpf.select(root,"xpointer(/XPSRevisionControl/CheckIn[1])");
          root.removeChild((Node)firstCheckIn.elementAt(0));
          root.removeChild(root.getFirstChild()); // remove EOL (end of line)
          setDirty();
          }
     }









