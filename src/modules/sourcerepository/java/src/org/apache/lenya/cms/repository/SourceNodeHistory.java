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

import org.apache.avalon.framework.service.ServiceManager;

/**
 * Revision history implementation.
 */
public class SourceNodeHistory implements History {

    private SourceNode node;
    private ServiceManager manager;

    /**
     * Ctor.
     * @param node The node which the history belongs to.
     * @param manager The service manager.
     */
    public SourceNodeHistory(SourceNode node, ServiceManager manager) {
        this.node = node;
        this.manager = manager;
    }

    public Revision getLatestRevision() {
        try {
            int latestRevisionNumber = this.node.getRcml().getLatestCheckInEntry().getVersion();
            return getRevision(latestRevisionNumber);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public Revision getRevision(int number) throws RepositoryException {
        return new SourceNodeRevision(this.node, number, this.manager);
    }

}
