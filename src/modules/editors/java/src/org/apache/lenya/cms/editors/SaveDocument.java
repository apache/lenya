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
package org.apache.lenya.cms.editors;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.lenya.cms.usecase.DocumentUsecase;
import org.apache.lenya.cms.usecase.UsecaseInvoker;
import org.apache.lenya.cms.usecase.UsecaseMessage;
import org.apache.lenya.util.Assert;

/**
 * <p>
 * This usecase saves the document from the request stream <em>before</em> the
 * view is displayed using the {@link EditDocument} usecase. That's kind of a
 * hack, since it violates the standard usecase principle, but it is very
 * convenient because you can save and re-load the document without a redirect.
 * </p>
 * 
 * @version $Id: EditDocument.java 495324 2007-01-11 18:44:04Z andreas $
 */
public class SaveDocument extends DocumentUsecase {

    protected static final String USECASE_NAME = "usecaseName";

    protected void doCheckPreconditions() throws Exception {
        super.doCheckPreconditions();

        String usecase = getParameterAsString(USECASE_NAME);
        Assert.notNull("usecase", usecase);

        UsecaseInvoker invoker = null;
        try {
            invoker = (UsecaseInvoker) this.manager.lookup(UsecaseInvoker.ROLE);
            Map params = new HashMap();
            params.put(EditDocument.SOURCE_URI, getParameter(EditDocument.SOURCE_URI));
            params.put(EditDocument.EVENT, getParameter(EditDocument.EVENT));
            invoker.invoke(getSourceURL(), usecase, params);

            if (invoker.getResult() != UsecaseInvoker.SUCCESS) {
                List messages = invoker.getErrorMessages();
                for (Iterator i = messages.iterator(); i.hasNext();) {
                    UsecaseMessage message = (UsecaseMessage) i.next();
                    addErrorMessage(message.getMessage(), message.getParameters());
                }
            }
        } finally {
            if (invoker != null) {
                this.manager.release(invoker);
            }
        }

    }

}
