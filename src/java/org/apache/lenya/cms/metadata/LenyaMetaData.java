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

package org.apache.lenya.cms.metadata;


import org.apache.avalon.framework.service.ServiceManager;
import org.apache.avalon.framework.logger.Logger;
import org.apache.lenya.cms.publication.DocumentException;

/**
 * Meta-data for Lenya's internal usage.
 * 
 * @version $Id$
 */
public class LenyaMetaData extends MetaDataImpl {

    private static final String LOCAL_META = "internal";

    public static final String NAMESPACE = "http://apache.org/cocoon/lenya/page-envelope/1.0";
    private static final String PREFIX = "lenya";

    /**
     * The name of the resource type attribute. 
     * An XML resource has a resource type; this information can be 
     * used e.g. for different rendering of different types.
     */
    public static final String ELEMENT_RESOURCE_TYPE = "resourceType";

    /**
     * The name of the content type attribute.
     * Any content managed by Lenya has a type; this information can
     * be used e.g. to provide an appropriate management interface.
     */
    public static final String ELEMENT_CONTENT_TYPE = "contentType";
    
    /**
     * The extension to use for the document source.
     */
    public static final String ELEMENT_EXTENSION = "extension";

    /**
     * A workflow version.
     * @see org.apache.lenya.cms.workflow.DocumentWorkflowable
     */
    public static final String ELEMENT_WORKFLOW_VERSION = "workflowVersion";

    /**
     * Determines if the document is just a placeholder in the trash and archive areas.
     * @see org.apache.lenya.cms.site.usecases.MoveSubsite
     */
    public static final String ELEMENT_PLACEHOLDER = "placeholder";

    public static final String ELEMENTE_HEIGHT = "height";
    public static final String ELEMENT_WIDTH = "width";

    public static final String[] ELEMENTS = {
       ELEMENT_RESOURCE_TYPE,
       ELEMENT_CONTENT_TYPE,
       ELEMENT_WORKFLOW_VERSION,
       ELEMENT_PLACEHOLDER,
       ELEMENTE_HEIGHT,
       ELEMENT_WIDTH,
       ELEMENT_EXTENSION
    };

    /**
     * @see MetaDataImpl#MetaDataImpl(java.lang.String, org.apache.avalon.framework.service.ServiceManager, org.apache.avalon.framework.logger.Logger)
     */
    public LenyaMetaData(String sourceUri, ServiceManager manager, Logger _logger) throws DocumentException {
        super(sourceUri, manager, _logger);
    }

    /**
     * @see MetaDataImpl#getNamespaces()
     */
    protected String[] getNamespaces() {
        return new String[] { NAMESPACE };
    }

    /**
     * @see MetaDataImpl#getPrefixes()
     */
    protected String[] getPrefixes() {
        return new String[] { PREFIX };
    }

    /**
     * @see MetaDataImpl#getElements()
     */
    protected String[] getElements() {
        return ELEMENTS;
    }

    /**
     * @see MetaDataImpl#getTerms()
     */
    protected String[] getTerms() {
        return new String[0];
    }

    /**
     * @see MetaDataImpl#getLocalElementName()
     */
    protected String getLocalElementName() {
        return LOCAL_META;
    }

}
