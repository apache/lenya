/*
 * $Id: TriggerHelper.java,v 1.5 2003/02/07 12:14:21 ah Exp $
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
package org.wyona.cms.scheduler.xml;

import org.apache.log4j.Category;

import org.dom4j.Attribute;
import org.dom4j.DocumentFactory;
import org.dom4j.Element;

import org.quartz.CronTrigger;
import org.quartz.SimpleTrigger;
import org.quartz.Trigger;

import org.wyona.cms.scheduler.xml.SchedulerXMLFactory;

import java.text.ParseException;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.List;


/**
 * DOCUMENT ME!
 *
 * @author ah
 */
public class TriggerHelper {
    static Category log = Category.getInstance(TriggerHelper.class);
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
        if (!element.getName().equals("trigger")) {
            throw new IllegalArgumentException();
        }

        String triggerType = element.attribute(TRIGGER_TYPE).getValue();

        // SimpleTrigger
        if (triggerType.equals(ONCE)) {
            List parameterElements = element.elements(SchedulerXMLFactory.getQName("parameter"));
            GregorianCalendar date = new GregorianCalendar();

            for (Iterator i = parameterElements.iterator(); i.hasNext();) {
                Element parameterElement = (Element) i.next();
                String name = parameterElement.attribute("name").getValue();
                String value = parameterElement.attribute("value").getValue();

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
            List parameterElements = element.elements(SchedulerXMLFactory.getQName("parameter"));
            Element parameterElement = (Element) parameterElements.iterator().next();
            String name = parameterElement.attribute("name").getValue();
            String value = parameterElement.attribute("value").getValue();
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
    public static Element createElement(Trigger trigger) {
        DocumentFactory factory = DocumentFactory.getInstance();
        Element triggerElement = SchedulerXMLFactory.createElement("trigger");
        triggerElement.add(factory.createAttribute(triggerElement, "type", ONCE));

        if (trigger == null) {
            return triggerElement;
        }

        GregorianCalendar startTime = new GregorianCalendar();
        startTime.setTime(trigger.getStartTime());

        Element yearElement = SchedulerXMLFactory.createElement("parameter");
        yearElement.add(factory.createAttribute(yearElement, "name", YEAR));
        yearElement.add(factory.createAttribute(yearElement, "value",
                Integer.toString(startTime.get(Calendar.YEAR))));
        triggerElement.add(yearElement);

        Element monthElement = SchedulerXMLFactory.createElement("parameter");
        monthElement.add(factory.createAttribute(monthElement, "name", MONTH));
        monthElement.add(factory.createAttribute(monthElement, "value",
                Integer.toString(startTime.get(Calendar.MONTH) + 1)));
        triggerElement.add(monthElement);

        Element dayElement = SchedulerXMLFactory.createElement("parameter");
        dayElement.add(factory.createAttribute(dayElement, "name", DAY));
        dayElement.add(factory.createAttribute(dayElement, "value",
                Integer.toString(startTime.get(Calendar.DAY_OF_MONTH))));
        triggerElement.add(dayElement);

        Element hourElement = SchedulerXMLFactory.createElement("parameter");
        hourElement.add(factory.createAttribute(hourElement, "name", HOUR));
        hourElement.add(factory.createAttribute(hourElement, "value",
                Integer.toString(startTime.get(Calendar.HOUR_OF_DAY))));
        triggerElement.add(hourElement);

        Element minuteElement = SchedulerXMLFactory.createElement("parameter");
        minuteElement.add(factory.createAttribute(minuteElement, "name", MINUTE));
        minuteElement.add(factory.createAttribute(minuteElement, "value",
                Integer.toString(startTime.get(Calendar.MINUTE))));
        triggerElement.add(minuteElement);

        return triggerElement;
    }
}
