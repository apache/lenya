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
package org.apache.lenya.transaction;

/**
 * This is a "Unit of Work" object (see "Unit of Work" pattern by Martin Fowler, 
 * <a href="http://www.martinfowler.com/eaaCatalog/unitOfWork.html">
 *   http://www.martinfowler.com/eaaCatalog/unitOfWork.html
 * </a>: the unit of work "maintains a list of objects affected by a business transaction and coordinates the writing out of changes and the resolution of concurrency problems".
 * 
 * <p>In the current design, this interface allows a use case to generate documents, while ensuring that only one instance of a document is created. This access is provided by the DocumentIdentityMap's DocumentFactory.</p>
 *
 * <p>This interface may be extended in the future to allow for access to further types of business objects.</p>
 * 
 * @version $Id: UnitOfWork.java 157924 2005-03-17 09:27:53Z jwkaltz $
 */
public interface UnitOfWork {

    /**
     * The Avalon role.
     */
    String ROLE = UnitOfWork.class.getName();

    /**
     * Returns the identity maps.
     * @return An array of identity maps.
     */
    IdentityMap[] getIdentityMaps();
    
    /**
     * Adds an identity map.
     * @param map The map to add.
     */
    void addIdentityMap(IdentityMap map);

    /**
     * Registers an object as new.
     * @param object The object.
     */
    void registerNew(Transactionable object);
    
    /**
     * Registers an object as modified.
     * @param object The object.
     */
    void registerDirty(Transactionable object);
    
    /**
     * Registers an object as removed.
     * @param object The object.
     */
    void registerRemoved(Transactionable object);
    
    /**
     * Commits the transaction.
     * @throws TransactionException if an error occurs.
     */
    void commit() throws TransactionException;
}
