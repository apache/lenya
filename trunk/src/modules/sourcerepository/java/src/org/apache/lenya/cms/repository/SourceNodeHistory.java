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

import java.util.Vector;

import org.apache.avalon.framework.container.ContainerUtil;
import org.apache.avalon.framework.logger.AbstractLogEnabled;
import org.apache.avalon.framework.logger.Logger;
import org.apache.avalon.framework.service.ServiceManager;
import org.apache.lenya.cms.rc.CheckInEntry;
import org.apache.lenya.cms.rc.RCML;

/**
 * Revision history implementation.
 */
public class SourceNodeHistory extends AbstractLogEnabled implements History {

    private SourceNode node;
    private ServiceManager manager;

    /**
     * Ctor.
     * @param node The node which the history belongs to.
     * @param manager The service manager.
     * @param logger The logger.
     */
    public SourceNodeHistory(SourceNode node, ServiceManager manager, Logger logger) {
        this.node = node;
        this.manager = manager;
        ContainerUtil.enableLogging(this, logger);
    }

    public Revision getLatestRevision() {
        try {
            int[] numbers = getRevisionNumbers();
            if (numbers.length > 0) {
                return getRevision(numbers[0]);
            }
            else {
                throw new RepositoryException("There is no revision for node [" + this.node + "] yet.");
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public Revision getRevision(int number) throws RepositoryException {
        return new SourceNodeRevision(this.node, number, this.manager, getLogger());
    }

    public int[] getRevisionNumbers() {
        RCML rcml = this.node.getRcml();
        try {
            Vector entries = rcml.getBackupEntries();
            int[] numbers = new int[entries.size()];
            for (int i = 0; i < entries.size(); i++) {
                CheckInEntry entry = (CheckInEntry) entries.get(i);
                numbers[i] = entry.getVersion();
            }
            return numbers;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
