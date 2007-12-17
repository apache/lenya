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

/**
 * The world.
 * @version $Id: World.java 473841 2006-11-12 00:46:38Z gregor $
 */
public final class World implements Identifiable, Accreditable, Serializable {
    
    private static final String NAME = "everyone";

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

    public String getName() {
        return NAME;
    }

    public Accreditable[] getAccreditables(AccreditableManager manager) {
        Accreditable[] accrs = { this };
        return accrs;
    }
}
