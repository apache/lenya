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

/* $Id: MailTask.java,v 1.34 2004/03/01 16:18:27 gregor Exp $  */

package org.apache.lenya.cms.mail;


import java.net.URL;
import java.util.Arrays;

import org.apache.avalon.framework.parameters.Parameters;
import org.apache.lenya.cms.task.AbstractTask;
import org.apache.lenya.net.SMTP;
import org.apache.lenya.xml.DocumentHelper;
import org.apache.lenya.xml.NamespaceHelper;
import org.apache.log4j.Category;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.Text;


/**
 * A task that sends an e-mail. Each parameter can either be provided as a task parameter or
 * extracted from an XML document. If the parameter "uri" starts with a <code>http://</code> or
 * <code>ftp://</code> prefix, the absolute URI is used. If not, the URI is interpreted as
 * relative to the local publication. <br/
 * ><br/
 * > The task parameters are:<br/
 * ><code><strong>uri</strong></code>: the URI to get the XML file from<br/
 * ><code><strong>server</strong></code>: the SMTP server<br/
 * ><code><strong>from</strong></code>:<br/
 * ><code><strong>to</strong></code>:<br/
 * ><code><strong>cc</strong></code>:<br/
 * ><code><strong>bcc</strong></code>:<br/
 * ><code><strong>subject</strong></code>:<br/
 * ><code><strong>body</strong></code>:<br/
 * ><br/
 * > All parameters are optional. If the uri parameter is provided, the document is fetched from
 * the URI and the parameters are extracted. Task parameters have a higher priority than elements
 * of the document. <br/
 * ><br/
 * > The document has the following form:<br/
 * ><br/
 * ><code> &lt;mail:mail xmlns:mail="http://apache.org/cocoon/lenya/mail/1.0"&gt;<br/
 * > &#160;&#160;&lt;mail:server&gt;mail.yourhost.com&lt;/mail:server&gt;<br/
 * > &#160;&#160;...<br/
 * > &lt;/mail:mail&gt;<br/></code>
 */
public class MailTask extends AbstractTask {
    private static Category log = Category.getInstance(MailTask.class);
    
    public static final String ELEMENT_TO = "to";
    public static final String ELEMENT_CC = "cc";
    public static final String ELEMENT_BCC = "bcc";
    public static final String ELEMENT_SUBJECT = "subject";
    public static final String ELEMENT_BODY = "body";
    public static final String ELEMENT_FROM = "from";
    public static final String ELEMENT_SERVER = "server";
    public static final String PARAMETER_URI = "uri";
    public static final String NAMESPACE_URI = "http://apache.org/cocoon/lenya/mail/1.0";

    /**
     * DOCUMENT ME!
     *
     * @param contextPath DOCUMENT ME!
     */
    public void execute(String contextPath) {
        log.debug("\n---------------------------" + "\n- Sending mail" +
            "\n---------------------------");

        try {
            Parameters taskParameters = new Parameters();

            String uri = getParameters().getParameter(PARAMETER_URI, "");
            log.debug("\nURI: " + uri);

            if (!uri.equals("")) {
                // generate absolute URI from relative URI
                if (!uri.startsWith("http://") && !uri.startsWith("ftp://") &&
                        !uri.startsWith("file://")) {
                    String absoluteUri = "http://127.0.0.1";
                    String serverPort = getParameters().getParameter(PARAMETER_SERVER_PORT, "");

                    if (!serverPort.equals("")) {
                        absoluteUri += (":" + Integer.parseInt(serverPort));
                    }

                    absoluteUri += (getParameters().getParameter(PARAMETER_CONTEXT_PREFIX) +
                    getParameters().getParameter(PARAMETER_PUBLICATION_ID) + uri);
                    uri = absoluteUri;
                }

                Document document = DocumentHelper.readDocument(new URL(uri));
                Element root = document.getDocumentElement();

                NamespaceHelper helper = new NamespaceHelper(NAMESPACE_URI, "mail", document);

                String[] keys = {
                    ELEMENT_SERVER, ELEMENT_FROM, ELEMENT_TO, ELEMENT_CC, ELEMENT_BCC,
                    ELEMENT_SUBJECT, ELEMENT_BODY
                };

                Element[] elements = helper.getChildren(root);

                for (int i = 0; i < elements.length; i++) {
                    if (elements[i].getChildNodes().getLength() > 0) {
                        Node firstChild = elements[i].getChildNodes().item(0);

                        if (firstChild instanceof Text) {
                            Text text = (Text) firstChild;
                            String key = elements[i].getLocalName();

                            if (Arrays.asList(keys).contains(key)) {
                                taskParameters.setParameter(key, text.getNodeValue());
                            }
                        }
                    }
                }
            }

            // task parameters have a higher priority than XML elements
            taskParameters = taskParameters.merge(getParameters());

            sendMail(taskParameters.getParameter(ELEMENT_SERVER),
                taskParameters.getParameter(ELEMENT_FROM), taskParameters.getParameter(ELEMENT_TO),
                taskParameters.getParameter(ELEMENT_CC, ""),
                taskParameters.getParameter(ELEMENT_BCC, ""),
                taskParameters.getParameter(ELEMENT_SUBJECT, ""),
                taskParameters.getParameter(ELEMENT_BODY, ""));
        } catch (Exception e) {
            log.error("Sending mail failed: ", e);
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param host DOCUMENT ME!
     * @param from DOCUMENT ME!
     * @param to DOCUMENT ME!
     * @param cc DOCUMENT ME!
     * @param bcc DOCUMENT ME!
     * @param subject DOCUMENT ME!
     * @param body DOCUMENT ME!
     */
    public void sendMail(String host, String from, String to, String cc, String bcc,
        String subject, String body) {
        SMTP smtp = new SMTP();
        smtp.send(from, to, cc, bcc, subject, body);
    }

    /**
     * DOCUMENT ME!
     *
     * @param host DOCUMENT ME!
     * @param from DOCUMENT ME!
     * @param to DOCUMENT ME!
     * @param cc DOCUMENT ME!
     * @param bcc DOCUMENT ME!
     * @param subject DOCUMENT ME!
     * @param body DOCUMENT ME!
     */
}
