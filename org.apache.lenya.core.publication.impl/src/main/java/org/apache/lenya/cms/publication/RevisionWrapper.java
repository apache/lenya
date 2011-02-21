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

import java.io.InputStream;

import org.apache.lenya.cms.metadata.MetaData;
import org.apache.lenya.cms.metadata.MetaDataException;
import org.apache.lenya.cms.repository.RepositoryException;
import org.apache.lenya.cms.repository.Revision;

public class RevisionWrapper implements Revision {
    
    private org.apache.lenya.cms.repository.Revision delegate;

    public RevisionWrapper(org.apache.lenya.cms.repository.Revision delegate) {
        this.delegate = delegate;
    }

    public int getNumber() {
       return this.delegate.getNumber();
    }

    public long getTime() {
        return this.delegate.getTime();
    }

    public String getUserId() {
        return this.delegate.getUserId();
    }
    
    /*** begin unimplemented method */
    //florent : this method are added unimplemented due to the use of repository.Revision and not still of publication.Revision
		public long getLastModified() throws RepositoryException {
			// TODO Auto-generated method stub
			return 0;
		}

		public long getContentLength() throws RepositoryException {
			// TODO Auto-generated method stub
			return 0;
		}

		public String getSourceURI() {
			// TODO Auto-generated method stub
			return null;
		}

		public boolean exists() throws RepositoryException {
			// TODO Auto-generated method stub
			return false;
		}

		public InputStream getInputStream() throws RepositoryException {
			// TODO Auto-generated method stub
			return null;
		}

		public String getMimeType() throws RepositoryException {
			// TODO Auto-generated method stub
			return null;
		}

		public MetaData getMetaData(String namespaceUri) throws MetaDataException {
			// TODO Auto-generated method stub
			return null;
		}

		public String[] getMetaDataNamespaceUris() throws MetaDataException {
			// TODO Auto-generated method stub
			return null;
		}

}
