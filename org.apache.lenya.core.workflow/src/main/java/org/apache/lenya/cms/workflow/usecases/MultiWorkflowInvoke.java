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
package org.apache.lenya.cms.workflow.usecases;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.apache.lenya.cms.usecase.AbstractUsecase;
import org.apache.lenya.cms.usecase.UsecaseInvoker;
import org.apache.lenya.cms.usecase.UsecaseMessage;

/**
 * Invocation usecase for the multi-workflow usecase.
 */
public class MultiWorkflowInvoke extends AbstractUsecase {

    protected static final String URL = "url";
    protected static final String USECASE_NAME = "usecaseName";

    private UsecaseInvoker usecaseInvoker;

    protected void doExecute() throws Exception {
        super.doExecute();

        String usecase = getParameterAsString(USECASE_NAME);
        assert usecase != null;
        String url = getParameterAsString(URL);
        assert url != null;

        UsecaseInvoker invoker = getUsecaseInvoker();
        invoker.invoke(url, usecase, new HashMap());

        if (invoker.getResult() != UsecaseInvoker.SUCCESS) {
            List messages = invoker.getErrorMessages();
            for (Iterator i = messages.iterator(); i.hasNext();) {
                UsecaseMessage message = (UsecaseMessage) i.next();
                addErrorMessage(message.getMessage(), message.getParameters());
            }
        }
    }

    protected UsecaseInvoker getUsecaseInvoker() {
        return usecaseInvoker;
    }

    /**
     * TODO: Bean wiring
     */
    public void setUsecaseInvoker(UsecaseInvoker usecaseInvoker) {
        this.usecaseInvoker = usecaseInvoker;
    }

}
