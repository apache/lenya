package org.wyona.cms.rc;

/**
 * @author Marc Liyanage
 * @version 1.0
 */
public class CheckInEntry extends RCMLEntry {

	public CheckInEntry(String identity, long time) {
		super(identity, time);
		type = RCML.ci;
	}
	
}
