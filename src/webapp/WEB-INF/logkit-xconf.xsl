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

<!-- $Id: logkit-xconf.xsl,v 1.5 2004/03/13 14:29:20 gregor Exp $ -->

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
    
    <category name="lenya" log-level="INFO">
      <log-target id-ref="lenya"/>
      <log-target id-ref="error"/>
    </category>
    
  </xsl:copy>
</xsl:template>


<xsl:template match="@*|node()">
  <xsl:copy>
    <xsl:apply-templates select="@*|node()"/>
  </xsl:copy>
</xsl:template>

   
</xsl:stylesheet> 
