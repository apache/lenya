/*
 * Copyright  1999-2004 The Apache Software Foundation
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
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
package org.apache.lenya.cms.usecase;

import java.util.Map;

import org.apache.avalon.framework.context.Context;
import org.apache.avalon.framework.context.ContextException;
import org.apache.avalon.framework.context.Contextualizable;
import org.apache.avalon.framework.logger.AbstractLogEnabled;
import org.apache.cocoon.components.ContextHelper;
import org.apache.lenya.cms.publication.DocumentIdentityMap;
import org.apache.lenya.cms.publication.Publication;
import org.apache.lenya.cms.publication.PublicationException;
import org.apache.lenya.cms.publication.PublicationFactory;

/**
 * Abstract base class for operations on documents.
 * 
 * @version $Id$
 */
public class UnitOfWorkImpl extends AbstractLogEnabled implements UnitOfWork, Contextualizable {

    /**
     * Ctor.
     */
    public UnitOfWorkImpl() {
    }

    private DocumentIdentityMap identityMap;

    /**
     * Returns the document identity map.
     * @return A document identity map.
     */
    public DocumentIdentityMap getIdentityMap() {
        
        if (this.identityMap == null) {
            Map objectModel = ContextHelper.getObjectModel(this.context);
            Publication publication;
            try {
                PublicationFactory factory = PublicationFactory.getInstance(getLogger());
                publication = factory.getPublication(objectModel);
            } catch (PublicationException e) {
                throw new RuntimeException(e);
            }
            if (getLogger().isDebugEnabled()) {
                getLogger().debug("Initializing unit of work for publication [" + publication.getId() + "]");
            }
            
            this.identityMap = new DocumentIdentityMap(publication);
        }
        
        return this.identityMap;
    }

    /**
     * @see org.apache.avalon.framework.context.Contextualizable#contextualize(org.apache.avalon.framework.context.Context)
     */
    public void contextualize(Context context)
            throws ContextException {
        this.context = context;
    }

    /** The environment context */
    private Context context;

}