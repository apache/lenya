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
package org.apache.lenya.cms.jcr.metadata;

import java.util.List;

import org.apache.avalon.framework.logger.Logger;
import org.apache.avalon.framework.service.ServiceManager;
import org.apache.lenya.cms.metadata.CustomMetaData;
import org.apache.lenya.cms.metadata.LenyaMetaData;
import org.apache.lenya.cms.metadata.MetaData;
import org.apache.lenya.cms.metadata.MetaDataManager;
import org.apache.lenya.cms.metadata.dublincore.DublinCoreImpl;
import org.apache.lenya.cms.publication.DocumentException;

/**
 * JCR based meta data manager.
 */
public class JCRMetaDataManager extends MetaDataManager {

    private JCRMetaData customMetaData, lenyaMetaData, dublinCore;

    /**
     * @param _sourceUri The source URI.
     * @param _serviceManager The service manager.
     * @param _logger The logger.
     */
    public JCRMetaDataManager(String _sourceUri, ServiceManager _serviceManager, Logger _logger) {
        super(_sourceUri, _serviceManager, _logger);
    }

    public MetaData getCustomMetaData() throws DocumentException {
        if (this.customMetaData == null) {
            this.customMetaData = new JCRMetaData(CustomMetaData.NAMESPACE,
                    getSourceURI(),
                    this.serviceManager,
                    getLogger());
        }
        return this.customMetaData;
    }

    public MetaData getDublinCoreMetaData() throws DocumentException {
        if (this.dublinCore == null) {
            this.dublinCore = new JCRMetaData(DublinCoreImpl.DC_NAMESPACE,
                    getSourceURI(),
                    this.serviceManager,
                    getLogger());
            List names  = DublinCoreImpl.getAttributeNames();
            String[] array = (String[]) names.toArray(new String[names.size()]);
            this.dublinCore.setPossibleKeys(array);
        }
        return this.dublinCore;
    }

    public MetaData getLenyaMetaData() throws DocumentException {
        if (this.lenyaMetaData == null) {
            this.lenyaMetaData = new JCRMetaData(LenyaMetaData.NAMESPACE,
                    getSourceURI(),
                    this.serviceManager,
                    getLogger());
            this.lenyaMetaData.setPossibleKeys(LenyaMetaData.ELEMENTS);
        }
        return this.lenyaMetaData;
    }

}
