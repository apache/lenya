/*
 * $Id: NewArticleCreator.java,v 1.8 2003/02/26 10:09:36 egli Exp $
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
package org.wyona.cms.pubs.forum;

import org.apache.log4j.Category;

import org.apache.avalon.framework.parameters.Parameters;

import org.w3c.dom.Document;

import org.wyona.cms.authoring.DefaultBranchCreator;

import org.wyona.xml.DOMUtil;

import org.wyona.util.DateUtil;

import java.io.File;

import java.util.Calendar;
import java.util.GregorianCalendar;


/**
 * DOCUMENT ME!
 *
 * @author Michael Wechner
 * @version 2002.11.10
 */
public class NewArticleCreator extends DefaultBranchCreator {
    static Category log = Category.getInstance(NewArticleCreator.class);

    public NewArticleCreator() {
	sampleResourceName = "Article.xml";
    }

    /**
     * DOCUMENT ME!
     *
     * @param childId DOCUMENT ME!
     * @param childType DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     *
     * @throws Exception DOCUMENT ME!
     */
    public String generateTreeId(String childId, short childType)
        throws Exception {
        long id = System.currentTimeMillis();

        return "" + id;
    }

    protected String getChildFileName(File parentDir, String childId) {
        return parentDir + File.pathSeparator + "articles"
	    + File.pathSeparator + childId
	    + File.pathSeparator + "index.xml";
    }

    protected void transformXML (Document doc, Parameters parameters)
	throws Exception {

        DOMUtil du = new DOMUtil();

        // Replace id
        du.setElementValue(doc, "/article/meta/id",
			   parameters.getParameter("id"));
	
        log.debug("system_name = " +
		  du.getElementValue(doc.getDocumentElement(), 
				     new org.wyona.xml.XPath("system_name")));

        // Replace editor
        du.setElementValue(doc, "/article/meta/editor",
			   parameters.getParameter("editor"));

        Calendar cal = new GregorianCalendar();

        // Replace year
        du.setElementValue(doc, "/article/meta/date/year",
			   Integer.toString(cal.get(Calendar.YEAR)));

        // Replace month 
        int month = cal.get(Calendar.MONTH) + 1;
        du.setElementValue(doc, "/article/meta/date/month",
			   Integer.toString(month));
        du.setAttributeValue(doc, "/article/meta/date/month/@name",
			     DateUtil.getMonthName(month));

        // Replace day 
        du.setElementValue(doc, "/article/meta/date/day",
			   Integer.toString(cal.get(Calendar.DAY_OF_MONTH)));
        du.setAttributeValue(doc, "/article/meta/date/day/@name",
			   DateUtil.getDayName(cal.get(Calendar.DAY_OF_WEEK)));

        // Replace hour 
        du.setElementValue(doc, "/article/meta/date/hour",
			   DateUtil.oneToTwoDigits(Integer.toString(cal.get(Calendar.HOUR_OF_DAY))));

        // Replace minute 
        du.setElementValue(doc, "/article/meta/date/minute",
			   DateUtil.oneToTwoDigits(Integer.toString(cal.get(Calendar.MINUTE))));
    }
}
