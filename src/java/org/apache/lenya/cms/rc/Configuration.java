/*
 * $Id: Configuration.java,v 1.5 2003/02/17 12:55:18 egli Exp $
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
package org.wyona.cms.rc;

import org.apache.log4j.Category;

import java.util.Properties;


/**
 * DOCUMENT ME!
 *
 * @author Michael Wechner (http://www.wyona.com)
 * @version 0.10.5
 */
public class Configuration {
    static Category log = Category.getInstance(Configuration.class);
    public String rcmlDirectory = null;
    public String backupDirectory = null;

    public String mountPoints = null;
    public String password = null;
    public String servlet = null;
    public String xslt = null;
    public String xslt_rcmlrollback = null;

    /**
     * Creates a new Configuration object.
     */
    public Configuration() {
        String propertiesFileName = "conf.properties";
        Properties properties = new Properties();

        try {
            properties.load(Configuration.class.getResourceAsStream(propertiesFileName));
        } catch (Exception e) {
            log.fatal(": Failed to load properties from resource: " + propertiesFileName);
        }

        rcmlDirectory = properties.getProperty("rcmlDirectory");
        backupDirectory = properties.getProperty("backupDirectory");

        mountPoints = properties.getProperty("mountPoints");
        password = properties.getProperty("password");
        servlet = properties.getProperty("servlet");

        xslt = properties.getProperty("xslt");
        xslt_rcmlrollback = properties.getProperty("xslt_rcmlrollback");
    }

    /**
     * DOCUMENT ME!
     *
     * @param args DOCUMENT ME!
     */
    public static void main(String[] args) {
        Configuration conf = new Configuration();
        log.debug("rcmlDirectory: " + conf.rcmlDirectory);
        log.debug("backupDirectory: " + conf.backupDirectory);

        log.debug("mountPoints :" + conf.mountPoints);
        log.debug("password :" + conf.password);
        log.debug("servlet :" + conf.servlet);

        log.debug("xslt :" + conf.xslt);
        log.debug("xslt_rcmlrollback :" + conf.xslt_rcmlrollback);
    }
}
