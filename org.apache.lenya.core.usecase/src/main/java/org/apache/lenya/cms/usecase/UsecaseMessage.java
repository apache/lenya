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
package org.apache.lenya.cms.usecase;

import org.apache.lenya.util.Assert;

/**
 * A message in a usecase. This an encapsulation for an internationalizable
 * message, containing a message content and potentially some parameters. The
 * parameters correspond to the placeholders in dictionary entries.
 * 
 * @version $Id$
 */
public class UsecaseMessage {

    private String message;
    private String[] params;

    /**
     * Ctor.
     * @param _message The message.
     */
    public UsecaseMessage(String _message) {
        Assert.notNull("message", _message);
        this.message = _message;
    }

    /**
     * Ctor.
     * @param _message The message.
     * @param _params The parameters.
     */
    public UsecaseMessage(String _message, String[] _params) {
        this(_message);

        Assert.notNull("params", _params);
        for (int i = 0; i < _params.length; i++) {
            Assert.notNull("params[" + i + "]", _params[i]);
        }
        this.params = (String[])_params.clone();
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
     * Returns the parameters of this message. If no parameters are set,
     * an empty String array is returned.
     * @return the parameters
     */
    public String[] getParameters() {
        return this.params == null ? new String[0] : this.params;
    }

    /**
     * @return A string representation, the parameters are included as a comma-separated list.
     */
    public String toString() {
        StringBuffer msg = new StringBuffer(getMessage());
        String[] params = getParameters();
        for (int i = 0; i < params.length; i++) {
            msg.append(", " + params[i]);
        }
        return msg.toString();
    }
}
