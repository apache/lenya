package org.wyona.cms.rc;

/**
 * @author Marc Liyanage
 * @version 1.0
 */
public class RCMLEntry {
    
	String identity = null;
	long time = 0;
	short type = 0;

	public RCMLEntry(String identity, long time) {
		this.identity=identity;
		this.time=time;
	}


}
