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
package org.apache.lenya.cms.site.usecases;

import org.apache.lenya.cms.publication.Publication;

/**
 * Archive usecase handler.
 * 
 * @version $Id:$
 */
public class Archive extends MoveSubsite {

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
        return Publication.ARCHIVE_AREA;
    }

    /**
     * @see org.apache.lenya.cms.site.usecases.MoveSubsite#getEvent()
     */
    protected String getEvent() {
        return "archive";
    }


}