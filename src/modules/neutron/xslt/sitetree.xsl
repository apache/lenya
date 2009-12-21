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

<!-- $Id: removeSourceTags.xsl 42703 2004-03-13 12:57:53Z gregor $ -->

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0"
  xmlns:nav="http://apache.org/cocoon/lenya/navigation/1.0"
  xmlns="DAV:">
  
  <xsl:template match="nav:site">
    <multistatus xmlns="DAV:">
      <xsl:apply-templates select="nav:node"/>
    </multistatus>
  </xsl:template>
  
  <xsl:template match="nav:node">
    <xsl:apply-templates select="nav:label"/>
    <xsl:apply-templates select="nav:node"/>
  </xsl:template>
  
  <xsl:template match="nav:label">
    <response>
      <href><xsl:value-of select="../@href"/></href>
      <propstat>
        <prop>
          <displayname><xsl:value-of select="."/></displayname>
          <resourcetype/>
          <getcontenttype>application/xhtml+xml</getcontenttype>
          <source>
            <link>
              <src><xsl:value-of select="../@href"/></src>
              <dst><xsl:value-of select="../@href"/></dst>
            </link>
          </source>
        </prop>
        <status>HTTP/1.1 200 OK</status>
      </propstat>
    </response>
  </xsl:template>
  
</xsl:stylesheet>
