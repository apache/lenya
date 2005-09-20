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
    
    private String message;
    private String[] params;

    /**
     * Ctor.
     * @param message The message.
     * @param params The parameters.
     */
    public Message(String message, String[] params) {
        this.message = message;
        this.params = params;
    }

    /**
     * Determine if this message has parameters
     * @return true if the message has parameters
     */
    public boolean hasParameters() {
        return params != null && params.length > 0;
    }

    /**
     * Retrieve the message content
     * @return the message
     */
    public String getMessage() {
        return message;
    }

    /**
     * Retrieve the parameters for this message
     * @return the parameters
     */
    public String[] getParameters() {
        return params;
    }

    /** 
     * Accomodates scripts which do no care about parameters
     * @return a string representation disregarding any parameters
     */
    public String toString() {
        return message;
    }

}