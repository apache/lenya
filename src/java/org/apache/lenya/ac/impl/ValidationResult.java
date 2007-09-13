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
package org.apache.lenya.ac.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.lenya.ac.Message;


/**
 * Validation result.
 */
public class ValidationResult {
    
    private boolean succeeded;

    /**
     * @param succeeded if the validation was successful.
     */
    public ValidationResult(boolean succeeded) {
        this.succeeded = succeeded;
    }
    
    /**
     * @param messages The error messages. If the array is empty, the validation succeeded.
     */
    public ValidationResult(Message[] messages) {
        this(messages.length == 0);
        for (int i = 0; i < messages.length; i++) {
            addMessage(messages[i]);
        }
    }
    
    private List messages = new ArrayList();
    
    /**
     * @param message The message.
     */
    public void addMessage(String message) {
        this.messages.add(new Message(message));
    }
    
    /**
     * @param message The message.
     */
    public void addMessage(Message message) {
        this.messages.add(message);
    }
    
    /**
     * @return The message.
     */
    public Message[] getMessages() {
        return (Message[]) this.messages.toArray(new Message[this.messages.size()]);
    }
    
    /**
     * @return if the validation was successful.
     */
    public boolean succeeded() {
        return this.succeeded;
    }
    
}
