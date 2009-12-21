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

<!-- $Id: generic.xsl 589872 2007-10-29 21:47:39Z andreas $ -->

<xsl:stylesheet version="1.0"
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:error="http://apache.org/cocoon/error/2.1"
  xmlns="http://www.w3.org/1999/xhtml"
  xmlns:i18n="http://apache.org/cocoon/i18n/2.1">
  
  <xsl:param name="root"/>
  
  <xsl:template match="error:notify">
    <html>
      <head>
        <title><i18n:text i18n:key="error-generic" /></title>
        <link rel="stylesheet" href="{$root}/css/page.css" type="text/css"/>
      </head>
      <body>
        
        <div id="page">
          <table width="100%" cellpadding="0" cellspacing="0" border="0">
            <tr>
              <td/>
              <td id="project-logo"><img src="{$root}/images/project-logo.png" alt="project logo"/></td>
            </tr>
          </table>
          <table width="100%" border="0" cellpadding="0" cellspacing="0">
            <tr>
              <td valign="top" style="width: 230px"/>
              <td valign="top">
                <div id="main">
                  <div id="body">
                    <h1>
                      <i18n:text i18n:key="error-generic" />
                    </h1>
                    <p>
                      <i18n:text i18n:key="error-generic-comment"/>
                    </p>
                    <xsl:comment>
                      <xsl:value-of select="error:message"/>
                      <xsl:value-of select="error:description"/>
                      <xsl:apply-templates select="error:extra"/>
                    </xsl:comment>
                  </div>
                </div>
              </td>
            </tr>
          </table>
        </div>
      </body>
    </html>
  </xsl:template>
  
  
  <xsl:template match="error:extra">
    <xsl:choose>
      <xsl:when test="contains(@error:description,'stacktrace')">
        <xsl:value-of select="@error:description"/>
        <xsl:value-of select="translate(.,'&#13;','')"/>
      </xsl:when>
      <xsl:otherwise>
        <xsl:value-of select="."/>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>
  
</xsl:stylesheet>
