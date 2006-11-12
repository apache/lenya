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

package org.apache.lenya.cms.ac.usecase;

import org.apache.lenya.ac.AccessControlException;
import org.apache.lenya.ac.Authorizer;
import org.apache.lenya.ac.Role;
import org.apache.lenya.cms.publication.Publication;

/**
 * Authorizer for usecases.
 * @version $Id: UsecaseAuthorizer.java 392449 2006-04-07 23:20:38Z michi $
 */
public interface UsecaseAuthorizer extends Authorizer {

    /**
     * Authorizes a usecase.
     * 
     * @param usecase The usecase ID.
     * @param roles The roles of the current identity.
     * @param publication The publication.
     * @param requestURI The request URI.
     * @return A boolean value.
     * @throws AccessControlException when something went wrong.
     */
    public boolean authorizeUsecase(String usecase, Role[] roles, Publication publication,
            String requestURI) throws AccessControlException;
}
