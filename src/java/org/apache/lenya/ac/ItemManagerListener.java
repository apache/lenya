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

/* $Id: ItemManagerListener.java,v 1.2 2004/03/03 12:56:31 gregor Exp $  */

package org.apache.lenya.ac;

/**
 * This class allows listening to objects that manages items.
 */
public interface ItemManagerListener {

    /**
	 * Called when an accreditable was added.
	 * 
	 * @param item The item that was added.
	 * @throws AccessControlException when an error occurs.
	 */
    void itemAdded(Item item) throws AccessControlException;

    /**
	 * Called when an accreditable was removed.
	 * 
	 * @param item The item that was removed.
	 * @throws AccessControlException when an error occurs.
	 */
    void itemRemoved(Item item) throws AccessControlException;

}
