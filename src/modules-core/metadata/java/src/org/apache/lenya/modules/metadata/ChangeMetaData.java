/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 *  contributor license agreements.  See the NOTICE file distributed with
 *  this work for additional information regarding copyright ownership.
 *  The ASF licenses this file to You under the Apache License, Version 2.0
 *  (the "License"); you may not use this file except in compliance with
 *  the License.  You may obtain a copy of the License at
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
package org.apache.lenya.modules.metadata;

import org.apache.lenya.cms.metadata.MetaData;
import org.apache.lenya.cms.publication.Document;
import org.apache.lenya.cms.publication.Publication;
import org.apache.lenya.cms.usecase.DocumentUsecase;
import org.apache.lenya.cms.usecase.UsecaseInvoker;
import org.apache.lenya.cms.workflow.usecases.InvokeWorkflow;
import org.apache.lenya.util.Assert;

/**
 * Save a meta data element.
 */
public class ChangeMetaData extends InvokeWorkflow {
    
    protected static final String PARAM_NAMESPACE = "namespace";
    protected static final String PARAM_ELEMENT = "element";
    protected static final String PARAM_VALUE = "value";
    protected static final String PARAM_OLD_VALUE = "oldValue";
    
    protected void prepareView() throws Exception {
        super.prepareView();
        UsecaseInvoker invoker = null;
        try {
            invoker = (UsecaseInvoker) this.manager.lookup(UsecaseInvoker.ROLE);
            invoker.invoke(getSourceURL(), getName(), getParameters());
        }
        finally {
            if (invoker != null) {
                this.manager.release(invoker);
            }
        }
    }

    protected void doCheckPreconditions() throws Exception {
        super.doCheckPreconditions();
        
        Document doc = getSourceDocument();
        if (doc == null) {
            return;
        }
        if (!doc.getArea().equals(Publication.AUTHORING_AREA)) {
            addErrorMessage("This usecase can only be invoked in the authoring area!");
            return;
        }

        String namespace = getParameterAsString(PARAM_NAMESPACE);
        Assert.notNull("namespace", namespace);
        String element = getParameterAsString(PARAM_ELEMENT);
        Assert.notNull("element", element);
        String value = getParameterAsString(PARAM_VALUE);
        Assert.notNull("value", value);
        String oldValue = getParameterAsString(PARAM_OLD_VALUE);
        Assert.notNull("old value", oldValue);
        
        MetaData meta = getSourceDocument().getMetaData(namespace);
        
        String currentValue = meta.getFirstValue(element);
        if (currentValue == null) {
            currentValue = "";
        }
        
        if (!oldValue.equals(currentValue)) {
            addErrorMessage("concurrent-change");
        }
    }

    protected void doExecute() throws Exception {
        
        super.doExecute();
        
        String namespace = getParameterAsString(PARAM_NAMESPACE);
        Assert.notNull("namespace", namespace);
        String element = getParameterAsString(PARAM_ELEMENT);
        Assert.notNull("element", element);
        String value = getParameterAsString(PARAM_VALUE);
        Assert.notNull("value", value);
        String oldValue = getParameterAsString(PARAM_OLD_VALUE);
        Assert.notNull("old value", oldValue);
        
        MetaData meta = getSourceDocument().getMetaData(namespace);
        meta.setValue(element, value);
    }
}
