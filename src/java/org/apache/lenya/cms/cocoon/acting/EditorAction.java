/*
 * $Id: EditorAction.java,v 1.13 2003/04/24 13:52:38 gregor Exp $
 * <License>
 * The Apache Software License
 *
 * Copyright (c) 2002 lenya. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice, this
 *    list of conditions and the following disclaimer in the documentation and/or
 *    other materials provided with the distribution.
 *
 * 3. All advertising materials mentioning features or use of this software must
 *    display the following acknowledgment: "This product includes software developed
 *    by lenya (http://www.lenya.org)"
 *
 * 4. The name "lenya" must not be used to endorse or promote products derived from
 *    this software without prior written permission. For written permission, please
 *    contact contact@lenya.org
 *
 * 5. Products derived from this software may not be called "lenya" nor may "lenya"
 *    appear in their names without prior written permission of lenya.
 *
 * 6. Redistributions of any form whatsoever must retain the following acknowledgment:
 *    "This product includes software developed by lenya (http://www.lenya.org)"
 *
 * THIS SOFTWARE IS PROVIDED BY lenya "AS IS" WITHOUT ANY WARRANTY EXPRESS OR IMPLIED,
 * INCLUDING THE WARRANTY OF NON-INFRINGEMENT AND THE IMPLIED WARRANTIES OF MERCHANTI-
 * BILITY AND FITNESS FOR A PARTICULAR PURPOSE. lenya WILL NOT BE LIABLE FOR ANY DAMAGES
 * SUFFERED BY YOU AS A RESULT OF USING THIS SOFTWARE. IN NO EVENT WILL lenya BE LIABLE
 * FOR ANY SPECIAL, INDIRECT OR CONSEQUENTIAL DAMAGES OR LOST PROFITS EVEN IF lenya HAS
 * BEEN ADVISED OF THE POSSIBILITY OF THEIR OCCURRENCE. lenya WILL NOT BE LIABLE FOR ANY
 * THIRD PARTY CLAIMS AGAINST YOU.
 *
 * Lenya includes software developed by the Apache Software Foundation, W3C,
 * DOM4J Project, BitfluxEditor and Xopus.
 * </License>
 */
package org.apache.lenya.cms.cocoon.acting;

import org.apache.avalon.framework.configuration.Configurable;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.avalon.framework.parameters.Parameters;

import org.apache.cocoon.acting.AbstractComplementaryConfigurableAction;
import org.apache.cocoon.environment.Redirector;
import org.apache.cocoon.environment.Request;
import org.apache.cocoon.environment.Session;
import org.apache.cocoon.environment.Source;
import org.apache.cocoon.environment.SourceResolver;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.InputStreamReader;

import java.util.HashMap;
import java.util.Map;

import org.apache.cocoon.environment.ObjectModelHelper;


/**
 * DOCUMENT ME!
 *
 * @author Martin Lüthi
 * @version 2002.01.27
 */
public class EditorAction extends AbstractComplementaryConfigurableAction implements Configurable {
    /**
     * DOCUMENT ME!
     *
     * @param conf DOCUMENT ME!
     *
     * @throws ConfigurationException DOCUMENT ME!
     */
    public void configure(Configuration conf) throws ConfigurationException {
        super.configure(conf);
    }

    /**
     * At this time, the action does nothing more than provide the tempfilename to the sitmap
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
    public Map act(Redirector redirector, SourceResolver resolver, Map objectModel, String src,
        Parameters parameters) throws Exception {
        // Get request object
        Request request = ObjectModelHelper.getRequest(objectModel);

        if (request == null) {
            getLogger().error("No request object");

            return null;
        }

        // Get session
        Session session = request.getSession(true);

        if (session == null) {
            getLogger().error("No session object");

            return null;
        }

        String tempFile = (String) session.getAttribute("org.apache.lenya.cms.editor.HTMLForm.tempFile");
        getLogger().error("**** tempfile=" + tempFile);

        // prepare the return params
        String xmlErrorFlag = "";
        String xmlErrorMessage = "";

        if (tempFile != null) {
            // get the Document and copy it to the temporary file
            getLogger().debug("**** saving ****");

            Source source = resolver.resolve("cocoon:/performrequest");
            getLogger().debug("======= URL:" + source.getSystemId());

            BufferedReader in = new BufferedReader(new InputStreamReader(source.getInputStream()));
            BufferedWriter out = new BufferedWriter(new FileWriter(tempFile));
            String line;

            while ((line = in.readLine()) != null) {
                out.write(line + "\n");
            }

            in.close();
            out.close();

            HashMap actionMap = new HashMap();
            actionMap.put("tempFile", tempFile);
            actionMap.put("xmlErrorFlag", xmlErrorFlag);
            actionMap.put("xmlErrorMessage", xmlErrorMessage);

            return actionMap;
        }

        return null;
    }
}
