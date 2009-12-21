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
import java.util.Collections;
import java.util.List;

import org.apache.lenya.ac.IPRange;

/**
 * Manage IP ranges.
 * 
 * @version $Id: IPRanges.java 407305 2006-05-17 16:21:49Z andreas $
 */
public class IPRanges extends AccessControlUsecase {

    protected static final String IP_RANGES = "ipRanges";

    /**
     * @see org.apache.lenya.cms.usecase.AbstractUsecase#initParameters()
     */
    protected void initParameters() {
        super.initParameters();
        
        IPRange[] ipRanges = getIpRangeManager().getIPRanges();
        List ipRangeList = new ArrayList();
        ipRangeList.addAll(Arrays.asList(ipRanges));
        Collections.sort(ipRangeList);
        setParameter(IP_RANGES, ipRangeList);
    }
}
