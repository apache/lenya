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
            Element i18nTranslateSubjectElement = i18nHelper.createElement("translate");
            subjectElement.appendChild(i18nTranslateSubjectElement);
            Element subjectI18nElement = i18nHelper.createElement("text", message.getSubject());
            i18nTranslateSubjectElement.appendChild(subjectI18nElement);

            String[] subjectParams = message.getSubjectParameters();
            for (int i = 0; i < subjectParams.length; i++) {
                Element paramElement = i18nHelper.createElement("param", subjectParams[i]);
                i18nTranslateSubjectElement.appendChild(paramElement);
            }

            Element bodyElement = helper.createElement("body");
            docElement.appendChild(bodyElement);
            Element i18nTranslateElement = i18nHelper.createElement("translate");
            bodyElement.appendChild(i18nTranslateElement);
            Element bodyI18nElement = i18nHelper.createElement("text", message.getBody());
            i18nTranslateElement.appendChild(bodyI18nElement);

            String[] msgParams = message.getBodyParameters();
            for (int i = 0; i < msgParams.length; i++) {
                Element paramElement = i18nHelper.createElement("param", msgParams[i]);
                i18nTranslateElement.appendChild(paramElement);
            }

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

                return new Message(subject, new String[0], body, new String[0],
                        message.getSender(), message.getRecipients());
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
