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
package org.apache.lenya.cms.metadata;

import org.apache.lenya.cms.publication.Document;
import org.apache.lenya.cms.publication.DocumentBuildException;
import org.apache.lenya.cms.metadata.dublincore.DublinCore;
import org.apache.lenya.cms.site.SiteUsecase;
import org.apache.lenya.cms.usecase.UsecaseException;

/**
 * Usecase to edit metadata for a resource.
 * 
 * @version $Id$
 */
public class Metadata extends SiteUsecase {

	private DublinCore dc;
	/**
     * Ctor.
     */
    public Metadata() {
        super();
    }

    /**
     * @see org.apache.lenya.cms.usecase.AbstractUsecase#doInitialize()
     */
    protected void doInitialize() throws Exception {
        super.doInitialize();

        try {
        	dc = getSourceDocument().getDublinCore();
            setParameter(dc.ELEMENT_CREATOR, dc.getFirstValue(dc.ELEMENT_CREATOR));
            setParameter(dc.ELEMENT_TITLE, dc.getFirstValue(dc.ELEMENT_TITLE));
            setParameter(dc.ELEMENT_DESCRIPTION, dc.getFirstValue(dc.ELEMENT_CREATOR));
            setParameter(dc.ELEMENT_SUBJECT, dc.getFirstValue(dc.ELEMENT_SUBJECT));
            setParameter(dc.ELEMENT_PUBLISHER, dc.getFirstValue(dc.ELEMENT_PUBLISHER));
            setParameter(dc.ELEMENT_RIGHTS, dc.getFirstValue(dc.ELEMENT_RIGHTS));
            setParameter(dc.ELEMENT_TYPE, dc.getFirstValue(dc.ELEMENT_TYPE));
            setParameter(dc.ELEMENT_DATE, dc.getFirstValue(dc.ELEMENT_DATE));
            setParameter(dc.ELEMENT_FORMAT, dc.getFirstValue(dc.ELEMENT_FORMAT));
            setParameter(dc.ELEMENT_SOURCE, dc.getFirstValue(dc.ELEMENT_SOURCE));
            setParameter(dc.ELEMENT_LANGUAGE, dc.getFirstValue(dc.ELEMENT_LANGUAGE));
            setParameter(dc.ELEMENT_RELATION, dc.getFirstValue(dc.ELEMENT_RELATION));
            setParameter(dc.ELEMENT_COVERAGE, dc.getFirstValue(dc.ELEMENT_COVERAGE));
        }
        catch (Exception e) {
        	getLogger().error("Unable to load Dublin Core metadata.", e);
        	addErrorMessage("Unable to load Dublin Core metadata.");
        }
    }
            
     /**
     * Validates the request parameters.
     * @throws UsecaseException if an error occurs.
     */
    void validate() throws UsecaseException {
    }

    /**
     * @see org.apache.lenya.cms.usecase.AbstractUsecase#doCheckExecutionConditions()
     */
    protected void doCheckExecutionConditions() throws Exception {
        validate();
    }

    /**
     * @see org.apache.lenya.cms.usecase.AbstractUsecase#doExecute()
     */
    protected void doExecute() throws Exception {
        super.doExecute();

        String creator = getParameterAsString(dc.ELEMENT_CREATOR);
        String title  = getParameterAsString(dc.ELEMENT_TITLE);
        String description = getParameterAsString(dc.ELEMENT_DESCRIPTION);
        String subject = getParameterAsString(dc.ELEMENT_SUBJECT);
        String publisher = getParameterAsString(dc.ELEMENT_PUBLISHER);
        String rights = getParameterAsString(dc.ELEMENT_RIGHTS);

        dc.setValue(dc.ELEMENT_CREATOR, creator);
        dc.setValue(dc.ELEMENT_TITLE, title);
        dc.setValue(dc.ELEMENT_DESCRIPTION, description);
        dc.setValue(dc.ELEMENT_SUBJECT, subject);
        dc.setValue(dc.ELEMENT_PUBLISHER, publisher);
        dc.setValue(dc.ELEMENT_RIGHTS, rights);
        dc.save();
        //TODO set workflow situation to edit here.
    }

    /**
     * @see org.apache.lenya.cms.usecase.Usecase#setParameter(java.lang.String, java.lang.Object)
     */
    public void setParameter(String name, Object value) {
        super.setParameter(name, value);
        
    }
}