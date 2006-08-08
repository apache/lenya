<?xml version="1.0" encoding="iso-8859-1"?>
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

<!-- $Id$ -->

<xsl:stylesheet version="1.0"
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:page="http://apache.org/cocoon/lenya/cms-page/1.0"
  xmlns:i18n="http://apache.org/cocoon/i18n/2.1"    
  >
  
  <xsl:import href="../util/page-util.xsl"/>
  
  <xsl:variable name="language"><xsl:value-of select="/missing-language/current-language"/></xsl:variable>
  
  <xsl:template match="/">
    
    <page:page>
      <page:title><i18n:text>Document not available for this language</i18n:text></page:title>
      <page:body>
        <div class="lenya-box">
          <div class="lenya-box-title">      <i18n:translate>
            <i18n:text i18n:key="error-missing-language" />
            <i18n:param>'<xsl:value-of select="$language"/>'</i18n:param>
          </i18n:translate>
          </div>
          <div class="lenya-box-body">
            <p>
              <i18n:translate>
                <i18n:text i18n:key="error-missing-language" />
                <i18n:param>'<xsl:value-of select="$language"/>'</i18n:param>
              </i18n:translate>
            </p>
            <p>
              <i18n:text>The following languages are available:</i18n:text>
            </p>
            <ul>
              <xsl:apply-templates select="missing-language/available-languages/available-language"/>
            </ul>
          </div>
        </div>
      </page:body>
    </page:page>
  </xsl:template>
  
  <xsl:template match="available-languages/available-language">
    <li>
      <a><xsl:attribute name="href"><xsl:value-of select="url"/></xsl:attribute><xsl:value-of select="language"/></a>
    </li>
  </xsl:template>
  
</xsl:stylesheet>
