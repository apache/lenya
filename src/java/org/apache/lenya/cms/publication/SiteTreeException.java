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

package org.apache.lenya.cms.publication;

/**
 * Exceptions for site tree handling.
 */
public class SiteTreeException extends Exception {
	
    /**
     * Creates a new SiteTreeException.
     */
    public SiteTreeException() {
    }

    /**
     * Creates a new SiteTreeException.
     * 
     * @param message the exception message
     */
    public SiteTreeException(String message) {
        super(message);
    }

    /**
     * Creates a new SiteTreeException.
     * 
     * @param message the exception message
     * @param cause the cause of the exception
     */
    public SiteTreeException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Creates a new SiteTreeException.
     * 
     * @param cause  the cause of the exception
     */
    public SiteTreeException(Throwable cause) {
        super(cause);
    }
}
