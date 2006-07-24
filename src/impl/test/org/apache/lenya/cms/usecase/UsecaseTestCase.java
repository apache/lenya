/*
 * Copyright  1999-2004 The Apache Software Foundation
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
package org.apache.lenya.cms.usecase;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.avalon.framework.service.ServiceException;
import org.apache.lenya.ac.AccessControlException;
import org.apache.lenya.ac.impl.AbstractAccessControlTest;

/**
 * Usecase test base class.
 */
public abstract class UsecaseTestCase extends AbstractAccessControlTest {

    /**
     * The test.
     * @throws ServiceException
     * @throws UsecaseException
     * @throws AccessControlException
     */
    public void testUsecase() throws ServiceException, UsecaseException, AccessControlException {

        login();
        prepareUsecase();

        UsecaseInvoker invoker = null;
        try {
            invoker = (UsecaseInvoker) getManager().lookup(UsecaseInvoker.ROLE);
            invoker.invoke(getRequest().getPathInfo(), getUsecaseName(), getParameters());

            List errorMessages = invoker.getErrorMessages();
            for (Iterator i = errorMessages.iterator(); i.hasNext();) {
                UsecaseMessage message = (UsecaseMessage) i.next();
                String m = message.getMessage();
                String[] params = message.getParameters();
                if (params != null) {
                    for (int j = 0; j < params.length; j++) {
                        m += " [" + params[j] + "]";
                    }
                }
                getLogger().error("Usecase error: " + m);
            }

            assertEquals(invoker.getResult(), UsecaseInvoker.SUCCESS);
            assertEquals(invoker.getErrorMessages().size(), 0);
        } finally {
            if (invoker != null) {
                getManager().release(invoker);
            }
        }

        checkPostconditions();

    }

    protected void login() throws AccessControlException {
        login("lenya");
    }

    protected void prepareUsecase() {
    }

    protected Map getParameters() {
        return new HashMap();
    }

    protected abstract String getUsecaseName();

    protected void checkPostconditions() {
    }

}
