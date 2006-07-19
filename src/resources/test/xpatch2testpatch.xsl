<?xml version="1.0" encoding="UTF-8"?>
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
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0"
  xmlns:xso="http://apache.org/cocoon/lenya/xslt/1.0"
  >

  <xsl:template match="/xconf">
    <xtest>
      <xsl:apply-templates select="@*|node()"/>
    </xtest>
  </xsl:template>
  
  
  <xsl:template match="@* [local-name() = 'xpath' or local-name() = 'remove' or local-name() = 'unless']">
    <xsl:choose>
      <xsl:when test="starts-with(., '/cocoon')">
        <xsl:attribute name="{local-name()}">/testcase/components<xsl:value-of select="substring-after(., '/cocoon')"/></xsl:attribute>
      </xsl:when>
      <xsl:otherwise>
        <xsl:copy-of select="."/>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>


  <xsl:template match="@*|node()">
    <xsl:copy>
      <xsl:apply-templates select="@*|node()"/>
    </xsl:copy>
  </xsl:template>

</xsl:stylesheet>