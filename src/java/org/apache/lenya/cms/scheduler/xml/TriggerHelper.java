/*
 * TriggerHelper.java
 *
 * Created on November 12, 2002, 1:59 PM
 */

package org.wyona.cms.scheduler.xml;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.List;

import org.dom4j.Attribute;
import org.dom4j.DocumentFactory;
import org.dom4j.Element;

import org.quartz.SimpleTrigger;
import org.quartz.CronTrigger;
import org.quartz.Trigger;

import org.wyona.cms.scheduler.xml.SchedulerXMLFactory;

import org.apache.log4j.Category;

/**
 *
 * @author  ah
 */
public class TriggerHelper {
    static Category log=Category.getInstance(TriggerHelper.class);
    
    public static final String YEAR = "year";
    public static final String MONTH = "month";
    public static final String DAY = "day";
    public static final String HOUR = "hour";
    public static final String MINUTE = "minute";
    public static final String TRIGGER_TYPE = "type";
    public static final String ONCE = "once";
    public static final String REPEATED = "repeated";
    
    public static Trigger createTrigger(
        Element element, String jobName, String jobGroup) {
        if (!element.getName().equals("trigger"))
                throw new IllegalArgumentException();
        
        String triggerType = element.attribute(TRIGGER_TYPE).getValue();

        if (triggerType.equals(ONCE)) {
            List parameterElements = element.elements(SchedulerXMLFactory.getQName("parameter"));
            GregorianCalendar date = new GregorianCalendar();
            for (Iterator i = parameterElements.iterator(); i.hasNext(); ) {
                Element parameterElement = (Element) i.next();
                String name = parameterElement.attribute("name").getValue();
                String value = parameterElement.attribute("value").getValue();
                if (name.equals(YEAR)) date.set(Calendar.YEAR, Integer.parseInt(value));
                if (name.equals(MONTH)) date.set(Calendar.MONTH, Integer.parseInt(value)-1);
                if (name.equals(DAY)) date.set(Calendar.DAY_OF_MONTH, Integer.parseInt(value));
                if (name.equals(HOUR)) date.set(Calendar.HOUR_OF_DAY, Integer.parseInt(value));
                if (name.equals(MINUTE)) date.set(Calendar.MINUTE, Integer.parseInt(value));

                if (name.equals(HOUR)) date.set(Calendar.HOUR_OF_DAY, Integer.parseInt(value));
            }
            return createSimpleTrigger(jobName, jobGroup, date.getTime());
        }

        if (triggerType.equals(REPEATED)) {
            return createCronTrigger(jobName, jobGroup, "45 * * * * ?");
        }
     
        throw new IllegalStateException("Trigger type '" + triggerType + "' not defined!");
    }
    
    private static int id = 0;
    
    protected static String createUniqueTriggerId() {
        return "trigger_" + id++;
    }
    
    public static Trigger createSimpleTrigger(String jobName, String jobGroup, Date date) {
        return new SimpleTrigger(
                createUniqueTriggerId(), "triggerGroup1", jobName, jobGroup,
                date, null, 0, 0);
    }
    
    /**
     * author Michael Wechner
     * Create CronTrigger
     * @param cron_expression Seconds, Minutes, Hours, Day of Month, Months, Day of Week (e.g. 34 * * * * ?)
     */
    public static Trigger createCronTrigger(String jobName, String jobGroup, String cron_expression) {
        try{
          return new CronTrigger(createUniqueTriggerId(), "triggerGroup1", jobName, jobGroup, cron_expression);
          }
        catch(ParseException e){
          log.error(".createCronTrigger(): "+e);
          }
        return null;
    }
    
    public static Element createElement(Trigger trigger) {
        
        DocumentFactory factory = DocumentFactory.getInstance();
        Element triggerElement = SchedulerXMLFactory.createElement("trigger");
        triggerElement.add(factory.createAttribute(triggerElement, "type", ONCE));

        if (trigger == null)
            return triggerElement;

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
            Integer.toString(startTime.get(Calendar.MONTH)+1)));
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
