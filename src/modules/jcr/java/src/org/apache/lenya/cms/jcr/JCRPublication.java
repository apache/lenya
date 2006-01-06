/*
 * Copyright  1999-2004 The Apache Software Foundation
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
package org.apache.lenya.cms.jcr;

import java.io.File;

import org.apache.lenya.cms.repo.Area;
import org.apache.lenya.cms.repo.RepositoryException;
import org.apache.lenya.cms.repo.Session;
import org.apache.lenya.cms.repo.impl.AbstractPublication;

/**
 * JCR publication.
 */
public class JCRPublication extends AbstractPublication {

    private String pubId;
    private JCRSession session;

    /**
     * Ctor.
     * @param session The session.
     * @param pubId The publication ID.
     */
    public JCRPublication(JCRSession session, String pubId) {
        this.pubId = pubId;
        this.session = session;
    }

    public String getPublicationId() {
        return this.pubId;
    }

    public Session getSession() {
        return this.session;
    }

    protected JCRSession getJCRSession() {
        return this.session;
    }

    public Area getArea(String area) throws RepositoryException {
        return getJCRSession().getArea(this, area);
    }

    public Area addArea(String area) throws RepositoryException {
        return getJCRSession().addArea(this, area);
    }

    public boolean existsArea(String area) throws RepositoryException {
        return getJCRSession().existsArea(this, area);
    }

    public Area[] getAreas() throws RepositoryException {
        return getJCRSession().getAreas(this);
    }

    /**
     * @return The configuration file (publication.xconf).
     */
    public File getConfigurationFile() {
        JCRRepository repo;
        try {
            repo = (JCRRepository) getSession().getRepository();
        } catch (RepositoryException e) {
            throw new RuntimeException(e);
        }
        String pubPath = ("lenya/pubs/" + getPublicationId()).replace('/', File.separatorChar);
        File pubDir = new File(repo.getWebappDirectory(), pubPath);
        File configFile = new File(pubDir, CONFIGURATION_FILE);
        return configFile;
    }

}
