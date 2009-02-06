<?xml version="1.0" encoding="iso-8859-1"?>
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

<!-- $Id$ -->

<xsl:stylesheet version="1.0"
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:i18n="http://apache.org/cocoon/i18n/2.1"
  xmlns="http://www.w3.org/1999/xhtml"
  >
  
  <xsl:param name="pub"/>
  <xsl:param name="area"/>
  <xsl:param name="path"/>
  <xsl:param name="requestUrl"/>
  <xsl:param name="contextPath"/>
  
  <xsl:variable name="language"><xsl:value-of select="/missing-language/current-language"/></xsl:variable>
  <xsl:variable name="prefix" select="concat($contextPath, '/', $pub, '/', $area)"/>
  <xsl:variable name="documentUrl" select="substring($requestUrl, string-length($prefix) + 1)"/>
  
  <xsl:template match="/">
    
    <html>
      <head>
        <title>
          <xsl:choose>
            <xsl:when test="missing-language/available-languages/available-language">
              <i18n:text>Document not available for this language</i18n:text>
            </xsl:when>
            <xsl:otherwise>
              <i18n:text>error-404</i18n:text>
            </xsl:otherwise>
          </xsl:choose>
        </title>
      </head>
      <body>
        <div id="body">
          <xsl:choose>
            <xsl:when test="missing-language/available-languages/available-language">
              <h1>
                <i18n:translate>
                  <i18n:text i18n:key="error-missing-language" />
                  <i18n:param>'<xsl:value-of select="$language"/>'</i18n:param>
                </i18n:translate>
              </h1>
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
            </xsl:when>
            <xsl:otherwise>
              <h1><i18n:text>error-document-existance-short</i18n:text></h1>
              <p>
                <i18n:translate>
                  <i18n:text i18n:key="error-document-existance" />
                  <i18n:param>'<xsl:value-of select="$documentUrl"/>'</i18n:param>
                </i18n:translate>
              </p>
              <xsl:if test="$area = 'authoring'">
                <p>
                  <a href="?lenya.usecase=sitemanagement.create&amp;path={$path}"><i18n:text>create-document</i18n:text></a>
                </p>
                <p>
                  <i18n:text>configure-content-repository</i18n:text>
                </p>
              </xsl:if>
            </xsl:otherwise>
          </xsl:choose>
        </div>
      </body>
    </html>
  </xsl:template>
  
  <xsl:template match="available-languages/available-language">
    <li>
      <a><xsl:attribute name="href"><xsl:value-of select="url"/></xsl:attribute><xsl:value-of select="language"/></a>
    </li>
  </xsl:template>
  
</xsl:stylesheet>
