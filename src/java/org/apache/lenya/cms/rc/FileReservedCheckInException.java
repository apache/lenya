package org.wyona.cms.rc;

import java.util.Date;

/**
 * @author Michael Wechner (http://www.wyona.com)
 * @version 0.7.5
 */
public class FileReservedCheckInException extends Exception {

//	public Date checkOutDate = null;
//	public String checkOutUsername = null;

	public String source=null;
	public Date date = null;
	public String username = null;
	public String typeString = null;
	public short type;

	public FileReservedCheckInException(String source, RCML rcml) throws Exception {

		this.source=source;

		try {
          
			RCMLEntry rcmlEntry = rcml.getLatestEntry();
			
			username = rcmlEntry.identity;
			date = new Date(rcmlEntry.time);
			type = rcmlEntry.type;

			if (type == RCML.co) {
				typeString = "Checkout";
			} else {
				typeString = "Checkin";
			}
         
		} catch (Exception exception) {
         
			throw new Exception("Unable to create FileReservedCheckInException object!");
         
		}
		
	}
	
	public String getMessage() {
	
		return "Unable to check in the file " +this.source+ " because of a " + this.typeString + " by user " + this.username + " at " + this.date;
	
	}
	

}
