/*
$Id: InsertCopyNode.java,v 1.4 2003/07/25 16:38:53 edith Exp $
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
package org.apache.lenya.cms.ant;

import org.apache.lenya.cms.publication.DefaultSiteTree;
import org.apache.lenya.cms.publication.SiteTreeException;
import org.apache.lenya.cms.publication.SiteTreeNode;

import org.xml.sax.SAXException;

import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;


/**
 * Ant task that copies a node of a tree and inserts it in  tree
 * @author edith
 *
 **/
public class InsertCopyNode extends TwoNodesTask {
    /**
     * Creates a new instance of InsertCopyNode
     */
    public InsertCopyNode() {
        super();
    }

	/**
	 * copies a node corresponding to a document with id firstdocumentid
	 * and inserts it in the same tree like a node corresponding to a document with id secdocumentid.
	 * @param firstdocumentid The document-id of the document correponding to the source node.
	 * @param secdocumentid  The ment-id of the document corresponding to the destination node.
	 * @param absolutetreepath The absolute path of the tree of the node.
	 * @throws SiteTreeException if there are problems with creating or saving the site tree.  
	 */
	private void manipulateTree(String firstdocumentid, String secdocumentid, String absolutetreepath) throws SiteTreeException {
		DefaultSiteTree tree = null;

		try {
			tree = new DefaultSiteTree(absolutetreepath);

			SiteTreeNode node = tree.getNode(firstdocumentid);

			if (node != null) {
				tree.addNode(secdocumentid, node.getLabels(), node.getHref(), node.getSuffix(),
					node.hasLink());
			} else {
				throw new SiteTreeException("Node " + node + " couldn't be found");
			}

				tree.save();
			} catch (ParserConfigurationException e) {
				throw new SiteTreeException("Exception when creating the site tree", e);
			} catch (SAXException e) {
				throw new SiteTreeException("Exception when creating the site tree", e);
			} catch (IOException e) {
				throw new SiteTreeException("Exception when saving the tree file", e);
			} catch (TransformerException e) {
				throw new SiteTreeException("Exception when saving the tree file", e);
			}
	}
    /**
     * copies a node corresponding to a document with id firstdocumentid
     * and inserts it like a node orresponding to a document with id secdocumentid.
     * @param firstdocumentid The document-id of the document correponding to the source node.
     * @param secdocumentid  The ment-id of the document corresponding to the destination node.
     * @param absolutefirsttreepath The absolute path of the tree of the src node.
     * @param absolutesectreepath The absolute path of the tree of the destination node.
     * @throws SiteTreeException if there are problems with creating or saving the site tree.  
     */
    public void manipulateTree(String firstdocumentid, String secdocumentid, String absolutefirsttreepath, String absolutesectreepath) throws SiteTreeException {
        if (absolutefirsttreepath.equals(absolutesectreepath)) {
			manipulateTree(firstdocumentid, secdocumentid, absolutefirsttreepath);
            return;
        }
        DefaultSiteTree firsttree = null;
		DefaultSiteTree sectree = null;

        try {
            firsttree = new DefaultSiteTree(absolutefirsttreepath);
			sectree = new DefaultSiteTree(absolutesectreepath);

            SiteTreeNode node = firsttree.getNode(firstdocumentid);

            if (node != null) {
                sectree.addNode(secdocumentid, node.getLabels(), node.getHref(), node.getSuffix(),
                    node.hasLink());
            } else {
                throw new SiteTreeException("Node " + node + " couldn't be found");
            }

                sectree.save();
			} catch (ParserConfigurationException e) {
				throw new SiteTreeException("Exception when creating the site tree", e);
			} catch (SAXException e) {
				throw new SiteTreeException("Exception when creating the site tree", e);
			} catch (IOException e) {
				throw new SiteTreeException("Exception when saving the tree file", e);
            } catch (TransformerException e) {
				throw new SiteTreeException("Exception when saving the tree file", e);
            }
    }
}
