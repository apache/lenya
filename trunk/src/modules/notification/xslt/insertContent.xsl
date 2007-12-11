<?xml version="1.0" encoding="UTF-8"?>
<!--
  Licensed to the Apache Software Foundation (ASF) under one or more
  contributor license agreements.  See the NOTICE file distributed with
  this work for additional information regarding copyright ownership.
  The ASF licenses this file to You under the Apache License, Version 2.0
  (the "License"); you may not use this file except in compliance with
  the License.  You may obtain a copy of the License at

      http://www.apache.org/licenses/LICENSE-2.0

  Unless required by applicable law or agreed to in writing, software
  distributed under the License is distributed on an "AS IS" BASIS,
  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  See the License for the specific language governing permissions and
  limitations under the License.
-->

<!-- $Id: doctypes.xmap 179488 2005-06-02 02:29:39Z gregor $ -->
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0"
  xmlns:not="http://apache.org/lenya/notification/2.0"
  xmlns:i18n="http://apache.org/cocoon/i18n/2.1"
  >

  <xsl:param name="subject"/>
  <xsl:param name="body"/>

  <xsl:template match="/not:message">
    <xsl:apply-templates/>
  </xsl:template>
  
  
  <xsl:template match="not:subject">
    <xsl:copy>
      <i18n:text><xsl:value-of select="$subject"/></i18n:text>
    </xsl:copy>
  </xsl:template>
  

  <xsl:template match="not:body">
    <xsl:copy>
      <i18n:text><xsl:value-of select="$body"/></i18n:text>
    </xsl:copy>
  </xsl:template>

  
</xsl:stylesheet>