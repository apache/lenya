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

import org.apache.lenya.cms.publication.Area;
import org.apache.lenya.cms.publication.Document;
import org.apache.lenya.cms.publication.DocumentLocator;
import org.apache.lenya.cms.publication.DocumentManager;
import org.apache.lenya.cms.publication.Publication;
import org.apache.lenya.cms.site.SiteNode;
import org.apache.lenya.cms.site.SiteStructure;
import org.apache.lenya.cms.usecase.AbstractUsecaseTest;

public class DeleteTest extends AbstractUsecaseTest {

    String PATH = "/features";
    String DELETE_URL = "/test/authoring" + PATH + ".html";

    protected String getUsecaseName() {
        return "admin.emptyTrash";
    }

    protected void prepareUsecase() throws Exception {
        super.prepareUsecase();

	DocumentManager docMgr = null;
	try {
	    docMgr = (DocumentManager) getManager().lookup(DocumentManager.ROLE);

	    Publication pub = getPublication("test");
	    Area area = pub.getArea(Publication.AUTHORING_AREA);
	    Area trashArea = pub.getArea("trash");
	    SiteStructure site = area.getSite();
	    SiteNode node = site.getNode(PATH);
	    Document doc_en = node.getLink("en").getDocument();
	    Document doc_de = node.getLink("de").getDocument();

	    DocumentLocator loc = DocumentLocator.getLocator(pub.getId(), "trash", PATH, doc_en.getLanguage());

	    docMgr.copyAll(area, PATH, trashArea, PATH);

	    SiteStructure trashSite = trashArea.getSite();
	    assertTrue(trashSite.contains(PATH));
	} finally {
	    if (docMgr != null) {
                getManager().release(docMgr);
            }
	}

	getFactory().getSession().commit();

    }

    protected void checkPostconditions() throws Exception {
        super.checkPostconditions();
        
        Publication pub = getPublication("test");
        SiteStructure trashSite = pub.getArea("trash").getSite();
	assertFalse(trashSite.contains(PATH));
    }
}
