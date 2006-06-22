package org.apache.lenya.cms.ac.usecases;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.apache.lenya.ac.Group;

/**
 * Manage groups.
 * 
 * @version $Id: Groups.java 407305 2006-05-17 16:21:49Z andreas $
 */
public class Groups extends AccessControlUsecase {
    
    protected static final String GROUPS = "groups";

    /**
     * @see org.apache.lenya.cms.usecase.AbstractUsecase#initParameters()
     */
    protected void initParameters() {
        super.initParameters();
        
        Group[] groups = getGroupManager().getGroups();
        List groupList = new ArrayList();
        groupList.addAll(Arrays.asList(groups));
        Collections.sort(groupList);
        setParameter(GROUPS, groupList);
    }
}
