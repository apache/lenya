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

/* $Id: DocumentCreator.java,v 1.7 2004/03/03 12:56:32 gregor Exp $  */

package org.apache.lenya.cms.authoring;

import java.io.File;
import java.util.Collections;

import org.apache.lenya.cms.publication.DefaultSiteTree;
import org.apache.lenya.cms.publication.DocumentType;
import org.apache.lenya.cms.publication.DocumentTypeBuildException;
import org.apache.lenya.cms.publication.DocumentTypeBuilder;
import org.apache.lenya.cms.publication.Label;
import org.apache.lenya.cms.publication.Publication;

public class DocumentCreator {
    /**
     * DOCUMENT ME!
     *
     * @param publication DOCUMENT ME!
     * @param authoringDirectory DOCUMENT ME!
     * @param area the area
     * @param parentId DOCUMENT ME!
     * @param childId DOCUMENT ME!
     * @param childName DOCUMENT ME!
     * @param childTypeString DOCUMENT ME!
     * @param documentTypeName DOCUMENT ME!
     * @param language the language of the document to be created.
     *
     * @throws CreatorException DOCUMENT ME!
     */
    public void create(
        Publication publication,
        File authoringDirectory,
        String area,
        String parentId,
        String childId,
        String childName,
        String childTypeString,
        String documentTypeName,
        String language)
        throws CreatorException {
        short childType;

        if (childTypeString.equals("branch")) {
            childType = ParentChildCreatorInterface.BRANCH_NODE;
        } else if (childTypeString.equals("leaf")) {
            childType = ParentChildCreatorInterface.LEAF_NODE;
        } else {
            throw new CreatorException(
                "No such child type: " + childTypeString);
        }

        if (!validate(parentId,
            childId,
            childName,
            childTypeString,
            documentTypeName)) {
            throw new CreatorException("Exception: Validation of parameters failed");
        }

        // Get creator
        DocumentType type;

        try {
            type =
                DocumentTypeBuilder.buildDocumentType(
                    documentTypeName,
                    publication);
        } catch (DocumentTypeBuildException e) {
            throw new CreatorException(e);
        }

        ParentChildCreatorInterface creator = type.getCreator();

        DefaultSiteTree siteTree;

        try {
            siteTree = publication.getSiteTree(area);
        } catch (Exception e) {
            throw new CreatorException(e);
        }

        Label[] labels = new Label[1];
        labels[0] = new Label(childName, language);

        try {
            siteTree.addNode(
                parentId,
                creator.generateTreeId(childId, childType),
                labels);
        } catch (Exception e) {
            throw new CreatorException(e);
        }

        File doctypesDirectory =
            new File(
                publication.getDirectory(),
                DocumentTypeBuilder.DOCTYPE_DIRECTORY);

        try {
            creator.create(
                new File(doctypesDirectory, "samples"),
                new File(authoringDirectory, parentId),
                childId,
                childType,
                childName,
                language,
                Collections.EMPTY_MAP);
        } catch (Exception e) {
            throw new CreatorException(e);
        }

        // commit (sort of)
        try {
            siteTree.save();
        } catch (Exception e) {
            throw new CreatorException(e);
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param parentid DOCUMENT ME!
     * @param childid DOCUMENT ME!
     * @param childname DOCUMENT ME!
     * @param childtype DOCUMENT ME!
     * @param doctype DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public boolean validate(
        String parentid,
        String childid,
        String childname,
        String childtype,
        String doctype) {
        return (childid.indexOf(" ") == -1)
            && (childid.length() > 0)
            && (childname.length() > 0);
    }
}
