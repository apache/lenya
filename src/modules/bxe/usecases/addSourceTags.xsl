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

<!-- $Id: addSourceTags.xsl 42703 2004-03-13 12:57:53Z gregor $ -->

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">

<xsl:param name="source"/>

<xsl:template match="/">
  <open>
  <source:write xmlns:source="http://apache.org/cocoon/source/1.0">
    <source:source><xsl:value-of select="$source"/></source:source>
    <source:fragment>
      <xsl:copy-of select="."/>
    </source:fragment>
  </source:write>
    <content>
      <xsl:copy-of select="."/>
    </content>
  </open>
</xsl:template>

</xsl:stylesheet>
