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
