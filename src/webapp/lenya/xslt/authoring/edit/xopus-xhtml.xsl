<?xml version="1.0"?>
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

<!-- $Id: xopus-xhtml.xsl,v 1.6 2004/03/13 12:42:05 gregor Exp $ -->

<xsl:stylesheet version="1.0"
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
>

<xsl:param name="documentType"/>
<xsl:param name="documentUrl"/>
<xsl:param name="publicationId"/>
<xsl:param name="completeArea"/>
<xsl:param name="contextPrefix"/>
<xsl:param name="xopusContext"/>


<xsl:variable name="lenyaContext" select="concat($contextPrefix, '/', $publicationId, '/', $completeArea)"/>
<xsl:variable name="lenyaDocumentUrl" select="concat($lenyaContext, $documentUrl)"/>

<xsl:template match="/*">
<html>
  <head>
    <title>Load Xopus ...</title>
    <script language="JavaScript" src="/{$xopusContext}/xopus/xopus.js"/>
  </head>
  <body bgcolor="#FFFFFF">
    <div xopus="true" autostart="true">
      ...Xopus hasn't started yet...
      <xml>
        <pipeline
            xml="{$lenyaDocumentUrl}?lenya.usecase=xopus&amp;lenya.step=xml"
            xsd="{$lenyaDocumentUrl}?lenya.usecase=xopus&amp;lenya.step=xsd&amp;doctype={$documentType}">
            
          <view id="defaultView" default="true">
            <transform xsl="/{$xopusContext}/xopusPlugins/preparexinclude.xsl"/>
            <resolveXIncludes/>
            <transform xsl="{$lenyaDocumentUrl}?lenya.usecase=xopus&amp;lenya.step=xslt&amp;doctype={$documentType}">
              <param name="contextprefix"><xsl:value-of select="$contextPrefix"/></param>
              <param name="publicationid"><xsl:value-of select="$publicationId"/></param>
              <param name="completearea"><xsl:value-of select="$completeArea"/></param>
            </transform>
          </view>
          
          <!--
          <view id="step1">
            <transform xsl="/{$xopusContext}/xopusPlugins/preparexinclude.xsl"/>
            <transform xsl="/{$xopusContext}/xopus/media/treeview/tree.xsl"></transform>
          </view>
          <view id="step2">
            <transform xsl="/{$xopusContext}/xopusPlugins/preparexinclude.xsl"/>
            <resolveXIncludes/>
            <transform xsl="/{$xopusContext}/xopus/media/treeview/tree.xsl"></transform>
          </view>
          -->
          
          <view id="treeView">
            <transform xsl="/{$xopusContext}/xopus/media/treeview/tree.xsl"></transform>
          </view>
        </pipeline>
      </xml>
    </div>
  </body>
</html>
</xsl:template>

</xsl:stylesheet>
