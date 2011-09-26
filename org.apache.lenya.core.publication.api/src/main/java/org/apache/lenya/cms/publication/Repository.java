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
package org.apache.lenya.cms.publication;

import javax.servlet.http.HttpServletRequest;
//import org.apache.lenya.cms.publication.Session;


//import org.apache.lenya.ac.Identity;

//TODO : remove this interface from here as there is publication and not repository
public interface Repository {
    
    //Session getSession(HttpServletRequest request);
    
    //florent : remove as it create cycliq dependencies.
    //note : can be re-enable now as identity is in lenya-core-identity
    // suppress this comment when ok
    //Session startSession(Identity identity, boolean modifiable);

    void removeSession(HttpServletRequest request);
    
}
