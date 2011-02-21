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

import javax.servlet.http.HttpServletRequest;

import org.apache.cocoon.processing.ProcessInfoProvider;
import org.apache.cocoon.spring.configurator.WebAppContextUtils;
import org.apache.lenya.cms.publication.Document;
import org.apache.lenya.cms.publication.Publication;
import org.apache.lenya.cms.site.SiteStructure;
import org.apache.lenya.cms.usecase.DocumentUsecase;

/**
 * Cut a document into the clipboard.
 * 
 * @version $Id$
 */
public class Cut extends DocumentUsecase {

    protected static final String MESSAGE_ISLIVE = "cut-error-islive";

    protected void doCheckPreconditions() throws Exception {
        super.doCheckPreconditions();
        if (hasErrors()) {
            return;
        }

        Document doc = getSourceDocument();
        if (!doc.getArea().equals(Publication.AUTHORING_AREA)) {
            addErrorMessage("only-in-authoring-area");
        }
        SiteStructure liveSite = doc.getPublication().getArea(Publication.LIVE_AREA).getSite();
        if (liveSite.contains(doc.getPath())) {
            addErrorMessage(MESSAGE_ISLIVE);
        }
    }

    /**
     * @see org.apache.lenya.cms.usecase.AbstractUsecase#doExecute()
     */
    protected void doExecute() throws Exception {
        super.doExecute();

        Clipboard clipboard = new Clipboard(getSourceDocument(), Clipboard.METHOD_CUT);
        ClipboardHelper helper = new ClipboardHelper();
        helper.saveClipboard(getRequest(), clipboard);
    }

    protected HttpServletRequest getRequest() {
        ProcessInfoProvider process = (ProcessInfoProvider) WebAppContextUtils
                .getCurrentWebApplicationContext().getBean(ProcessInfoProvider.ROLE);
        return process.getRequest();
    }

}
