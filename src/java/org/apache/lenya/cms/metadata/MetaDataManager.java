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

import java.util.Iterator;
import java.util.Map;
import org.apache.avalon.framework.container.ContainerUtil;
import org.apache.avalon.framework.logger.AbstractLogEnabled;
import org.apache.avalon.framework.logger.Logger;
import org.apache.avalon.framework.service.ServiceManager;
import org.apache.lenya.cms.metadata.dublincore.DublinCoreImpl;
import org.apache.lenya.cms.publication.DocumentException;

/**
 * Manager for meta data of a Lenya resource/document
 * 
 * @version $Id$
 */
public class MetaDataManager extends AbstractLogEnabled {
    
    private String sourceUri;
    protected ServiceManager serviceManager;

    private DublinCoreImpl dublinCoreMetaData;
    private LenyaMetaData lenyaMetaData;
    private CustomMetaData customMetaData;

    /**
     * Ctor.
     */
    public MetaDataManager(String _sourceUri, ServiceManager _serviceManager, Logger _logger) {
        ContainerUtil.enableLogging(this, _logger);
        this.sourceUri = _sourceUri;
        this.serviceManager = _serviceManager;
    }

    /**
     * Retrieve the dublin core meta-data managed by this instance.
     * @return the dublin core meta-data
     * @throws DocumentException if the meta-data could not be retrieved
     */
    public MetaData getDublinCoreMetaData() throws DocumentException {
        if (dublinCoreMetaData == null) {
            dublinCoreMetaData = new DublinCoreImpl(this.sourceUri, this.serviceManager, getLogger());
        }
        return dublinCoreMetaData;
    }

    /**
     * Set the dublin-core meta-data managed by this instance.
     * @param dcMetaDataValues the dublin-core meta-data
     * @throws DocumentException if the meta-data could not be written
     */
    public void setDublinCoreMetaData(Map dcMetaDataValues) throws DocumentException {
        dublinCoreMetaData = new DublinCoreImpl(this.sourceUri, this.serviceManager, getLogger());
        setMetaData(dublinCoreMetaData, dcMetaDataValues);
    }

    /**
     * Retrieve the Lenya internal meta-data managed by this instance.
     * @return the Lenya meta-data
     * @throws DocumentException if the meta-data could not be retrieved
     */
    public MetaData getLenyaMetaData() throws DocumentException {
        if (lenyaMetaData == null) {
            lenyaMetaData = new LenyaMetaData(this.sourceUri, this.serviceManager, getLogger());
        }
        return lenyaMetaData;
    }

    /**
     * Set the Lenya internal meta-data managed by this instance.
     * @param lenyaMetaDataValues the Lenya meta-data
     * @throws DocumentException if the meta-data could not be written
     */
    public void setLenyaMetaData(Map lenyaMetaDataValues) throws DocumentException {
        lenyaMetaData = new LenyaMetaData(this.sourceUri, this.serviceManager, getLogger());
        setMetaData(lenyaMetaData, lenyaMetaDataValues);
    }

    /**
     * Retrieve the custom meta-data managed by this instance.
     * @return the custom meta-data
     * @throws DocumentException if the meta-data could not be retrieved
     */
    public MetaData getCustomMetaData() throws DocumentException {
        if (customMetaData == null) {
            customMetaData = new CustomMetaData(this.sourceUri, this.serviceManager, getLogger());
        }
        return customMetaData;
    }

    /**
     * Set the custom meta-data managed by this instance.
     * @param customMetaDataValues the custom meta-data
     * @throws DocumentException if the meta-data could not be written
     */
    public void setCustomMetaData(Map customMetaDataValues) throws DocumentException {
        customMetaData = new CustomMetaData(this.sourceUri, this.serviceManager, getLogger());
        setMetaData(customMetaData, customMetaDataValues);
    }

    /**
     * Set values of meta-data managed by this instance.
     * @throws DocumentException if meta-data could not be written
     */
    public void setMetaData(Map dcMetaData, Map lenyaMetaData, Map customMetaData) throws DocumentException {
        if (dcMetaData != null)
            setMetaData(getDublinCoreMetaData(), dcMetaData);
        if (lenyaMetaData != null)
            setMetaData(getLenyaMetaData(), lenyaMetaData);
        if (customMetaData != null)
            setMetaData(getCustomMetaData(), customMetaData);
    }

    private void setMetaData(MetaData _metaData, Map _metaDataMap) throws DocumentException {
        Iterator iter = _metaDataMap.entrySet().iterator();
        while (iter.hasNext()) {
            Map.Entry entry = (Map.Entry) iter.next();
            _metaData.setValue((String) entry.getKey(), (String) entry.getValue());
        }
    }

    /**
     * Replace the contents of the meta-data managed by this instance with
     * the contents of the meta-data managed by another instance of
     * MetaDataManager.
     * @param sourceManager the MetaDataManager from which to read the new meta-data
     * @throws DocumentException if something goes wrong
     */
    public void replaceMetaData(MetaDataManager sourceManager) throws DocumentException {
        MetaData source = sourceManager.getDublinCoreMetaData();
        MetaData dest = this.getDublinCoreMetaData();
        dest.replaceBy(source);
        source = sourceManager.getLenyaMetaData();
        dest = this.getLenyaMetaData();
        dest.replaceBy(source);
        source = sourceManager.getCustomMetaData();
        dest = this.getCustomMetaData();
        dest.replaceBy(source);
    }
    
    protected String getSourceURI() {
        return this.sourceUri;
    }
}
