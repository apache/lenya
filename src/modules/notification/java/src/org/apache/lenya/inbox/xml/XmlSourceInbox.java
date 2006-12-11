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
package org.apache.lenya.inbox.xml;

import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;

import org.apache.avalon.framework.service.ServiceManager;
import org.apache.lenya.ac.AccessControlException;
import org.apache.lenya.ac.Group;
import org.apache.lenya.ac.Identifiable;
import org.apache.lenya.ac.User;
import org.apache.lenya.cms.cocoon.source.SourceUtil;
import org.apache.lenya.inbox.Inbox;
import org.apache.lenya.notification.Message;
import org.apache.lenya.notification.Notifier;
import org.apache.lenya.util.Assert;
import org.apache.lenya.xml.DocumentHelper;
import org.apache.lenya.xml.NamespaceHelper;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Inbox based on XML sources.
 */
public class XmlSourceInbox implements Inbox {

    private ServiceManager manager;
    private User user;

    /**
     * @param manager The service manager.
     * @param user The user.
     */
    public XmlSourceInbox(ServiceManager manager, User user) {
        this.manager = manager;
        this.user = user;
    }

    public synchronized void add(Message message) {
        messages().add(message);
        save();
    }

    public synchronized void remove(Message message) {
        messages().remove(message);
        save();
    }

    public Message[] getMessages() {
        List messages = messages();
        return (Message[]) messages.toArray(new Message[messages.size()]);
    }

    private List messages;

    protected List messages() {
        if (this.messages == null) {
            load();
        }
        return this.messages;
    }

    private long lastModified = -1;

    protected synchronized void load() {
        this.messages = new ArrayList();
        try {

            if (SourceUtil.exists(getSourceUri(), this.manager)) {

                this.lastModified = SourceUtil.getLastModified(getSourceUri(), this.manager);
                Document xml = SourceUtil.readDOM(getSourceUri(), this.manager);

                Assert.isTrue("document element is <inbox>", xml.getDocumentElement()
                        .getLocalName().equals("inbox"));
                NamespaceHelper helper = new NamespaceHelper(Notifier.NAMESPACE, "", xml);

                Element[] messageElements = helper.getChildren(xml.getDocumentElement(), "message");
                for (int i = 0; i < messageElements.length; i++) {
                    String senderId = messageElements[i].getAttribute("sender");
                    User sender = getUser(senderId);

                    Element recipientElement = helper.getFirstChild(messageElements[i],
                            "recipients");

                    Element[] userElements = helper.getChildren(recipientElement, "user");
                    Element[] groupElements = helper.getChildren(recipientElement, "group");

                    Identifiable[] recipients = new Identifiable[userElements.length
                            + groupElements.length];

                    for (int u = 0; u < userElements.length; u++) {
                        String id = userElements[u].getAttribute("id");
                        recipients[u] = getUser(id);
                    }

                    for (int g = 0; g < groupElements.length; g++) {
                        String id = groupElements[g].getAttribute("id");
                        recipients[userElements.length + g] = getGroup(id);
                    }

                    Element bodyElement = helper.getFirstChild(messageElements[i], "body");
                    Element bodyTextElement = helper.getFirstChild(bodyElement, "text");
                    String body = DocumentHelper.getSimpleElementText(bodyTextElement);
                    Element[] bodyParamElements = helper.getChildren(bodyElement, "param");
                    String[] bodyParams = new String[bodyParamElements.length];
                    for (int p = 0; p < bodyParamElements.length; p++) {
                        bodyParams[p] = DocumentHelper.getSimpleElementText(bodyParamElements[p]);
                    }

                    Element subjectElement = helper.getFirstChild(messageElements[i], "subject");
                    Element subjectTextElement = helper.getFirstChild(subjectElement, "text");
                    String subject = DocumentHelper.getSimpleElementText(subjectTextElement);
                    Element[] subjectParamElements = helper.getChildren(subjectElement, "param");
                    String[] subjectParams = new String[subjectParamElements.length];
                    for (int p = 0; p < subjectParamElements.length; p++) {
                        subjectParams[p] = DocumentHelper
                                .getSimpleElementText(subjectParamElements[p]);
                    }

                    Message message = new Message(subject, subjectParams, body, bodyParams, sender,
                            recipients);
                    this.messages.add(message);
                }
            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    protected User getUser(String id) throws AccessControlException {
        return this.user.getAccreditableManager().getUserManager().getUser(id);
    }

    protected Group getGroup(String id) throws AccessControlException {
        return this.user.getAccreditableManager().getGroupManager().getGroup(id);
    }

    private String sourceUri;

    protected String getSourceUri() {
        if (this.sourceUri == null) {
            String configUri = this.user.getAccreditableManager().getConfigurationCollectionUri();
            if (configUri.endsWith("/")) {
                configUri = configUri.substring(0, configUri.length() - 1);
            }
            this.sourceUri = configUri + "/inboxes/" + this.user.getId() + ".xml";
        }
        return this.sourceUri;
    }

    protected synchronized void save() {
        try {

            long newLastModified = SourceUtil.getLastModified(getSourceUri(), this.manager);
            if (this.lastModified > -1 && newLastModified > this.lastModified) {
                throw new RuntimeException("The inbox file [" + getSourceUri()
                        + "] has been changed externally and can't be saved.");
            }

            NamespaceHelper helper = buildXml();
            SourceUtil.writeDOM(helper.getDocument(), getSourceUri(), this.manager);
            this.lastModified = SourceUtil.getLastModified(getSourceUri(), this.manager);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    protected NamespaceHelper buildXml() throws ParserConfigurationException {
        NamespaceHelper helper = new NamespaceHelper(Notifier.NAMESPACE, "", "inbox");

        Message[] messages = getMessages();
        for (int i = 0; i < messages.length; i++) {
            Element messageElement = helper.createElement("message");
            helper.getDocument().getDocumentElement().appendChild(messageElement);
            User sender = (User) messages[i].getSender();
            messageElement.setAttribute("sender", sender.getId());

            Element recipientsElement = helper.createElement("recipients");
            messageElement.appendChild(recipientsElement);

            Identifiable[] recipients = messages[i].getRecipients();
            for (int r = 0; r < recipients.length; r++) {
                if (recipients[r] instanceof User) {
                    Element userElement = helper.createElement("user");
                    userElement.setAttribute("id", ((User) recipients[r]).getId());
                    recipientsElement.appendChild(userElement);
                } else if (recipients[r] instanceof Group) {
                    Element groupElement = helper.createElement("group");
                    groupElement.setAttribute("id", ((Group) recipients[r]).getId());
                    recipientsElement.appendChild(groupElement);
                }
            }

            Element subjectElement = helper.createElement("subject");
            messageElement.appendChild(subjectElement);
            Element subjectTextElement = helper.createElement("text", messages[i].getSubject());
            subjectElement.appendChild(subjectTextElement);
            String[] subjectParams = messages[i].getSubjectParameters();
            for (int p = 0; p < subjectParams.length; p++) {
                Element paramElement = helper.createElement("param", subjectParams[p]);
                subjectElement.appendChild(paramElement);
            }

            Element bodyElement = helper.createElement("body");
            messageElement.appendChild(bodyElement);
            Element bodyTextElement = helper.createElement("text", messages[i].getBody());
            bodyElement.appendChild(bodyTextElement);
            String[] bodyParams = messages[i].getBodyParameters();
            for (int p = 0; p < bodyParams.length; p++) {
                Element paramElement = helper.createElement("param", bodyParams[p]);
                bodyElement.appendChild(paramElement);
            }
        }
        return helper;
    }
}
