/*
 * $Id: DefaultDocumentIdToPathMapper.java,v 1.3 2003/06/25 14:32:35 andreas Exp $
 * <License>
 * The Apache Software License
 *
 * Copyright (c) 2003 Wyona. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. All advertising materials mentioning features or use of this
 *    software must display the following acknowledgment: "This product
 *    includes software developed by Wyona (http://www.wyona.com)"
 *
 * 4. The name "Lenya" must not be used to endorse or promote products
 *    derived from this software without prior written permission. For
 *    written permission, please contact contact@wyona.com
 *
 * 5. Products derived from this software may not be called "Lenya" nor
 *    may "Lenya" appear in their names without prior written permission
 *    of Wyona.
 *
 * 6. Redistributions of any form whatsoever must retain the following
 *    acknowledgment: "This product includes software developed by Wyona
 *    (http://www.wyona.com)"
 *
 * THIS SOFTWARE IS PROVIDED BY Wyona "AS IS" WITHOUT ANY WARRANTY EXPRESS
 * OR IMPLIED, INCLUDING THE WARRANTY OF NON-INFRINGEMENT AND THE IMPLIED
 * WARRANTIES OF MERCHANTIBILITY AND FITNESS FOR A PARTICULAR PURPOSE.
 * Wyona WILL NOT BE LIABLE FOR ANY DAMAGES SUFFERED BY YOU AS A RESULT
 * OF USING THIS SOFTWARE. IN NO EVENT WILL lenya BE LIABLE FOR ANY SPECIAL,
 * INDIRECT OR CONSEQUENTIAL DAMAGES OR LOST PROFITS EVEN IF Wyona HAS BEEN
 * ADVISED OF THE POSSIBILITY OF THEIR OCCURRENCE. Wyona WILL NOT BE LIABLE
 * FOR ANY THIRD PARTY CLAIMS AGAINST YOU.
 *
 * Lenya includes software developed by the Apache Software Foundation, W3C,
 * DOM4J Project, BitfluxEditor and Xopus.
 * </License>
 */

package org.apache.lenya.cms.publication;

import java.io.File;

/**
 * @author egli
 * 
 * 
 */
public class DefaultDocumentIdToPathMapper implements DocumentIdToPathMapper {

	/* (non-Javadoc)
	 * @see org.apache.lenya.cms.publication.DocumentIdToPathMapper#computeDocumentPath(java.lang.String)
	 */
	public String computeDocumentPath(
		Publication publication,
		String area,
		String documentId) {
		File path =
			new File(
				publication.getDirectory(),
				"content"
					+ File.separator
					+ area
					+ File.separator
					+ documentId
					+ File.separator
					+ "index.xml");
		return path.getAbsolutePath();
	}

    /* (non-Javadoc)
     * @see org.apache.lenya.cms.publication.DocumentIdToPathMapper#getFile(org.apache.lenya.cms.publication.Publication, java.lang.String, java.lang.String, java.lang.String)
     */
    public File getFile(
        Publication publication,
        String area,
        String documentId,
        String language) {
            
        String languageSuffix = "";
        if (language != null && !"".equals(language)) {
            languageSuffix = "_" + language;
        }
            
        File file =
            new File(
                publication.getDirectory(),
                "content"
                    + File.separator
                    + area
                    + File.separator
                    + documentId
                    + File.separator
                    + "index"
                    + languageSuffix
                    +".xml");
        return file;
    }

    /* (non-Javadoc)
     * @see org.apache.lenya.cms.publication.DocumentIdToPathMapper#getFiles(org.apache.lenya.cms.publication.Publication, java.lang.String, java.lang.String)
     */
    public File[] getFiles(
        Publication publication,
        String area,
        String documentId) {
        
        //SiteTree tree = new DefaultSiteTree()
        return null;
    }

}
