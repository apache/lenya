/*
$Id: TriggerHelper.java,v 1.14 2003/07/23 13:21:34 gregor Exp $
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
package org.apache.lenya.cms.scheduler.xml;

import org.apache.lenya.cms.scheduler.SchedulerWrapper;
import org.apache.lenya.xml.NamespaceHelper;

import org.apache.log4j.Category;

import org.quartz.CronTrigger;
import org.quartz.SimpleTrigger;
import org.quartz.Trigger;

import org.w3c.dom.Element;

import java.text.ParseException;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;


/**
 * DOCUMENT ME!
 *
 * @author ah
 */
public final class TriggerHelper {
    private TriggerHelper() {
    }

    private static Category log = Category.getInstance(TriggerHelper.class);
    public static final String YEAR = "year";
    public static final String MONTH = "month";
    public static final String DAY = "day";
    public static final String HOUR = "hour";
    public static final String MINUTE = "minute";
    public static final String TRIGGER_TYPE = "type";
    public static final String ONCE = "once";
    public static final String REPEATED = "repeated";
    public static final String CRON_EXPRESSION = "expression";
    private static int id = 0;

    /**
     * DOCUMENT ME!
     *
     * @param element DOCUMENT ME!
     * @param jobName DOCUMENT ME!
     * @param jobGroup DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public static Trigger createTrigger(Element element, String jobName, String jobGroup) {
        if (!element.getLocalName().equals("trigger")) {
            throw new IllegalArgumentException();
        }

        String triggerType = element.getAttribute(TRIGGER_TYPE);

        NamespaceHelper helper = SchedulerWrapper.getNamespaceHelper();

        // SimpleTrigger
        if (triggerType.equals(ONCE)) {
            Element[] parameterElements = helper.getChildren(element, "parameter");
            GregorianCalendar date = new GregorianCalendar();

            for (int i = 0; i < parameterElements.length; i++) {
                String name = parameterElements[i].getAttribute("name");
                String value = parameterElements[i].getAttribute("value");

                if (name.equals(YEAR)) {
                    date.set(Calendar.YEAR, Integer.parseInt(value));
                }

                if (name.equals(MONTH)) {
                    date.set(Calendar.MONTH, Integer.parseInt(value) - 1);
                }

                if (name.equals(DAY)) {
                    date.set(Calendar.DAY_OF_MONTH, Integer.parseInt(value));
                }

                if (name.equals(HOUR)) {
                    date.set(Calendar.HOUR_OF_DAY, Integer.parseInt(value));
                }

                if (name.equals(MINUTE)) {
                    date.set(Calendar.MINUTE, Integer.parseInt(value));
                }

                if (name.equals(HOUR)) {
                    date.set(Calendar.HOUR_OF_DAY, Integer.parseInt(value));
                }
            }

            return createSimpleTrigger(jobName, jobGroup, date.getTime());
        }

        // CronTrigger
        if (triggerType.equals(REPEATED)) {
            Element[] parameterElements = helper.getChildren(element, "parameter");
            String name = parameterElements[0].getAttribute("name");
            String value = parameterElements[0].getAttribute("value");
            String cron_expression;

            if (name.equals(CRON_EXPRESSION)) {
                cron_expression = value;
            } else {
                cron_expression = "45 * * * * ?";
            }

            return createCronTrigger(jobName, jobGroup, cron_expression);
        }

        throw new IllegalStateException("Trigger type '" + triggerType + "' not defined!");
    }

    protected static String createUniqueTriggerId() {
        return "trigger_" + id++;
    }

    /**
     * DOCUMENT ME!
     *
     * @param jobName DOCUMENT ME!
     * @param jobGroup DOCUMENT ME!
     * @param date DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public static Trigger createSimpleTrigger(String jobName, String jobGroup, Date date) {
        return new SimpleTrigger(createUniqueTriggerId(), "triggerGroup1", jobName, jobGroup, date,
            null, 0, 0);
    }

    /**
     * author Michael Wechner Create CronTrigger
     *
     * @param jobName DOCUMENT ME!
     * @param jobGroup DOCUMENT ME!
     * @param cron_expression Seconds, Minutes, Hours, Day of Month, Months, Day of Week (e.g. 34
     *        ?)
     *
     * @return DOCUMENT ME!
     */
    public static Trigger createCronTrigger(String jobName, String jobGroup, String cron_expression) {
        try {
            return new CronTrigger(createUniqueTriggerId(), "triggerGroup1", jobName, jobGroup,
                cron_expression);
        } catch (ParseException e) {
            log.error(".createCronTrigger(): " + e);
        }

        return null;
    }

    /**
     * DOCUMENT ME!
     *
     * @param trigger DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public static Element createElement(NamespaceHelper helper, Trigger trigger) {
        Element triggerElement = helper.createElement("trigger");
        triggerElement.setAttribute("type", ONCE);

        if (trigger == null) {
            return triggerElement;
        }

        GregorianCalendar startTime = new GregorianCalendar();
        startTime.setTime(trigger.getStartTime());

        Element yearElement = helper.createElement("parameter");
        yearElement.setAttribute("name", YEAR);
        yearElement.setAttribute("value", Integer.toString(startTime.get(Calendar.YEAR)));
        triggerElement.appendChild(yearElement);

        Element monthElement = helper.createElement("parameter");
        monthElement.setAttribute("name", MONTH);
        monthElement.setAttribute("value", Integer.toString(startTime.get(Calendar.MONTH) + 1));
        triggerElement.appendChild(monthElement);

        Element dayElement = helper.createElement("parameter");
        dayElement.setAttribute("name", DAY);
        dayElement.setAttribute("value", Integer.toString(startTime.get(Calendar.DAY_OF_MONTH)));
        triggerElement.appendChild(dayElement);

        Element hourElement = helper.createElement("parameter");
        hourElement.setAttribute("name", HOUR);
        hourElement.setAttribute("value", Integer.toString(startTime.get(Calendar.HOUR_OF_DAY)));
        triggerElement.appendChild(hourElement);

        Element minuteElement = helper.createElement("parameter");
        minuteElement.setAttribute("name", MINUTE);
        minuteElement.setAttribute("value", Integer.toString(startTime.get(Calendar.MINUTE)));
        triggerElement.appendChild(minuteElement);

        return triggerElement;
    }
}
