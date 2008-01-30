<?xml version="1.0"?>
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

<!-- $Id: content.xsl 473841 2006-11-12 00:46:38Z gregor $ -->

 <xsl:stylesheet version="1.0"
   xmlns="http://www.w3.org/1999/xhtml"
   xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
   xmlns:session="http://www.apache.org/xsp/session/2.0"
   xmlns:i18n="http://apache.org/cocoon/i18n/2.1"
   xmlns:page="http://apache.org/cocoon/lenya/cms-page/1.0"
   >
  
  <xsl:output version="1.0" indent="yes" encoding="UTF-8"/>
  
  <xsl:template match="/">
    <page:page>
      <page:title><i18n:text>clear-uriparameterizer-cache</i18n:text></page:title>
      <page:body>
        
        <xsl:choose>
          <xsl:when test="normalize-space(usecase) != ''">
            <p>
              <i18n:text>uriparameterizer-cache-cleared</i18n:text>
            </p>
          </xsl:when>
          <xsl:otherwise>
            <p>
              <i18n:text>clear-uriparameterizer-cache?</i18n:text>
            </p>
            <form method="get" action="index.html" name="clear-cache-form">
              <input type="hidden" name="lenya.usecase" value="clearUriParameterizerCache"/>
              <input i18n:attr="value" type="submit" value="Submit" name="submit"/>
              &#160;
              <input i18n:attr="value" type="submit" value="Cancel" name="cancel"/>
            </form>
          </xsl:otherwise>
        </xsl:choose>
        
      </page:body>
    </page:page>
  </xsl:template>

</xsl:stylesheet>
  
