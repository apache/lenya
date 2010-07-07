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

/**
 * 
 */
package org.apache.lenya.cms.observation;

public class TestListener implements RepositoryListener {
    
    private boolean changed = false;
    private boolean removed = false;

    public void eventFired(RepositoryEvent repoEvent) {
        if (!(repoEvent instanceof DocumentEvent)) {
            return;
        }
        DocumentEvent event = (DocumentEvent) repoEvent;
        if (event.getDescriptor().equals(DocumentEvent.CHANGED)) {
            this.changed = true;
        }
        else if (event.getDescriptor().equals(DocumentEvent.REMOVED)) {
            this.removed = true;
        }
    }
    
    public boolean wasChanged() {
        return this.changed;
    }
    
    public boolean wasRemoved() {
        return this.removed;
    }
    
    public void reset() {
        this.changed = false;
        this.removed = false;
    }

}
