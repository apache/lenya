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

/* $Id: Accreditable.java,v 1.2 2004/03/03 12:56:31 gregor Exp $  */

package org.apache.lenya.ac;


/**
 * An Accreditable can be accredited with roles on a certain URI
 * using a {@link Credential}.
 */
public interface Accreditable {
    /**
     * Returns the set of accreditables of this accreditable.
     * The set contains the accreditable itself and all collections it belongs to.
     * @return An array of accreditables.
     */
    Accreditable[] getAccreditables();
}
