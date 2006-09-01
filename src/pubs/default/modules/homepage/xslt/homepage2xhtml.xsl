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

<xsl:stylesheet version="1.0"
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns="http://www.w3.org/1999/xhtml"
  xmlns:xhtml="http://www.w3.org/1999/xhtml"
  xmlns:lenya="http://apache.org/cocoon/lenya/page-envelope/1.0" 
  >
  
  <xsl:import href="fallback://lenya/modules/xhtml/xslt/xhtml2xhtml.xsl"/>
  
  <xsl:param name="pubId"/>
  <xsl:param name="contextPath"/>
  
  <xsl:template match="/xhtml:html">
    <xsl:copy-of select="lenya:meta"/>
    <html>
      <head>
        <link rel="stylesheet" href="{$contextPath}/{$pubId}/modules/homepage/css/homepage.css" type="text/css"/>
      </head>
      <body>
        <xsl:if test="$rendertype = 'edit'">
          <xsl:attribute name="bxe_xpath">/xhtml:html/xhtml:body</xsl:attribute>
        </xsl:if>
        <xsl:apply-templates select="xhtml:body/node()"/>
      </body>
    </html>
  </xsl:template>
  
  
</xsl:stylesheet>
