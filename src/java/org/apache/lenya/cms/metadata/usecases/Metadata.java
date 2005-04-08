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
package org.apache.lenya.cms.metadata.usecases;

import org.apache.lenya.cms.metadata.dublincore.DublinCore;
import org.apache.lenya.cms.site.usecases.SiteUsecase;
import org.apache.lenya.cms.usecase.UsecaseException;
import org.apache.lenya.transaction.TransactionException;

/**
 * Usecase to edit metadata for a resource.
 * 
 * @version $Id: Metadata.java 123979 2005-01-03 14:59:45Z andreas $
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
     * @see org.apache.lenya.cms.usecase.Usecase#lockInvolvedObjects()
     */
    public void lockInvolvedObjects() throws UsecaseException {
        super.lockInvolvedObjects();
        try {
            getSourceDocument().lock();
        } catch (TransactionException e) {
            throw new UsecaseException(e);
        }
    }
    
    /**
     * @see org.apache.lenya.cms.usecase.AbstractUsecase#initParameters()
     */
    protected void initParameters() {
        super.initParameters();

        try {
        	this.dc = getSourceDocument().getDublinCore();
            setParameter(DublinCore.ELEMENT_CREATOR, this.dc.getFirstValue(DublinCore.ELEMENT_CREATOR));
            setParameter(DublinCore.ELEMENT_TITLE, this.dc.getFirstValue(DublinCore.ELEMENT_TITLE));
            setParameter(DublinCore.ELEMENT_DESCRIPTION, this.dc.getFirstValue(DublinCore.ELEMENT_CREATOR));
            setParameter(DublinCore.ELEMENT_SUBJECT, this.dc.getFirstValue(DublinCore.ELEMENT_SUBJECT));
            setParameter(DublinCore.ELEMENT_PUBLISHER, this.dc.getFirstValue(DublinCore.ELEMENT_PUBLISHER));
            setParameter(DublinCore.ELEMENT_RIGHTS, this.dc.getFirstValue(DublinCore.ELEMENT_RIGHTS));
            setParameter(DublinCore.ELEMENT_TYPE, this.dc.getFirstValue(DublinCore.ELEMENT_TYPE));
            setParameter(DublinCore.ELEMENT_DATE, this.dc.getFirstValue(DublinCore.ELEMENT_DATE));
            setParameter(DublinCore.ELEMENT_FORMAT, this.dc.getFirstValue(DublinCore.ELEMENT_FORMAT));
            setParameter(DublinCore.ELEMENT_SOURCE, this.dc.getFirstValue(DublinCore.ELEMENT_SOURCE));
            setParameter(DublinCore.ELEMENT_LANGUAGE, this.dc.getFirstValue(DublinCore.ELEMENT_LANGUAGE));
            setParameter(DublinCore.ELEMENT_RELATION, this.dc.getFirstValue(DublinCore.ELEMENT_RELATION));
            setParameter(DublinCore.ELEMENT_COVERAGE, this.dc.getFirstValue(DublinCore.ELEMENT_COVERAGE));
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
	    // do nothing
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

        String creator = getParameterAsString(DublinCore.ELEMENT_CREATOR);
        String title  = getParameterAsString(DublinCore.ELEMENT_TITLE);
        String description = getParameterAsString(DublinCore.ELEMENT_DESCRIPTION);
        String subject = getParameterAsString(DublinCore.ELEMENT_SUBJECT);
        String publisher = getParameterAsString(DublinCore.ELEMENT_PUBLISHER);
        String rights = getParameterAsString(DublinCore.ELEMENT_RIGHTS);

        this.dc.setValue(DublinCore.ELEMENT_CREATOR, creator);
        this.dc.setValue(DublinCore.ELEMENT_TITLE, title);
        this.dc.setValue(DublinCore.ELEMENT_DESCRIPTION, description);
        this.dc.setValue(DublinCore.ELEMENT_SUBJECT, subject);
        this.dc.setValue(DublinCore.ELEMENT_PUBLISHER, publisher);
        this.dc.setValue(DublinCore.ELEMENT_RIGHTS, rights);
        //TODO set workflow situation to edit here.
    }

    /**
     * @see org.apache.lenya.cms.usecase.Usecase#setParameter(java.lang.String, java.lang.Object)
     */
    public void setParameter(String name, Object value) {
        super.setParameter(name, value);
        
    }
}