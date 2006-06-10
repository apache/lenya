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

/* $Id$  */

package org.apache.lenya.cms.scheduler.xml;

import java.io.IOException;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import org.apache.lenya.cms.scheduler.SchedulerStore;
import org.apache.lenya.util.NamespaceMap;
import org.apache.lenya.xml.NamespaceHelper;
import org.apache.log4j.Category;
import org.quartz.CronTrigger;
import org.quartz.SimpleTrigger;
import org.quartz.Trigger;
import org.w3c.dom.Element;


/**
 * Utility class to work with trigger XML.
 */
public final class TriggerHelper {
    
    /**
     * Ctor.
     */
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
    public static final String PREFIX = "trigger";

    /**
     * Creates a trigger from an XML element.
     *
     * @param element The XML element.
     * @param jobName The job name.
     * @param jobGroup The job group.
     *
     * @return A trigger.
     */
    public static Trigger createTrigger(Element element, String jobName, String jobGroup) {
        if (!element.getLocalName().equals("trigger")) {
            throw new IllegalArgumentException();
        }

        String triggerType = element.getAttribute(TRIGGER_TYPE);

        NamespaceHelper helper = SchedulerStore.getNamespaceHelper();

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

    /**
     * Creates a unique trigger ID.
     * @return A string.
     */
    protected static String createUniqueTriggerId() {
        return "trigger_" + id++;
    }

    /**
     * Creates a simple trigger.
     *
     * @param jobName The job name.
     * @param jobGroup The job group.
     * @param date The trigger date.
     *
     * @return DOCUMENT ME!
     */
    public static Trigger createSimpleTrigger(String jobName, String jobGroup, Date date) {
        return new SimpleTrigger(createUniqueTriggerId(), "triggerGroup1", jobName, jobGroup, date,
            null, 0, 0);
    }

    /**
     * Creates a cron trigger.
     *
     * @param jobName The job name.
     * @param jobGroup The job group.
     * @param cron_expression Seconds, Minutes, Hours, Day of Month, Months, Day of Week (e.g. 34
     *        ?)
     *
     * @return A trigger.
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
     * Creates an XML element containing trigger information.
     * @param helper The namespace helper to use.
     * @param trigger The trigger.
     * @return An XML element.
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
    
    /**
     * Extracts the date from the scheduler parameters.
     * @param schedulerParameters The scheduler parameters.
     * @return A date.
     * @throws IOException when something went wrong.
     */
    public static Date getDate(NamespaceMap schedulerParameters) throws IOException {
        NamespaceMap triggerParameters =
            new NamespaceMap(schedulerParameters.getMap(), PREFIX);
        String startDay = (String) triggerParameters.get(DAY);
        String startMonth = (String) triggerParameters.get(MONTH);
        String startYear = (String) triggerParameters.get(YEAR);
        String startHour = (String) triggerParameters.get(HOUR);
        String startMin = (String) triggerParameters.get(MINUTE);

        Date startTime = null;

        try {
            // Month value is 0-based
            startTime =
                new GregorianCalendar(
                    Integer.parseInt(startYear),
                    Integer.parseInt(startMonth) - 1,
                    Integer.parseInt(startDay),
                    Integer.parseInt(startHour),
                    Integer.parseInt(startMin))
                    .getTime();
        } catch (NumberFormatException e) {
            log.error(
                "NumberFormatException with parameters "
                    + "startYear, startMonth, startDay, startHour, startMin: "
                    + startDay
                    + ", "
                    + startMonth
                    + ", "
                    + startDay
                    + ", "
                    + startHour
                    + ", "
                    + startMin,
                e);
            throw new IOException("Parsing scheduling date/time failed!");
        }
        return startTime;
    }

}
