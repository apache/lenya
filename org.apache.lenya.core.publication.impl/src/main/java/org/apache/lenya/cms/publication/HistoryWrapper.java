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
package org.apache.lenya.cms.publication;

import org.apache.lenya.cms.repository.RepositoryException;
import org.apache.lenya.cms.repository.History;
import org.apache.lenya.cms.repository.Revision;

public class HistoryWrapper implements History {

    private org.apache.lenya.cms.repository.History delegate;

    public HistoryWrapper(org.apache.lenya.cms.repository.History delegate) {
        this.delegate = delegate;
    }

    public Revision getLatestRevision() {
        //florent : don't know where this come...
    	//return new RevisionWrapper(this.delegate.getLatestRevision());
    	return this.delegate.getLatestRevision();
    }

    public Revision getRevision(int number)
            //florent throws org.apache.lenya.cms.publication.RepositoryException {
    throws RepositoryException {
        try {
        //florent : don't know where this come...
        	//return new RevisionWrapper(this.delegate.getLatestRevision());
            return this.delegate.getRevision(number);
        } catch (RepositoryException e) {
            //florent throw new org.apache.lenya.cms.publication.RepositoryException(e);
        	throw new RepositoryException(e);
        }
    }

    public int[] getRevisionNumbers() {
        return this.delegate.getRevisionNumbers();
    }

}
