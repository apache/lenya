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

/* $Id: AbstractRole.java,v 1.3 2004/03/03 12:56:33 gregor Exp $  */

package org.apache.lenya.ac.impl;

import org.apache.lenya.ac.Role;


/**
 * A Role embodies the privilege to do certain things.
 */
public abstract class AbstractRole extends AbstractItem implements Role {
    /**
     * Creates a new instance of Role.
     */
    public AbstractRole() {
    }

    /**
     * Creates a new instance of Role.
     * @param name The role name.
     */
    public AbstractRole(String name) {
        assert name != null;
        setName(name);
    }

}
