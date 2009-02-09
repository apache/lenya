package org.apache.lenya.cms.repository;

import java.io.InputStream;

import org.apache.lenya.cms.metadata.MetaDataOwner;

/**
 * Super interface for nodes and revisions.
 */
public interface ContentHolder extends MetaDataOwner {

    /**
     * @return The last modification date. The date is measured in milliseconds
     *         since the epoch (00:00:00 GMT, January 1, 1970), and is 0 if it's
     *         unknown.
     * @throws RepositoryException if the node does not exist.
     */
    long getLastModified() throws RepositoryException;

    /**
     * @return The content length.
     * @throws RepositoryException if the node does not exist.
     */
    long getContentLength() throws RepositoryException;

    /**
     * Accessor for the source URI of this node
     * @return the source URI
     */
    String getSourceURI();

    /**
     * @return if the item exists.
     * @throws RepositoryException if an error occurs.
     */
    boolean exists() throws RepositoryException;

    /**
     * @return The input stream.
     * @throws RepositoryException if the node does not exist.
     */
    InputStream getInputStream() throws RepositoryException;

    /**
     * @return The MIME type.
     * @throws RepositoryException if the node does not exist.
     */
    String getMimeType() throws RepositoryException;

}
