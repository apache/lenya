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

/* $Id: CreatorException.java,v 1.5 2004/03/03 12:56:32 gregor Exp $  */

package org.apache.lenya.cms.authoring;

public class CreatorException extends Exception {
    /**
     *
     */
    public CreatorException() {
        super();
    }

    /**
     * Create an instance of <code>CreatorException</code>
     * 
     * @param message the exception message
     */
    public CreatorException(String message) {
        super(message);
    }

    /**
     * Create an instance of <code>CreatorException</code>
     * 
     * @param cause the cause of the exception
     */
    public CreatorException(Throwable cause) {
        super(cause);
    }

    /**
     * Create an instance of <code>CreatorException</code>
     * 
     * @param message the exception message
     * @param cause the cause of the exception
     */
    public CreatorException(String message, Throwable cause) {
        super(message, cause);
    }
}
