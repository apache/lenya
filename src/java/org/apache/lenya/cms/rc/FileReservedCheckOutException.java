package org.wyona.cms.rc;

import java.util.Date;

/**
 * @author Michael Wechner (http://www.wyona.com)
 * @version 0.7.5
 */
public class FileReservedCheckOutException extends Exception
	{
	public String source=null;
	public Date checkOutDate = null;
	public String checkOutUsername = null;
     
     
	public FileReservedCheckOutException(String source, RCML rcml) throws Exception {

		this.source=source;

		try {
          
			CheckOutEntry coe = rcml.getLatestCheckOutEntry();
			
			checkOutUsername = coe.identity;
			checkOutDate = new Date(coe.time);
         
		} catch (Exception exception) {
         
			throw new Exception("Unable to create FileReservedCheckOutException object!");
         
		}



	}
    
    
    
}
