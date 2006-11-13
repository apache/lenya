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
package org.apache.lenya.cms.site.usecases;

import java.util.Vector;

import org.apache.lenya.cms.rc.RCML;

/**
 * Usecase to display revisions of a resource.
 * 
 * @version $Id$
 */
public class Revisions extends SiteUsecase {

    private RCML rcml = null;

    /**
     * @see org.apache.lenya.cms.usecase.AbstractUsecase#initParameters() TODO
     *      filter out checkin entries
     */
    protected void initParameters() {
        super.initParameters();

        try {
            this.rcml = getSourceDocument().getRepositoryNode().getRcml();
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }

        Vector entries;
        try {
            entries = this.rcml.getBackupEntries();
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }
        setParameter("entries", entries);
    }

    /**
     * @see org.apache.lenya.cms.usecase.AbstractUsecase#doExecute() TODO add
     *      rollback and view revision functionality
     */
    protected void doExecute() throws Exception {
        super.doExecute();
    }

}