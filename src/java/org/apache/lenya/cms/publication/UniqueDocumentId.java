/*
$Id: UniqueDocumentId.java,v 1.7 2003/08/25 17:43:46 edith Exp $
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

import org.apache.log4j.Category;


/**
 * class to compute an unique document id for a document, if there is
 * already a node in the sitetree for a document with this id. It will
 * documentid_"number of version"
 * @author edith
 *
 */
public class UniqueDocumentId {
    private static Category log = Category.getInstance(UniqueDocumentId.class);

    /** compute an unique document id
     * @param publication The publication the document belongs to.
	 * @param area The area the document belongs to.
     * @param documentid The documentid .
     * @return the unique documentid
     */
    public String computeUniqueDocumentId(Publication publication, String area, String documentid) {
		SiteTree tree;
		try {
			tree = publication.getSiteTree(area);
            SiteTreeNode node = tree.getNode(documentid);
            String suffix = null;
            int version = 0;
            String idwithoutsuffix = null;

            if (node != null) {
                int l = documentid.length();
                int index = documentid.lastIndexOf("-");

                if ((index < l) & (index > 0)) {
                    suffix = documentid.substring(index+1);
                    idwithoutsuffix = documentid.substring(0, index);
                    version = Integer.parseInt(suffix);
                } else {
                    idwithoutsuffix = documentid;
                }

                while (node != null) {
                    version = version + 1;
                    suffix = (new Integer(version)).toString();
                    documentid = idwithoutsuffix + "-" + suffix;
                    log.debug("version: " + version);
                    node = tree.getNode(documentid);
                }
            }
		} catch (SiteTreeException e) {
			e.printStackTrace();
		}

        return documentid;
    }
}
