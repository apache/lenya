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

/* $Id: CMSProjectCreator.java,v 1.12 2004/03/20 11:46:20 gregor Exp $  */

package org.oscom.lenya.cms;

import java.util.Map;

import org.apache.log4j.Category;

import org.w3c.dom.Document;

import org.apache.lenya.cms.authoring.DefaultLeafCreator;
import org.apache.lenya.xml.DOMUtil;

public class CMSProjectCreator extends DefaultLeafCreator {
    private static Category log = Category.getInstance(CMSProjectCreator.class);
	
	/**
	 *  (non-Javadoc)
	 * @see org.apache.lenya.cms.authoring.DefaultCreator#transformXML(org.w3c.dom.Document, java.lang.String, short, java.lang.String, java.util.Map)
	 */
    protected void transformXML (Document doc,
				 String childId, short childType, String childName,
				 Map parameters)
	throws Exception {

        DOMUtil du = new DOMUtil();
        du.setElementValue(doc, "/system/id", childId);
        du.setElementValue(doc, "/system/system_name", childName);
	
        log.debug("system_name = " +
		  du.getElementValue(doc.getDocumentElement(), 
				     new org.apache.lenya.xml.XPath("system_name")));
    }

}

