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
package org.apache.lenya.cms.publication.usecases;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;

import org.apache.avalon.framework.configuration.DefaultConfiguration;
import org.apache.avalon.framework.configuration.DefaultConfigurationBuilder;
import org.apache.avalon.framework.configuration.DefaultConfigurationSerializer;
import org.apache.excalibur.source.ModifiableSource;
import org.apache.excalibur.source.Source;
import org.apache.excalibur.source.SourceResolver;
import org.apache.excalibur.source.SourceUtil;
import org.apache.lenya.cms.publication.Publication;
import org.apache.lenya.cms.publication.PublicationException;
import org.apache.lenya.cms.publication.PublicationFactory;
import org.apache.lenya.cms.publication.PublicationImpl;
import org.apache.lenya.cms.usecase.AbstractUsecase;
import org.apache.lenya.xml.DocumentHelper;
import org.apache.lenya.xml.NamespaceHelper;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Create a new publication based on a template publication.
 * 
 * @version $Id:$
 */
public class CreatePublicationFromTemplate extends AbstractUsecase {

    protected static final String AVAILABLE_TEMPLATES = "availableTemplates";
    protected static final String PUBLICATION_ID = "publicationId";
    protected static final String PUBLICATION_NAME = "publicationName";
    protected static final String TEMPLATE = "template";

    /**
     * @see org.apache.lenya.cms.usecase.AbstractUsecase#initParameters()
     */
    protected void initParameters() {
        super.initParameters();

        PublicationFactory factory = PublicationFactory.getInstance(getLogger());
        try {
            Publication[] pubs = factory.getPublications(this.manager);
            List templates = new ArrayList();
            for (int i = 0; i < pubs.length; i++) {
                if (pubs[i].supportsTemplating()) {
                    templates.add(pubs[i].getId());
                }
            }
            setParameter(AVAILABLE_TEMPLATES, templates);

            setParameter(PUBLICATION_NAME, "New Publication");

        } catch (PublicationException e) {
            throw new RuntimeException(e);
        }

    }

    /**
     * @see org.apache.lenya.cms.usecase.AbstractUsecase#doCheckExecutionConditions()
     */
    protected void doCheckExecutionConditions() throws Exception {
        super.doCheckExecutionConditions();

        String publicationId = getParameterAsString(PUBLICATION_ID);

        if (publicationId.trim().equals("")) {
            addErrorMessage("Please enter a publication ID!");
        } else {
            PublicationFactory factory = PublicationFactory.getInstance(getLogger());
            Publication publication = factory.getPublication(this.manager, publicationId);
            if (publication.exists()) {
                addErrorMessage("A publication with this ID already exists.");
            }
        }
    }

    /**
     * @see org.apache.lenya.cms.usecase.AbstractUsecase#doExecute()
     */
    protected void doExecute() throws Exception {
        super.doExecute();

        String templateId = getParameterAsString(TEMPLATE);

        SourceResolver resolver = null;
        Source contextSource = null;
        try {
            resolver = (SourceResolver) this.manager.lookup(SourceResolver.ROLE);
            Source context = resolver.resolveURI("context://");
            String contextPath = SourceUtil.getFile(context).getAbsolutePath();
            PublicationFactory factory = PublicationFactory.getInstance(getLogger());

            Publication template = factory.getPublication(templateId, contextPath);
            createPublication(template, getParameterAsString(PUBLICATION_ID), resolver);

        } finally {
            if (resolver != null) {
                if (contextSource != null) {
                    resolver.release(contextSource);
                }
                this.manager.release(resolver);
            }
        }

    }

    protected static final String[] sourcesToCopy = { "publication.xml",
            "config/publication.xconf", "config/ac/passwd/visit.rml",
            "config/ac/passwd/reviewer.gml", "config/ac/passwd/review.rml",
            "config/ac/passwd/localhost.ipml", "config/ac/passwd/lenya.iml",
            "config/ac/passwd/ldap.properties.sample", "config/ac/passwd/editor.gml",
            "config/ac/passwd/edit.rml", "config/ac/passwd/alice.iml",
            "config/ac/passwd/admin.rml", "config/ac/passwd/admin.gml", "config/ac/ac.xconf",
            "config/doctypes/doctypes.xconf", "config/workflow/workflow.xml",
            "content/authoring/sitetree.xml", "content/authoring/index/index_en.xml" };

