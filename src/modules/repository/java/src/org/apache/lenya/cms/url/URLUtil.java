/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 *  contributor license agreements.  See the NOTICE file distributed with
 *  this work for additional information regarding copyright ownership.
 *  The ASF licenses this file to You under the Apache License, Version 2.0
 *  (the "License"); you may not use this file except in compliance with
 *  the License.  You may obtain a copy of the License at
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
package org.apache.lenya.cms.url;

import org.apache.avalon.framework.logger.Logger;
import org.apache.avalon.framework.service.ServiceManager;
import org.apache.cocoon.environment.Request;
import org.apache.lenya.cms.publication.URLInformation;
import org.apache.lenya.cms.repo.Area;
import org.apache.lenya.cms.repo.Publication;
import org.apache.lenya.cms.repo.RepositoryException;
import org.apache.lenya.cms.repo.Session;
import org.apache.lenya.cms.repo.Translation;
import org.apache.lenya.cms.repo.cocoon.SessionUtil;
import org.apache.lenya.cms.repo.metadata.MetaData;
import org.apache.lenya.cms.repo.metadata.MetaDataRegistry;
import org.apache.lenya.cms.url.impl.LanguageSuffixMapper;
import org.apache.lenya.util.ServletHelper;

/**
 * URL mapping utility class.
 */
public class URLUtil {

    /**
     * @param manager The service manager.
     * @param request The request.
     * @param logger The logger.
     * @return A publication.
     * @throws RepositoryException if an error occurs.
     */
    public static Publication getPublication(ServiceManager manager, Request request, Logger logger)
            throws RepositoryException {
        Session session = SessionUtil.getSession(manager);
        String url = ServletHelper.getWebappURI(request);
        String pubId = new URLInformation(url).getPublicationId();
        return session.getPublication(pubId);
    }

    /**
     * @param session The session.
     * @param webappUrl The web application URL.
     * @param logger The logger.
     * @return A translation or <code>null</code> if the request doesn't point to a translation.
     * @throws RepositoryException if an error occurs.
     */
    public static Translation getTranslation(Session session, String webappUrl, Logger logger)
            throws RepositoryException {

        String pubId = webappUrl.substring(1).split("/")[0];
        Publication pub = session.getPublication(pubId);

        String prefix = "/" + pub.getPublicationId() + "/";
        final String pubUrl = webappUrl.substring(prefix.length());

        String areaId = pubUrl.split("/")[0];

        if (!pub.existsArea(areaId)) {
            throw new RepositoryException("The area [" + areaId + "] does not exist!");
        }

        Area area = pub.getArea(areaId);

        String translationUrl = pubUrl.substring(areaId.length());

        URLMapper mapper = getURLMapper(pub);
        return mapper.getTranslation(area, translationUrl);
    }

    /**
     * Returns the URL of a translation.
     * @param pub The publication.
     * @param trans The translation.
     * @param logger The logger.
     * @return A string.
     * @throws RepositoryException if an error occurs.
     */
    public static String getWebappURL(Publication pub, Translation trans, Logger logger)
            throws RepositoryException {
        URLMapper mapper = getURLMapper(pub);
        Area area = trans.getAsset().getContent().getArea();
        String transUrl = mapper.getURL(trans);
        if (transUrl == null) {
            throw new RepositoryException("The translation URL must not be null!");
        }
        return "/" + pub.getPublicationId() + "/" + area.getAreaID() + transUrl;

    }

    /**
     * @param pub The publication.
     * @return An URL mapper.
     * @throws RepositoryException if an error occurs.
     */
    public static URLMapper getURLMapper(Publication pub) throws RepositoryException {

        MetaDataRegistry registry = pub.getSession().getRepository().getMetaDataRegistry();
        if (!registry.isRegistered(URLMapperElements.ELEMENT_SET)) {
            registry.register(URLMapperElements.ELEMENT_SET, URLMapperElements.ELEMENTS);
        }

        MetaData metaData = pub.getMetaData(URLMapperElements.ELEMENT_SET);
        String className = metaData.getValue(URLMapperElements.URL_MAPPER);
        if (className == null) {
            className = LanguageSuffixMapper.class.getName();
            metaData.setValue(URLMapperElements.URL_MAPPER, className);
        }

        URLMapper mapper;
        try {
            Class klass = Class.forName(className);
            mapper = (URLMapper) klass.newInstance();
        } catch (Exception e) {
            throw new RepositoryException(e);
        }
        return mapper;
    }

}
