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
package org.apache.lenya.cms.repo;

public class VersioningTest extends AbstractRepositoryTest {

    public void testVersioning() throws RepositoryException {
        Session session = getSession();

        Publication pub = session.addPublication(PUBLICATION_ID);
        Area area = pub.addArea("authoring");

        AssetType type = session.getRepository().getAssetTypeResolver().resolve(ASSET_TYPYE);

        Asset asset = area.getContent().addAsset(type);
        Translation trans = asset.addTranslation(LANGUAGE_DE, "hello", "application/xml");

        trans.setLabel("foo");
        trans.checkin();
        
        Exception ex = null;
        try {
            trans.setLabel("bar");
        } catch (Exception e) {
            ex = e;
        }
        assertTrue(ex != null && ex instanceof RepositoryException);

        session.logout();
        session.getRepository().shutdown();
    }

}
