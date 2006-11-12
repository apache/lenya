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

package org.apache.lenya.cms.publication;

/**
 * Document build exception.
 *
 * @version $Id$
 */
public class DocumentBuildException extends PublicationException {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
     * Constructor.
     */
    public DocumentBuildException() {
        super();
    }

    /**
     * Constructor.
     * @param message A message.
     */
    public DocumentBuildException(String message) {
        super(message);
    }

    /**
     * Constructor.
     * @param cause The cause of the exception.
     */
    public DocumentBuildException(Throwable cause) {
        super(cause);
    }

    /**
     * Constructor.
     * @param message A message.
     * @param cause The cause of the exception.
     */
    public DocumentBuildException(String message, Throwable cause) {
        super(message, cause);
    }
}
