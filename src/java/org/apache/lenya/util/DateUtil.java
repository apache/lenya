/*
<License>

 ============================================================================
                   The Apache Software License, Version 1.1
 ============================================================================

 Copyright (C) 1999-2003 The Apache Software Foundation. All rights reserved.

 Redistribution and use in source and binary forms, with or without modifica-
 tion, are permitted provided that the following conditions are met:

 1. Redistributions of  source code must  retain the above copyright  notice,
    this list of conditions and the following disclaimer.

 2. Redistributions in binary form must reproduce the above copyright notice,
    this list of conditions and the following disclaimer in the documentation
    and/or other materials provided with the distribution.

 3. The end-user documentation included with the redistribution, if any, must
    include  the following  acknowledgment:  "This product includes  software
    developed  by the  Apache Software Foundation  (http://www.apache.org/)."
    Alternately, this  acknowledgment may  appear in the software itself,  if
    and wherever such third-party acknowledgments normally appear.

 4. The names "Apache Lenya" and  "Apache Software Foundation"  must  not  be
    used to  endorse or promote  products derived from  this software without
    prior written permission. For written permission, please contact
    apache@apache.org.

 5. Products  derived from this software may not  be called "Apache", nor may
    "Apache" appear  in their name,  without prior written permission  of the
    Apache Software Foundation.

 THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED WARRANTIES,
 INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 FITNESS  FOR A PARTICULAR  PURPOSE ARE  DISCLAIMED.  IN NO  EVENT SHALL  THE
 APACHE SOFTWARE  FOUNDATION  OR ITS CONTRIBUTORS  BE LIABLE FOR  ANY DIRECT,
 INDIRECT, INCIDENTAL, SPECIAL,  EXEMPLARY, OR CONSEQUENTIAL  DAMAGES (INCLU-
 DING, BUT NOT LIMITED TO, PROCUREMENT  OF SUBSTITUTE GOODS OR SERVICES; LOSS
 OF USE, DATA, OR  PROFITS; OR BUSINESS  INTERRUPTION)  HOWEVER CAUSED AND ON
 ANY  THEORY OF LIABILITY,  WHETHER  IN CONTRACT,  STRICT LIABILITY,  OR TORT
 (INCLUDING  NEGLIGENCE OR  OTHERWISE) ARISING IN  ANY WAY OUT OF THE  USE OF
 THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

 This software  consists of voluntary contributions made  by many individuals
 on  behalf of the Apache Software  Foundation and was  originally created by
 Michael Wechner <michi@apache.org>. For more information on the Apache Soft-
 ware Foundation, please see <http://www.apache.org/>.

 Lenya includes software developed by the Apache Software Foundation, W3C,
 DOM4J Project, BitfluxEditor, Xopus, and WebSHPINX.
</License>
*/
package org.apache.lenya.util;

import java.util.Calendar;
import java.util.GregorianCalendar;

/**
 * DOCUMENT ME!
 *
 * @author Edith Chevrier
 * @author Christian Egli
 * @author Michael Wechner
 * @version $Id: DateUtil.java,v 1.10 2003/08/11 22:19:25 michi Exp $
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
        String hour = oneToTwoDigits("" + cal.get(Calendar.HOUR));
        String minute = oneToTwoDigits("" + cal.get(Calendar.MINUTE));
        String second = oneToTwoDigits("" + cal.get(Calendar.SECOND));
        return year + "-" + month + "-" + day + "T" + hour + ":" + minute + ":" + second;
    }
}
