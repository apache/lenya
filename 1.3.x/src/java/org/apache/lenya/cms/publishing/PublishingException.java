/*
 * Copyright  1999-2004 The Apache Software Foundation
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

/* $Id$  */

package org.apache.lenya.cms.publishing;

public class PublishingException extends Exception {
    /**
     * Creates a new PublishingException.
     */
    public PublishingException() {
    }

    /**
     * Creates a new PublishingException.
     * 
     * @param message the exception message
     */
    public PublishingException(String message) {
        super(message);
    }

    /**
     * Creates a new PublishingException.
     * 
     * @param message the exception message
     * @param cause the cause of the exception
     */
    public PublishingException(String message, Throwable cause) {
        super(message + " " + cause.getMessage());
    }

    /**
     * Creates a new PublishingException.
     * 
     * @param cause  the cause of the exception
     */
    public PublishingException(Throwable cause) {
        super(cause.getMessage());
    }
}
