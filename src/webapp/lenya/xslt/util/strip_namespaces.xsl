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

<!--
The XHTML namespace is declared as the default namespace
to cause the stylesheet to output XHTML using the empty string
as namespace prefix (<html> instead of <xhtml:html>).
-->

<!-- $Id$ -->
<xsl:stylesheet version="1.0"
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns="http://www.w3.org/1999/xhtml">
  
  <xsl:output method="xml" version="1.0" encoding="UTF-8" indent="yes"/>
  
  <xsl:template match="*">
    <xsl:element name="{local-name()}" namespace="{namespace-uri()}">
      <xsl:copy-of select="@*"/>
      <xsl:apply-templates/>
    </xsl:element>
  </xsl:template>

  <!-- fixme: this might be generalized to also pass on processing instructions etc...-->
  <xsl:template match="comment()">
    <xsl:copy/>
  </xsl:template>
                                          
  <!--
    Workaround to prevent the serializer from collapsing these
    elements, since browsers currently can not handle things like
      <textarea/>
    The XHTML serializer currently used by Lenya can not be
    configured to avoid this collapsing; as long as that is the case
    this workaround is needed.
    -->  
  <xsl:template match="textarea|script|style">
   <xsl:element name="{local-name()}">
      <xsl:copy-of select="@*"/>
      <xsl:apply-templates/>
	  <xsl:if test="string-length(.) = 0"><xsl:text> </xsl:text></xsl:if>
    </xsl:element>
  </xsl:template>
</xsl:stylesheet>
