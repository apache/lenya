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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.avalon.framework.logger.AbstractLogEnabled;
import org.apache.lenya.ac.Identity;

/**
 * Default implementation of a unit of work.
 * 
 * @version $Id: UnitOfWorkImpl.java 159567 2005-03-31 07:27:09Z andreas $
 */
public class UnitOfWorkImpl extends AbstractLogEnabled implements UnitOfWork {

    /**
     * Ctor.
     */
    public UnitOfWorkImpl() {
    }

    private List identityMaps = new ArrayList();

    /**
     * @see org.apache.lenya.transaction.UnitOfWork#getIdentityMaps()
     */
    public IdentityMap[] getIdentityMaps() {
        return (IdentityMap[]) this.identityMaps.toArray(new IdentityMap[this.identityMaps.size()]);
    }

    /**
     * @see org.apache.lenya.transaction.UnitOfWork#addIdentityMap(org.apache.lenya.transaction.IdentityMap)
     */
    public void addIdentityMap(IdentityMap map) {
        this.identityMaps.add(map);
        map.setUnitOfWork(this);
    }

    private Set newObjects = new HashSet();
    private Set modifiedObjects = new HashSet();
    private Set removedObjects = new HashSet();

    /**
     * @see org.apache.lenya.transaction.UnitOfWork#registerNew(org.apache.lenya.transaction.Transactionable)
     */
    public void registerNew(Transactionable object) {
        this.newObjects.add(object);
    }

    /**
     * @see org.apache.lenya.transaction.UnitOfWork#registerDirty(org.apache.lenya.transaction.Transactionable)
     */
    public void registerDirty(Transactionable object) {
        System.out.println(this + " register dirty: " + object);
        this.modifiedObjects.add(object);
    }

    /**
     * @see org.apache.lenya.transaction.UnitOfWork#registerRemoved(org.apache.lenya.transaction.Transactionable)
     */
    public void registerRemoved(Transactionable object) {
        this.removedObjects.add(object);
    }

    /**
     * @see org.apache.lenya.transaction.UnitOfWork#commit()
     */
    public void commit() throws TransactionException {
        if (getLogger().isDebugEnabled()) {
            getLogger().debug("commit");
        }
        for (Iterator i = this.newObjects.iterator(); i.hasNext();) {
            Transactionable t = (Transactionable) i.next();
            t.create();
            t.save();
        }
        for (Iterator i = this.modifiedObjects.iterator(); i.hasNext(); ) {
            Transactionable t = (Transactionable) i.next();
            if (getLogger().isDebugEnabled()) {
                getLogger().debug("save [" + t + "]");
            }
            t.save();
        }
        for (Iterator i = this.removedObjects.iterator(); i.hasNext(); ) {
            Transactionable t = (Transactionable) i.next();
            t.delete();
        }
    }
    
    private Identity identity;

    /**
     * @see org.apache.lenya.transaction.UnitOfWork#getIdentity()
     */
    public Identity getIdentity() {
        return this.identity;
    }

    /**
     * @see org.apache.lenya.transaction.UnitOfWork#setIdentity(org.apache.lenya.ac.Identity)
     */
    public void setIdentity(Identity identity) {
        this.identity = identity;
    }

}