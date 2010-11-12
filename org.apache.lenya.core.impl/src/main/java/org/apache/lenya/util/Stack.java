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

/* $Id$  */

package org.apache.lenya.util;

import java.util.Vector;


/**
 * A helper class to implement a stack. Unlike java.util.Stack, this stack
 * discards old elements once maxsize is reached.
 * @see java.util.Stack
 */
public class Stack extends Vector {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	int maxsize = 0;

    /**
     * Creates a new Stack object.
     * @param _maxsize The maximum size of the stack
     */
    public Stack(int _maxsize) {
        this.maxsize = _maxsize;
    }

    /**
     * Push an object on the stack
     * @param object The object
     */
    public void push(Object object) {
        insertElementAt(object, 0);

        if (size() == (this.maxsize + 1)) {
            removeElementAt(this.maxsize);
        }
    }
}
