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
package org.apache.lenya.cms.ac.usecases;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.apache.lenya.ac.Group;

/**
 * Manage groups.
 * 
 * @version $Id:$
 */
public class Groups extends AccessControlUsecase {
    
    protected static final String GROUPS = "groups";

    /**
     * @see org.apache.lenya.cms.usecase.AbstractUsecase#initParameters()
     */
    protected void initParameters() {
        super.initParameters();
        
        setExitUsecase(getName(), null);
        
        Group[] groups = getGroupManager().getGroups();
        List groupList = new ArrayList();
        groupList.addAll(Arrays.asList(groups));
        Collections.sort(groupList);
        setParameter(GROUPS, groupList);
    }
}
