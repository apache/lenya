<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
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

<!-- $Id: tree2ls.xsl,v 1.2 2004/03/13 12:42:07 gregor Exp $ -->

<xsl:template match="/">
  <xsl:apply-templates/>
</xsl:template>
  
<xsl:template match="array">
 <xsl:apply-templates select="tocTab"/>
</xsl:template>
  
<xsl:template match="tocTab">
  <xsl:variable name="hrefWithRemovedContextPrefix"><xsl:value-of select="substring-after(@href,'/docs/')"/></xsl:variable>
  <xsl:choose>
    <xsl:when test="contains($hrefWithRemovedContextPrefix,'#')">
      <xsl:text> </xsl:text><xsl:value-of select="substring-before($hrefWithRemovedContextPrefix,'#')"/>
    </xsl:when>
    <xsl:otherwise>
      <xsl:text> </xsl:text><xsl:value-of select="$hrefWithRemovedContextPrefix"/>
    </xsl:otherwise>
  </xsl:choose>
</xsl:template>

</xsl:stylesheet>

