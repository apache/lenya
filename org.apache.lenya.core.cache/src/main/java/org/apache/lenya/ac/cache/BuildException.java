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

package org.apache.lenya.ac.cache;

/**
 * Exception for problems when cacheable objects are built.
 */
public class BuildException extends Exception {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
     * Ctor.
     */
    public BuildException() {
        super();
    }

    /**
     * Ctor.
     * @param message The message.
     */
    public BuildException(String message) {
        super(message);
    }

    /**
     * Ctor.
     * @param message The message.
     * @param cause The cause.
     */
    public BuildException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Ctor.
     * @param cause The cause.
     */
    public BuildException(Throwable cause) {
        super(cause);
    }

}
