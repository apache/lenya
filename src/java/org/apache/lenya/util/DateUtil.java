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

/* $Id: DateUtil.java,v 1.12 2004/03/17 22:35:56 michi Exp $  */

package org.apache.lenya.util;

import java.util.Calendar;
import java.util.GregorianCalendar;

/**
 * DOCUMENT ME!
 * @deprecated use java.text.DateFormat instead
 */
public class DateUtil {
    private String year = null;
    private String month = null;
    private String day = null;

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

    /**
     * 2003-08-11T23:14:48
     */
    public static String getCurrentDate() {
        Calendar cal = new GregorianCalendar();
        String year = "" + cal.get(Calendar.YEAR);
        String month = oneToTwoDigits("" + (cal.get(Calendar.MONTH) + 1));
        String day = oneToTwoDigits("" + cal.get(Calendar.DAY_OF_MONTH));
        String hour = oneToTwoDigits("" + cal.get(Calendar.HOUR_OF_DAY));
        String minute = oneToTwoDigits("" + cal.get(Calendar.MINUTE));
        String second = oneToTwoDigits("" + cal.get(Calendar.SECOND));
        return year + "-" + month + "-" + day + "T" + hour + ":" + minute + ":" + second;
    }
}
