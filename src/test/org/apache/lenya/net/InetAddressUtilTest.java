/*
* Licensed to the Apache Software Foundation (ASF) under one or more
* contributor license agreements.  See the NOTICE file distributed with
* this work for additional information regarding copyright ownership.
* The ASF licenses this file to You under the Apache License, Version 2.0
* (the "License"); you may not use this file except in compliance with
* the License.  You may obtain a copy of the License at
*
*     http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/
package org.apache.lenya.net;

import java.net.InetAddress;

/**
 *
 */
public class InetAddressUtilTest {
    /**
     *
     */
    public static void main(String[] args) {
        if (args.length != 3) {
            System.out.println("Usage: " + new InetAddressUtilTest().getClass().getName() + " network subnet ip");
            return;
        }

        try {
            InetAddress network = InetAddress.getByName(args[0]); // "195.226.6.64");
            InetAddress subnet = InetAddress.getByName(args[1]); // "255.255.255.0");
            InetAddress ip = InetAddress.getByName(args[2]); // "195.226.6.70");
            System.out.println(InetAddressUtil.contains(network, subnet, ip));
        } catch(Exception e) {
            System.err.println(e);
        }
    }
}
