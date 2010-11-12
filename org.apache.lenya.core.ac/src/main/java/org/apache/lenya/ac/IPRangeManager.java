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

package org.apache.lenya.ac;

/**
 * An IP range manager.
 */
public interface IPRangeManager extends ItemManager {
    
    /**
     * Get all IP ranges.
     *
     * @return an array of IP ranges.
     */
    IPRange[] getIPRanges();
    
    /**
     * Add the given IP range
     *
     * @param id IP range that is to be added
     * @return An IP range.
     * @throws AccessControlException when the IP range is already contained.
     */
    IPRange add(String id) throws AccessControlException;
    
    /**
     * Remove the given IP range
     *
     * @param range IP range that is to be removed
     * @throws AccessControlException when the IP range is not contained.
     */
    void remove(IPRange range) throws AccessControlException;
    
    /**
     * Get the IPRange with the given id.
     *
     * @param rangeId user id of requested IP range
     * @return the requested IP range or null if there is
     * no IP range with the given id
     */
    IPRange getIPRange(String rangeId);
    
}