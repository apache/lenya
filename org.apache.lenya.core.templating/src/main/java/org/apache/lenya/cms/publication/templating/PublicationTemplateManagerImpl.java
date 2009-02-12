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

package org.apache.lenya.cms.publication.templating;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.cocoon.spring.configurator.WebAppContextUtils;
import org.apache.cocoon.util.AbstractLogEnabled;
import org.apache.commons.logging.Log;
import org.apache.excalibur.source.SourceResolver;
import org.apache.lenya.cms.publication.Publication;
import org.springframework.web.context.WebApplicationContext;

/**
 * Manager for publication templates.
 * 
 * @version $Id$
 */
public class PublicationTemplateManagerImpl extends AbstractLogEnabled implements
        PublicationTemplateManager {

    private SourceResolver sourceResolver;

    /**
     * @see org.apache.lenya.cms.publication.templating.PublicationTemplateManager#visit(org.apache.lenya.cms.publication.Publication,
     *      java.lang.String, org.apache.lenya.cms.publication.templating.SourceVisitor)
     */
    public void visit(Publication publication, String path, SourceVisitor visitor) {

        try {

            String[] baseUris = getBaseURIs(publication);
            for (int i = 0; i < baseUris.length; i++) {
                String uri = baseUris[i] + "/" + path;

                if (getLogger().isDebugEnabled()) {
                    getLogger().debug("Trying to resolve URI [" + uri + "]");
                }

                visitor.visit(this.sourceResolver, uri);
            }

        } catch (Exception e) {
            throw new TemplatingException("Visiting path [" + path + "] failed: ", e);
        }

    }

    /**
     * Returns the publication.
     * @return A publication. protected Publication getPublication1() { return this.publication; }
     */

    /**
     * Returns the base URIs in traversing order.
     * @param publication The original publication.
     * @return An array of strings.
     */
    protected String[] getBaseURIs(Publication publication) {

        List uris = new ArrayList();

        Publication[] publications = getPublications(publication);
        for (int i = 0; i < publications.length; i++) {
            uris.add(getBaseURI(publications[i]));
        }

        String coreBaseURI = publication.getPubBaseUri() + "/";
        uris.add(coreBaseURI);

        return (String[]) uris.toArray(new String[uris.size()]);
    }

    /**
     * Returns the base URI for a certain publication.
     * @param publication The publication.
     * @return A string.
     */
    public static String getBaseURI(Publication publication) {
        return publication.getSourceUri();
    }

    /**
     * @see org.apache.lenya.cms.publication.templating.PublicationTemplateManager#visit(org.apache.lenya.cms.publication.Publication,
     *      org.apache.lenya.cms.publication.templating.PublicationVisitor)
     */
    public void visit(Publication publication, PublicationVisitor visitor) {
        try {
            Publication[] publications = getPublications(publication);
            for (int i = 0; i < publications.length; i++) {
                if (getLogger().isDebugEnabled()) {
                    getLogger().debug("Visiting publication [" + publications[i] + "]");
                }
                visitor.visit(publications[i]);
            }
        } catch (Exception e) {
            throw new TemplatingException("Visiting publications failed: ", e);
        }
    }

    /**
     * Returns the publications in traversing order.
     * @param publication The original publication.
     * @return An array of strings.
     */
    protected Publication[] getPublications(Publication publication) {

        List publications = new ArrayList();

        publications.add(publication);

        String templateId = publication.getTemplateId();
        if (templateId != null) {
            Publication template = publication.getSession().getPublication(templateId);
            Publication[] templateTemplates = getPublications(template);
            publications.addAll(Arrays.asList(templateTemplates));
        }

        return (Publication[]) publications.toArray(new Publication[publications.size()]);
    }

    public Object getSelectableHint(Publication publication, String role, final String originalHint) {
        Object selectableHint = null;

        try {
            ExistingServiceVisitor resolver = new ExistingServiceVisitor(role, originalHint,
                    getLogger());
            visit(publication, resolver);
            selectableHint = resolver.getSelectableHint();
            if (selectableHint == null) {
                selectableHint = originalHint;
            }

        } catch (Exception e) {
            String message = "Resolving hint [" + originalHint + "] failed: ";
            getLogger().error(message, e);
            throw new RuntimeException(message, e);
        }
        return selectableHint;
    }

    public void setSourceResolver(SourceResolver sourceResolver) {
        this.sourceResolver = sourceResolver;
    }

    /**
     * Searches for a declared service of the form "publicationId/service".
     */
    public static class ExistingServiceVisitor implements PublicationVisitor {

        /**
         * Ctor.
         * @param selector The service selector to use.
         * @param hint The hint to check.
         * @param logger The logger.
         */
        public ExistingServiceVisitor(String role, Object hint, Log logger) {
            this.hint = hint;
            this.logger = logger;
            this.role = role;
        }

        private Object hint;
        private Object selectableHint = null;
        private Log logger;
        private String role;

        /**
         * @see org.apache.lenya.cms.publication.templating.PublicationVisitor#visit(org.apache.lenya.cms.publication.Publication)
         */
        public void visit(Publication publication) {
            String publicationHint = publication.getId() + "/" + this.hint;
            boolean success = false;
            WebApplicationContext context = WebAppContextUtils.getCurrentWebApplicationContext();
            if (context.containsBean(this.role + "/" + publicationHint)) {
                this.selectableHint = publicationHint;
                success = true;
            }
            if (this.logger.isDebugEnabled()) {
                this.logger.debug("Checking hint [" + publicationHint + "]: " + success);
            }
        }

        /**
         * @return The publication hint that could be selected or <code>null</code> if no hint could
         *         be selected.
         */
        public Object getSelectableHint() {
            return this.selectableHint;
        }

    }

}
