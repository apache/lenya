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

<!-- $Id: sitetree2areatree.xsl,v 1.2 2004/03/13 12:42:06 gregor Exp $ -->

<!--
        Adds an area attribute to a sitetree root element.
-->

<xsl:stylesheet version="1.0"
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:s="http://apache.org/cocoon/lenya/sitetree/1.0"
    >

<xsl:param name="area"/>
   
<xsl:template match="s:site">
	<s:site area="{$area}">
		<xsl:copy-of select="@*"/>
		<xsl:apply-templates/>
	</s:site>
</xsl:template>
	

<xsl:template match="@*|node()">
	<xsl:copy><xsl:apply-templates select="@*|node()"/></xsl:copy>
</xsl:template>

</xsl:stylesheet> 
