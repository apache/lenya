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
package org.apache.lenya.cms.site;

import org.apache.lenya.cms.publication.Document;
import org.apache.lenya.cms.publication.DocumentBuildException;
import org.apache.lenya.cms.usecase.DocumentUsecase;

/**
 * Super class for site related usecases.
 * 
 * @version $Id$
 */
public class SiteUsecase extends DocumentUsecase {

    protected static final String AREA = "area";
    protected static final String DOCUMENTID = "documentid";
    protected static final String LANGUAGEEXISTS = "languageexists";

    /**
     * Ctor.
     */
    public SiteUsecase() {
        super();
    }
    
    /**
     * @see org.apache.lenya.cms.usecase.AbstractUsecase#doInitialize()
     */
    protected void doInitialize() throws Exception {
        super.doInitialize();
        doc = getSourceDocument();
    }
       
    private Document doc;

    /**
     * Returns the currently edited document.
     * @return A document.
     */
    protected Document getDocument() {
        return this.doc;
    }
    
    /**
     * @see org.apache.lenya.cms.usecase.Usecase#setParameter(java.lang.String, java.lang.Object)
     */
    /*TODO make common parameters available to site usecases: area, documentid, languageexists etc */
    public void setParameter(String name, Object value) {
        super.setParameter(name, value);
        
        if (true) {
            
            setParameter(AREA, this.doc.getArea());
            setParameter(DOCUMENTID, this.doc.getId());
            setParameter(LANGUAGEEXISTS, "");
        }
    }

    
}
