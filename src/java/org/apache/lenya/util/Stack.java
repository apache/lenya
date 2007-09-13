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

/* $Id: Stack.java 473841 2006-11-12 00:46:38Z gregor $  */

package org.apache.lenya.util;

import java.util.Vector;


/**
 * DOCUMENT ME!
 */

// FIXME: this class seems pretty useless. Why not remove it?
public class Stack extends Vector {
    int maxsize = 0;

    /**
     * Creates a new Stack object.
     *
     * @param maxsize DOCUMENT ME!
     */
    public Stack(int maxsize) {
        this.maxsize = maxsize;
    }

    /**
     * DOCUMENT ME!
     *
     * @param args DOCUMENT ME!
     */
    public static void main(String[] args) {
    }

    /**
     * DOCUMENT ME!
     *
     * @param object DOCUMENT ME!
     */
    public void push(Object object) {
        insertElementAt(object, 0);

        if (size() == (maxsize + 1)) {
            removeElementAt(maxsize);
        }
    }
}
