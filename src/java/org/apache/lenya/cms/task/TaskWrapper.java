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

/* $Id: TaskWrapper.java,v 1.4 2004/03/01 16:18:19 gregor Exp $  */

package org.apache.lenya.cms.task;

import java.util.Map;

import org.apache.lenya.xml.NamespaceHelper;
import org.w3c.dom.Element;

/**
 * A TaskWrapper encapsulates all information that is needed to execute a
 * certain task, including e.g. workflow and notification parameters.
 */
public interface TaskWrapper {
    
    /**
     * Executes the task.
     * @throws ExecutionException when something went wrong.
     */
    void execute() throws ExecutionException;
    
    /**
     * Saves the wrapper parameters to an XML element.
     * @param helper The namespace helper of the document.
     * @return An XML element.
     */
    Element save(NamespaceHelper helper);
    
    /**
     * @param helper The namespace helper of the document.
     * Restores the wrapper parameters from an XML element.
     * @param element An XML element.
     */
    void restore(NamespaceHelper helper, Element element);
    
    /**
     * Returns the task wrapper parameters.
     * @return A map.
     */
    Map getParameters();
    
}