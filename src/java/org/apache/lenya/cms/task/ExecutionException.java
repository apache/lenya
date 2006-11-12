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

/* $Id$  */

package org.apache.lenya.cms.task;


/**
 * An Exception that is thrown when an error occurs during a task execution.
 */
public class ExecutionException extends Exception {
    /** Creates a new instance of ExecutionException */
    public ExecutionException() {
    }

    /** Creates a new instance of ExecutionException 
     * 
     * @param message the exception message 
     */
    public ExecutionException(String message) {
        super(message);
    }

    /** Creates a new instance of ExecutionException 
     * 
     * @param message the exception message
     * @param cause the exception cause
     */
    public ExecutionException(String message, Throwable cause) {
        super(message, cause);
    }

    /** Creates a new instance of ExecutionException
     * 
     * @param cause the exception cause
     */
    public ExecutionException(Throwable cause) {
        super(cause);
    }
}
