/*
 * $Id
 * <License>
 * The Apache Software License
 *
 * Copyright (c) 2002 lenya. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice, this
 *    list of conditions and the following disclaimer in the documentation and/or
 *    other materials provided with the distribution.
 *
 * 3. All advertising materials mentioning features or use of this software must
 *    display the following acknowledgment: "This product includes software developed
 *    by lenya (http://www.lenya.org)"
 *
 * 4. The name "lenya" must not be used to endorse or promote products derived from
 *    this software without prior written permission. For written permission, please
 *    contact contact@lenya.org
 *
 * 5. Products derived from this software may not be called "lenya" nor may "lenya"
 *    appear in their names without prior written permission of lenya.
 *
 * 6. Redistributions of any form whatsoever must retain the following acknowledgment:
 *    "This product includes software developed by lenya (http://www.lenya.org)"
 *
 * THIS SOFTWARE IS PROVIDED BY lenya "AS IS" WITHOUT ANY WARRANTY EXPRESS OR IMPLIED,
 * INCLUDING THE WARRANTY OF NON-INFRINGEMENT AND THE IMPLIED WARRANTIES OF MERCHANTI-
 * BILITY AND FITNESS FOR A PARTICULAR PURPOSE. lenya WILL NOT BE LIABLE FOR ANY DAMAGES
 * SUFFERED BY YOU AS A RESULT OF USING THIS SOFTWARE. IN NO EVENT WILL lenya BE LIABLE
 * FOR ANY SPECIAL, INDIRECT OR CONSEQUENTIAL DAMAGES OR LOST PROFITS EVEN IF lenya HAS
 * BEEN ADVISED OF THE POSSIBILITY OF THEIR OCCURRENCE. lenya WILL NOT BE LIABLE FOR ANY
 * THIRD PARTY CLAIMS AGAINST YOU.
 *
 * Lenya includes software developed by the Apache Software Foundation, W3C,
 * DOM4J Project, BitfluxEditor and Xopus.
 * </License>
 */
package org.apache.lenya.cms.authoring;

import java.io.File;
import java.util.Collections;

import org.apache.lenya.cms.publication.DefaultSiteTree;
import org.apache.lenya.cms.publication.DocumentType;
import org.apache.lenya.cms.publication.DocumentTypeBuildException;
import org.apache.lenya.cms.publication.DocumentTypeBuilder;
import org.apache.lenya.cms.publication.Label;
import org.apache.lenya.cms.publication.Publication;

/**
 * @author andreas
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class DocumentCreator {

    public void create(
        Publication publication,
        File authoringDirectory,
        String treeFileName,
        String parentId,
        String childId,
        String childName,
        String childTypeString,
        String documentTypeName)
        throws CreatorException {

        short childType;

        if (childTypeString.equals("branch")) {
            childType = ParentChildCreatorInterface.BRANCH_NODE;
        } else if (childTypeString.equals("leaf")) {
            childType = ParentChildCreatorInterface.LEAF_NODE;
        } else {
            throw new CreatorException("No such child type: " + childTypeString);
        }

        if (!validate(parentId, childId, childName, childTypeString, documentTypeName)) {
            throw new CreatorException("Exception: Validation of parameters failed");
        }

        // Get creator
        DocumentType type;
        try {
            type = DocumentTypeBuilder.buildDocumentType(documentTypeName, publication);
        } catch (DocumentTypeBuildException e) {
            throw new CreatorException(e);
        }
        ParentChildCreatorInterface creator = type.getCreator();
        
        DefaultSiteTree siteTree;
        try {
            siteTree = new DefaultSiteTree(new File(authoringDirectory, treeFileName));
        } catch (Exception e) {
            throw new CreatorException(e);
        }
        Label[] labels = new Label[1];
        labels[0] = new Label(childName, null);
        try {
            siteTree.addNode(parentId, creator.generateTreeId(childId, childType), labels);
        } catch (Exception e) {
            throw new CreatorException(e);
        }
        
        File doctypesDirectory = new File(publication.getDirectory(), DocumentTypeBuilder.DOCTYPE_DIRECTORY);

        try {
            creator.create(
                new File(doctypesDirectory, "samples"),
                new File(authoringDirectory, parentId),
                childId,
                childType,
                childName,
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
    public boolean validate(String parentid,
                String childid, String childname, String childtype,
                String doctype) {
                    
        return
            childid.indexOf(" ") == -1 &&
            childid.length() > 0 &&
            childname.length() > 0;
    }
}
