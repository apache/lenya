<?xml version="1.0" encoding="UTF-8" ?>

<!--
    Document   : web-xml.xsl
    Created on : 2003.3.6, 11:39
    Author     : Andreas Hartmann
    Author     : Michael Wechner
    Description: Add Scheduler to web.xml
-->

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
        <param-value>/lenya/content/scheduler/scheduler.xconf</param-value>
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
    
    
<xsl:template match="@*|node()">
  <xsl:copy>
    <xsl:apply-templates select="@*|node()"/>
  </xsl:copy>
</xsl:template>


</xsl:stylesheet> 
