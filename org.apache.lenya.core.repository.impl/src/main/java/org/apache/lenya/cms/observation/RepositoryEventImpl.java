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

import org.apache.commons.lang.Validate;
import org.apache.lenya.cms.repository.Session;

/**
 * A repository event provides additional information if a document was added, changed, or removed.
 */
public class RepositoryEventImpl implements RepositoryEvent {

    private Session session;
    private Object descriptor = null;
    private int revision = -1;
    private Object source;

    /**
     * Ctor.
     * @param session The session.
     * @param source The source of the event.
     * @param descriptor More information about the event.
     */
    public RepositoryEventImpl(Session session, Object source, Object descriptor) {
        this(session, descriptor);
        Validate.notNull(source);
        this.source = source;
    }
    
    /**
     * Ctor.
     * @param session The session.
     * @param descriptor More information about the event.
     */
    public RepositoryEventImpl(Session session, Object descriptor) {
        Validate.notNull(session);
        Validate.notNull(descriptor);
        this.session = session;
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
    
    /* (non-Javadoc)
		 * @see org.apache.lenya.cms.observation.RepositoryEvent#toString()
		 */
    public String toString() {
        return "identity:" + getSession().getIdentity().toString() + " " + getNodeUri() + " " + getDescriptor();
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

    public Object getSource() {
        return source;
    }

}
