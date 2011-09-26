package org.apache.lenya.cms.observation;

public class Observer {
	String eventType; //TODO : an enum for define eventType
	String eventFired;
	Object observedObject;
	
	String observerID;
	
	public void event(String eventType, String eventFired,Object observedObject){}
}
