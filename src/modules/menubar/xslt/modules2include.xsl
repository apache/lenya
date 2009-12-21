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

<xsl:stylesheet version="1.0"
    xmlns:lenya="http://apache.org/cocoon/lenya/publication/1.1"
    xmlns:menu="http://apache.org/cocoon/lenya/menubar/1.0"
    xmlns:i="http://apache.org/cocoon/include/1.0"
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
>
  
  <xsl:param name="pub"/>

  <xsl:template match="lenya:publication">
    <menu:menu>
      <i:include src="cocoon:/menu-xml/pub/{$pub}.xml" strip-root="true"/>
      <xsl:apply-templates select="lenya:modules/lenya:module"/>
    </menu:menu>
  </xsl:template>

  <xsl:template match="lenya:module">
    <xsl:if test="not(preceding-sibling::lenya:module[@name = current()/@name])">
      <i:include src="cocoon:/menu-xml/module/{@name}.xml" strip-root="true"/>
    </xsl:if>
  </xsl:template>

</xsl:stylesheet>
