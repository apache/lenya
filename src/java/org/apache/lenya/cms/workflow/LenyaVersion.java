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

/* $Id: CMSVersion.java 156931 2005-03-10 10:26:24Z andreas $  */

package org.apache.lenya.cms.workflow;

import org.apache.lenya.workflow.impl.VersionImpl;

/**
 * A CMS version
 */
public class LenyaVersion extends VersionImpl {

    /**
     * Ctor.
     * @param event The event.
     * @param state The state.
     */
    public LenyaVersion(String event, String state) {
        super(event, state);
    }

}
