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

<!-- $Id: pdfoutline.xsl,v 1.2 2004/03/13 12:42:13 gregor Exp $ -->

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:fo="http://www.w3.org/1999/XSL/Format"
                xmlns:fox="http://xml.apache.org/fop/extensions"
                version="1.0">

<xsl:template match="document" mode="outline">
  <fox:bookmarks>
    <fox:outline internal-destination="{generate-id()}">
      <fox:label>
        <xsl:value-of select="header/title"/>
      </fox:label>
      <xsl:apply-templates select="body/section" mode="outline"/>
    </fox:outline>
  </fox:bookmarks>
</xsl:template>

<xsl:template match="section" mode="outline">
  <fox:outline internal-destination="{generate-id()}">
    <fox:label>
      <xsl:number format="1.1.1.1.1.1.1" count="section" level="multiple"/>
      <xsl:text> </xsl:text>
      <xsl:value-of select="title"/>
    </fox:label>
    <xsl:apply-templates select="section" mode="outline"/>
  </fox:outline>
</xsl:template>

</xsl:stylesheet>
