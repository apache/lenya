/*
 * $Id: DeleteSchedulerEntryTask.java,v 1.1 2004/01/09 11:14:39 andreas Exp $ <License>
 * 
 * ============================================================================ The Apache Software
 * License, Version 1.1
 * ============================================================================
 * 
 * Copyright (C) 1999-2003 The Apache Software Foundation. All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without modifica- tion, are permitted
 * provided that the following conditions are met: 1. Redistributions of source code must retain
 * the above copyright notice, this list of conditions and the following disclaimer. 2.
 * Redistributions in binary form must reproduce the above copyright notice, this list of
 * conditions and the following disclaimer in the documentation and/or other materials provided
 * with the distribution. 3. The end-user documentation included with the redistribution, if any,
 * must include the following acknowledgment: "This product includes software developed by the
 * Apache Software Foundation (http://www.apache.org/)." Alternately, this acknowledgment may
 * appear in the software itself, if and wherever such third-party acknowledgments normally appear. 4.
 * The names "Apache Lenya" and "Apache Software Foundation" must not be used to endorse or promote
 * products derived from this software without prior written permission. For written permission,
 * please contact apache@apache.org. 5. Products derived from this software may not be called
 * "Apache", nor may "Apache" appear in their name, without prior written permission of the Apache
 * Software Foundation.
 * 
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR ITS CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLU- DING, BUT
 * NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR
 * BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT,
 * STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 * 
 * This software consists of voluntary contributions made by many individuals on behalf of the
 * Apache Software Foundation and was originally created by Michael Wechner <michi@apache.org> .
 * For more information on the Apache Soft- ware Foundation, please see <http://www.apache.org/> .
 * 
 * Lenya includes software developed by the Apache Software Foundation, W3C, DOM4J Project,
 * BitfluxEditor, Xopus, and WebSHPINX. </License>
 */
package org.apache.lenya.cms.ant;

import java.io.File;

import org.apache.lenya.cms.publication.Document;
import org.apache.lenya.cms.publication.DocumentBuilder;
import org.apache.lenya.cms.publication.DocumentException;
import org.apache.lenya.cms.publication.Label;
import org.apache.lenya.cms.publication.Publication;
import org.apache.lenya.cms.publication.SiteTree;
import org.apache.lenya.cms.publication.SiteTreeNode;
import org.apache.lenya.cms.publication.SiteTreeNodeVisitor;
import org.apache.lenya.cms.scheduler.LoadQuartzServlet;
import org.apache.tools.ant.BuildException;

/**
 * Moves the scheduler entry for a document.
 * 
 * @author <a href="andreas@apache.org">Andreas Hartmann</a>
 */
public class DeleteSchedulerEntryTask extends PublicationTask implements SiteTreeNodeVisitor {

    /**
     * @see org.apache.tools.ant.Task#execute()
     */
    public void execute() throws BuildException {
        try {
            log("Document ID: [" + documentId + "]");
            log("Area:        [" + area + "]");

            Publication publication = getPublication();
            SiteTree tree = publication.getSiteTree(area);
            SiteTreeNode node = tree.getNode(documentId);

            node.acceptSubtree(this);
        } catch (Exception e) {
            throw new BuildException(e);
        }
    }

    private String area, documentId, servletContextPath;

    /**
     * @param string The area.
     */
    public void setArea(String string) {
        area = string;
    }

    /**
     * @param string The document-id.
     */
    public void setDocumentId(String string) {
        documentId = string;
    }

    /**
     * Sets the servlet context path.
     * @param servletContextPath A string.
     */
    public void setServletContextPath(String servletContextPath) {
        this.servletContextPath = servletContextPath;
    }

    /**
     * @see org.apache.lenya.cms.publication.SiteTreeNodeVisitor#visitSiteTreeNode(org.apache.lenya.cms.publication.SiteTreeNode)
     */
    public void visitSiteTreeNode(SiteTreeNode node) throws DocumentException {
        Publication publication = getPublication();

        Label[] labels = node.getLabels();
        for (int i = 0; i < labels.length; i++) {

            String language = labels[i].getLanguage();
            DocumentBuilder builder = publication.getDocumentBuilder();

            try {
                String url = builder.buildCanonicalUrl(publication, area, documentId, language);
                Document document = builder.buildDocument(publication, url);

                String servletContext = new File(servletContextPath).getCanonicalPath();
                log("Deleting scheduler entry for document [" + document + "]");
                log("Resolving servlet [" + servletContext + "]");

                LoadQuartzServlet servlet = LoadQuartzServlet.getServlet(servletContext);
                servlet.deleteDocumentJobs(document);

            } catch (Exception e) {
                throw new DocumentException(e);
            }

        }
    }

}