    /**
     * Creates a publication from a template.
     * @param template The template.
     * @param publicationId The ID of the new publication.
     * @param resolver The source resolver to use.
     * @throws Exception if an error occurs.
     */
    protected void createPublication(Publication template, String publicationId,
            SourceResolver resolver) throws Exception {

        Source publicationsSource = null;
        ModifiableSource metaSource = null;
        ModifiableSource configSource = null;
        try {

            publicationsSource = resolver.resolveURI("context://"
                    + PublicationImpl.PUBLICATION_PREFIX_URI);
            String publicationsUri = publicationsSource.getURI();

            for (int i = 0; i < sourcesToCopy.length; i++) {

                String source = sourcesToCopy[i];
                copySource(template, publicationId, resolver, publicationsUri, source);
            }

            metaSource = (ModifiableSource) resolver.resolveURI(publicationsUri + "/"
                    + publicationId + "/publication.xml");
            Document metaDoc = DocumentHelper.readDocument(metaSource.getInputStream());
            NamespaceHelper helper = new NamespaceHelper(
                    "http://apache.org/cocoon/lenya/publication/1.0", "lenya", metaDoc);
            Element nameElement = helper.getFirstChild(metaDoc.getDocumentElement(), "name");
            String name = getParameterAsString(PUBLICATION_NAME);
            DocumentHelper.setSimpleElementText(nameElement, name);

            save(metaDoc, metaSource);

            configSource = (ModifiableSource) resolver.resolveURI(publicationsUri + "/"
                    + publicationId + "/config/publication.xconf");
            DefaultConfiguration config = (DefaultConfiguration) new DefaultConfigurationBuilder()
                    .build(configSource.getInputStream());
            DefaultConfiguration templatesConfig = new DefaultConfiguration("templates");
            DefaultConfiguration templateConfig = new DefaultConfiguration("template");
            templateConfig.setAttribute("id", template.getId());
            templatesConfig.addChild(templateConfig);
            config.addChild(templatesConfig);
            OutputStream oStream = configSource.getOutputStream();
            new DefaultConfigurationSerializer().serialize(oStream, config);
            if (oStream != null) {
                oStream.flush();
                try {
                    oStream.close();
                } catch (Throwable t) {
                    if (getLogger().isDebugEnabled()) {
                        getLogger().debug("Exception closing output stream: ", t);
                    }
                    throw new RuntimeException("Could not write document: ", t);
                }
            }

        } finally {
            if (publicationsSource != null) {
                resolver.release(publicationsSource);
            }
            if (metaSource != null) {
                resolver.release(metaSource);
            }
            if (configSource != null) {
                resolver.release(configSource);
            }
        }

    }

    protected void copySource(Publication template, String publicationId, SourceResolver resolver,
            String publicationsUri, String source) throws MalformedURLException, IOException {
        Source templateSource = null;
        ModifiableSource targetSource = null;
        try {
            templateSource = resolver.resolveURI(publicationsUri + "/" + template.getId() + "/"
                    + source);
            targetSource = (ModifiableSource) resolver.resolveURI(publicationsUri + "/"
                    + publicationId + "/" + source);

            org.apache.lenya.cms.cocoon.source.SourceUtil.copy(templateSource, targetSource, false);
        } finally {
            if (templateSource != null) {
                resolver.release(templateSource);
            }
            if (targetSource != null) {
                resolver.release(targetSource);
            }
        }
    }

    protected void save(Document metaDoc, ModifiableSource metaSource) throws IOException,
            TransformerConfigurationException, TransformerException {
        OutputStream oStream = metaSource.getOutputStream();
        DocumentHelper.writeDocument(metaDoc, new OutputStreamWriter(oStream));
        if (oStream != null) {
            oStream.flush();
            try {
                oStream.close();
            } catch (Throwable t) {
                if (getLogger().isDebugEnabled()) {
                    getLogger().debug("Exception closing output stream: ", t);
                }
                throw new RuntimeException("Could not write document: ", t);
            }
        }
    }
}