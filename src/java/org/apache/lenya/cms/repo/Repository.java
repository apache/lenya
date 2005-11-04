package org.apache.lenya.cms.repo;

public interface Repository {

    Session createSession() throws RepositoryException;
    
}
