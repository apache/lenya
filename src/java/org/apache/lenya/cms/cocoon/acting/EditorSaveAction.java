/*
 * $Id: EditorSaveAction.java,v 1.5 2003/02/07 12:14:08 ah Exp $
 * <License>
 * The Apache Software License
 *
 * Copyright (c) 2002 wyona. All rights reserved.
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
 *    by wyona (http://www.wyona.org)"
 *
 * 4. The name "wyona" must not be used to endorse or promote products derived from
 *    this software without prior written permission. For written permission, please
 *    contact contact@wyona.org
 *
 * 5. Products derived from this software may not be called "wyona" nor may "wyona"
 *    appear in their names without prior written permission of wyona.
 *
 * 6. Redistributions of any form whatsoever must retain the following acknowledgment:
 *    "This product includes software developed by wyona (http://www.wyona.org)"
 *
 * THIS SOFTWARE IS PROVIDED BY wyona "AS IS" WITHOUT ANY WARRANTY EXPRESS OR IMPLIED,
 * INCLUDING THE WARRANTY OF NON-INFRINGEMENT AND THE IMPLIED WARRANTIES OF MERCHANTI-
 * BILITY AND FITNESS FOR A PARTICULAR PURPOSE. wyona WILL NOT BE LIABLE FOR ANY DAMAGES
 * SUFFERED BY YOU AS A RESULT OF USING THIS SOFTWARE. IN NO EVENT WILL wyona BE LIABLE
 * FOR ANY SPECIAL, INDIRECT OR CONSEQUENTIAL DAMAGES OR LOST PROFITS EVEN IF wyona HAS
 * BEEN ADVISED OF THE POSSIBILITY OF THEIR OCCURRENCE. wyona WILL NOT BE LIABLE FOR ANY
 * THIRD PARTY CLAIMS AGAINST YOU.
 *
 * Wyona includes software developed by the Apache Software Foundation, W3C,
 * DOM4J Project, BitfluxEditor and Xopus.
 * </License>
 */
package org.wyona.cms.cocoon.acting;

import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.avalon.framework.parameters.Parameters;
import org.apache.avalon.framework.thread.ThreadSafe;

import org.apache.cocoon.Constants;
import org.apache.cocoon.acting.AbstractValidatorAction;
import org.apache.cocoon.environment.Context;
import org.apache.cocoon.environment.Redirector;
import org.apache.cocoon.environment.Request;
import org.apache.cocoon.environment.Session;
import org.apache.cocoon.environment.SourceResolver;
import org.apache.cocoon.util.Tokenizer;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;


/**
 * DOCUMENT ME!
 *
 * @author Martin Lüthi
 * @version 2002.01.10
 */
public class EditorSaveAction extends AbstractValidatorAction implements ThreadSafe {
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
    public Map act(Redirector redirector, SourceResolver resolver, Map objectModel, String src,
        Parameters parameters) throws Exception {
        // Get request object
        Request request = (Request) objectModel.get(Constants.REQUEST_OBJECT);

        if (request == null) {
            getLogger().error("No request object");

            return null;
        }

        //     String editfilename = (String)request.getParameter("filename");
        //     String tempfilename = (String)request.getParameter("tempfilename");
        Session session = request.getSession(true);

        if (session == null) {
            getLogger().error("No session object");

            return null;
        }

        String tempfilename = (String) session.getAttribute(
                "org.wyona.cms.cocoon.acting.EditorTempfile");
        String editfilename = (String) session.getAttribute(
                "org.wyona.cms.cocoon.acting.EditorEditfile");
        String finalredirect = (String) session.getAttribute(
                "org.wyona.cms.cocoon.acting.EditorFinalRedirect");

        //    String editfilename=request.getParameter("filename");
        getLogger().error("=======> editfilename   =" + editfilename);
        getLogger().error("=======> tempfilename   =" + tempfilename);
        getLogger().error("=======> finalRedirect   =" + finalredirect);

        if (editfilename != null) {
            try {
                File editFile = new File(editfilename);

                //         if (!editFile.isAbsolute()) {
                //           editfilename = context.getRealPath("")+File.separator+editfilename;
                //           getLogger().error("=======> new editfilename   ="+editfilename);
                //         }
                // good old Java has no file copy method, so we do it the hard way
                InputStream is = new FileInputStream(tempfilename);
                OutputStream os = new FileOutputStream(editfilename);

                byte[] bytes_buffer = new byte[1024];
                int bytes_read;

                while ((bytes_read = is.read(bytes_buffer)) >= 0) {
                    os.write(bytes_buffer, 0, bytes_read);
                }

                getLogger().error("------------------------------------------ Writing file  " +
                    editfilename);

                HashMap sitemapParams = new HashMap();
                sitemapParams.put("tempfilename", tempfilename);
                sitemapParams.put("finalRedirect", finalredirect);

                return sitemapParams;
            } catch (Exception e) {
                getLogger().error("filename not existing " + editfilename + " " + e);

                return null;
            }
        }

        return null;
    }
}
