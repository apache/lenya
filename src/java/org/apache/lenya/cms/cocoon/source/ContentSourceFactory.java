/*
 * Copyright  1999-2005 The Apache Software Foundation
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
package org.apache.lenya.cms.cocoon.source;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Map;

import org.apache.avalon.framework.logger.AbstractLogEnabled;
import org.apache.avalon.framework.service.ServiceException;
import org.apache.avalon.framework.service.ServiceManager;
import org.apache.avalon.framework.service.Serviceable;
import org.apache.excalibur.source.Source;
import org.apache.excalibur.source.SourceFactory;
import org.apache.lenya.cms.repo.Area;
import org.apache.lenya.cms.repo.Asset;
import org.apache.lenya.cms.repo.Translation;
import org.apache.lenya.cms.repo.Publication;
import org.apache.lenya.cms.repo.Repository;
import org.apache.lenya.cms.repo.Session;
import org.apache.lenya.cms.repo.SiteNode;
import org.apache.lenya.cms.repo.avalon.RepositoryFactory;

/**
 * Repository source factory.
 */
public class ContentSourceFactory extends AbstractLogEnabled implements SourceFactory, Serviceable {

    protected static final String SCHEME = "content";

    public Source getSource(String location, Map parameters) throws IOException,
            MalformedURLException {

        RepositoryFactory factory = null;
        Source source = null;

        try {
            if (this.repository == null) {
                factory = (RepositoryFactory) this.manager.lookup(RepositoryFactory.ROLE);
                this.repository = factory.getRepository();
            }

            Session session = this.repository.createSession();

            String[] parts = location.split(":");

            if (parts.length != 2) {
                throw new MalformedURLException("The URL must be of the form [content:/locator]!");
            }

            final String scheme = parts[0];
            if (!scheme.equals(SCHEME)) {
                throw new MalformedURLException("The scheme must be [" + SCHEME + "]!");
            }

            final String locator = parts[1];
            if (locator.startsWith("//")) {
                String docIdentifier = locator.substring("//".length());
                String[] steps = docIdentifier.split("/");
                String pubId = steps[0];
                String areaId = steps[1];
                String language = steps[2];
                String prefix = "//" + pubId + "/" + areaId + "/" + language;
                String path = docIdentifier.substring(prefix.length());
                Publication pub = session.getPublication(pubId);
                Area area = pub.getArea(areaId);
                SiteNode siteNode = area.getSite().getNode(path);
                Asset contentNode = siteNode.getAsset();
                Translation document = contentNode.getTranslation(language);
                source = new ContentSource(document, getLogger());
            } else if (locator.startsWith("/")) {
                throw new MalformedURLException("Only absolute locations are supported!");
            } else {
                throw new MalformedURLException("The locator must start with either one or two slashes.");
            }

        } catch (MalformedURLException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            if (factory != null) {
                this.manager.release(factory);
            }
        }

        return source;
    }

    public void release(Source source) {
    }

    private ServiceManager manager;
    private Repository repository;

    public void service(ServiceManager manager) throws ServiceException {
        this.manager = manager;
    }

}
