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
package org.apache.lenya.notification;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.apache.avalon.framework.context.Context;
import org.apache.avalon.framework.context.ContextException;
import org.apache.avalon.framework.context.Contextualizable;
import org.apache.avalon.framework.logger.AbstractLogEnabled;
import org.apache.avalon.framework.service.ServiceException;
import org.apache.avalon.framework.service.ServiceManager;
import org.apache.avalon.framework.service.Serviceable;
import org.apache.cocoon.components.ContextHelper;
import org.apache.cocoon.environment.Request;
import org.apache.cocoon.environment.Session;
import org.apache.cocoon.transformation.I18nTransformer;
import org.apache.excalibur.source.Source;
import org.apache.excalibur.source.SourceResolver;
import org.apache.lenya.ac.Group;
import org.apache.lenya.ac.Identifiable;
import org.apache.lenya.ac.User;
import org.apache.lenya.xml.DocumentHelper;
import org.apache.lenya.xml.NamespaceHelper;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Base class for notifier implementation.
 */
public abstract class AbstractNotifier extends AbstractLogEnabled implements Notifier, Serviceable,
        Contextualizable {

    public void notify(Message message) throws NotificationException {

        Set noDuplicates = new HashSet();

        Identifiable[] recipients = message.getRecipients();

        for (int i = 0; i < recipients.length; i++) {
            if (recipients[i] instanceof Group) {
                Group group = (Group) recipients[i];
                noDuplicates.addAll(Arrays.asList(group.getMembers()));
            } else {
                noDuplicates.add(recipients[i]);
            }
        }

        for (Iterator i = noDuplicates.iterator(); i.hasNext();) {
            Identifiable identifiable = (Identifiable) i.next();
            if (identifiable instanceof User) {
                User user = (User) identifiable;
                Message translatedMessage = translateMessage(user.getDefaultMenuLocale(), message);
                notify(user, translatedMessage);
            }
        }

    }

    protected abstract void notify(User user, Message translatedMessage)
            throws NotificationException;

    protected Element createTranslateElement(NamespaceHelper helper, Text text) {
        Element translateElement = helper.createElement("translate");
        Element textElement = helper.createElement("text", text.getText());
        translateElement.appendChild(textElement);

        Text[] params = text.getParameters();
        for (int i = 0; i < params.length; i++) {
            Element paramElement;
            if (params[i].translate()) {
                if (params[i].getParameters().length > 0) {
                    getLogger().warn("Parameters can't have parameters (not supported by Cocoon i18n): " + text);
                }
                paramElement = helper.createElement("param");
                Element paramTextElement = helper.createElement("text", params[i].getText());
                paramElement.appendChild(paramTextElement);
            } else {
                paramElement = helper.createElement("param", params[i].getText());
            }
            translateElement.appendChild(paramElement);
        }
        return translateElement;
    }

    protected Message translateMessage(String locale, Message message) throws NotificationException {

        SourceResolver resolver = null;
        Source source = null;
        try {

            NamespaceHelper helper = new NamespaceHelper(NAMESPACE, "not", "message");
            Document doc = helper.getDocument();
            NamespaceHelper i18nHelper = new NamespaceHelper(I18nTransformer.I18N_NAMESPACE_URI,
                    "i18n", doc);

            Element docElement = doc.getDocumentElement();

            Element subjectElement = helper.createElement("subject");
            docElement.appendChild(subjectElement);
            Element i18nTranslateSubjectElement = createTranslateElement(i18nHelper, message
                    .getSubjectText());
            subjectElement.appendChild(i18nTranslateSubjectElement);

            Element bodyElement = helper.createElement("body");
            docElement.appendChild(bodyElement);
            Element i18nTranslateBodyElement = createTranslateElement(i18nHelper, message
                    .getBodyText());
            bodyElement.appendChild(i18nTranslateBodyElement);

            Session session = this.request.getSession();
            session.setAttribute("notification.dom", doc);

            resolver = (SourceResolver) this.manager.lookup(SourceResolver.ROLE);
            source = resolver.resolveURI("cocoon://modules/notification/message/" + locale, null,
                    null);

            if (source.exists()) {
                doc = DocumentHelper.readDocument(source.getInputStream());
                helper = new NamespaceHelper(NAMESPACE, "not", doc);

                subjectElement = helper.getFirstChild(doc.getDocumentElement(), "subject");
                String subject = DocumentHelper.getSimpleElementText(subjectElement);

                bodyElement = helper.getFirstChild(doc.getDocumentElement(), "body");
                String body = DocumentHelper.getSimpleElementText(bodyElement);

                return new Message(new Text(subject, false), new Text(body, false), message
                        .getSender(), message.getRecipients());
            } else {
                // this happens in the test
                getLogger().info("cocoon protocol not available, not translating message");
                return message;
            }
        } catch (Exception e) {
            throw new NotificationException(e);
        } finally {
            if (resolver != null) {
                if (source != null) {
                    resolver.release(source);
                }
                this.manager.release(resolver);
            }
        }

    }

    protected ServiceManager manager;

    public void service(ServiceManager manager) throws ServiceException {
        this.manager = manager;
    }

    private Request request;

    public void contextualize(Context context) throws ContextException {
        this.request = ContextHelper.getRequest(context);
    }

}
