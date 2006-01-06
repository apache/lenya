/*
 * Copyright  1999-2005 The Apache Software Foundation
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
package org.apache.lenya.cms.cocoon.components.modules.input;

import java.util.Map;

import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.cocoon.environment.ObjectModelHelper;
import org.apache.cocoon.environment.Request;
import org.apache.lenya.cms.repo.Document;
import org.apache.lenya.cms.repo.DocumentType;
import org.apache.lenya.cms.repo.impl.RepositoryUtil;

/**
 * Resource type module.
 */
public class ResourceTypeModule extends AbstractServiceableInputModule {

    public Object getAttribute(String name, Configuration modeConf, Map objectModel)
            throws ConfigurationException {
        Object value = null;

        try {
            Request request = ObjectModelHelper.getRequest(objectModel);
            Document document = RepositoryUtil.getDocument(this.manager, request, getLogger());
            DocumentType docType = document.getContentNode().getDocumentType();

            if (name.startsWith("format-")) {
                String[] steps = name.split("-");
                String format = steps[1];
                value = docType.getFormatURI(format);
            }
            else {
                throw new ConfigurationException("Attribute [" + name + "] not supported!");
            }

        } catch (Exception e) {
            throw new ConfigurationException("Resolving attribute [" + name + "] failed: ", e);
        } finally {
        }

        return value;
    }

}
