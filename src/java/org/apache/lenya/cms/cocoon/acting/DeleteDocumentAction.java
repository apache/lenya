/*
 * $Id: DeleteDocumentAction.java,v 1.6 2003/02/20 13:40:40 gregor Exp $
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

import org.apache.avalon.framework.configuration.Configurable;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.avalon.framework.parameters.Parameters;

import org.apache.cocoon.acting.AbstractComplementaryConfigurableAction;
import org.apache.cocoon.environment.Redirector;
import org.apache.cocoon.environment.Request;
import org.apache.cocoon.environment.SourceResolver;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

import java.util.Map;
import org.apache.cocoon.environment.ObjectModelHelper;


/**
 * DOCUMENT ME!
 *
 * @author Michael Wechner
 * @version 2002.02.27
 */
public class DeleteDocumentAction extends AbstractComplementaryConfigurableAction
    implements Configurable {
    private String treeAuthoringPath = null;
    private String docsPath = null;

    /**
     * DOCUMENT ME!
     *
     * @param conf DOCUMENT ME!
     *
     * @throws ConfigurationException DOCUMENT ME!
     */
    public void configure(Configuration conf) throws ConfigurationException {
        super.configure(conf);

        treeAuthoringPath = conf.getChild("tree-authoring").getAttribute("href");
        docsPath = conf.getChild("docs").getAttribute("href");
        getLogger().debug("CONFIGURATION:\nAUTHORING PATH OF TREE=" + treeAuthoringPath);
    }

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
        org.apache.cocoon.environment.Source input_source = resolver.resolve("");
        String sitemapParentPath = input_source.getSystemId();
        sitemapParentPath = sitemapParentPath.substring(5); // Remove "file:" protocoll
        getLogger().debug("PARENT PATH OF SITEMAP: " + sitemapParentPath);

        // Get request object
        Request request = ObjectModelHelper.getRequest(objectModel);

        if (request == null) {
            getLogger().error("No request object");

            return null;
        }

        return null;
    }

    /**
     * DOCUMENT ME!
     *
     * @param parentid DOCUMENT ME!
     * @param childid DOCUMENT ME!
     * @param childname DOCUMENT ME!
     * @param childtype DOCUMENT ME!
     * @param doctype DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public boolean validate(String parentid, String childid, String childname, String childtype,
        String doctype) {
        getLogger().debug(".validate(): parentid=" + parentid + " ; childid=" + childid +
            " ; childname=" + childname + " ; childtype=" + childtype + " ; doctype=" + doctype);

        if ((childid.indexOf(" ") >= 0) || (childid.length() == 0)) {
            return false;
        }

        if (childname.length() == 0) {
            return false;
        }

        return true;
    }

    /**
     * Write input stream to Logger (for debugging)
     *
     * @param in an <code>InputStream</code> value
     *
     * @return an <code>InputStream</code> value
     *
     * @exception Exception if an error occurs
     */
    private InputStream intercept(InputStream in) throws Exception {
        byte[] buffer = new byte[1024];
        int bytes_read;
        ByteArrayOutputStream bufferOut = new ByteArrayOutputStream();

        while ((bytes_read = in.read(buffer)) != -1) {
            bufferOut.write(buffer, 0, bytes_read);
        }

        getLogger().debug("Intercepted Input Stream:\n\n" + bufferOut.toString());

        return new ByteArrayInputStream(bufferOut.toByteArray());
    }

    /**
     * Write output stream to Logger (for debugging)
     *
     * @param out an <code>OutputStream</code> value
     *
     * @return an <code>OutputStream</code> value
     *
     * @exception Exception if an error occurs
     */
    private OutputStream intercept(OutputStream out) throws Exception {
        return null;
    }
}
