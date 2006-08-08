package org.apache.lenya.cms.janitor;

import java.io.File;

import org.apache.avalon.framework.activity.Startable;
import org.apache.avalon.framework.logger.AbstractLogEnabled;
import org.apache.avalon.framework.service.ServiceException;
import org.apache.avalon.framework.service.ServiceManager;
import org.apache.avalon.framework.service.Serviceable;
import org.apache.avalon.framework.thread.ThreadSafe;
import org.apache.lenya.cms.cocoon.source.SourceUtil;
import org.apache.lenya.cms.observation.ObservationRegistry;
import org.apache.lenya.cms.observation.RepositoryEvent;
import org.apache.lenya.cms.observation.RepositoryListener;
import org.apache.lenya.cms.publication.Document;
import org.apache.lenya.cms.publication.Publication;
import org.apache.lenya.cms.publication.PublicationException;
import org.apache.lenya.cms.publication.PublicationUtil;

/**
 * The content janitor cleans up empty directories after a document is removed.
 */
public class ContentJanitor extends AbstractLogEnabled implements Serviceable, Startable,
        ThreadSafe, RepositoryListener {

    public void documentChanged(RepositoryEvent event) {
    }

    public void documentRemoved(RepositoryEvent event) {
        try {
            Publication pub = PublicationUtil
                    .getPublication(this.manager, event.getPublicationId());
            File contentFile = pub.getContentDirectory(event.getArea());
            String contentUri = contentFile.toURI().toString();
            SourceUtil.deleteEmptyCollections(contentUri, this.manager);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void start() throws Exception {
        ObservationRegistry registry = null;
        try {
            registry = (ObservationRegistry) this.manager.lookup(ObservationRegistry.ROLE);
            registry.registerListener(this);
        } finally {
            if (registry != null) {
                this.manager.release(registry);
            }
        }
    }

    public void stop() throws Exception {
    }

    private ServiceManager manager;

    public void service(ServiceManager manager) throws ServiceException {
        this.manager = manager;
    }

}
