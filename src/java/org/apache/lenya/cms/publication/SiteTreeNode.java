/*
$Id: SiteTreeNode.java,v 1.13 2003/09/18 13:14:38 andreas Exp $
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
package org.apache.lenya.cms.publication;

/**
 * This interface is a wrapper around the more general w3c.Node which
 * hides some details which are irrelevant for site tree nodes. It basically
 * delegates everything to the Node.
 *
 * @author $Author: andreas $
 * @version $Revision: 1.13 $
 */
public interface SiteTreeNode {

    /**
     * Get the parent-id of this node.
     * 
     * @return the parent-id.
     */
    String getParentId();

    /**
     * Get the absolute parent-id of this node.
     * 
     * @return  the absolute parent-id.
     */
    String getAbsoluteParentId();

    /**
     * Get the id of this node.
     * 
     * @return the node id.
     */
    String getId();

    /**
     * Get all labels for this node (independent of their language attribute).
     * 
     * @return an <code>Array</code> of labels.
     */
    Label[] getLabels();

    /**
     * Get the label for a specific language.
     * 
     * @param xmlLanguage the language for which the label is requested.
     * 
     * @return a <code>Label</code> if there is one for the given language, 
     * null otherwise. 
     */
    Label getLabel(String xmlLanguage);

    /**
     * Add a label to this node iff the node does not have this label already.
     * 
     * @param label the label to be added.
     */
    void addLabel(Label label);

    /**
     * Remove a label from this node.
     * 
     * @param label the label to be removed.
     */
    void removeLabel(Label label);

    /**
     * Get the href of this node.
     * 
     * @return the href.
     */
    String getHref();

    /**
     * Get the suffix of this node.
     * 
     * @return the suffix.
     */
    String getSuffix();

    /**
     * Check whether this node has a link.
     * 
     * @return true if this node has a link.
     */
    boolean hasLink();

    /**
     * Get the sitetreenodes, which are children of this node
     * 
     * @return the children.
     */
    SiteTreeNode[] getChildren();

    /**
     * Get the sitetreenodes, which are children of this node
     * and contain a label for the given language.
     * 
     * @param language A language string.
     * @return the children.
     */
    SiteTreeNode[] getChildren(String language);

    /**
     * Remove the children of the node
     * 
     * @return the removed node
     */
    SiteTreeNode[] removeChildren();

	/**
	 * Get the sitetreenodes, which are the siblings following this node
	 * 
	 * @return the children.
	 */
	SiteTreeNode[] getNextSiblings();
    /**
     * Call the visit method of the visitor, that mean
     * the operation that shall be perfoemed on this node
     * (Visitor pattern)
     * @param visitor The visitor.
     * 
     * @throws DocumentException if an error occurs
     */
    void accept(SiteTreeNodeVisitor visitor) throws DocumentException;

    /**
     * Traverse the node ant its children and call the
     * accept method.
     * @param visitor The visitor.
     * 
     * @throws DocumentException if an error occurs
     */
    void acceptSubtree(SiteTreeNodeVisitor visitor) throws DocumentException;

    /**
     * Sets a label of an this node. If the label does not exist, it is added.
     * Otherwise, the existing label is replaced.
     * 
     * @param label the label to add
     */
    void setLabel(Label label);
}
