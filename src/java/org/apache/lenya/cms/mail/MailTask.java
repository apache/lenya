/*
 * $Id: MailTask.java,v 1.27 2003/04/09 17:26:59 andreas Exp $
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
package org.lenya.cms.mail;

//import com.sun.mail.smtp.SMTPMessage;
import org.apache.avalon.framework.parameters.Parameters;

import org.apache.log4j.Category;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.Text;

import org.lenya.cms.task.AbstractTask;
import org.lenya.xml.DocumentHelper;
import org.lenya.xml.NamespaceHelper;

import java.util.Arrays;
import java.util.Date;
import java.util.Properties;
import java.net.URL;
import org.lenya.net.SMTP;

/*
import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
*/

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
 * ><code> &lt;mail:mail xmlns:mail="http://www.lenya.org/2002/mail"&gt;<br/
 * > &#160;&#160;&lt;mail:server&gt;mail.yourhost.com&lt;/mail:server&gt;<br/
 * > &#160;&#160;...<br/
 * > &lt;/mail:mail&gt;<br/></code>
 *
 * @author <a href="mailto:ah@lenya.org">Andreas Hartmann</a>
 */
public class MailTask extends AbstractTask {
    static Category log = Category.getInstance(MailTask.class);
    public static final String ELEMENT_TO = "to";
    public static final String ELEMENT_CC = "cc";
    public static final String ELEMENT_BCC = "bcc";
    public static final String ELEMENT_SUBJECT = "subject";
    public static final String ELEMENT_BODY = "body";
    public static final String ELEMENT_FROM = "from";
    public static final String ELEMENT_SERVER = "server";
    public static final String PARAMETER_URI = "uri";
    public static final String NAMESPACE_URI = "http://www.lenya.org/2002/mail";

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

                Element elements[] = helper.getChildren(root);

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
    /*
    public void sendMail(String host, String from, String to, String cc, String bcc,
        String subject, String body) {
        log.debug("\nTrying to send mail:" +
            "\n-------------------------------------------------------------" + "\nhost:    " + to +
            "\n-------------------------------------------------------------" + "\nfrom:    " + to +
            "\n-------------------------------------------------------------" + "\nto:      " + to +
            "\n-------------------------------------------------------------" + "\ncc:      " + cc +
            "\n-------------------------------------------------------------" + "\nbcc:     " +
            bcc + "\n-------------------------------------------------------------" +
            "\nsubject: " + subject +
            "\n-------------------------------------------------------------" + "\nbody:\n" + body +
            "\n-------------------------------------------------------------\n\n");

        try {
            Properties props = new Properties();
            props.put("mail.smtp.host", host);

            Session mailSession = Session.getInstance(props, null);

            SMTPMessage pm = new SMTPMessage(mailSession);
            
            // avoid "quoted printable" problems
            pm.setAllow8bitMIME(true);

            // set from
            pm.setFrom(new InternetAddress(from));

            // set to
            pm.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));

            // set cc
            if (!cc.equals("")) {
                pm.setRecipients(Message.RecipientType.CC, InternetAddress.parse(cc));
            }

            // set bcc
            if (!bcc.equals("")) {
                pm.setRecipients(Message.RecipientType.BCC, InternetAddress.parse(bcc));
            }

            // set subject
            if (!subject.equals("")) {
                pm.setSubject(subject);
            }

            // set date
            pm.setSentDate(new Date());

            // set content
            if (!body.equals("")) {
                pm.setText(body);
            }

            // send mail
            // Transport trans = mailSession.getTransport("smtp");
            Transport.send(pm);

            // success
        } catch (Exception e) {
            log.error("Sending mail failed: ", e);
        }
    }
     */

}
