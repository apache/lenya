/*
 * Copyright  1999-2004 The Apache Software Foundation
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
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

import java.util.HashMap;
import java.util.Map;

import org.apache.lenya.cms.publication.Area;
import org.apache.lenya.cms.site.SiteStructure;
import org.apache.lenya.cms.usecase.AbstractUsecaseTest;

/**
 * Test class for moving subsites to a different area.
 */
public class MoveSubsiteTest extends AbstractUsecaseTest {

    protected static final String DELETE_URL = "/test/authoring/doctypes/xhtml-document.html";
    protected static final String PATH = "/doctypes/xhtml-document";

    protected String getUsecaseName() {
        return "site.delete";
    }

    protected void checkPostconditions() throws Exception {
        super.checkPostconditions();
        
        Area authoring = getPublication("test").getArea("authoring");
        SiteStructure authoringSite = authoring.getSite();
        assertFalse(authoringSite.contains(PATH));
        
        Area trash = getPublication("test").getArea("trash");
        SiteStructure trashSite = trash.getSite();
        assertTrue(trashSite.contains(PATH));
        assertEquals(trashSite.getNode(PATH).getUuid(), this.uuid);
        
    }

    protected Map getParameters() {
        Map params = new HashMap();
        params.put("private.sourceUrl", DELETE_URL);
        return params;
    }
    
    private String uuid;

    protected void prepareUsecase() throws Exception {
        super.prepareUsecase();
        Area authoring = getPublication("test").getArea("authoring");
        SiteStructure authoringSite = authoring.getSite();
        this.uuid = authoringSite.getNode(PATH).getUuid();
    }

}
