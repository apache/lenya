package org.wyona.cms.rc;

/**
 * @author Michael Wechner (http://www.wyona.com)
 * @version 0.7.19
 */
public class CheckOutEntry extends RCMLEntry {

	public CheckOutEntry(String identity, long time) {
		super(identity, time);
		type = RCML.co;
	}
	
}
