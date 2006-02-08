package org.apache.lenya.cms.jcr.jackrabbit;

import java.util.Arrays;

import javax.jcr.Repository;
import javax.jcr.Session;
import javax.jcr.SimpleCredentials;

import org.apache.jackrabbit.core.RepositoryImpl;
import org.apache.jackrabbit.core.WorkspaceImpl;
import org.apache.lenya.cms.jcr.JCRRepository;
import org.apache.lenya.cms.repo.RepositoryException;

/**
 * Jackrabbit-based repository.
 */
public class JackrabbitRepository extends JCRRepository {

    /**
     * Ctor.
     * @param repository The JCR repository.
     */
    public JackrabbitRepository(Repository repository) {
        super(repository);
    }

    public void shutdown() throws RepositoryException {
        if (this.internalSession != null) {
            this.internalSession.logout();
        }
        ((RepositoryImpl) getRepository()).shutdown();
    }

    protected Session getSession(String workspaceName) throws RepositoryException {
        try {
            Session defaultWorkspaceSession = getRepository().login(new SimpleCredentials("john",
                    "".toCharArray()));
            WorkspaceImpl defaultWorkspace = (WorkspaceImpl) defaultWorkspaceSession.getWorkspace();
            String[] workspaces = defaultWorkspace.getAccessibleWorkspaceNames();
            if (!Arrays.asList(workspaces).contains(workspaceName)) {
                defaultWorkspace.createWorkspace(workspaceName);
                // create = true;
            }

            return getRepository().login(new SimpleCredentials("john", "".toCharArray()),
                    workspaceName);
        } catch (javax.jcr.RepositoryException e) {
            throw new RepositoryException(e);
        }
    }

}
