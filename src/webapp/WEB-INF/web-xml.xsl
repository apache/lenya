<?xml version="1.0" encoding="UTF-8" ?>

<!--
    Document   : web-xml.xsl
    Created on : 6. März 2003, 11:39
    Author     : andreas
    Description:
        Purpose of transformation follows.
-->

<xsl:stylesheet version="1.0"
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform">


<xsl:template match="web-app">
  <xsl:copy>
    <xsl:copy-of select="@*"/>
    <xsl:apply-templates/>
    
    <xsl:comment>Scheduler</xsl:comment>
    
    <servlet>
      <servlet-name>QuartzSchedulerServlet</servlet-name>
      <servlet-class>org.lenya.cms.scheduler.LoadQuartzServlet</servlet-class>
      <init-param>
        <param-name>scheduler-configurations</param-name>
        <param-value>/lenya/content/scheduler/scheduler.xconf</param-value>
      </init-param>
    <load-on-startup>1</load-on-startup>
    </servlet>

    <servlet-mapping>
      <servlet-name>QuartzSchedulerServlet</servlet-name>
<!--
      <url-pattern>/load</url-pattern>
-->
      <url-pattern>/servlet/QuartzSchedulerServlet</url-pattern>
    </servlet-mapping>

    <xsl:comment>/Scheduler</xsl:comment>
    
  </xsl:copy>
</xsl:template>
    
    
<xsl:template match="@*|node()">
  <xsl:copy>
    <xsl:apply-templates select="@*|node()"/>
  </xsl:copy>
</xsl:template>
    
</xsl:stylesheet> 
