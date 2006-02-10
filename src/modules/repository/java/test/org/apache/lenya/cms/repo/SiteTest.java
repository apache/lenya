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

import javax.jcr.ItemExistsException;

import org.apache.avalon.framework.logger.ConsoleLogger;
import org.apache.lenya.cms.url.URLUtil;

public class SiteTest extends RepositoryTest {
    
    public void testSite() throws RepositoryException {
        
        Session session = getSession();
        
        Publication pub = session.addPublication(PUBLICATION_ID);
        assertSame(pub, session.getPublication(PUBLICATION_ID));

        assertFalse(pub.existsArea(AREA_ID));
        Area area = pub.addArea(AREA_ID);

        Content content = area.getContent();
        Site site = area.getSite();

        AssetType doctype = session.getRepository().getAssetTypeResolver().resolve(ASSET_TYPYE);
        
        Asset asset1 = content.addAsset(doctype);
        asset1.addTranslation(LANGUAGE_DE, "hello", "application/xml");
        
        Asset asset2 = content.addAsset(doctype);
        asset2.addTranslation(LANGUAGE_DE, "hello", "application/xml");
        
        SiteNode parent = site.addChild("parent", asset1);
        SiteNode child = parent.addChild("child", asset2);
        assertSame(asset2.getAssetId(), child.getAsset().getAssetId());
        
        doTestSite(site, asset1);
        doTestUrlMapping(child);
        
    }

    protected void doTestSite(Site site, Asset asset) throws RepositoryException {
        SiteNode foo = site.addChild("foo", asset);
        SiteNode bar = site.addChild("bar", asset);

        RepositoryException ex = null;
        try {
            site.move(foo.getPath(), bar.getPath());
        } catch (RepositoryException e) {
            ex = e;
        }
        assertTrue(ex.getCause() instanceof ItemExistsException);
        site.move("/foo", "/bar/baz");

        SiteNode barBaz = bar.getChild("baz");
        assertSame(foo.getAsset().getAssetId(), barBaz.getAsset().getAssetId());

    }

    /**
     * Test the URL mapping.
     * @param child The child site node.
     * @throws RepositoryException if an error occurs.
     */
    public void doTestUrlMapping(SiteNode child) throws RepositoryException {

        SiteNode parent = child.getParent();

        Area area = parent.getAsset().getContent().getArea();
        Publication pub = area.getPublication();

        String webappUrl = "/" + pub.getPublicationId() + "/" + area.getAreaID() + "/"
                + parent.getName() + "/" + child.getName() + "_" + LANGUAGE_DE;

        Translation childTrans = child.getAsset().getTranslation(LANGUAGE_DE);
        Translation trans = URLUtil.getTranslation(pub.getSession(), webappUrl, new ConsoleLogger());

        assertSame(trans.getAsset().getAssetId(), childTrans.getAsset().getAssetId());
        assertSame(trans.getLanguage(), childTrans.getLanguage());

        String derivedUrl = URLUtil.getWebappURL(pub, trans, new ConsoleLogger());
        Translation derivedTrans = URLUtil.getTranslation(pub.getSession(), derivedUrl, new ConsoleLogger());

        assertSame(trans.getAsset().getAssetId(), derivedTrans.getAsset().getAssetId());
        assertSame(trans.getLanguage(), derivedTrans.getLanguage());

    }
}
