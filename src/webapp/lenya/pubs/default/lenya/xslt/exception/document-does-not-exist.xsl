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

<!-- $Id: document-does-not-exist.xsl 180003 2005-06-04 16:40:42Z gregor $ -->

<xsl:stylesheet version="1.0"
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:page="http://apache.org/cocoon/lenya/cms-page/1.0"
  xmlns:i18n="http://apache.org/cocoon/i18n/2.1"    
  >
  
  <xsl:param name="documentid"/>
  <xsl:param name="documenturl"/>
  <xsl:param name="area"/>
  
  <xsl:template match="/">
    
    <page:page>
      <page:title><i18n:text>error-404</i18n:text></page:title>
      <page:body>
        <div class="lenya-box">
          <div class="lenya-box-title"><i18n:text>error-document-existance-short</i18n:text></div>
          <div class="lenya-box-body">
            <p>
              <i18n:translate>
                <i18n:text i18n:key="error-document-existance" />
                <i18n:param>'<xsl:value-of select="$documenturl"/>'</i18n:param>
                <i18n:param>'<xsl:value-of select="$documentid"/>'</i18n:param>
              </i18n:translate>
            </p>
            <xsl:if test="$area = 'authoring'">
              <p>
                <a href="?lenya.usecase=site.create&amp;documentId={$documentid}"><i18n:text>create-document</i18n:text></a>
              </p>
            </xsl:if>
          </div>
        </div>
      </page:body>
    </page:page>
  </xsl:template>
  
</xsl:stylesheet>
