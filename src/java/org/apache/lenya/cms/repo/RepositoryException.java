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
package org.apache.lenya.cms.repo;

/**
 * Repository exception.
 */
public class RepositoryException extends Exception {

    /**
     * Ctor.
     */
    public RepositoryException() {
        super();
    }

    /**
     * Ctor.
     * @param arg0 The message.
     * @param arg1 The cause.
     */
    public RepositoryException(String arg0, Throwable arg1) {
        super(arg0, arg1);
    }

    /**
     * Ctor.
     * @param arg0 The message.
     */
    public RepositoryException(String arg0) {
        super(arg0);
    }

    /**
     * @param arg0
     */
    public RepositoryException(Throwable arg0) {
        super(arg0);
        // TODO Auto-generated constructor stub
    }

}
