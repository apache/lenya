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

/* $Id$  */

package org.apache.lenya.cms.workflow;

import java.util.Date;

import org.apache.lenya.workflow.Event;
import org.apache.lenya.workflow.State;
import org.apache.lenya.workflow.impl.Version;

/**
 * A CMS version
 */
public class CMSVersion extends Version {
    
    private Date date;

    /**
     * Returns the date.
     * @return A string.
     */
    public Date getDate() {
        return this.date;
    }

    /**
     * Sets the date.
     * @param _date A date.
     */
    public void setDate(Date _date) {
        this.date = _date;
    }

    /**
     * Returns the user ID.
     * @return A string.
     */
    public String getUserId() {
        return this.userId;
    }

    /**
     * Sets the user ID.
     * @param _userId A user ID.
     */
    public void setUserId(String _userId) {
        this.userId = _userId;
    }

    /**
     * Ctor.
     * @param event The event.
     * @param state The state.
     */
    public CMSVersion(Event event, State state) {
        super(event, state);
    }
    
    private String userId;

}
