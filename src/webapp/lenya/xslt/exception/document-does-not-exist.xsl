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
  xmlns:page="http://apache.org/cocoon/lenya/cms-page/1.0"
  xmlns:i18n="http://apache.org/cocoon/i18n/2.1"
  xmlns="http://www.w3.org/1999/xhtml"
  >
  
  <xsl:import href="../util/page-util.xsl"/>
  
  <xsl:param name="area"/>
  <xsl:param name="path"/>
  <xsl:param name="documenturl"/>
  <xsl:param name="baseUrl"/>
  
  <xsl:variable name="language"><xsl:value-of select="/missing-language/current-language"/></xsl:variable>
  
  <xsl:template match="/">
    
    <page:page>
      <page:title>
        <xsl:choose>
          <xsl:when test="missing-language/available-languages/available-language">
            <i18n:text>Document not available for this language</i18n:text>
          </xsl:when>
          <xsl:otherwise>
            <i18n:text>error-404</i18n:text>
          </xsl:otherwise>
        </xsl:choose>
      </page:title>
      <page:body>
        <div class="lenya-box">
          <xsl:choose>
            <xsl:when test="missing-language/available-languages/available-language">
              <div class="lenya-box-title">
                <i18n:translate>
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
            </xsl:when>
            <xsl:otherwise>
              <div class="lenya-box-title"><i18n:text>error-document-existance-short</i18n:text></div>
              <div class="lenya-box-body">
                <p>
                  <i18n:translate>
                    <i18n:text i18n:key="error-document-existance" />
                    <i18n:param>'<xsl:value-of select="$documenturl"/>'</i18n:param>
                  </i18n:translate>
                </p>
                <xsl:if test="$area = 'authoring'">
                  <p>
                    <a href="?lenya.usecase=sitemanagement.create&amp;path={$path}"><i18n:text>create-document</i18n:text></a>
                  </p>
                  <p>
                    <strong>NOTE:</strong> Please make sure that your content repository
                    is configured correctly within (local.)publication.xconf.
                  </p>
                </xsl:if>
              </div>
            </xsl:otherwise>
          </xsl:choose>
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
