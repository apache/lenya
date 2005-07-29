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
package org.apache.lenya.cms.site.usecases;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.avalon.framework.logger.Logger;
import org.apache.avalon.framework.service.ServiceException;
import org.apache.avalon.framework.service.ServiceManager;
import org.apache.lenya.cms.publication.Document;
import org.apache.lenya.cms.publication.Resource;
import org.apache.lenya.cms.publication.ResourcesManager;

/**
 * Utility methods for asset handling.
 */
public final class AssetUtil {

    public static List getAssetNodes(Document document, ServiceManager manager, Logger logger)
            throws ServiceException {
        ResourcesManager resMgr = null;
        List nodes = new ArrayList();
        try {
            resMgr = (ResourcesManager) manager.lookup(ResourcesManager.ROLE);
            Resource[] resources = resMgr.getResources(document);
            for (int i = 0; i < resources.length; i++) {
                nodes.addAll(Arrays.asList(resources[i].getRepositoryNodes()));
            }
        } finally {
            if (resMgr != null) {
                manager.release(resMgr);
            }
        }
        return nodes;
    }

    public static List getCopiedAssetNodes(Document sourceDocument, Document targetDocument,
            ServiceManager manager, Logger logger) throws ServiceException {
        ResourcesManager resMgr = null;
        List nodes = new ArrayList();
        try {
            resMgr = (ResourcesManager) manager.lookup(ResourcesManager.ROLE);
            Resource[] resources = resMgr.getResources(sourceDocument);
            for (int i = 0; i < resources.length; i++) {
                Resource targetResource = new Resource(targetDocument, resources[i].getName(),
                        manager, logger);
                nodes.addAll(Arrays.asList(targetResource.getRepositoryNodes()));
            }
        } finally {
            if (resMgr != null) {
                manager.release(resMgr);
            }
        }
        return nodes;
    }

}
