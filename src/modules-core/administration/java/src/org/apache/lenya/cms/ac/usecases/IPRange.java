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

/**
 * Show information about an IP range.
 * 
 * @version $Id: IPRange.java 407305 2006-05-17 16:21:49Z andreas $
 */
public class IPRange extends AccessControlUsecase {

    protected static final String IP_RANGE_ID = "ipRangeId";
    protected static final String IP_RANGE = "ipRange";

    /**
     * @see org.apache.lenya.cms.usecase.Usecase#setParameter(java.lang.String, java.lang.Object)
     */
    public void setParameter(String name, Object value) {
        super.setParameter(name, value);

        if (name.equals(IP_RANGE_ID)) {
            String ipRangeId = (String) value;
            org.apache.lenya.ac.IPRange ipRange = getIpRangeManager().getIPRange(ipRangeId);
            if (ipRange == null) {
                addErrorMessage("iprange_no_such_iprange", new String[] { ipRangeId });
            } else {
                setParameter(IP_RANGE, ipRange);
            }
        }
    }
}