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
package org.apache.lenya.cms.ant;

import org.apache.avalon.framework.service.ServiceManager;
import org.apache.lenya.cms.publication.Document;
import org.apache.lenya.cms.publication.DocumentManager;
import org.apache.lenya.cms.publication.DocumentType;
import org.apache.lenya.cms.publication.DocumentTypeResolver;
import org.apache.lenya.cms.publication.Publication;
import org.apache.lenya.cms.site.SiteManager;
import org.apache.tools.ant.BuildException;

/**
 * Test task.
 *
 * @version $Id:$
 */
public class TestTask extends PublicationTask {

    /**
     * @see org.apache.tools.ant.Task#execute()
     */
    public void execute() throws BuildException {
        super.execute();
        
        ServiceManager manager = getServiceManager();
        DocumentTypeResolver resolver = null;
        try {
            resolver = (DocumentTypeResolver) manager.lookup(DocumentTypeResolver.ROLE);
            Document document = getIdentityMap().get(getPublication(), Publication.AUTHORING_AREA, "/index");
            DocumentType doctype = resolver.resolve(document);
            String message = "Document type of [" + document + "] is [" + doctype.getName() + "]";
            log(message);
            System.out.println(message);
        }
        catch (Exception e) {
            throw new BuildException(e);
        }
        finally {
            if (resolver != null) {
                manager.release(resolver);
            }
        }
        
    }
}
