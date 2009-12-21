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
package org.apache.lenya.cms.site.usecases;

import java.util.Arrays;

import org.apache.lenya.cms.publication.Document;
import org.apache.lenya.cms.publication.DocumentBuilder;
import org.apache.lenya.cms.publication.DocumentException;
import org.apache.lenya.cms.publication.DocumentLocator;
import org.apache.lenya.cms.publication.Publication;
import org.apache.lenya.cms.publication.PublicationException;
import org.apache.lenya.cms.site.SiteStructure;

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
            setParameter(LANGUAGE,parent.getLanguage());
            try {
                setParameter(PARENT_PATH, parent.getPath());
            } catch (DocumentException e) {
                throw new RuntimeException(e);
            }
        }

        String[] languages = getPublication().getLanguages();
        if (languages.length == 0) {
            addErrorMessage("The publication doesn't contain any languages!");
        }
        setParameter(LANGUAGES, languages);

        Document sourceDoc = getSourceDocument();
        String[] childOnly = { RELATION_CHILD };
        String[] childAndAfter = { RELATION_CHILD, RELATION_AFTER };
        String[] relations = sourceDoc == null ? childOnly : childAndAfter;
        
        setParameter(RELATIONS, relations);
        setParameter(RELATION, RELATION_CHILD);

        String path = getParameterAsString(PATH);
        boolean provided = path != null && !path.equals("");
        setParameter(PATH_PROVIDED, Boolean.valueOf(provided));
    }

    protected void doCheckPreconditions() throws Exception {
        super.doCheckPreconditions();
        if (getParameterAsBoolean(PATH_PROVIDED, false)) {
            validateProvidedPath();
        }
    }

    protected void validateProvidedPath() throws PublicationException {
        String path = getParameterAsString(PATH);
        Publication pub = getPublication();
        SiteStructure site = pub.getArea(getArea()).getSite();
        if (site.contains(path)) {
            addErrorMessage("path-already-exists");
        }
        else if (path.length() <= 2 || !path.startsWith("/") || path.endsWith("/")) {
            addErrorMessage("invalid-path");
        }
        else {
            String[] steps = path.substring(1).split("/");
            DocumentBuilder builder = pub.getDocumentBuilder();
            for (int i = 0; i < steps.length; i++) {
                if (!builder.isValidDocumentName(steps[i])) {
                    addErrorMessage("node-name-special-characters");
                }
            }
            if (steps.length > 1) {
                String parentPath = path.substring(0, path.lastIndexOf("/"));
                if (!site.contains(parentPath)) {
                    addErrorMessage("parent-does-not-exist");
                }
            }
        }
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

        String relation = getParameterAsString(RELATION);

        if (!Arrays.asList(getSupportedRelations()).contains(relation)) {
            addErrorMessage("The relation '" + relation + "' is not supported.");
        }

        if (getParameterAsBoolean(PATH_PROVIDED, false)) {
            validateProvidedPath();
        } else {
            String nodeName = getNodeName();
            if (nodeName.equals("")) {
                addErrorMessage("missing-node-name");
            } else if (!getPublication().getDocumentBuilder().isValidDocumentName(nodeName)) {
                addErrorMessage("node-name-special-characters");
            }
        }
    }

    protected String getNodeName() {
        return getParameterAsString(NODE_NAME).trim();
    }

    protected boolean isPathValid() {
        String nodeName = getNewDocumentName();
        DocumentBuilder builder = getPublication().getDocumentBuilder();
        return !nodeName.trim().equals("") && builder.isValidDocumentName(nodeName);
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
            nodeName = getNodeName();
        }
        return nodeName;
    }

    /**
     * @return The relation between the source document and the created
     *         document.
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
            Document sourceDoc = getSourceDocument();
            if (sourceDoc == null) {
                return "/" + getNewDocumentName();
            } else {
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
