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
package org.apache.lenya.transaction;

/**
 * A versionable object.
 *
 * @version $Id$
 */
public interface Versionable extends Lockable {

    /**
     * Checks the object in.
     * @throws TransactionException if an error occurs.
     */
    void checkin() throws TransactionException;

    /**
     * Checks the object out.
     * @throws TransactionException if an error occurs.
     */
    void checkout() throws TransactionException;

    /**
     * @return if the object is checked out.
     * @throws TransactionException if an error occurs.
     */
    boolean isCheckedOut() throws TransactionException;
    
    /**
     * @return if the object is checked out by its session.
     * @throws TransactionException if an error occurs.
     */
    boolean isCheckedOutBySession() throws TransactionException;

    /**
     * Checks if the object has been changed since it has been locked.
     * @return A boolean value.
     * @throws TransactionException if an error occurs.
     */
    boolean hasChanged() throws TransactionException;

}