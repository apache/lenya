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

import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.lenya.cms.publication.Publication;

/**
 * <p>
 * Component to manage publication templates.
 * </p>
 * <p>
 * When a source is obtained, the traversing order is
 * </p>
 * <ol>
 * <li>the publication itself,</li>
 * <li>all its templates,</li>
 * <li>the core.</li>
 * </ol>
 * @version $Id$
 */
public interface PublicationTemplateManager {

    /**
     * The avalon role.
     */
    String ROLE = PublicationTemplateManager.class.getName();

    /**
     * Hands to publication to the template manager.
     * @param publication A publication.
     * @throws ConfigurationException if setting up fails.
     */
    void setup(Publication publication) throws ConfigurationException;

    /**
     * Returns the template publications of a publication in order of their priority.
     * @return An array of publications.
     */
    Publication[] getTemplates();

    /**
     * <p>
     * Visits the versions of a source in traversing order.
     * The source doesn't have to exist to be visited.
     * </p>
     * @param path The path of the source, relatively to the publication directory.
     * @param visitor The visitor.
     */
    void visit(String path, SourceVisitor visitor);
    
    /**
     * Visits the publications in traversing order.
     * The core is not visited.
     * @param visitor The visitor.
     */
    void visit(PublicationVisitor visitor);

}