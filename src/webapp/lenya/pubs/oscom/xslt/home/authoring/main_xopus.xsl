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

<!-- $Id: main_xopus.xsl,v 1.7 2004/03/13 12:42:08 gregor Exp $ -->

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
 
<xsl:output version="1.0" indent="yes" encoding="ISO-8859-1"/>

<xsl:include href="../../../../../xslt/xopus/root.xsl"/>

<xsl:template match="cmsbody">
  <xsl:apply-templates select="oscom"/>
</xsl:template>

<xsl:include href="../../html_authoring.xsl"/>

<xsl:template name="body">
 <div id="article_xopus" xopus="true" autostart="true">
<b>............ LOADING ............</b>
 	<xml>
		<pipeline xml="home.xml" xsd="home.xsd">
			<view id="defaultView" default="true">
				<transform xsl="Home/authoring/xopus.xsl"></transform>
			</view>
			<view id="treeView">
				<transform xsl="Home/authoring/tree.xsl"></transform>
			</view>
		</pipeline>
    </xml>
 </div>
</xsl:template>
 
</xsl:stylesheet>  
