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

/* $Id: AbstractSchedulerListener.java,v 1.2 2004/03/01 16:18:26 gregor Exp $  */

package org.apache.lenya.cms.scheduler;

import org.apache.log4j.Category;
import org.quartz.SchedulerException;
import org.quartz.SchedulerListener;
import org.quartz.Trigger;

public class AbstractSchedulerListener implements SchedulerListener {

	private static final Category log = Category.getInstance(AbstractSchedulerListener.class);

	/**
	 * @see org.quartz.SchedulerListener#jobScheduled(org.quartz.Trigger)
	 */
	public void jobScheduled(Trigger trigger) {
		log.debug("Job scheduled");
		log.debug("    Trigger: [" + trigger + "]");
	}

	/**
	 * @see org.quartz.SchedulerListener#jobUnscheduled(java.lang.String, java.lang.String)
	 */
	public void jobUnscheduled(String name, String group) {
		log.debug("Job unscheduled.");
		log.debug("    Trigger name:  [" + name + "]");
		log.debug("    Trigger group: [" + group + "]");
	}

	/**
	 * @see org.quartz.SchedulerListener#triggerFinalized(org.quartz.Trigger)
	 */
	public void triggerFinalized(Trigger trigger) {
		log.debug("Trigger finalized.");
		log.debug("    Trigger:  [" + trigger + "]");
	}

	/**
	 * @see org.quartz.SchedulerListener#triggersPaused(java.lang.String, java.lang.String)
	 */
	public void triggersPaused(String name, String group) {
		log.debug("Triggers paused.");
		log.debug("    Trigger name:  [" + name + "]");
		log.debug("    Trigger group: [" + group + "]");
	}

	/**
	 * @see org.quartz.SchedulerListener#triggersResumed(java.lang.String, java.lang.String)
	 */
	public void triggersResumed(String name, String group) {
		log.debug("Triggers resumed.");
		log.debug("    Trigger name:  [" + name + "]");
		log.debug("    Trigger group: [" + group + "]");
	}

	/**
	 * @see org.quartz.SchedulerListener#jobsPaused(java.lang.String, java.lang.String)
	 */
	public void jobsPaused(String name, String group) {
		log.debug("Jobs paused.");
		log.debug("    Job name:  [" + name + "]");
		log.debug("    Job group: [" + group + "]");
	}

	/**
	 * @see org.quartz.SchedulerListener#jobsResumed(java.lang.String, java.lang.String)
	 */
	public void jobsResumed(String name, String group) {
		log.debug("Jobs resumed.");
		log.debug("    Job name:  [" + name + "]");
		log.debug("    Job group: [" + group + "]");
	}

	/**
	 * @see org.quartz.SchedulerListener#schedulerError(java.lang.String,
	 *      org.quartz.SchedulerException)
	 */
	public void schedulerError(String message, SchedulerException exception) {
		log.debug("Scheduler exception occured.");
		log.debug("    Message:  [" + message + "]");
		log.debug(exception);
	}

	/**
	 * @see org.quartz.SchedulerListener#schedulerShutdown()
	 */
	public void schedulerShutdown() {
		log.debug("Scheduler shut down.");
	}

}
