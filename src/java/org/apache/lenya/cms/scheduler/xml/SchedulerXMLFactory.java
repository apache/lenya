/*
 * SchedulerFactory.java
 *
 * Created on November 13, 2002, 9:56 AM
 */

package org.wyona.cms.scheduler.xml;

import org.dom4j.DocumentFactory;
import org.dom4j.Element;
import org.dom4j.Namespace;
import org.dom4j.QName;

/**
 *
 * @author  ah
 */
public class SchedulerXMLFactory {
    
    public static final Namespace namespace = new Namespace("sch", "http://www.wyona.org/2002/sch");

    public static Namespace getNamespace() {
        return namespace;
    }
    
    public static Element createElement(String localName) {
        return DocumentFactory.getInstance().createElement(getQName(localName));
    }
    
    public static QName getQName(String localName) {
        return new QName(localName, getNamespace());
    }
    
}
