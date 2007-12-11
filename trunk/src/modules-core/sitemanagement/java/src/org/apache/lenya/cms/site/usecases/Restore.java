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

import org.apache.lenya.cms.publication.Area;
import org.apache.lenya.cms.publication.Document;
import org.apache.lenya.cms.publication.DocumentLocator;
import org.apache.lenya.cms.publication.Publication;
import org.apache.lenya.cms.site.SiteUtil;
import org.apache.lenya.cms.workflow.WorkflowUtil;
import org.apache.lenya.workflow.Workflowable;

/**
 * Restore usecase handler.
 * 
 * @version $Id:$
 */
public class Restore extends MoveSubsite {

    /**
     * @see org.apache.lenya.cms.site.usecases.MoveSubsite#getSourceAreas()
     */
    protected String[] getSourceAreas() {
        return new String[] { Publication.TRASH_AREA, Publication.ARCHIVE_AREA };
    }

    /**
     * @see org.apache.lenya.cms.site.usecases.MoveSubsite#getTargetArea()
     */
    protected String getTargetArea() {
        return Publication.AUTHORING_AREA;
    }

    /**
     * @see org.apache.lenya.cms.site.usecases.MoveSubsite#getEvent()
     */
    protected String getEvent() {
        return "restore";
    }

    protected void doCheckPreconditions() throws Exception {
        super.doCheckPreconditions();

        String targetAreaName = getTargetArea(); 
        Document doc = getSourceDocument();
        if(doc == null) {
            return;
        }
        // Check to see if parent node exists in target to prevent ghost nodes
        Area targetArea = doc.getPublication().getArea(targetAreaName);
        DocumentLocator targetLoc = doc.getLocator().getAreaVersion(targetAreaName);
        targetLoc = SiteUtil.getAvailableLocator(this.manager, getDocumentFactory(), targetLoc);
        String targetPath = targetLoc.getPath();
        targetPath = targetPath.substring(0,targetPath.lastIndexOf('/'));
        if(!targetArea.getSite().contains(targetPath)) {
            addErrorMessage("The authoring path [" + targetPath + "] does not exist.");
        }
    }
  
    protected void doCheckPostconditions() throws Exception {
        super.doCheckPostconditions();

        Document doc = getTargetDocument(true);
        Workflowable workflowable = WorkflowUtil.getWorkflowable(this.manager, getSession(),
                getLogger(), doc);
        String state = workflowable.getLatestVersion().getState();
        if (!state.equals("authoring")) {
            addErrorMessage("The state is [" + state + "] instead of [authoring]!");
        }

    }

}
