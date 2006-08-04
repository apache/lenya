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

import java.util.HashMap;
import java.util.Map;

public class MockRevisionController {
    
    private static Map histories = new HashMap();

    public static History getHistory(MockTransactionable transactionable) {
        History history = (History) histories.get(transactionable.getId());
        if (history == null) {
            history = new History();
            histories.put(transactionable.getId(), history);
        }
        return history;
    }

    public static class History {
        
        public History() {
        }
        
        private int version = 0;
        private boolean checkedOut = false;
        
        public void newVersion() {
            this.version++;
        }
        
        public int getLatestVersion() {
            return this.version;
        }
        
        public boolean isCheckedOut() {
            return this.checkedOut;
        }
        
        public void checkOut() {
            this.checkedOut = true;
        }

        public void checkIn() {
            this.checkedOut = false;
        }
        
        
    }

}
