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

import org.apache.lenya.cms.repository.Session;
import org.apache.lenya.util.Assert;

/**
 * A repository event provides additional information if a document was added, changed, or removed.
 */
public class RepositoryEvent {

    private Session session;
    private Object descriptor = null;
    private int revision = -1;

    /**
     * Ctor.
     * @param session The session.
     * @param descriptor More information about the event.
     */
    public RepositoryEvent(Session session, Object descriptor) {
        
        Assert.notNull("session", session);
        this.session = session;
        
        Assert.notNull("descriptor", descriptor);
        this.descriptor = descriptor;
    }
    
    /**
     * @return The session.
     */
    public Session getSession() {
        return this.session;
    }

    /**
     * @return The descriptor.
     */
    public Object getDescriptor() {
        return this.descriptor;
    }
    
    public String toString() {
        return "user:" + getSession().getIdentity().getUser() + " " + getNodeUri() + " " + getDescriptor();
    }
    
    private String nodeUri;
    
    /**
     * @param nodeUri The source URI of the affected node.
     */
    public void setNodeUri(String nodeUri) {
        this.nodeUri = nodeUri;
    }
    
    /**
     * @return The source URI of the affected node.
     */
    public String getNodeUri() {
        return this.nodeUri;
    }

    /**
     * @param revision The latest revision of the node at the time the event was created.
     */
    public void setRevision(int revision) {
        this.revision = revision;
    }
    
    /**
     * @return The latest revision of the node at the time the event was created.
     */
    public int getRevision() {
        return this.revision;
    }

}
