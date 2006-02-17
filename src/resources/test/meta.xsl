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

  <xsl:namespace-alias stylesheet-prefix="xso" result-prefix="xsl"/>
  
  <xsl:param name="element"/>

  <xsl:template match="/">
    <xso:stylesheet version="1.0">
      
      <xsl:apply-templates/>
      
      <xso:template match="@*|node()">
        <xso:copy>
          <xso:apply-templates select="@*|node()"/>
        </xso:copy>
      </xso:template>
      
    </xso:stylesheet>
  </xsl:template>
  
  <xsl:template match="/*">
    <xso:template match="{$element}">
      <xso:copy>
        <xso:copy-of select="*"/>
        <xsl:copy-of select="*"/>
      </xso:copy>
    </xso:template>
  </xsl:template>

  <xsl:template match="@*|node()">
    <xsl:copy>
      <xsl:apply-templates select="@*|node()"/>
    </xsl:copy>
  </xsl:template>

</xsl:stylesheet>