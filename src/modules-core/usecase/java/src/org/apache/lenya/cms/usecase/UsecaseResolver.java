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
package org.apache.lenya.cms.usecase;

import org.apache.avalon.framework.service.ServiceException;

/**
 * Usecase resolver interface.
 *
 * @version $Id$
 */
public interface UsecaseResolver {

    /**
     * The Avalon role.
     */
    String ROLE = UsecaseResolver.class.getName();
    
    /**
     * Resolves a usecase object.
     * @param webappUrl The web application URL.
     * @param name The name of the usecase.
     * @return A usecase object.
     * @throws ServiceException if the object could not be created.
     */
    Usecase resolve(String webappUrl, String name) throws ServiceException;
    
    /**
     * Checks if a certain usecase is registered.
     * @param webappUrl The web application URL.
     * @param name The usecase name.
     * @return A boolean value.
     * @throws ServiceException if an error occurs.
     */
    boolean isRegistered(String webappUrl, String name) throws ServiceException;
    
    /**
     * Releases a usecase object.
     * @param usecase The usecase object.
     * @throws ServiceException if an error occurs.
     */
    void release(Usecase usecase) throws ServiceException;
    
    /**
     * @return The names of all registered usecases in alphabetical order.
     */
    String[] getUsecaseNames();

    /**
     * @param usecaseName The usecase to register.
     */
    void register(String usecaseName);

}
