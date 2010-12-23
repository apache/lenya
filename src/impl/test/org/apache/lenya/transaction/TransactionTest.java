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

import org.apache.cocoon.core.container.ContainerTestCase;
import org.apache.lenya.ac.Identity;

public class TransactionTest extends ContainerTestCase {

    public void testTransaction() throws TransactionException {

        Identity lenya = new Identity();
        lenya.addIdentifiable(new MockUser("lenya"));

        Identity alice = new Identity();
        alice.addIdentifiable(new MockUser("alice"));

        IdentityMap lenyaMap = new IdentityMapImpl(getLogger());
        UnitOfWork lenyaUnit = new UnitOfWorkImpl(lenyaMap, lenya, getLogger());
        IdentifiableFactory lenyaFactory = new MockFactory(lenyaUnit);

        IdentityMap aliceMap = new IdentityMapImpl(getLogger());
        UnitOfWork aliceUnit = new UnitOfWorkImpl(aliceMap, alice, getLogger());
        IdentifiableFactory aliceFactory = new MockFactory(aliceUnit);

        MockTransactionable lenyaT1 = (MockTransactionable) lenyaMap.get(lenyaFactory, "t1");
        MockTransactionable aliceT1 = (MockTransactionable) aliceMap.get(aliceFactory, "t1");

        checkDoubleLock(lenyaT1);
        checkLockAndModify(lenyaUnit, lenyaT1, aliceT1);

    }

    protected void checkDoubleLock(MockTransactionable t) throws TransactionException {
        t.lock();
        try {
            t.lock();
            assertTrue("No exception thrown!", false);
        } catch (LockException ignore) {
        }
        t.unlock();
    }

    protected void checkLockAndModify(UnitOfWork lenyaUnit, MockTransactionable lenyaT1,
            MockTransactionable aliceT1) throws TransactionException {
        lenyaT1.lock();
        aliceT1.write();
        try {
            lenyaUnit.commit();
            assertTrue("No exception thrown!", false);
        } catch (ConcurrentModificationException ignore) {
        }
        lenyaT1.unlock();
    }

}
