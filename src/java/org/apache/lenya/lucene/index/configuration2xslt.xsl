<?xml version="1.0" encoding="UTF-8" ?>

<!--
/*
 * Copyright  1999-2004 The Apache Software Foundation
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 */

/* $Id: configuration2xslt.xsl,v 1.7 2004/05/16 23:20:21 michi Exp $  */
-->

<xsl:stylesheet version="1.0"
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:xsl-out="http://apache.org/cocoon/lenya/alias"
    xmlns:luc="http://apache.org/cocoon/lenya/lucene/1.0"
    >

<xsl:namespace-alias stylesheet-prefix="xsl-out" result-prefix="xsl"/>    
<xsl:preserve-space elements="*"/>

<xsl:output method="xml" indent="yes" encoding="ISO-8859-1" />
    
<xsl:template match="luc:document">
  <xsl-out:stylesheet version="1.0">
  
    <xsl-out:param name="filename"/>
    
    <xsl:apply-templates select="luc:variable"/>
  
    <xsl-out:template match="/">
      <luc:document>
        <xsl-out:attribute name="filename"><xsl-out:value-of select="$filename"/></xsl-out:attribute>
      
        <xsl:for-each select="luc:field">
          <luc:field name="{@name}" type="{@type}">
            <xsl:for-each select="namespace">
              <xsl:attribute name="{@prefix}:dummy" namespace="{.}"/>
            </xsl:for-each>
            <xsl:apply-templates select="@xpath"/>
            <xsl:apply-templates select="xpath"/>
          </luc:field>
        </xsl:for-each>
      
      </luc:document>
    </xsl-out:template>
  
  </xsl-out:stylesheet>
</xsl:template>


<xsl:template match="luc:variable">
  <xsl-out:variable name="{@name}" select="{@value}"/>
</xsl:template>


<xsl:template match="@xpath">
  <xsl-out:value-of select="{.}"/>
</xsl:template>


<xsl:template match="xpath">
  <xsl-out:value-of select="{.}"/>
</xsl:template>

</xsl:stylesheet> 
