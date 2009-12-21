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

import org.apache.lenya.cms.publication.Publication;
import org.apache.lenya.cms.publication.PublicationException;
import org.apache.lenya.cms.publication.PublicationUtil;
import org.apache.lenya.cms.repository.Node;
import org.apache.lenya.cms.usecase.AbstractUsecase;

/**
 * Force check in of the live node 
 * 
 */
public class ForceLiveCheckIn extends AbstractUsecase {

    /**
     * @see org.apache.lenya.cms.usecase.AbstractUsecase#doCheckPreconditions()
     */
    protected void doCheckPreconditions() throws Exception {
        super.doCheckPreconditions();
        Publication pub = getPublication();
        if(pub == null) {
            return;
        }
        Node node = getNode();
        if (!node.isCheckedOut()) {
            String[] params = { "Live" };
            addErrorMessage("not-checked-out", params);
        }
    }

    /**
     * @see org.apache.lenya.cms.usecase.AbstractUsecase#doExecute()
     */
    protected void doExecute() throws Exception {
        super.doExecute();

        getNode().forceCheckIn();

    }

    private Node getNode() throws PublicationException {
        return getPublication().getArea(Publication.LIVE_AREA).getSite().getRepositoryNode();
    }
 
    private Publication getPublication() throws PublicationException {
        return PublicationUtil.getPublicationFromUrl(this.manager, getDocumentFactory(),getSourceURL());
    }
}
