/*
 <License>
 </License>
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
 * @author <a href="andreas@apache.org">Andreas Hartmann </a>
 * @version $Id: UnitOfWork.java,v 1.2 2004/06/28 20:25:32 andreas Exp $
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
                publication = PublicationFactory.getPublication(objectModel);
            } catch (PublicationException e) {
                throw new RuntimeException(e);
            }
            if (getLogger().isDebugEnabled()) {
                getLogger().debug("Initializing unit of work for publication [" + publication.getId() + "]");
            }
            
            this.identityMap = new DocumentIdentityMap(publication);
        }
        
        return identityMap;
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