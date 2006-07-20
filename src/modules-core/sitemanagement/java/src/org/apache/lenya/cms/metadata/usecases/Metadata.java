/*
 * Copyright 1999-2005 The Apache Software Foundation
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 * 
 */
package org.apache.lenya.cms.metadata.usecases;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.lenya.cms.metadata.MetaData;
import org.apache.lenya.cms.repository.Node;
import org.apache.lenya.cms.site.usecases.SiteUsecase;
import org.apache.lenya.cms.usecase.UsecaseException;

/**
 * Usecase to edit metadata for a resource.
 * 
 * @version $Id$
 */
public class Metadata extends SiteUsecase {

    /**
     * Ctor.
     */
    public Metadata() {
        super();
    }

    /**
     * @see org.apache.lenya.cms.usecase.AbstractUsecase#getNodesToLock()
     */
    protected Node[] getNodesToLock() throws UsecaseException {
        Node[] objects = { getSourceDocument().getRepositoryNode() };
        return objects;
    }

    /**
     * @see org.apache.lenya.cms.usecase.AbstractUsecase#initParameters()
     */
    protected void initParameters() {
        super.initParameters();
        
        // dc metadata
        
        try {
            
            List numbers = new ArrayList();
            
            Map num2namespace = new HashMap();
            
            List keyList = new ArrayList();
            
            String[] namespaces = getSourceDocument().getMetaDataNamespaceUris();
            
            for (int nsIndex = 0; nsIndex < namespaces.length; nsIndex++) {
                MetaData meta = getSourceDocument().getMetaData(namespaces[nsIndex]);
                boolean matched = false;
                String[] keys = meta.getPossibleKeys();
                for (int keyIndex = 0; keyIndex < keys.length; keyIndex++) {
                    if (meta.getElementSet().getElement(keys[keyIndex]).isEditable()) {
                        String value = meta.getFirstValue(keys[keyIndex]);
                        if (value != null) {
                            String key = nsIndex + "." + keys[keyIndex];
                            setParameter(key, value);
                            keyList.add(key);
                            matched = true;
                        }
                    }
                }
                if (matched) {
                    numbers.add("" + nsIndex);
                    num2namespace.put("" + nsIndex, namespaces[nsIndex]);
                }
            }
            
            setParameter("numbers", numbers);
            setParameter("namespaces", num2namespace);
            
            Collections.sort(keyList);
            setParameter("keys", keyList);

        } catch (Exception e) {
            getLogger().error("Unable to load meta data.", e);
            addErrorMessage("Unable to load meta data: " + e.getMessage());
        }

    }

    /**
     * Validates the request parameters.
     * 
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

        String[] namespaces = getSourceDocument().getMetaDataNamespaceUris();

        for (int nsIndex = 0; nsIndex < namespaces.length; nsIndex++) {
            MetaData meta = getSourceDocument().getMetaData(namespaces[nsIndex]);

            String[] keys = meta.getPossibleKeys();
            for (int keyIndex = 0; keyIndex < keys.length; keyIndex++) {
                String value = getParameterAsString(nsIndex + "." + keys[keyIndex]);
                if (value != null) {
                    meta.setValue(keys[keyIndex], value);
                }
            }
        }

    }

}