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
package org.apache.lenya.cms.repo.adapter;

import org.apache.avalon.framework.activity.Initializable;
import org.apache.avalon.framework.logger.AbstractLogEnabled;
import org.apache.avalon.framework.service.ServiceException;
import org.apache.avalon.framework.service.ServiceManager;
import org.apache.avalon.framework.service.Serviceable;
import org.apache.lenya.cms.repo.Area;
import org.apache.lenya.cms.repo.Asset;
import org.apache.lenya.cms.repo.AssetTypeResolver;
import org.apache.lenya.cms.repo.Publication;
import org.apache.lenya.cms.repo.Repository;
import org.apache.lenya.cms.repo.RepositoryException;
import org.apache.lenya.cms.repo.SiteNode;
import org.apache.lenya.cms.repo.Translation;
import org.apache.lenya.cms.repo.avalon.RepositoryFactory;
import org.apache.lenya.cms.repository.Node;
import org.apache.lenya.cms.repository.NodeFactory;
import org.apache.lenya.cms.repository.Session;
import org.apache.lenya.cms.repository.SourceNode;
import org.apache.lenya.transaction.Identifiable;
import org.apache.lenya.transaction.IdentityMap;

/**
 * Repository node factory.
 */
public class RepoNodeFactory extends AbstractLogEnabled implements NodeFactory, Serviceable,
        Initializable {

    private Session oldSession;

    public void setSession(Session session) {
        this.oldSession = session;
    }

    public Identifiable build(IdentityMap map, String key) throws Exception {

        String url = key;
        String path = url.substring("lenya://lenya/pubs/".length());
        String pubId = path.split("/")[0];

        path = path.substring((pubId + "/content/").length());
        String areaId = path.split("/")[0];

        String docPath = path.substring(areaId.length());

        int lastSlashIndex = docPath.lastIndexOf("/");
        int underscoreIndex = docPath.lastIndexOf("_");

        if (underscoreIndex == -1 || lastSlashIndex > underscoreIndex) {
            return new SourceNode(this.oldSession, key, this.manager, getLogger());
        } else {

            try {
                String language = docPath.substring(underscoreIndex + 1, underscoreIndex + 3);

                docPath = docPath.substring(0, docPath.length() - "/index_de.xml".length());

                Publication pub = this.session.getPublication(pubId);
                Area area = pub.getArea(areaId);

                Translation trans = null;

                SiteNode siteNode = area.getSite().getNode(docPath);
                if (siteNode != null) {
                    Asset asset = siteNode.getAsset();
                    if (asset != null) {
                        trans = asset.getTranslation(language);
                    }
                }
                return new RepoNode(trans, getLogger());
            } catch (RepositoryException e) {
                throw new RepositoryException("Error resolving translation for URL [" + key + "]: "
                        + e.getMessage(), e);
            }
        }
    }

    public String getType() {
        return Node.IDENTIFIABLE_TYPE;
    }

    private ServiceManager manager;
    private org.apache.lenya.cms.repo.Session session;

    public void service(ServiceManager manager) throws ServiceException {
        this.manager = manager;
    }

    public void initialize() throws Exception {
        RepositoryFactory factory = null;
        try {
            factory = (RepositoryFactory) manager.lookup(RepositoryFactory.ROLE);
            Repository repository = factory.getRepository();
            AssetTypeResolver resolver = new ResourceTypeWrapperResolver(this.manager);
            repository.setAssetTypeResolver(resolver);
            this.session = repository.createSession();
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            if (factory != null) {
                manager.release(factory);
            }
        }
    }

}
