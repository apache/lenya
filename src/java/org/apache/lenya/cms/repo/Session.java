package org.apache.lenya.cms.repo;

public interface Session {

    Publication getPublication(String id) throws RepositoryException;
    
    public void save() throws RepositoryException;
    
}
