package org.apache.lenya.cms.jcr;

import org.apache.lenya.cms.repo.RepositoryException;

/**
 * Abstract node wrapper builder.
 */
public abstract class AbstractNodeWrapperBuilder implements NodeWrapperBuilder {

    protected abstract NodeWrapper getNodeInternal(JCRSession session, BuilderParameters parameters)
            throws RepositoryException;

    public NodeWrapper getNode(JCRSession session, BuilderParameters parameters)
            throws RepositoryException {
        NodeWrapper node = getNodeInternal(session, parameters);
        if (node == null) {
            throw new RepositoryException("The node does not exist.");
        }
        return node;
    }
    
    public boolean existsNode(JCRSession session, BuilderParameters parameters) throws RepositoryException {
        return getNodeInternal(session, parameters) != null;
    }

}
