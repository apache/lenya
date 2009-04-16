<?xml version="1.0" encoding="UTF-8" ?>
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

<!-- $Id: xhtml2xhtml.xsl 201776 2005-06-25 18:25:26Z gregor $ -->

<xsl:stylesheet version="1.0"
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:xhtml="http://www.w3.org/1999/xhtml"
    xmlns:lenya="http://apache.org/cocoon/lenya/page-envelope/1.0" 
    xmlns="http://www.w3.org/1999/xhtml"
    xmlns:dc="http://purl.org/dc/elements/1.1/"
    exclude-result-prefixes="xhtml lenya"
    >

  <xsl:template match="/">
    <html>
      <body>
        <h1>CForms/Ajax Example</h1>
        <p>
          Please edit with CForms editor to add more contacts. The CForms editor uses Ajax to reduce network traffic
          and to increase response times.
        </p>
        <xsl:apply-templates select="data"/>
      </body>
    </html>

 </xsl:template>

  <xsl:template match="data">
    <xsl:apply-templates/>
  </xsl:template>

  <xsl:template match="contacts">
    <h3>Contacts:</h3>
    <p>
      <xsl:choose>
        <xsl:when test="contact">
          <ul>
            <xsl:apply-templates/>
          </ul>
         </xsl:when>
        <xsl:otherwise>
          <i>No contacts to display.</i>
        </xsl:otherwise>
      </xsl:choose>
    </p>
  </xsl:template>

  <xsl:template match="contact">
    <li>
      <xsl:value-of select="firstname"/><xsl:text> </xsl:text><xsl:value-of select="lastname"/>
    </li>
  </xsl:template>
</xsl:stylesheet> 
