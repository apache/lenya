/*
 * $Id: XmlUtil.java,v 1.8 2003/03/04 19:44:56 gregor Exp $
 * <License>
 * The Apache Software License
 *
 * Copyright (c) 2002 lenya. All rights reserved.
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
 *    by lenya (http://www.lenya.org)"
 *
 * 4. The name "lenya" must not be used to endorse or promote products derived from
 *    this software without prior written permission. For written permission, please
 *    contact contact@lenya.org
 *
 * 5. Products derived from this software may not be called "lenya" nor may "lenya"
 *    appear in their names without prior written permission of lenya.
 *
 * 6. Redistributions of any form whatsoever must retain the following acknowledgment:
 *    "This product includes software developed by lenya (http://www.lenya.org)"
 *
 * THIS SOFTWARE IS PROVIDED BY lenya "AS IS" WITHOUT ANY WARRANTY EXPRESS OR IMPLIED,
 * INCLUDING THE WARRANTY OF NON-INFRINGEMENT AND THE IMPLIED WARRANTIES OF MERCHANTI-
 * BILITY AND FITNESS FOR A PARTICULAR PURPOSE. lenya WILL NOT BE LIABLE FOR ANY DAMAGES
 * SUFFERED BY YOU AS A RESULT OF USING THIS SOFTWARE. IN NO EVENT WILL lenya BE LIABLE
 * FOR ANY SPECIAL, INDIRECT OR CONSEQUENTIAL DAMAGES OR LOST PROFITS EVEN IF lenya HAS
 * BEEN ADVISED OF THE POSSIBILITY OF THEIR OCCURRENCE. lenya WILL NOT BE LIABLE FOR ANY
 * THIRD PARTY CLAIMS AGAINST YOU.
 *
 * Wyona includes software developed by the Apache Software Foundation, W3C,
 * DOM4J Project, BitfluxEditor and Xopus.
 * </License>
 */
package org.lenya.util;


// SAX
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.SAXException;

import java.io.IOException;

// JAXP
import javax.xml.parsers.FactoryConfigurationError;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;


/**
 * XmlUtil.java $Id: XmlUtil.java,v 1.8 2003/03/04 19:44:56 gregor Exp $ Created: Thu Jan 24 18:27:05
 * 2002 Utility Class for checking XML content for well-formedness
 *
 * @author <a href="mailto:memo@otego.com">Memo Birgi</a>
 * @version 0.1
 */
public class XmlUtil {
    /**
     * DOCUMENT ME!
     *
     * @param xmlFile Name of the XML-File to be checked.
     *
     * @return If well-formed: "OK"; if not: the error message.
     */
    public static String check(String xmlFile) {
        String retMsg = "OK";

        try {
            SAXParserFactory factory = SAXParserFactory.newInstance();
            SAXParser parser = factory.newSAXParser();

            parser.parse(xmlFile, new DefaultHandler());
        } catch (FactoryConfigurationError e) {
            retMsg = "unable to get a document builder factory";
        } catch (ParserConfigurationException e) {
            retMsg = "unable to configure parser";
        } catch (SAXException e) {
            retMsg = e.toString();
        } catch (IOException e) {
            retMsg = "i/o error";
        }

        return retMsg;
    }

    public class MyHandler
        extends DefaultHandler {
        // custom handler 
    }
}
 // XmlUtil
