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
 */

package org.apache.lenya.cms.site;

import org.apache.lenya.cms.publication.PublicationException;

/**
 * Site structure management exception.
 * 
 * @author <a href="andreas@apache.org">Andreas Hartmann</a>
 * @version $Id: SiteException.java,v 1.1 2004/02/18 18:47:07 andreas Exp $
 */
public class SiteException extends PublicationException {

    /**
     * Ctor.
     */
    public SiteException() {
        super();
    }

    /**
     * Ctor.
     * @param message The message.
     */
    public SiteException(String message) {
        super(message);
    }

    /**
     * Ctor.
     * @param cause The cause.
     */
    public SiteException(Throwable cause) {
        super(cause);
    }

    /**
     * Ctor.
     * @param message The message.
     * @param cause The cause.
     */
    public SiteException(String message, Throwable cause) {
        super(message, cause);
    }

}
