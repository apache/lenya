<?xml version="1.0"?>
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

<!-- $Id: parsefragment.xsl,v 1.2 2004/03/13 12:49:05 gregor Exp $ -->

<xsl:stylesheet version="1.0" 
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:tf="http://chaperon.sourceforge.net/schema/textfragment/1.0">

 <xsl:param name="parse_element">math</xsl:param>

 <xsl:template match="*[name()=$parse_element]">
  <xsl:element name="{$parse_element}">
   <tf:textfragment>
    <xsl:value-of select="."/>
   </tf:textfragment>
  </xsl:element>
 </xsl:template>

  <xsl:template match="@*|*|text()|processing-instruction()" priority="-1">
    <xsl:copy>
      <xsl:apply-templates select="@*|*|text()|processing-instruction()"/>
    </xsl:copy>
  </xsl:template>

</xsl:stylesheet>
