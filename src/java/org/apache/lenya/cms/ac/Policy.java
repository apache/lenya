/*
 * $Id: Policy.java,v 1.7 2003/03/06 13:10:56 andreas Exp $
 * <License>
 * The Apache Software License
 *
 * Copyright (c) 2002 lenya. All rights reserved.
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
 *    includes software developed by lenya (http://www.lenya.org)"
 *
 * 4. The name "lenya" must not be used to endorse or promote products
 *    derived from this software without prior written permission. For
 *    written permission, please contact contact@lenya.org
 *
 * 5. Products derived from this software may not be called "lenya" nor
 *    may "lenya" appear in their names without prior written permission
 *    of lenya.
 *
 * 6. Redistributions of any form whatsoever must retain the following
 *    acknowledgment: "This product includes software developed by lenya
 *    (http://www.lenya.org)"
 *
 * THIS SOFTWARE IS PROVIDED BY lenya "AS IS" WITHOUT ANY WARRANTY EXPRESS
 * OR IMPLIED, INCLUDING THE WARRANTY OF NON-INFRINGEMENT AND THE IMPLIED
 * WARRANTIES OF MERCHANTIBILITY AND FITNESS FOR A PARTICULAR PURPOSE.
 * lenya WILL NOT BE LIABLE FOR ANY DAMAGES SUFFERED BY YOU AS A RESULT
 * OF USING THIS SOFTWARE. IN NO EVENT WILL lenya BE LIABLE FOR ANY SPECIAL,
 * INDIRECT OR CONSEQUENTIAL DAMAGES OR LOST PROFITS EVEN IF lenya HAS BEEN
 * ADVISED OF THE POSSIBILITY OF THEIR OCCURRENCE. lenya WILL NOT BE LIABLE
 * FOR ANY THIRD PARTY CLAIMS AGAINST YOU.
 *
 * Wyona includes software developed by the Apache Software Foundation, W3C,
 * DOM4J Project, BitfluxEditor and Xopus.
 * </License>
 */
package org.lenya.cms.ac;

import org.apache.avalon.framework.logger.Logger;

import org.apache.xpath.XPathAPI;

import org.w3c.dom.Document;
import org.w3c.dom.Node;


/**
 * DOCUMENT ME!
 *
 * @author Michael Wechner
 * @version 1.12.28
 */
public class Policy {
    private static String ROOT = "ac/policy/object[@match=\"/\"]";
    private static String SUBJECTS = "subjects";
    private static String ACTIONS = "actions";
    private Document doc = null;
    private Logger logger = null;

    /**
     * Creates a new Policy object.
     *
     * @param doc DOCUMENT ME!
     * @param logger DOCUMENT ME!
     */
    public Policy(Document doc, Logger logger) {
        this.doc = doc;
        this.logger = logger;
    }

    /**
     * DOCUMENT ME!
     *
     * @param action DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public boolean authorizeWorld(String action) {
        String xpath = "/" + ROOT + "/" + SUBJECTS + "/world/" + ACTIONS + "/" + action;

        return checkXPath(xpath);
    }

    /**
     * DOCUMENT ME!
     *
     * @param action DOCUMENT ME!
     * @param ip DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public boolean authorizeMachine(String action, String ip) {
        String xpath = "/" + ROOT + "/" + SUBJECTS + "/machine[@ip='" + ip + "']/" + ACTIONS + "/" +
            action;

        return checkXPath(xpath);
    }

    /**
     * DOCUMENT ME!
     *
     * @param action DOCUMENT ME!
     * @param id DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public boolean authorizeUser(String action, String id) {
        String xpath = "/" + ROOT + "/" + SUBJECTS + "/user[@id='" + id + "']/" + ACTIONS + "/" +
            action;

        return checkXPath(xpath);
    }

    /**
     * DOCUMENT ME!
     *
     * @param action DOCUMENT ME!
     * @param id DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public boolean authorizeGroup(String action, String id) {
        String xpath = "/" + ROOT + "/" + SUBJECTS + "/group[@id='" + id + "']/" + ACTIONS + "/" +
            action;

        return checkXPath(xpath);
    }

    private boolean checkXPath(String xpath) {
        try {
            Node node = XPathAPI.selectSingleNode(doc, xpath);

            if (node != null) {
                logger.debug("XPath exists: " + xpath);

                return true;
            }
        } catch (Exception e) {
            logger.error("" + e);
        }

        logger.debug("No such XPath: " + xpath);

        return false;
    }
}
