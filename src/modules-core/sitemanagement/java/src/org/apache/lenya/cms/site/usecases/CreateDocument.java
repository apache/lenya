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

import java.util.Arrays;

import org.apache.avalon.framework.service.ServiceSelector;
import org.apache.lenya.cms.publication.Document;
import org.apache.lenya.cms.publication.DocumentBuilder;
import org.apache.lenya.cms.publication.DocumentException;
import org.apache.lenya.cms.publication.DocumentLocator;
import org.apache.lenya.cms.publication.Publication;

/**
 * Usecase to create a document.
 * 
 * @version $Id$
 */
public class CreateDocument extends Create {

    protected static final String PARENT_PATH = "parentPath";

    protected static final String DOCUMENT_TYPE = "doctype";

    protected static final String RELATION = "relation";
    protected static final String RELATIONS = "relations";
    protected static final String RELATION_CHILD = "child";
    protected static final String RELATION_BEFORE = "sibling before";
    protected static final String RELATION_AFTER = "sibling after";
    protected static final String PATH_PROVIDED = "pathProvided";

    /**
     * @see org.apache.lenya.cms.usecase.AbstractUsecase#initParameters()
     */
    protected void initParameters() {
        super.initParameters();

        Document parent = getSourceDocument();
        if (parent == null) {
            setParameter(PARENT_PATH, "");
        } else {
            try {
                setParameter(PARENT_PATH, parent.getPath());
            } catch (DocumentException e) {
                throw new RuntimeException(e);
            }
        }

        String[] languages = getPublication().getLanguages();
        if (languages.length == 0){
            addErrorMessage("The publication doesn't contain any languages!");
        }
        setParameter(LANGUAGES, languages);
        
        String[] relations = { RELATION_CHILD, RELATION_AFTER };
        setParameter(RELATIONS, relations);
        setParameter(RELATION, RELATION_CHILD);

        String path = getParameterAsString(PATH);
        boolean provided = path != null && !path.equals("");
        setParameter(PATH_PROVIDED, Boolean.valueOf(provided));
    }

    /**
     * Override this method to support other relations.
     * @return The supported relations.
     */
    protected String[] getSupportedRelations() {
        return new String[] { RELATION_CHILD, RELATION_AFTER };
    }

    /**
     * @see org.apache.lenya.cms.usecase.AbstractUsecase#doCheckExecutionConditions()
     */
    protected void doCheckExecutionConditions() throws Exception {
        super.doCheckExecutionConditions();

        String nodeName = getParameterAsString(NODE_NAME);
        String relation = getParameterAsString(RELATION);

        if (!Arrays.asList(getSupportedRelations()).contains(relation)) {
            addErrorMessage("The relation '" + relation + "' is not supported.");
        }
        
        Publication pub = getPublication();

        ServiceSelector selector = null;
        DocumentBuilder builder = null;
        try {
            selector = (ServiceSelector) this.manager.lookup(DocumentBuilder.ROLE + "Selector");
            String hint = pub.getDocumentBuilderHint();
            builder = (DocumentBuilder) selector.select(hint);

            boolean provided = getParameterAsBoolean(PATH_PROVIDED, false);
            if (!provided && !builder.isValidDocumentName(nodeName)) {
                addErrorMessage("The document ID may not contain any special characters.");
            } else {
                String newPath = getNewDocumentPath();
                if (pub.getArea(getArea()).getSite().contains(newPath)) {
                    addErrorMessage("The document with path " + newPath + " already exists.");
                }
            }
        } finally {
            if (selector != null) {
                if (builder != null) {
                    selector.release(builder);
                }
                this.manager.release(selector);
            }
        }
    }

    /**
     * @see Create#getNewDocumentName()
     */
    protected String getNewDocumentName() {
        String nodeName;
        if (getParameterAsBoolean(PATH_PROVIDED, false)) {
            final String path = getParameterAsString(PATH);
            nodeName = path.substring(path.lastIndexOf("/") + 1);
        } else {
            nodeName = getParameterAsString(NODE_NAME);
        }
        return nodeName;
    }

    /**
     * @return The relation between the source document and the created document.
     */
    protected String getRelation() {
        return getParameterAsString(RELATION);
    }

    /**
     * @see Create#getNewDocumentPath()
     */
    protected String getNewDocumentPath() {
        if (getParameterAsBoolean(PATH_PROVIDED, false)) {
            return getParameterAsString(PATH);
        } else {
            String relation = getRelation();
            DocumentLocator sourceLoc = getSourceDocument().getLocator();
            if (relation.equals(RELATION_CHILD)) {
                return sourceLoc.getChild(getNewDocumentName()).getPath();
            } else if (relation.equals(RELATION_BEFORE) || relation.equals(RELATION_AFTER)) {
                return sourceLoc.getParent().getChild(getNewDocumentName()).getPath();
            } else {
                throw new IllegalStateException("unsupported relation " + relation);
            }
        }
    }

    /**
     * @see org.apache.lenya.cms.site.usecases.Create#getDocumentTypeName()
     */
    protected String getDocumentTypeName() {
        return getParameterAsString(DOCUMENT_TYPE);
    }

    protected String getSourceExtension() {
        return "xml";
    }

    protected boolean createVersion() {
        return false;
    }

}
