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
import java.util.Date;

import org.apache.lenya.ac.Identifiable;
import org.apache.lenya.util.Assert;

/**
 * A notification message.
 */
public class Message {

    private Text subject;
    private Text body;

    private Identifiable sender;
    private Identifiable[] recipients;
    private Date time;

    /**
     * @param subject The subject.
     * @param body The body.
     * @param sender The sender.
     * @param recipients The recipients.
     */
    public Message(Text subject, Text body, Identifiable sender, Identifiable[] recipients) {
        Assert.notNull("subject", subject);
        Assert.notNull("body", body);
        Assert.notNull("sender", sender);
        Assert.notNull("recipients", recipients);
        this.subject = subject;
        this.body = body;
        this.sender = sender;
        this.recipients = recipients;
        this.time = new Date();
    }

    /**
     * Constructor.
     * @param subject The subject.
     * @param subjectParams The subject parameters.
     * @param body The body.
     * @param bodyParams The body parameters.
     * @param sender The sender.
     * @param recipients The recipients.
     * @deprecated Use {@link #Message(Text, Text, Identifiable, Identifiable[])} instead.
     */
    public Message(String subject, String[] subjectParams, String body, String[] bodyParams,
            Identifiable sender, Identifiable[] recipients) {
        this(new Text(subject, subjectParams), new Text(body, bodyParams), sender, recipients);
    }

    /**
     * Determine if this message has parameters
     * @return true if the message has parameters
     */
    public boolean hasBodyParameters() {
        Text[] params = this.body.getParameters();
        return params != null && params.length > 0;
    }

    /**
     * Retrieve the message content
     * @return the message
     * @deprecated Use {@link #getBodyText()} instead.
     */
    public String getBody() {
        return this.body.getText();
    }

    /**
     * Retrieve the parameters for this message
     * @return the parameters
     * @deprecated Use {@link #getBodyText()} instead.
     */
    public String[] getBodyParameters() {
        return convert(this.body.getParameters());
    }

    protected String[] convert(Text[] textParams) {
        String[] params = new String[textParams.length];
        for (int i = 0; i < params.length; i++) {
            params[i] = textParams[i].getText();
        }
        return params;
    }

    /**
     * Determine if this message has parameters
     * @return true if the message has parameters
     * @deprecated Use {@link #getSubjectText()} instead.
     */
    public boolean hasSubjectParameters() {
        Text[] params = this.subject.getParameters();
        return params != null && params.length > 0;
    }

    /**
     * Retrieve the message subject
     * @return the subject
     * @deprecated Use {@link #getSubjectText()} instead.
     */
    public String getSubject() {
        return this.subject.getText();
    }

    /**
     * Retrieve the parameters for this message
     * @return the parameters
     * @deprecated Use {@link #getSubjectText()} instead.
     */
    public String[] getSubjectParameters() {
        return convert(this.subject.getParameters());
    }

    /**
     * @return The sender.
     */
    public Identifiable getSender() {
        return this.sender;
    }

    /**
     * @return The recipients.
     */
    public Identifiable[] getRecipients() {
        // don't expose the internal array
        return (Identifiable[]) Arrays.asList(this.recipients).toArray(
                new Identifiable[this.recipients.length]);
    }

    /**
     * @return The time when the message was sent.
     */
    public Date getTime() {
        return (Date)this.time.clone();
    }

    /**
     * @return The message subject.
     */
    public Text getSubjectText() {
        return this.subject;
    }

    /**
     * @return The message body.
     */
    public Text getBodyText() {
        return this.body;
    }

}
