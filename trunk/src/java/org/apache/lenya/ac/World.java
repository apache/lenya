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

package org.apache.lenya.ac;

import java.io.Serializable;
import java.util.Collections;

/**
 * The world.
 * @version $Id$
 */
public final class World implements Identifiable, Serializable {
    
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
     * Creates a new World object.
     */
    private World() {
	    // do nothing
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
     * @see org.apache.lenya.ac.Accreditable#getAccreditables()
     */
    public Accreditable[] getAccreditables() {
        return (Accreditable[]) Collections.singleton(this).toArray(new Accreditable[1]);
    }
}
