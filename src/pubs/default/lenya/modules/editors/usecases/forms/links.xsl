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
  xmlns="http://apache.org/lenya/pubs/default/1.0"
  xmlns:links="http://apache.org/lenya/pubs/default/1.0"
>

<xsl:import href="../../../../../../usecases/edit/forms/form.xsl"/>

<xsl:template match="links:links">
  
<namespace prefix="" uri="http://apache.org/lenya/pubs/default/1.0"/>
  
<node name="Title" select="links/title[@tagID='{title/@tagID}']">
  <content><input type="text" name="&lt;xupdate:update select=&quot;/links/title[@tagID='{title/@tagID}']&quot;&gt;" size="40"><xsl:attribute name="value"><xsl:value-of select="title"/></xsl:attribute></input></content>
</node>

<xsl:apply-templates select="link"/>

</xsl:template>

</xsl:stylesheet>  
