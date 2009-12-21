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

<!-- $Id: oneform.xsl 42908 2004-04-26 14:57:25Z michi $ -->

<xsl:stylesheet version="1.0"
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:page="http://apache.org/cocoon/lenya/cms-page/1.0"
  xmlns:i18n="http://apache.org/cocoon/i18n/2.1"
  >

  <xsl:output indent="no" />
  <xsl:param name="docid" />
  <xsl:param name="language" />

  <xsl:include href="copy-mixed-content.xsl" />
  
  <xsl:template match="/">
    <input type="hidden" name="namespaces"><xsl:attribute name="value"><xsl:apply-templates mode="namespaces" /></xsl:attribute></input>
    <textarea name="content" style="display: none"/>
    <textarea id="editorContent" cols="120" rows="80">
      <xsl:apply-templates mode="mixedcontent" />
    </textarea>
  </xsl:template>
</xsl:stylesheet>
