/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 *  contributor license agreements.  See the NOTICE file distributed with
 *  this work for additional information regarding copyright ownership.
 *  The ASF licenses this file to You under the Apache License, Version 2.0
 *  (the "License"); you may not use this file except in compliance with
 *  the License.  You may obtain a copy of the License at
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

import org.apache.lenya.cms.metadata.Element;
import org.apache.lenya.cms.metadata.MetaData;
import org.apache.lenya.cms.metadata.MetaDataRegistry;
import org.apache.lenya.cms.publication.Document;
import org.apache.lenya.cms.repository.Node;
import org.apache.lenya.cms.site.usecases.SiteUsecase;
import org.apache.lenya.cms.usecase.UsecaseException;
import org.apache.lenya.cms.workflow.WorkflowUtil;

/**
 * Usecase to edit metadata for a resource.
 * 
 * @version $Id$
 */
public class Metadata extends SiteUsecase {

    /**
     * @see org.apache.lenya.cms.usecase.AbstractUsecase#getNodesToLock()
     */
    protected Node[] getNodesToLock() throws UsecaseException {
        Node[] objects = new Node[0];
        if(getSourceDocument() != null) {
            objects = new Node[] { getSourceDocument().getRepositoryNode() };
        }
        return objects;
    }
    
    /**
     * Object to pass a meta data entry to the view.
     */
    public static class MetaDataWrapper {
        
        private String[] values;
        private Element element;
        private boolean editable;
        
        /**
         * @param element The element.
         * @param values The values for the element.
         * @param canChange If the element value can be changed via the GUI. A <code>true</code>
         *     value is only effective if the element itself is editable.
         */
        public MetaDataWrapper(Element element, String[] values, boolean canChange) {
            this.values = values;
            this.element = element;
            this.editable = element.isEditable() && canChange;
        }
        
        /**
         * @return The values for the element.
         */
        public String[] getValues() {
            return this.values;
        }
        
        /**
         * @return The element.
         */
        public Element getElement() {
            return this.element;
        }
        
        /**
         * @return If the value can be changed via the GUI.
         */
        public boolean isEditable() {
            return this.editable;
        }
        
    }

    /**
     * @see org.apache.lenya.cms.usecase.AbstractUsecase#initParameters()
     */
    protected void initParameters() {
        super.initParameters();
        
        Document doc = getSourceDocument();
        if (doc == null) {
            return;
        }
        

        MetaDataRegistry registry = null;
        try {
            boolean canChange = WorkflowUtil.canInvoke(this.manager, getSession(), getLogger(), doc, "edit");
            
            if (!canChange) {
                addInfoMessage("cannot-change-metadata");
            }
            
            registry = (MetaDataRegistry) this.manager.lookup(MetaDataRegistry.ROLE);

            List numbers = new ArrayList();
            Map num2namespace = new HashMap();
            List keyList = new ArrayList();

            String[] namespaces = registry.getNamespaceUris();

            for (int nsIndex = 0; nsIndex < namespaces.length; nsIndex++) {
                MetaData meta = doc.getMetaData(namespaces[nsIndex]);
                String[] keys = meta.getPossibleKeys();
                for (int keyIndex = 0; keyIndex < keys.length; keyIndex++) {
                    String key = "ns" + nsIndex + "." + keys[keyIndex];
                    String[] values = meta.getValues(keys[keyIndex]);
                    Element element = meta.getElementSet().getElement(keys[keyIndex]);
                    setParameter(key, new MetaDataWrapper(element, values, canChange));
                    keyList.add(key);
                }
                numbers.add("" + nsIndex);
                num2namespace.put("" + nsIndex, namespaces[nsIndex]);
            }

            setParameter("numbers", numbers);
            setParameter("namespaces", num2namespace);

            Collections.sort(keyList);
            setParameter("keys", keyList);

        } catch (Exception e) {
            getLogger().error("Unable to load meta data.", e);
            addErrorMessage("Unable to load meta data: " + e.getMessage());
        } finally {
            if (registry != null) {
                this.manager.release(registry);
            }
        }
    }

}
