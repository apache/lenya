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

/* $Id: ReservedCheckoutAction.java,v 1.17 2004/03/01 16:18:21 gregor Exp $  */

package org.apache.lenya.cms.cocoon.acting;

import java.util.HashMap;
import java.util.Map;

import org.apache.avalon.framework.parameters.Parameters;
import org.apache.cocoon.environment.Redirector;
import org.apache.cocoon.environment.SourceResolver;
import org.apache.lenya.cms.rc.FileReservedCheckOutException;
import org.apache.log4j.Category;

/**
 * Action doing reserved checkout
 */
public class ReservedCheckoutAction extends RevisionControllerAction {
    Category log = Category.getInstance(ReservedCheckoutAction.class);

    /**
     * DOCUMENT ME!
     *
     * @param redirector DOCUMENT ME!
     * @param resolver DOCUMENT ME!
     * @param objectModel DOCUMENT ME!
     * @param src DOCUMENT ME!
     * @param parameters DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     *
     * @throws Exception DOCUMENT ME!
     */
    public Map act(
        Redirector redirector,
        SourceResolver resolver,
        Map objectModel,
        String src,
        Parameters parameters)
        throws Exception {
        super.act(redirector, resolver, objectModel, src, parameters);

        HashMap actionMap = new HashMap();

        //check out
        try {
            getLogger().debug(".act(): Filename: " + getFilename());
            getLogger().debug(".act(): Username: " + getUsername());

            if (getFilename() == null) {
                throw new Exception("Filename is null");
            }

            if (getUsername() == null) {
                throw new Exception("Username is null");
            }

            getRc().reservedCheckOut(getFilename(), getUsername());
        } catch (FileReservedCheckOutException e) {
            actionMap.put("exception", "fileReservedCheckOutException");
            actionMap.put("filename", getFilename());
            actionMap.put("user", e.getCheckOutUsername());
            actionMap.put("date", e.getCheckOutDate());
            getLogger().warn(
                "Document "
                    + getFilename()
                    + " already checked-out by "
                    + e.getCheckOutUsername()
                    + " since "
                    + e.getCheckOutDate());

            return actionMap;
        } catch (Exception e) {
            actionMap.put("exception", "genericException");
            actionMap.put("filename", getFilename());
            actionMap.put("message", "" + e);
            log.error("The document " + getFilename() + " couldn't be checked out: ", e);

            return actionMap;
        }

        return null;
    }
}
