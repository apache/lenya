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

import java.util.Collections;
import java.util.Map;

import org.apache.lenya.cms.publication.Document;
import org.apache.lenya.cms.publication.Publication;
import org.apache.lenya.cms.site.SiteStructure;
import org.apache.lenya.cms.usecase.AbstractUsecaseTest;

public class CopyPasteTest extends AbstractUsecaseTest {

    String PARENT_PATH = "/doctypes";
    String CHILD_URL = "/test/authoring/doctypes/cforms.html";
    String VERIFICATION_PATH = "/doctypes/cforms/doctypes/cforms";

    protected String getUsecaseName() {
        return "sitemanagement.paste";
    }

    protected Map getParameters() {
        return Collections.singletonMap("private.sourceUrl", CHILD_URL);
    }

    protected void prepareUsecase() throws Exception {
        super.prepareUsecase();

        Publication pub = getPublication("test");
        SiteStructure site = pub.getArea(Publication.AUTHORING_AREA).getSite();
        Document parent = site.getNode(PARENT_PATH).getLink(pub.getDefaultLanguage()).getDocument();

        Clipboard clipboard = new Clipboard(parent, Clipboard.METHOD_COPY);
        ClipboardHelper helper = new ClipboardHelper();
        helper.saveClipboard(this.context, clipboard);
    }

    protected void checkPostconditions() throws Exception {
        super.checkPostconditions();
        
        Publication pub = getPublication("test");
        SiteStructure site = pub.getArea(Publication.AUTHORING_AREA).getSite();
        assertTrue(site.contains(VERIFICATION_PATH));
    }
}
