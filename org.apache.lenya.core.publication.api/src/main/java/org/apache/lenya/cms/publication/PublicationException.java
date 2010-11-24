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

import org.apache.lenya.cms.repository.RepositoryException;

/**
 * Publication Exception
 */
//florent : change exceptions extension in order to repect dependencies
//public class PublicationException extends RepositoryException {
public class PublicationException extends DocumentException {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
     * Creates a new PublicationException.
     */
    public PublicationException() {
        super();
    }

    /**
     * Creates a new PublicationException.
     * @param message the exception message
     */
    public PublicationException(String message) {
        super(message);
    }

    /**
     * Creates a new PublicationException.
     * @param message the exception message
     * @param cause the cause of the exception
     */
    public PublicationException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Creates a new PublicationException.
     * @param cause the cause of the exception
     */
    public PublicationException(Throwable cause) {
        super(cause);
    }

}
