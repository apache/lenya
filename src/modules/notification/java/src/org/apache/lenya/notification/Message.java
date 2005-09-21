/*
 * Copyright  1999-2005 The Apache Software Foundation
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
package org.apache.lenya.notification;

/**
 * A notification message.
 */
public class Message {

    private String subject;
    private String[] subjectParams;
    private String body;
    private String[] bodyParams;

    /**
     * Ctor.
     * @param subject The subject.
     * @param subjectParams The subject parameters.
     * @param body The body.
     * @param bodyParams The body parameters.
     */
    public Message(String subject, String[] subjectParams, String body, String[] bodyParams) {
        this.subject = subject;
        this.subjectParams = subjectParams;
        this.body = body;
        this.bodyParams = bodyParams;
    }

    /**
     * Determine if this message has parameters
     * @return true if the message has parameters
     */
    public boolean hasBodyParameters() {
        return bodyParams != null && bodyParams.length > 0;
    }

    /**
     * Retrieve the message content
     * @return the message
     */
    public String getBody() {
        return body;
    }

    /**
     * Retrieve the parameters for this message
     * @return the parameters
     */
    public String[] getBodyParameters() {
        return bodyParams;
    }

    /**
     * Determine if this message has parameters
     * @return true if the message has parameters
     */
    public boolean hasSubjectParameters() {
        return subjectParams != null && subjectParams.length > 0;
    }

    /**
     * Retrieve the message subject
     * @return the subject
     */
    public String getSubject() {
        return subject;
    }

    /**
     * Retrieve the parameters for this message
     * @return the parameters
     */
    public String[] getSubjectParameters() {
        return subjectParams;
    }

}