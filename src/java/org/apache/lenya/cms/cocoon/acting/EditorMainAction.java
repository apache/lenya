/*
$Id
<License>

 ============================================================================
                   The Apache Software License, Version 1.1
 ============================================================================

 Copyright (C) 1999-2003 The Apache Software Foundation. All rights reserved.

 Redistribution and use in source and binary forms, with or without modifica-
 tion, are permitted provided that the following conditions are met:

 1. Redistributions of  source code must  retain the above copyright  notice,
    this list of conditions and the following disclaimer.

 2. Redistributions in binary form must reproduce the above copyright notice,
    this list of conditions and the following disclaimer in the documentation
    and/or other materials provided with the distribution.

 3. The end-user documentation included with the redistribution, if any, must
    include  the following  acknowledgment:  "This product includes  software
    developed  by the  Apache Software Foundation  (http://www.apache.org/)."
    Alternately, this  acknowledgment may  appear in the software itself,  if
    and wherever such third-party acknowledgments normally appear.

 4. The names "Apache Lenya" and  "Apache Software Foundation"  must  not  be
    used to  endorse or promote  products derived from  this software without
    prior written permission. For written permission, please contact
    apache@apache.org.

 5. Products  derived from this software may not  be called "Apache", nor may
    "Apache" appear  in their name,  without prior written permission  of the
    Apache Software Foundation.

 THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED WARRANTIES,
 INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 FITNESS  FOR A PARTICULAR  PURPOSE ARE  DISCLAIMED.  IN NO  EVENT SHALL  THE
 APACHE SOFTWARE  FOUNDATION  OR ITS CONTRIBUTORS  BE LIABLE FOR  ANY DIRECT,
 INDIRECT, INCIDENTAL, SPECIAL,  EXEMPLARY, OR CONSEQUENTIAL  DAMAGES (INCLU-
 DING, BUT NOT LIMITED TO, PROCUREMENT  OF SUBSTITUTE GOODS OR SERVICES; LOSS
 OF USE, DATA, OR  PROFITS; OR BUSINESS  INTERRUPTION)  HOWEVER CAUSED AND ON
 ANY  THEORY OF LIABILITY,  WHETHER  IN CONTRACT,  STRICT LIABILITY,  OR TORT
 (INCLUDING  NEGLIGENCE OR  OTHERWISE) ARISING IN  ANY WAY OUT OF THE  USE OF
 THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

 This software  consists of voluntary contributions made  by many individuals
 on  behalf of the Apache Software  Foundation and was  originally created by
 Michael Wechner <michi@apache.org>. For more information on the Apache Soft-
 ware Foundation, please see <http://www.apache.org/>.

 Lenya includes software developed by the Apache Software Foundation, W3C,
 DOM4J Project, BitfluxEditor, Xopus, and WebSHPINX.
</License>
*/
package org.apache.lenya.cms.cocoon.acting;

import org.apache.avalon.framework.configuration.Configurable;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.avalon.framework.parameters.Parameters;

import org.apache.cocoon.acting.AbstractComplementaryConfigurableAction;
import org.apache.cocoon.environment.Context;
import org.apache.cocoon.environment.ObjectModelHelper;
import org.apache.cocoon.environment.Redirector;
import org.apache.cocoon.environment.Request;
import org.apache.cocoon.environment.Session;
import org.apache.cocoon.environment.SourceResolver;

import org.apache.excalibur.source.Source;
import org.apache.excalibur.xml.sax.SAXParser;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.CharArrayReader;
import java.io.File;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.io.StringReader;

import java.util.HashMap;
import java.util.Map;


/**
 * $Id: EditorMainAction.java,v 1.21 2003/06/30 11:46:29 andreas Exp $
 *
 * @author Martin L?thi
 * @version 2002.01.22
 */
