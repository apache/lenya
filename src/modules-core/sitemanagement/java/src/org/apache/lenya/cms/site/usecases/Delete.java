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
package org.apache.lenya.cms.site.usecases;

import org.apache.lenya.cms.publication.Publication;

/**
 * Delete a document and all its descendants, including all language versions. The documents are
 * moved to the trash.
 * 
 * @version $Id$
 */
public class Delete extends MoveSubsite {

    /**
     * @see org.apache.lenya.cms.site.usecases.MoveSubsite#getSourceAreas()
     */
    protected String[] getSourceAreas() {
        return new String[] { Publication.AUTHORING_AREA };
    }

    /**
     * @see org.apache.lenya.cms.site.usecases.MoveSubsite#getTargetArea()
     */
    protected String getTargetArea() {
        return Publication.TRASH_AREA;
    }

    /**
     * @see org.apache.lenya.cms.site.usecases.MoveSubsite#getEvent()
     */
    protected String getEvent() {
        return "delete";
    }

}