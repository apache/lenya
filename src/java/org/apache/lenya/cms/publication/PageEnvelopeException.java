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

/* $Id: PageEnvelopeException.java,v 1.7 2004/03/01 16:18:17 gregor Exp $  */

package org.apache.lenya.cms.publication;


/**
 * This exception is thrown when the creation of a {@link PageEnvelope} object fails.
 */
public class PageEnvelopeException extends Exception {
    /**
     * Creates a new PageEnvelopeException.
     */
    public PageEnvelopeException() {
    }

    /**
     * Creates a new PageEnvelopeException.
     * 
     * @param message the exception message
     */
    public PageEnvelopeException(String message) {
        super(message);
    }

    /**
     * Creates a new PageEnvelopeException.
     * 
     * @param message the exception message
     * @param cause the cause of the exception
     */
    public PageEnvelopeException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Creates a new PageEnvelopeException.
     * 
     * @param cause  the cause of the exception
     */
    public PageEnvelopeException(Throwable cause) {
        super(cause);
    }
}
