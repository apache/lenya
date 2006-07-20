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
 * Custom meta-data is arbitrary meta-data that can be associated
 * to a document or a resource.
 *
 * The following code illustrates how you would add custom 
 * metadata to a document:
 * <pre> 
 *    MetaData customMetaData = document.getMetaDataManager().getCustomMetaData();
 *    customMetaData.setValue("some-great-attribute", "some great value");
 *    customMetaData.setValue("another-great-attribute", "some fantastic value");
 *    customMetaData.save();
 * </pre>
 * @version $Id$
 */
public class CustomMetaData extends MetaDataImpl {

    private static final String LOCAL_META = "custom";

    public static final String NAMESPACE = "http://apache.org/cocoon/lenya/page-envelope/1.0";
    private static final String PREFIX = "lenya";

    /**
     * @see MetaDataImpl#MetaDataImpl(java.lang.String, org.apache.avalon.framework.service.ServiceManager, org.apache.avalon.framework.logger.Logger)
     */
    public CustomMetaData(String sourceUri, ServiceManager manager, Logger _logger) throws DocumentException {
        super(sourceUri, manager, _logger);
    }

    /**
     * @see MetaDataImpl#useFixedElements()
     */
    protected boolean useFixedElements() {
        return false;
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
        return new String[0];
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

    public ElementSet getElementSet() {
        // TODO Auto-generated method stub
        return null;
    }

}
