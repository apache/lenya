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
package org.apache.lenya.cms.site.tree2;

import org.apache.lenya.ac.Identity;
import org.apache.lenya.cms.observation.AbstractRepositoryListener;
import org.apache.lenya.cms.observation.RepositoryEvent;
import org.apache.lenya.cms.publication.Area;
import org.apache.lenya.cms.publication.DocumentFactory;
import org.apache.lenya.cms.publication.DocumentUtil;
import org.apache.lenya.cms.repository.RepositoryUtil;
import org.apache.lenya.cms.repository.Session;
import org.apache.lenya.util.Reporting;

public class SiteTreeRevisionVerifier extends AbstractRepositoryListener {

    public void eventFired(RepositoryEvent event) {
        String uri = event.getNodeUri();
        if (uri != null && uri.endsWith("/" + SiteTreeImpl.SITETREE_FILE_NAME)) {
            // pubs/{pubId}/content/{area}/sitetree.xml
            String[] steps = uri.split("/");
            String pubId = steps[steps.length - 4];
            String areaName = steps[steps.length - 2];
            try {
                Session session = RepositoryUtil.createSession(this.manager,
                        Identity.ANONYMOUS, false);
                DocumentFactory factory = DocumentUtil.createDocumentFactory(this.manager, session);
                Area area = factory.getPublication(pubId).getArea(areaName);
                SiteTreeImpl tree = new SiteTreeImpl(this.manager, area, getLogger());
                int treeRev = tree.getRevision();
                int rcmlRev = tree.getRevision(tree.getRepositoryNode());
                if (treeRev != rcmlRev) {
                    throw new IllegalStateException("Tree revision " + treeRev
                            + " does not match RCML revision " + rcmlRev + ". "
                            + Reporting.REPORT_TO_COMMUNITY);
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }
}
