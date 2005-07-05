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
package org.apache.lenya.cms.site.tree;

import java.io.File;

import org.apache.avalon.framework.container.ContainerUtil;
import org.apache.avalon.framework.logger.AbstractLogEnabled;
import org.apache.avalon.framework.service.ServiceManager;
import org.apache.excalibur.source.Source;
import org.apache.excalibur.source.SourceResolver;
import org.apache.excalibur.source.SourceUtil;
import org.apache.lenya.cms.publication.Publication;
import org.apache.lenya.cms.publication.PublicationFactory;
import org.apache.lenya.transaction.Identifiable;
import org.apache.lenya.transaction.IdentityMap;
import org.apache.lenya.transaction.IdentifiableFactory;

/**
 * Factory for sitetree objects.
 * 
 * @version $Id: SiteTreeFactory.java 179568 2005-06-02 09:27:26Z jwkaltz $
 */
public class SiteTreeFactory extends AbstractLogEnabled implements IdentifiableFactory {

    protected ServiceManager manager;

    /**
     * Ctor.
     * @param manager The service manager.
     */
    public SiteTreeFactory(ServiceManager manager) {
        this.manager = manager;
    }

    /**
     * @see org.apache.lenya.transaction.IdentifiableFactory#build(org.apache.lenya.transaction.IdentityMap,
     *      java.lang.String)
     */
    public Identifiable build(IdentityMap map, String key) throws Exception {
        String[] snippets = key.split(":");
        String publicationId = snippets[0];
        String area = snippets[1];

        SourceResolver resolver = null;
        Source source = null;
        DefaultSiteTree tree;
        try {
            resolver = (SourceResolver) this.manager.lookup(SourceResolver.ROLE);
            source = resolver.resolveURI("context://");
            File servletContext = SourceUtil.getFile(source);

            PublicationFactory factory = PublicationFactory.getInstance(getLogger());
            Publication publication = factory.getPublication(publicationId, servletContext
                    .getAbsolutePath());
           
            tree = new DefaultSiteTree(publication, area, this.manager);
            ContainerUtil.enableLogging(tree, getLogger());
            
        } finally {
            if (resolver != null) {
                if (source != null) {
                    resolver.release(source);
                }
                this.manager.release(resolver);
            }
        }
        return tree;
    }

}