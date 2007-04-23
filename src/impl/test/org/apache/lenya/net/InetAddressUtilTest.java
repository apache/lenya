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
import java.net.UnknownHostException;

import org.apache.cocoon.core.container.ContainerTestCase;

/**
 * Inet address util test
 */
public class InetAddressUtilTest extends ContainerTestCase {

    public void testInetAddressUtil() throws UnknownHostException {

        String[] args = { "195.226.6.64", "255.255.255.0", "195.226.6.70" };

        if (args.length != 3) {
            System.out.println("Usage: InetAddressUtilTest network subnet ip");
            return;
        }

        InetAddress network = InetAddress.getByName(args[0]); // "195.226.6.64");
        InetAddress subnet = InetAddress.getByName(args[1]); // "255.255.255.0");
        InetAddress ip = InetAddress.getByName(args[2]); // "195.226.6.70");
        
        InetAddressUtil util = new InetAddressUtil(getLogger());
        System.out.println(util.contains(network, subnet, ip));
    }
}
