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
package org.apache.lenya.cms.workflow;

import org.apache.lenya.cms.observation.AbstractRepositoryListener;
import org.apache.lenya.cms.observation.DocumentEvent;
import org.apache.lenya.cms.observation.RepositoryEvent;
import org.apache.lenya.workflow.Version;

/**
 * Notify users of workflow changes.
 */
public class WorkflowNotifier extends AbstractRepositoryListener {

    public void eventFired(RepositoryEvent event) {
        if (!(event instanceof DocumentEvent)) {
            return;
        }
        DocumentEvent docEvent = (DocumentEvent) event;
        if (!(docEvent.getDescriptor() instanceof WorkflowEventDescriptor)) {
            return;
        }

        WorkflowEventDescriptor descriptor = (WorkflowEventDescriptor) docEvent.getDescriptor();
        Version version = descriptor.getVersion();
    }

}
