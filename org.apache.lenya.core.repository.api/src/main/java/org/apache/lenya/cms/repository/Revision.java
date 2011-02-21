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
 * A revision.
 */
public interface Revision extends ContentHolder{
//florent : remove while not implement abstract method of contenHolder
// sourceNodeRevision implement it, but have to implement them in RevisionWrapper.java
//extends ContentHolder {
    
    /**
     * @return The revision number.
     */
    int getNumber();
    
    /**
     * @return The time when this revision was created.
     */
    long getTime();
    
    /**
     * @return The ID of the user who created this revision.
     */
    String getUserId();
    
}
