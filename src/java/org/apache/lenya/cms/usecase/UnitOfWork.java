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
package org.apache.lenya.cms.usecase;

import org.apache.lenya.cms.publication.DocumentIdentityMap;

/**
 * This is a "Unit of Work" object (see "Unit of Work" pattern by Martin Fowler, 
 * <a href="http://www.martinfowler.com/eaaCatalog/unitOfWork.html">
 *   http://www.martinfowler.com/eaaCatalog/unitOfWork.html
 * </a>: the unit of work "maintains a list of objects affected by a business transaction and coordinates the writing out of changes and the resolution of concurrency problems".
 * 
 * <p>In the current design, this interface allows a use case to generate documents, while ensuring that only one instance of a document is created. This access is provided by the DocumentIdentityMap's DocumentFactory.</p>
 *
 * <p>This interface may be extended in the future to allow for access to further types of business objects.</p>
 * 
 * @version $Id$
 */
public interface UnitOfWork {

    /**
     * The Avalon role.
     */
    String ROLE = UnitOfWork.class.getName();

    /**
     * Returns the document identity map.
     * @return An identity map.
     */
    DocumentIdentityMap getIdentityMap();

}
