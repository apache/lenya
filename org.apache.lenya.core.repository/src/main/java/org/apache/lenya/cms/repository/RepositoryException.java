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
package org.apache.lenya.cms.repository;

import org.apache.lenya.transaction.TransactionException;

/**
 * Repository exception.
 * @version $Id:$
 */
public class RepositoryException extends TransactionException {

    /**
     * Ctor.
     */
    public RepositoryException() {
        super();
    }

    /**
     * Ctor.
     * @param arg0
     * @param arg1
     */
    public RepositoryException(String arg0, Throwable arg1) {
        super(arg0, arg1);
    }

    /**
     * Ctor.
     * @param arg0
     */
    public RepositoryException(String arg0) {
        super(arg0);
    }

    /**
     * Ctor.
     * @param arg0
     */
    public RepositoryException(Throwable arg0) {
        super(arg0);
    }

}
