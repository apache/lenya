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

/* $Id: CheckInEntry.java,v 1.9 2004/03/01 16:18:22 gregor Exp $  */

package org.apache.lenya.cms.rc;


public class CheckInEntry extends RCMLEntry {
    /**
     * Creates a new CheckInEntry object.
     *
     * @param identity DOCUMENT ME!
     * @param time DOCUMENT ME!
     */
    public CheckInEntry(String identity, long time) {
        super(identity, time);
        setType(RCML.ci);
    }
}
