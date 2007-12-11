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
package org.apache.lenya.cms.publication.templating;

import org.apache.avalon.framework.service.ServiceException;
import org.apache.avalon.framework.service.ServiceSelector;
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
     * <p>
     * Visits the versions of a source in traversing order. The source doesn't have to exist to be
     * visited.
     * </p>
     * @param publication The original publication.
     * @param path The path of the source, relatively to the publication directory.
     * @param visitor The visitor.
     */
    void visit(Publication publication, String path, SourceVisitor visitor);

    /**
     * Visits the publications in traversing order. The core is not visited.
     * @param publication The original publication.
     * @param visitor The visitor.
     */
    void visit(Publication publication, PublicationVisitor visitor);

    /**
     * Returns the hint for the publiation which declares a service.
     * @param publication The original publication.
     * @param selector The service selector.
     * @param originalHint The original hint.
     * @return An object.
     * @throws ServiceException if an error occurs.
     */
    Object getSelectableHint(Publication publication, ServiceSelector selector, String originalHint)
            throws ServiceException;

}