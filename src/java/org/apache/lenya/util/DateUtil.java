/*
 * $Id: DateUtil.java,v 1.4 2003/03/04 17:46:47 gregor Exp $
 * <License>
 * The Apache Software License
 *
 * Copyright (c) 2002 wyona. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice, this
 *    list of conditions and the following disclaimer in the documentation and/or
 *    other materials provided with the distribution.
 *
 * 3. All advertising materials mentioning features or use of this software must
 *    display the following acknowledgment: "This product includes software developed
 *    by wyona (http://www.wyona.org)"
 *
 * 4. The name "wyona" must not be used to endorse or promote products derived from
 *    this software without prior written permission. For written permission, please
 *    contact contact@wyona.org
 *
 * 5. Products derived from this software may not be called "wyona" nor may "wyona"
 *    appear in their names without prior written permission of wyona.
 *
 * 6. Redistributions of any form whatsoever must retain the following acknowledgment:
 *    "This product includes software developed by wyona (http://www.wyona.org)"
 *
 * THIS SOFTWARE IS PROVIDED BY wyona "AS IS" WITHOUT ANY WARRANTY EXPRESS OR IMPLIED,
 * INCLUDING THE WARRANTY OF NON-INFRINGEMENT AND THE IMPLIED WARRANTIES OF MERCHANTI-
 * BILITY AND FITNESS FOR A PARTICULAR PURPOSE. wyona WILL NOT BE LIABLE FOR ANY DAMAGES
 * SUFFERED BY YOU AS A RESULT OF USING THIS SOFTWARE. IN NO EVENT WILL wyona BE LIABLE
 * FOR ANY SPECIAL, INDIRECT OR CONSEQUENTIAL DAMAGES OR LOST PROFITS EVEN IF wyona HAS
 * BEEN ADVISED OF THE POSSIBILITY OF THEIR OCCURRENCE. wyona WILL NOT BE LIABLE FOR ANY
 * THIRD PARTY CLAIMS AGAINST YOU.
 *
 * Wyona includes software developed by the Apache Software Foundation, W3C,
 * DOM4J Project, BitfluxEditor and Xopus.
 * </License>
 */
package org.lenya.util;

/**
 * DOCUMENT ME!
 *
 * @author Edith Chevrier
 * @version 2002.7.8
 */
public class DateUtil {
    public String year = null;
    public String month = null;
    public String day = null;

    /**
     * Creates a new DateUtil object.
     */
    public DateUtil() {
    }

    /**
     * Creates a new DateUtil object.
     *
     * @param year DOCUMENT ME!
     * @param month DOCUMENT ME!
     * @param day DOCUMENT ME!
     */
    public DateUtil(String year, String month, String day) {
        this.year = year;
        this.month = month;
        this.day = day;
    }

    /**
     * DOCUMENT ME!
     *
     * @param args DOCUMENT ME!
     */
    public static void main(String[] args) {
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public String toString() {
        return year + "." + month + "." + day;
    }

    /**
     * DOCUMENT ME!
     *
     * @param oneDigit DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public static String oneToTwoDigits(String oneDigit) {
        if (oneDigit.length() == 1) {
            return "0" + oneDigit;
        }

        return oneDigit;
    }

    /**
     * DOCUMENT ME!
     *
     * @param twoDigit DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public static String twoToOneDigit(String twoDigit) {
        if ((twoDigit.charAt(0) == '0') && (twoDigit.length() == 2)) {
            return "" + twoDigit.charAt(1);
        }

        return twoDigit;
    }

    /**
     * DOCUMENT ME!
     *
     * @param i DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public static String getMonthName(int i) {
        String name = null;

        switch (i) {
        case 1:
            name = "January";

            break;

        case 2:
            name = "February";

            break;

        case 3:
            name = "March";

            break;

        case 4:
            name = "April";

            break;

        case 5:
            name = "May";

            break;

        case 6:
            name = "June";

            break;

        case 7:
            name = "July";

            break;

        case 8:
            name = "August";

            break;

        case 9:
            name = "September";

            break;

        case 10:
            name = "October";

            break;

        case 11:
            name = "November";

            break;

        case 12:
            name = "December";

            break;

        default:
            name = "Hey, that's not a valid month";

            break;
        }

        return name;
    }

    /**
     * DOCUMENT ME!
     *
     * @param i DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public static String getDayName(int i) {
        String name = null;

        switch (i) {
        case 1:
            name = "Sunday";

            break;

        case 2:
            name = "Monday";

            break;

        case 3:
            name = "Tuesday";

            break;

        case 4:
            name = "Wednesday";

            break;

        case 5:
            name = "Thursday";

            break;

        case 6:
            name = "Friday";

            break;

        case 7:
            name = "Saturday";

            break;

        default:
            name = "Hey, that's not a valid day";

            break;
        }

        return name;
    }
}
