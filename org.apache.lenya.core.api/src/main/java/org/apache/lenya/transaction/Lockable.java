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
 * Object that can be locked.
 *
 * @version $Id$
 */
public interface Lockable {
    
    /**
     * Locks this object.
     * @throws TransactionException if an error occurs.
     */
    void lock() throws TransactionException;

    /**
     * Unlocks this object.
     * @throws TransactionException if an error occurs.
     */
    void unlock() throws TransactionException;

    /**
     * @return if this object is locked.
     * @throws TransactionException if an error occurs.
     */
    boolean isLocked() throws TransactionException;

    /**
     * @return The lock which is held by this object.
     */
    Lock getLock();

}