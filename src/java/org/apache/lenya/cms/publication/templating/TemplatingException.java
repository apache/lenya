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
package org.apache.lenya.cms.publication.templating;

/**
 * @version $Id$
 */
public class TemplatingException extends RuntimeException {

    /**
     * Ctor.
     */
    public TemplatingException() {
        super();
    }
    
    /**
     * Ctor.
     * @param message The message.
     */
    public TemplatingException(String message) {
        super(message);
    }
    
    /**
     * Ctor.
     * @param message The message.
     * @param cause The cause.
     */
    public TemplatingException(String message, Throwable cause) {
        super(message, cause);
    }
    
    /**
     * Ctor.
     * @param cause The cause.
     */
    public TemplatingException(Throwable cause) {
        super(cause);
    }
    
}
