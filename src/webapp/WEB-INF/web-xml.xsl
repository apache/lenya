<?xml version="1.0" encoding="UTF-8" ?>
<!--
  Copyright 1999-2004 The Apache Software Foundation

  Licensed under the Apache License, Version 2.0 (the "License");
  you may not use this file except in compliance with the License.
  You may obtain a copy of the License at

      http://www.apache.org/licenses/LICENSE-2.0

  Unless required by applicable law or agreed to in writing, software
  distributed under the License is distributed on an "AS IS" BASIS,
  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  See the License for the specific language governing permissions and
  limitations under the License.
-->

<!-- $Id: web-xml.xsl,v 1.7 2004/03/13 14:29:20 gregor Exp $ -->

<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

  <xsl:param name="enableUploads"/>

  <xsl:template match="/">
    <xsl:text disable-output-escaping="yes">
      <![CDATA[
      <!DOCTYPE web-app
      PUBLIC "-//Sun Microsystems, Inc.//DTD Web Application 2.2//EN"
      "http://java.sun.com/j2ee/dtds/web-app_2_2.dtd">
      ]]>
    </xsl:text>

    <xsl:apply-templates select="@*|node()"/>
  </xsl:template>    

  <xsl:template match="/web-app/servlet[position() = count(/web-app/servlet)]">
    <!--
    <xsl:copy-of select="."/>
    -->
    <xsl:copy>
      <xsl:apply-templates select="@*|node()"/>
    </xsl:copy>
    
    <xsl:comment>Scheduler</xsl:comment>
    <servlet>
      <servlet-name>QuartzSchedulerServlet</servlet-name>
      <servlet-class>org.apache.lenya.cms.scheduler.LoadQuartzServlet</servlet-class>
      <init-param>
        <param-name>scheduler-configurations</param-name>
        <param-value>/lenya/config/scheduler/scheduler.xconf</param-value>
      </init-param>
      <load-on-startup>1</load-on-startup>
    </servlet>
    <xsl:comment>/Scheduler</xsl:comment>
  </xsl:template>

  <xsl:template match="/web-app/servlet-mapping[position() = count(/web-app/servlet-mapping)]">
    <xsl:copy-of select="."/>
    
    <xsl:comment>Scheduler</xsl:comment>
    <servlet-mapping>
      <servlet-name>QuartzSchedulerServlet</servlet-name>
      <url-pattern>/servlet/QuartzSchedulerServlet</url-pattern>
    </servlet-mapping>
    <xsl:comment>/Scheduler</xsl:comment>
  </xsl:template>

  <xsl:template match="/web-app/servlet[position() = 1]/init-param[normalize-space(param-name) = 'enable-uploads']">
    <init-param>
      <param-name>enable-uploads</param-name>
      <param-value><xsl:value-of select="$enableUploads"/></param-value>
    </init-param>
  </xsl:template>
  
  <xsl:template match="/web-app/servlet[position() = 1]/init-param[normalize-space(param-name) = 'overwrite-uploads']">
    <init-param>
      <param-name>overwrite-uploads</param-name>
      <param-value>allow</param-value>
    </init-param>
  </xsl:template>
  
  <xsl:template match="@*|node()">
    <xsl:copy>
      <xsl:apply-templates select="@*|node()"/>
    </xsl:copy>
  </xsl:template>


</xsl:stylesheet> 
