<?xml version="1.0"?>

<!--
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

/* $Id: scheduler.xsl,v 1.4 2004/03/01 16:18:24 gregor Exp $  */
-->

<xsl:stylesheet version="1.0"
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:xsp="http://apache.org/xsp"
  xmlns:xsp-scheduler="http://apache.org/cocoon/lenya/xsp/scheduler/1.0"
  xmlns:sch="http://apache.org/cocoon/lenya/scheduler/1.0"
>

<xsl:template match="xsp-scheduler:insert-data">
	<sch:scheduler-data>
  <xsp:logic>
    {
			java.util.GregorianCalendar calendar = new java.util.GregorianCalendar();
			
				<sch:year><xsp:expr>calendar.get(java.util.Calendar.YEAR)</xsp:expr></sch:year>
				<sch:month><xsp:expr>calendar.get(java.util.Calendar.MONTH) + 1</xsp:expr></sch:month>
				<sch:day><xsp:expr>calendar.get(java.util.Calendar.DAY_OF_MONTH)</xsp:expr></sch:day>
				<sch:hour><xsp:expr>calendar.get(java.util.Calendar.HOUR_OF_DAY)</xsp:expr></sch:hour>
				<sch:minute><xsp:expr>calendar.get(java.util.Calendar.MINUTE)</xsp:expr></sch:minute>
				<sch:second><xsp:expr>calendar.get(java.util.Calendar.SECOND)</xsp:expr></sch:second>
    
    	org.apache.lenya.cms.cocoon.scheduler.SchedulerHelper helper = 
    		new org.apache.lenya.cms.cocoon.scheduler.SchedulerHelper(objectModel, parameters, getLogger());
    	java.util.Map parameters = helper.createParameters();
    	for (java.util.Iterator i = parameters.keySet().iterator(); i.hasNext(); ) {
    			String key = (String) i.next();
    			String value = (String) parameters.get(key);
    			<sch:parameter>
    				<xsp:attribute name="name"><xsp:expr>key</xsp:expr></xsp:attribute>
    				<xsp:attribute name="value"><xsp:expr>value</xsp:expr></xsp:attribute>
    			</sch:parameter>
    	}
    
    }
  </xsp:logic>
  </sch:scheduler-data>
</xsl:template>


<xsl:template match="@*|node()">
	<xsl:copy><xsl:apply-templates select="@*|node()"/></xsl:copy>
</xsl:template>

</xsl:stylesheet>
