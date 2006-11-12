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

import java.io.File;

import org.apache.lenya.cms.publication.Publication;
import org.apache.lenya.cms.rc.RCEnvironment;
import org.apache.lenya.cms.rc.RCML;
import org.apache.lenya.cms.rc.RevisionController;
import org.apache.lenya.cms.site.usecases.SiteUsecase;

import java.util.Vector;

/**
 * Usecase to display revisions of a resource.
 * 
 * @version $Id$
 */
public class Revisions extends SiteUsecase {

    private RevisionController rc = null;
    private RCML rcml = null;

    /**
     * @see org.apache.lenya.cms.usecase.AbstractUsecase#initParameters() TODO
     *      filter out checkin entries
     */
    protected void initParameters() {
        super.initParameters();

        try {
            final Publication publication = getSourceDocument().getPublication();
            final String publicationPath = publication.getDirectory().getCanonicalPath();
            final RCEnvironment rcEnvironment = RCEnvironment.getInstance(publication
                    .getServletContext().getCanonicalPath());
            String rcmlDirectory = rcEnvironment.getRCMLDirectory();
            rcmlDirectory = publicationPath + File.separator + rcmlDirectory;
            String backupDirectory = rcEnvironment.getBackupDirectory();
            backupDirectory = publicationPath + File.separator + backupDirectory;
            this.rc = new RevisionController(rcmlDirectory, backupDirectory, publicationPath);
            final String filename = getSourceDocument().getFile().getCanonicalPath()
                    .substring(publication.getDirectory().getCanonicalPath().length());
            this.rcml = this.rc.getRCML(filename);

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