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

import org.apache.lenya.cms.repository.RepositoryException;

public class MockTransactionable implements Transactionable {
    
    private String id;
    
    public MockTransactionable(String id, UnitOfWork unit) {
        this.id = id;
        this.unit = unit;
    }
    
    public void write() {
        MockRevisionController.getHistory(this).newVersion();
    }
    
    public void changed() {
    }

    public void createTransactionable() throws TransactionException {
    }

    public void deleteTransactionable() throws TransactionException {
    }

    public void removed() {
    }

    public void saveTransactionable() throws TransactionException {
    }

    public void checkin() throws TransactionException {
        MockRevisionController.getHistory(this).checkIn();
    }

    public void checkout() throws TransactionException {
        MockRevisionController.getHistory(this).checkOut(getUserId());
    }

    public boolean hasChanged() throws TransactionException {
        try {
            int currentVersion = getLatestVersion();
            int lockVersion = getLock().getVersion();
            return currentVersion > lockVersion;
        } catch (Exception e) {
            throw new RepositoryException(e);
        }
    }

    protected int getLatestVersion() {
        int currentVersion = MockRevisionController.getHistory(this).getLatestVersion();
        return currentVersion;
    }

    public boolean isCheckedOut() throws TransactionException {
        return MockRevisionController.getHistory(this).isCheckedOut();
    }

    public boolean isCheckedOutBySession() throws TransactionException {
        String user = MockRevisionController.getHistory(this).getCheckOutUser();
        return user != null && user.equals(getUserId());
    }
    
    private String getUserId() {
        return ((UnitOfWorkImpl) this.unit).getIdentity().getUser().getId();
    }

    private Lock lock;

    public Lock getLock() {
        return this.lock;
    }

    public boolean isLocked() throws TransactionException {
        return this.lock != null;
    }
    
    private UnitOfWork unit;

    public void lock() throws TransactionException {
        this.lock = unit.createLock(this, getLatestVersion());
    }

    public void unlock() throws TransactionException {
        this.lock = null;
        unit.removeLock(this);
    }

    public String getId() {
        return this.id;
    }

}
