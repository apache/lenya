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
package org.apache.lenya.cms.repository;

/**
 * The revision history of a node.
 */
public interface History {
    
    /**
     * @return The available revision numbers in descending order (starting with the newest one).
     */
    int[] getRevisionNumbers();
    
    /**
     * @return The latest revision.
     * @throws RuntimeException if the node has no revisions yet.
     */
    Revision getLatestRevision();
    
    /**
     * @param number A revision number.
     * @return A revision.
     * @throws RepositoryException if no revision with this number exists.
     */
    Revision getRevision(int number) throws RepositoryException;

}
