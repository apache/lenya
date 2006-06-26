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

/* $Id: TwoTuple.java 416058 2006-06-21 18:24:05Z andreas $  */

package org.apache.lenya.cms.ant;

/**
 * Helper class to hold two values.
 */
public class TwoTuple {
    /**
     * <code>x</code> The x value
     */
    public int x;
    /**
     * <code>y</code> The y value
     */
    public int y;

    /**
     * Constructor
     * @param _x The x value
     * @param _y The y value
     * 
     */
    public TwoTuple(int _x, int _y) {
        this.x = _x;
        this.y = _y;
    }
}