public class EditorMainAction extends AbstractComplementaryConfigurableAction
    implements Configurable {
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

        // Get request object
        Context context = ObjectModelHelper.getContext(objectModel);

        // the absolute path where Cocoon resides
        //     String xxx = context.getRealPath("/");
        //     getLogger().error("=======> context real path ="+xxx);
        //getLogger().error("=======> editfilename ="+parameters.getParameter("editfilename"));
        // Get uri
        String request_uri = request.getRequestURI();
        String sitemap_uri = request.getSitemapURI();
        String action = request.getParameter("action");
        String save = request.getParameter("save");
        getLogger().debug("**** request-uri=" + request_uri);
        getLogger().debug("**** sitemap-uri=" + sitemap_uri);
        getLogger().debug("**** action=" + action);

        // prepare the return params
        String xmlErrorFlag = "";
        String xmlErrorMessage = "";

        if ((save != null) && save.equals("Save")) {
            // get the Document and copy it to the temporary file
            getLogger().debug("**** saving ****");

            Source source = resolver.resolveURI("cocoon:/saveedit");
            getLogger().debug("======= URL:" + source.getURI());

            String editFile = (String) session.getAttribute(
                    "org.apache.lenya.cms.editor.HTMLForm.editFile");
            getLogger().debug("======= Saving to :" + editFile);

            BufferedReader in = new BufferedReader(new InputStreamReader(source.getInputStream()));

            // first, copy to a temporary string buffer (so we can get it back later, and more than once!)
            StringBuffer trans = new StringBuffer();
            String line;

            while ((line = in.readLine()) != null) {
                // we need this in order to let EditOnPro save the XHTML markup:
                line = org.apache.lenya.util.StringUtil.replace(line, "&lt;", "<");
                line = org.apache.lenya.util.StringUtil.replace(line, "&gt;", ">");
                trans.append(line + "\n");
            }

            getLogger().debug("----- Contents of the StringBuffer (trans): " + trans.toString());

            // check well-formedness
            SAXParser parser = (SAXParser) this.manager.lookup(SAXParser.ROLE); // get the cocoon parser

            try {
                InputSource iS = new InputSource(new CharArrayReader(trans.toString().toCharArray()));

                if (iS == null) {
                    getLogger().error("----- InputStream is null!");
                } else {
                    //parser.setContentHandler(new AbstractXMLConsumer() { }); // the parser must have a content handler, it will not be used
                    parser.parse(iS, new org.xml.sax.helpers.DefaultHandler());
                }
            } catch (SAXException saxE) { // this is the exception we want to catch
                xmlErrorFlag = "X"; // set the flag for signalling to the page's stylesheet (body.xsl)
                xmlErrorMessage = saxE.getMessage(); // this will also be sent to the stylesheet
                getLogger().debug("----- SAX-Exception (message): " + saxE.getMessage());
                getLogger().debug("----- SAX-Exception (stacktrace): ", saxE.getException());

                String tempFile = (String) session.getAttribute(
                        "org.apache.lenya.cms.editor.HTMLForm.tempFile");

                if (tempFile != null) {
                    in.close();

                    HashMap actionMap = new HashMap();
                    actionMap.put("tempFile", tempFile);
                    actionMap.put("xmlErrorFlag", xmlErrorFlag);
                    actionMap.put("xmlErrorMessage", xmlErrorMessage);

                    return actionMap;
                }

                return null;
            }catch (Exception e) { // something went wrong
                getLogger().error("----- Exception occured: ", e);

                return null;
            }

            // then, copy the string buffer to the temporary file  
            StringReader in2 = new StringReader(trans.toString());
            BufferedWriter out = new BufferedWriter(new FileWriter(editFile));
            int c;

            while ((c = in2.read()) != -1) {
                out.write((char) c);
            }

            in.close();
            in2.close();
            out.close();

            return null;
        } else if ((action != null) && action.equals("request")) {
            getLogger().debug("**** request (do nothing) ****");

            HashMap actionMap = new HashMap();

            return actionMap;
        } else { // we are not saving

            // here comes the checkout, revision control aso.
            boolean checkout = true;

            if (checkout) {
                String formeditorPath = context.getRealPath("formeditor");
                String tempFile = formeditorPath + request.getRequestURI();

                String lenyaPath = "lenya/pubs/ethz-mat/docs/ethz/mat/";
                String editFile = context.getRealPath("/") + lenyaPath + request.getSitemapURI();

                getLogger().debug("**** tempfile=" + tempFile);
                getLogger().debug("**** editfile=" + editFile);

                File tf = new File(tempFile);
                boolean success = new File(tf.getParent()).mkdirs();

                // get the Document and copy it to the temporary file
                Source source = resolver.resolveURI("cocoon:/" + request.getSitemapURI() + ".temp");
                getLogger().debug("======= URL:" + source.getURI());

                BufferedReader in = new BufferedReader(new InputStreamReader(
                            source.getInputStream()));
                BufferedWriter out = new BufferedWriter(new FileWriter(tempFile));
                String line;

                while ((line = in.readLine()) != null) {
                    out.write(line + "\n");
                }

                in.close();
                out.close();

                session.setAttribute("org.apache.lenya.cms.editor.HTMLForm.tempFile", tempFile);
                session.setAttribute("org.apache.lenya.cms.editor.HTMLForm.editFile", editFile);

                HashMap actionMap = new HashMap();
                actionMap.put("tempFile", tempFile);
                actionMap.put("editFile", editFile);

                return actionMap;
            }
        }

        return null;
    }
}
