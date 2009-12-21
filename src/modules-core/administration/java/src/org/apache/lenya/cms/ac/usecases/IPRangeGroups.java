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
package org.apache.lenya.cms.ac.usecases;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.apache.lenya.ac.Group;
import org.apache.lenya.ac.IPRange;
import org.apache.lenya.cms.usecase.UsecaseException;

/**
 * Usecase to edit a IP range's group affiliation.
 */
public class IPRangeGroups extends AccessControlUsecase {

    protected static final String IP_RANGE_GROUPS = "ipRangeGroups";
    protected static final String OTHER_GROUPS = "otherGroups";
    protected static final String ADD = "add";
    protected static final String REMOVE = "remove";
    protected static final String IP_RANGE_GROUP = "ipRangeGroup";
    protected static final String OTHER_GROUP = "otherGroup";

    /**
     * @see org.apache.lenya.cms.usecase.AbstractUsecase#doExecute()
     */
    protected void doExecute() throws Exception {
        super.doExecute();
        
        IPRange ipRange = getIpRange();
        ipRange.removeFromAllGroups();
        
        List ipRangeGroups = (List) getParameter(IP_RANGE_GROUPS);
        for (Iterator i = ipRangeGroups.iterator(); i.hasNext(); ) {
            Group group = (Group) i.next();
            group.add(ipRange);
        }
        ipRange.save();
    }
    
    protected IPRange getIpRange() {
        String ipRangeId = getParameterAsString(IPRangeProfile.ID);
        IPRange ipRange = getIpRangeManager().getIPRange(ipRangeId);
        if (ipRange == null) {
            throw new RuntimeException("IP range [" + ipRangeId + "] not found.");
        }
        return ipRange;
    }

    /**
     * @see org.apache.lenya.cms.usecase.Usecase#advance()
     */
    public void advance() throws UsecaseException {
        super.advance();

        String add = getParameterAsString(ADD);
        String remove = getParameterAsString(REMOVE);
        if (add != null || remove != null) {

            List ipRangeGroups = (List) getParameter(IP_RANGE_GROUPS);
            List otherGroups = (List) getParameter(OTHER_GROUPS);
            
            if (add != null) {
                String groupId = getParameterAsString(OTHER_GROUP);
                if (groupId != null) {
                    if (getLogger().isDebugEnabled()) {
                        getLogger().debug("add group [" + groupId + "]");
                    }
                    Group group = getGroupManager().getGroup(groupId);
                    ipRangeGroups.add(group);
                    otherGroups.remove(group);
                }
            }

            if (remove != null) {
                String groupId = getParameterAsString(IP_RANGE_GROUP);
                if (groupId != null) {
                    if (getLogger().isDebugEnabled()) {
                        getLogger().debug("remove group [" + groupId + "]");
                    }
                    Group group = getGroupManager().getGroup(groupId);
                    otherGroups.add(group);
                    ipRangeGroups.remove(group);
                }
            }
            
            deleteParameter(ADD);
            deleteParameter(REMOVE);
            deleteParameter(IP_RANGE_GROUP);
            deleteParameter(OTHER_GROUP);
        }

    }

    protected void initParameters() {
        super.initParameters();
        Group[] ipRangeGroupArray = getIpRange().getGroups();
        
        List ipRangeGroups = new ArrayList(Arrays.asList(ipRangeGroupArray));
        setParameter(IP_RANGE_GROUPS, ipRangeGroups);

        Group[] allGroups = getGroupManager().getGroups();
        List otherGroups = new ArrayList();
        for (int i = 0; i < allGroups.length; i++) {
            if (!ipRangeGroups.contains(allGroups[i])) {
                otherGroups.add(allGroups[i]);
            }
        }
        setParameter(OTHER_GROUPS, otherGroups);
    }

}