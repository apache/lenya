<?xml version="1.0" encoding="UTF-8" ?>

<!--
    Document   : cocoon-xconf.xsl
    Created on : 6. März 2003, 11:39
    Author     : andreas
    Description:
        Purpose of transformation follows.
-->

<xsl:stylesheet version="1.0"
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

<xsl:template match="targets">
  <xsl:copy>
    <xsl:copy-of select="@*"/>
    <xsl:apply-templates/>
    
    <cocoon id="lenya">
      <filename>${context-root}/WEB-INF/logs/lenya.log</filename>
      <format type="cocoon">
        %7.7{priority} %{time}   [%{category}] (%{uri}) %{thread}/%{class:short}: %{message}\n%{throwable}
      </format>
      <append>false</append>
    </cocoon>
    
  </xsl:copy>
</xsl:template>


<xsl:template match="categories">
  <xsl:copy>
    <xsl:copy-of select="@*"/>
    <xsl:apply-templates/>
    
    <category name="lenya" log-level="LENYA">
      <log-target id-ref="lenya"/>
      <log-target id-ref="error"/>
    </category>
    
  </xsl:copy>
</xsl:template>


<xsl:template match="category">
  <category name="@name" log-level="DEBUG">
    <xsl:apply-templates/>
  </category>
</xsl:template>


<xsl:template match="@*|node()">
  <xsl:copy>
    <xsl:apply-templates select="@*|node()"/>
  </xsl:copy>
</xsl:template>

   
</xsl:stylesheet> 
