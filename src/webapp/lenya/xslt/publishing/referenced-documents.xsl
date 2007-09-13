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

<xsl:stylesheet version="1.0"
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:i18n="http://apache.org/cocoon/i18n/2.1"      
  xmlns:page="http://apache.org/cocoon/lenya/cms-page/1.0"
  xmlns:usecase="http://apache.org/cocoon/lenya/usecase/1.0"
  xmlns:not="http://apache.org/cocoon/lenya/notification/1.0"
  xmlns:sch="http://apache.org/cocoon/lenya/scheduler/1.0"
  xmlns="http://www.w3.org/1999/xhtml"
  >
  
  <xsl:output version="1.0" indent="yes" encoding="UTF-8"/>

  <xsl:variable name="document-id"><xsl:value-of select="/usecase:publish/usecase:document-id"/></xsl:variable>
  <xsl:variable name="document-language"><xsl:value-of select="/usecase:publish/usecase:language"/></xsl:variable>
  <xsl:variable name="task-id"><xsl:value-of select="/usecase:publish/usecase:task-id"/></xsl:variable>
  <xsl:variable name="referer"><xsl:value-of select="/usecase:publish/usecase:referer"/></xsl:variable>


  <xsl:template match="/usecase:publish">

    <xsl:text>Referenced Documents for Document ID: </xsl:text><xsl:value-of select="$document-id"/>
    <xsl:text>
</xsl:text>
    <xsl:text>List of referenced documents which are not published

</xsl:text>
    <xsl:apply-templates select="referenced-documents"/>
    
  </xsl:template>

  <xsl:template match="referenced-documents">
    <xsl:for-each select="referenced-document">
      <xsl:value-of select="@id"/><xsl:value-of select="."/><xsl:text> </xsl:text><xsl:value-of select="@href"/><xsl:text>
</xsl:text>
    </xsl:for-each>
  </xsl:template>
  

</xsl:stylesheet>  
