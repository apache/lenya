/*
 * $Id: AbstractSchedulerListener.java,v 1.1 2003/10/26 17:29:31 andreas Exp $ <License>
 * 
 * ============================================================================ The Apache Software
 * License, Version 1.1
 * ============================================================================
 * 
 * Copyright (C) 1999-2003 The Apache Software Foundation. All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without modifica- tion, are permitted
 * provided that the following conditions are met:
 *  1. Redistributions of source code must retain the above copyright notice, this list of
 * conditions and the following disclaimer.
 *  2. Redistributions in binary form must reproduce the above copyright notice, this list of
 * conditions and the following disclaimer in the documentation and/or other materials provided
 * with the distribution.
 *  3. The end-user documentation included with the redistribution, if any, must include the
 * following acknowledgment: "This product includes software developed by the Apache Software
 * Foundation (http://www.apache.org/)." Alternately, this acknowledgment may appear in the
 * software itself, if and wherever such third-party acknowledgments normally appear.
 *  4. The names "Apache Lenya" and "Apache Software Foundation" must not be used to endorse or
 * promote products derived from this software without prior written permission. For written
 * permission, please contact apache@apache.org.
 *  5. Products derived from this software may not be called "Apache", nor may "Apache" appear in
 * their name, without prior written permission of the Apache Software Foundation.
 * 
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR ITS CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLU- DING, BUT
 * NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR
 * BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT,
 * STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 * 
 * This software consists of voluntary contributions made by many individuals on behalf of the
 * Apache Software Foundation and was originally created by Michael Wechner <michi@apache.org> .
 * For more information on the Apache Soft- ware Foundation, please see <http://www.apache.org/> .
 * 
 * Lenya includes software developed by the Apache Software Foundation, W3C, DOM4J Project,
 * BitfluxEditor, Xopus, and WebSHPINX. </License>
 */
package org.apache.lenya.cms.scheduler;

import org.apache.log4j.Category;
import org.quartz.SchedulerException;
import org.quartz.SchedulerListener;
import org.quartz.Trigger;

/**
 * @author andreas
 * 
 * To change the template for this generated type comment go to Window - Preferences - Java - Code
 * Generation - Code and Comments
 */
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
