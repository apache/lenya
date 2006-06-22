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
package org.apache.lenya.cms.site.usecases;

import org.apache.lenya.cms.site.usecases.SiteUsecase;
import org.apache.lenya.cms.usecase.UsecaseException;

/**
 * Usecase to display the Scheduler tab in the site area for a document.
 * 
 * @version $Id: Scheduler.java 123658 2004-12-29 17:35:03Z gregor $
 */
public class Scheduler extends SiteUsecase {

	/**
     * Ctor.
     */
    public Scheduler() {
        super();
    }

    /**
     * @see org.apache.lenya.cms.usecase.AbstractUsecase#initParameters()
     */
    protected void initParameters() {
        super.initParameters();
        try {
	        setParameter("live", "");
        } catch (Exception e) {
        	addErrorMessage("Could not read a value.");
        	getLogger().error("Could not read value for Scheduler usecase. " + e.toString());
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
        try {
		    // do nothing
        } catch (Exception e) {
            addErrorMessage("Scheduler reported error");
        }
    }

    /**
     * @see org.apache.lenya.cms.usecase.Usecase#setParameter(java.lang.String, java.lang.Object)
     */
    public void setParameter(String name, Object value) {
        super.setParameter(name, value);
    }
}