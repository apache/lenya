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

package org.apache.lenya.workflow;


/**
 * Workflow exception.
 */
public class WorkflowException extends Exception {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
     * Constructor
     */
    public WorkflowException() {
        super();
    }

    /**
     * Create a WorkflowException.
     * @param message The message.
     */
    public WorkflowException(String message) {
        super(message);
    }

    /**
     * Create a WorkflowException.
     * @param message The message.
     * @param cause The cause.
     */
    public WorkflowException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Create a WorkflowException.
     * @param cause The cause.
     */
    public WorkflowException(Throwable cause) {
        super(cause);
    }
}
