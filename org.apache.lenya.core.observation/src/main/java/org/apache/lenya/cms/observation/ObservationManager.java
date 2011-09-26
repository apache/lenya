/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 *  contributor license agreements.  See the NOTICE file distributed with
 *  this work for additional information regarding copyright ownership.
 *  The ASF licenses this file to You under the Apache License, Version 2.0
 *  (the "License"); you may not use this file except in compliance with
 *  the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 */
package org.apache.lenya.cms.observation;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.apache.cocoon.util.AbstractLogEnabled;
import org.apache.commons.lang.Validate;
//florent : change observation 
//import org.apache.lenya.cms.publication.Document;
//import org.apache.lenya.cms.publication.DocumentIdentifier;

/**
 * Observation manager. Works as an observation registry and sends the notifications.
 */
public class ObservationManager extends AbstractLogEnabled implements ObservationRegistry {

    private Map identifier2listeners = new HashMap();
    private Set listeners = new HashSet();

    public synchronized void registerListener(RepositoryListener listener, Object observeable)
            throws ObservationException {
        //florent
    	/*Document doc = (Document) observeable;
        Set listeners = getListeners(doc.getIdentifier());
        if (listeners.contains(listener)) {
            throw new ObservationException("The listener [" + listener
                    + "] is already registered for the document [" + doc + "].");
        }
        listeners.add(listener);*/
    }
    
   //florent : change observation management
    /*
   protected Set getListeners(DocumentIdentifier doc) {
        Set listeners = (Set) this.identifier2listeners.get(doc);
        if (listeners == null) {
            listeners = new HashSet();
            this.identifier2listeners.put(doc, listeners);
        }
        return listeners;
    }*/

    public synchronized void registerListener(RepositoryListener listener)
            throws ObservationException {
        if (this.listeners.contains(listener)) {
            throw new ObservationException("The listener [" + listener + "] is already registered.");
        }
        this.listeners.add(listener);
    }

    	/*
    protected Set getAllListeners(DocumentIdentifier doc) {
        Set allListeners = new HashSet();
        synchronized (this) {
            allListeners.addAll(this.listeners);
            allListeners.addAll(getListeners(doc));
        }
        return allListeners;
    }*/

    protected void notify(Set listeners, RepositoryEvent event) {
        for (Iterator i = listeners.iterator(); i.hasNext();) {
            RepositoryListener listener = (RepositoryListener) i.next();
            listener.eventFired(event);
        }
    }

    public void eventFired(RepositoryEvent event) {
        Validate.notNull(event);
        Set listeners = this.listeners;
        Object source = event.getSource();
        if (source instanceof DocumentEventSource) {
        	//florent
        	/*
            DocumentIdentifier id = ((DocumentEventSource) source).getIdentifier();
            listeners = getAllListeners(id);
            */
        }
        notify(listeners, event);
    }

}
