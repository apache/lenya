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

/* $Id: RevisionControlException.java,v 1.2 2004/03/01 16:18:22 gregor Exp $  */

package org.apache.lenya.cms.rc;

/**
 * A revision control exception.
 */
public class RevisionControlException extends Exception {

    /**
     * Ctor.
     */
    public RevisionControlException() {
        super();
    }

    /**
     * Ctor.
     * @param message The message.
     */
    public RevisionControlException(String message) {
        super(message);
    }

    /**
     * Ctor.
     * @param cause The cause.
     */
    public RevisionControlException(Throwable cause) {
        super(cause);
    }

    /**
     * Ctor.
     * @param message The message.
     * @param cause The cause.
     */
    public RevisionControlException(String message, Throwable cause) {
        super(message, cause);
    }

}
