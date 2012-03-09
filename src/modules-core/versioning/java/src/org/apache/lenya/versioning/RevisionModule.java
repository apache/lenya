package org.apache.lenya.versioning;

import java.util.Map;

import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.avalon.framework.service.ServiceException;
import org.apache.avalon.framework.service.ServiceManager;
import org.apache.avalon.framework.service.Serviceable;
import org.apache.cocoon.components.modules.input.AbstractInputModule;
import org.apache.cocoon.environment.ObjectModelHelper;
import org.apache.cocoon.environment.Request;
import org.apache.lenya.cms.publication.Document;
import org.apache.lenya.cms.publication.DocumentFactory;
import org.apache.lenya.cms.publication.DocumentUtil;
import org.apache.lenya.cms.repository.RepositoryUtil;
import org.apache.lenya.cms.repository.Session;
import org.apache.lenya.util.ServletHelper;

public class RevisionModule extends AbstractInputModule implements Serviceable {

    private ServiceManager manager;

    @Override
    public Object getAttribute(String name, Configuration modeConf, Map objectModel)
            throws ConfigurationException {
        if ("current".equals(name)) {
            final Request request = ObjectModelHelper.getRequest(objectModel);
            return Integer.toString(getRevision(request));
        }
        throw new IllegalArgumentException("Unknow attribute: " + name);
    }

    protected int getRevision(final Request request) {
        final String webappUrl = ServletHelper.getWebappURI(request);
        try {
            final Session session = RepositoryUtil.getSession(this.manager, request);
            final DocumentFactory docFactory = DocumentUtil.createDocumentFactory(this.manager,
                    session);
            final Document document = docFactory.getFromURL(webappUrl);
            return document.getRepositoryNode().getHistory().getLatestRevision().getNumber();
        } catch (Exception e) {
            throw new RuntimeException("Error getting document from webapp URL [" + webappUrl
                    + "]: " + e.getMessage(), e);
        }
    }

    public void service(ServiceManager mgr) throws ServiceException {
        this.manager = mgr;
    }

}
