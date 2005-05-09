/*
 * Copyright  1999-2005 The Apache Software Foundation
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

import org.apache.lenya.cms.metadata.MetaData;
import org.apache.lenya.cms.metadata.dublincore.DublinCore;
import org.apache.lenya.cms.publication.DocumentType;
import org.apache.lenya.cms.publication.DocumentTypeResolver;
import org.apache.lenya.cms.site.usecases.SiteUsecase;
import org.apache.lenya.cms.usecase.UsecaseException;

/**
 * Usecase to display the overview tab in the site area for a document.
 * 
 * @version $Id: Overview.java 123984 2005-01-03 15:02:18Z andreas $
 */
public class Overview extends SiteUsecase {

    /**
     * Ctor.
     */
    public Overview() {
        super();
    }

    /**
     * @see org.apache.lenya.cms.usecase.AbstractUsecase#initParameters()
     */
    protected void initParameters() {
        super.initParameters();

        DocumentTypeResolver doctypeResolver = null;
        try {
            MetaData dc = (MetaData) getSourceDocument().getMetaDataManager().getDublinCoreMetaData();
            setParameter("languages", getSourceDocument().getLanguages());
            setParameter("title", dc.getFirstValue(DublinCore.ELEMENT_TITLE));

            doctypeResolver = (DocumentTypeResolver) this.manager.lookup(DocumentTypeResolver.ROLE);
            DocumentType doctype = doctypeResolver.resolve(getSourceDocument());
            setParameter("languages", getSourceDocument().getLanguages());
            setParameter("lastmodified", getSourceDocument().getLastModified());
            setParameter("resourcetype", doctype.getName());
            setParameter("live", "");
        } catch (final Exception e) {
            addErrorMessage("Could not read a value. See log files for details.");
            getLogger().error("Could not read value for Overview usecase. ", e);
        } finally {
            if (doctypeResolver != null) {
                this.manager.release(doctypeResolver);
            }
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
    }

    /**
     * @see org.apache.lenya.cms.usecase.Usecase#setParameter(java.lang.String, java.lang.Object)
     */
    public void setParameter(String name, Object value) {
        super.setParameter(name, value);
    }
}
