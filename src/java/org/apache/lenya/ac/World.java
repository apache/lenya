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

/* $Id: World.java,v 1.2 2004/03/03 12:56:31 gregor Exp $  */

package org.apache.lenya.ac;

import java.util.Collections;

public final class World implements Identifiable {
    /**
     * Creates a new World object.
     */
    private World() {
    }

    private static World instance;

    /**
     * Returns the singleton world object.
     * @return A world object.
     */
    public static World getInstance() {
        if (instance == null) {
            instance = new World();
        }

        return instance;
    }

    /**
     * @see org.apache.lenya.cms.ac2.Accreditable#getAccreditables()
     */
    public Accreditable[] getAccreditables() {
        return (Accreditable[]) Collections.singleton(this).toArray(new Accreditable[1]);
    }
}
