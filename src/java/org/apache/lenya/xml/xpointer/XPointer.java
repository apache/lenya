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

/* $Id: XPointer.java,v 1.13 2004/03/01 16:18:26 gregor Exp $  */

package org.apache.lenya.xml.xpointer;

import java.util.Vector;

import org.w3c.dom.Node;


/**
 * XPointer interface
 */
public interface XPointer {
	
    /**
     * DOCUMENT ME!
     * 
     * @param node DOCUMENT ME!
     * @param selectString DOCUMENT ME!
     * 
     * @return DOCUMENT ME!
     *
     * @exception Exception ...
     */
    Vector select(Node node, String selectString, Vector namespaces) throws Exception;
}
