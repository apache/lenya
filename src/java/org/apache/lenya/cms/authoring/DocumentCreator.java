/*
$Id: DocumentCreator.java,v 1.5 2003/08/07 16:48:05 egli Exp $
<License>

 ============================================================================
                   The Apache Software License, Version 1.1
 ============================================================================

 Copyright (C) 1999-2003 The Apache Software Foundation. All rights reserved.

 Redistribution and use in source and binary forms, with or without modifica-
 tion, are permitted provided that the following conditions are met:

 1. Redistributions of  source code must  retain the above copyright  notice,
    this list of conditions and the following disclaimer.

 2. Redistributions in binary form must reproduce the above copyright notice,
    this list of conditions and the following disclaimer in the documentation
    and/or other materials provided with the distribution.

 3. The end-user documentation included with the redistribution, if any, must
    include  the following  acknowledgment:  "This product includes  software
    developed  by the  Apache Software Foundation  (http://www.apache.org/)."
    Alternately, this  acknowledgment may  appear in the software itself,  if
    and wherever such third-party acknowledgments normally appear.

 4. The names "Apache Lenya" and  "Apache Software Foundation"  must  not  be
    used to  endorse or promote  products derived from  this software without
    prior written permission. For written permission, please contact
    apache@apache.org.

 5. Products  derived from this software may not  be called "Apache", nor may
    "Apache" appear  in their name,  without prior written permission  of the
    Apache Software Foundation.

 THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED WARRANTIES,
 INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 FITNESS  FOR A PARTICULAR  PURPOSE ARE  DISCLAIMED.  IN NO  EVENT SHALL  THE
 APACHE SOFTWARE  FOUNDATION  OR ITS CONTRIBUTORS  BE LIABLE FOR  ANY DIRECT,
 INDIRECT, INCIDENTAL, SPECIAL,  EXEMPLARY, OR CONSEQUENTIAL  DAMAGES (INCLU-
 DING, BUT NOT LIMITED TO, PROCUREMENT  OF SUBSTITUTE GOODS OR SERVICES; LOSS
 OF USE, DATA, OR  PROFITS; OR BUSINESS  INTERRUPTION)  HOWEVER CAUSED AND ON
 ANY  THEORY OF LIABILITY,  WHETHER  IN CONTRACT,  STRICT LIABILITY,  OR TORT
 (INCLUDING  NEGLIGENCE OR  OTHERWISE) ARISING IN  ANY WAY OUT OF THE  USE OF
 THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

 This software  consists of voluntary contributions made  by many individuals
 on  behalf of the Apache Software  Foundation and was  originally created by
 Michael Wechner <michi@apache.org>. For more information on the Apache Soft-
 ware Foundation, please see <http://www.apache.org/>.

 Lenya includes software developed by the Apache Software Foundation, W3C,
 DOM4J Project, BitfluxEditor, Xopus, and WebSHPINX.
</License>
*/
package org.apache.lenya.cms.authoring;

import org.apache.lenya.cms.publication.DefaultSiteTree;
import org.apache.lenya.cms.publication.DocumentType;
import org.apache.lenya.cms.publication.DocumentTypeBuildException;
import org.apache.lenya.cms.publication.DocumentTypeBuilder;
import org.apache.lenya.cms.publication.Label;
import org.apache.lenya.cms.publication.Publication;

import java.io.File;

import java.util.Collections;

/**
 * @author andreas
 *
 */
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
