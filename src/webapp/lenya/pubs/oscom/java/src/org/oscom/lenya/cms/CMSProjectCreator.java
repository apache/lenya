/*
 * $Id: CMSProjectCreator.java,v 1.4 2003/02/27 15:59:34 egli Exp $
 * <License>
 * The Apache Software License
 *
 * Copyright (c) 2002 wyona. All rights reserved.
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
 *    by wyona (http://www.wyona.org)"
 *
 * 4. The name "wyona" must not be used to endorse or promote products derived from
 *    this software without prior written permission. For written permission, please
 *    contact contact@wyona.org
 *
 * 5. Products derived from this software may not be called "wyona" nor may "wyona"
 *    appear in their names without prior written permission of wyona.
 *
 * 6. Redistributions of any form whatsoever must retain the following acknowledgment:
 *    "This product includes software developed by wyona (http://www.wyona.org)"
 *
 * THIS SOFTWARE IS PROVIDED BY wyona "AS IS" WITHOUT ANY WARRANTY EXPRESS OR IMPLIED,
 * INCLUDING THE WARRANTY OF NON-INFRINGEMENT AND THE IMPLIED WARRANTIES OF MERCHANTI-
 * BILITY AND FITNESS FOR A PARTICULAR PURPOSE. wyona WILL NOT BE LIABLE FOR ANY DAMAGES
 * SUFFERED BY YOU AS A RESULT OF USING THIS SOFTWARE. IN NO EVENT WILL wyona BE LIABLE
 * FOR ANY SPECIAL, INDIRECT OR CONSEQUENTIAL DAMAGES OR LOST PROFITS EVEN IF wyona HAS
 * BEEN ADVISED OF THE POSSIBILITY OF THEIR OCCURRENCE. wyona WILL NOT BE LIABLE FOR ANY
 * THIRD PARTY CLAIMS AGAINST YOU.
 *
 * Wyona includes software developed by the Apache Software Foundation, W3C,
 * DOM4J Project, BitfluxEditor and Xopus.
 * </License>
 */
package org.oscom.wyona.cms;

import java.util.Map;

import org.apache.log4j.Category;

import org.w3c.dom.Document;

import org.wyona.cms.authoring.DefaultLeafCreator;
import org.wyona.xml.DOMUtil;

/**
 * DOCUMENT ME!
 *
 * @author Michael Wechner
 * @version 2003.2.11
 */
public class CMSProjectCreator extends DefaultLeafCreator {
    static Category log = Category.getInstance(CMSProjectCreator.class);

    public CMSProjectCreator() {
	sampleResourceName = "CMSProject.xml";
    }

    protected void transformXML (Document doc,
				 String childId, short childType, String childName,
				 Map parameters)
	throws Exception {

        DOMUtil du = new DOMUtil();
        du.setElementValue(doc, "/system/id", childId);
        du.setElementValue(doc, "/system/system_name", childName);
	
        log.debug("system_name = " +
		  du.getElementValue(doc.getDocumentElement(), 
				     new org.wyona.xml.XPath("system_name")));
    }

}

