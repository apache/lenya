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
package org.apache.lenya.transaction;

import org.apache.avalon.framework.service.ServiceException;

/**
 * Operation interface.
 * 
 * @version $Id: Operation.java 158432 2005-03-21 09:56:48Z andreas $
 */
public interface Operation {

    /**
     * The Avalon role.
     */
    String ROLE = Operation.class.getName();
    
    /**
     * Returns the unit of work object.
     * @return A unit of work.
     * @throws ServiceException if the unit-of-work component could not be retrieved by the service manager
     */
    UnitOfWork getUnitOfWork() throws ServiceException;
    
    /**
     * @param unit The unit of work.
     */
    void setUnitOfWork(UnitOfWork unit);

}
