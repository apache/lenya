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
package org.apache.lenya.cms.publication.templating;

import java.io.File;

import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.DefaultConfigurationBuilder;
import org.apache.lenya.cms.publication.Publication;

/**
 * Publication visitor which returns the first publication implementing a certain usecase.
 * 
 * @version $Id$
 */
public class ExistingUsecaseResolver implements PublicationVisitor {

    private String usecase;
    private Publication publication;

    /**
     * Ctor.
     * @param _usecase The name of the usecase to resolve.
     */
    public ExistingUsecaseResolver(String _usecase) {
        this.usecase = _usecase;
    }

    protected static final String ELEMENT_USECASES = "usecases";
    protected static final String ELEMENT_USECASE = "usecase";
    protected static final String ATTRIBUTE_NAME = "name";

    /**
     * @see org.apache.lenya.cms.publication.templating.PublicationVisitor#visit(org.apache.lenya.cms.publication.Publication)
     */
    public void visit(Publication _publication) {

        if (this.publication == null) {
            File configFile = new File(_publication.getDirectory(), Publication.CONFIGURATION_FILE);
            DefaultConfigurationBuilder builder = new DefaultConfigurationBuilder();

            try {
                Configuration config = builder.buildFromFile(configFile);
                Configuration usecasesConfig = config.getChild(ELEMENT_USECASES);
                if (usecasesConfig != null) {
                    Configuration[] usecaseConfigs = usecasesConfig.getChildren(ELEMENT_USECASE);
                    for (int i = 0; i < usecaseConfigs.length; i++) {
                        String usecaseName = usecaseConfigs[i].getAttribute(ATTRIBUTE_NAME);
                        if (usecaseName.equals(this.usecase)) {
                            this.publication = _publication;
                        }
                    }
                }
            } catch (Exception e) {
                throw new RuntimeException("Resolving usecases failed: ", e);
            }
        }
    }

    /**
     * Returns the resolved publication.
     * @return A publication or <code>null</code> if no publication contains the usecase.
     */
    public Publication getPublication() {
        return this.publication;
    }

}