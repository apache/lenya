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

/* $Id: CopyDocumentToArea.java,v 1.2 2004/03/01 16:18:27 gregor Exp $  */

package org.apache.lenya.cms.publication.task;

import org.apache.lenya.cms.publication.Document;
import org.apache.lenya.cms.task.ExecutionException;

/**
 * Copies a document to another area.
 */
public class CopyDocumentToArea extends DocumentTask {
    
    public static final String PARAMETER_DESTINATION_AREA = "destination-area";

    /**
     * @see org.apache.lenya.cms.task.Task#execute(java.lang.String)
     */
    public void execute(String servletContextPath) throws ExecutionException {
        
        Document document = getDocument();
        String area;
        try {
            area = getParameters().getParameter(PARAMETER_DESTINATION_AREA);
            getPublication().copyDocumentToArea(document, area);
        } catch (Exception e) {
            throw new ExecutionException(e);
        }
    }

}
