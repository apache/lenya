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
<!-- CVS $Id: search2html.xsl 47285 2004-09-27 12:52:44Z cziegeler $ -->
<xsl:stylesheet 
  version="1.0"
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:search="http://apache.org/cocoon/search/1.0"
  xmlns:xhtml="http://www.w3.org/1999/xhtml"
  xmlns:cinclude="http://apache.org/cocoon/include/1.0"
  xmlns:openSearch="http://a9.com/-/spec/opensearchrss/1.0/"
  xmlns:nutch="http://www.nutch.org/opensearchrss/1.0/"
  exclude-result-prefixes="xhtml cinclude search xsl openSearch"
>

<xsl:param name="url"/>
<xsl:param name="area"/>
<xsl:param name="pub"/>
<xsl:param name="root"/>
<xsl:param name="opensearch"/>

  <xsl:template match="/rss/channel">  
    <cmsbody>
      <cinclude:include src="cocoon:/modules/sitetree/{$pub}/{$area}/breadcrumb{$url}.xml"/>
      <cinclude:include src="cocoon:/modules/sitetree/{$pub}/{$area}/menu{$url}.xml"/>
      <cinclude:include src="cocoon:/modules/sitetree/{$pub}/{$area}/tabs{$url}.xml"/>
      <cinclude:include src="cocoon:/modules/sitetree/{$pub}/{$area}/search{$url}.xml"/>      
      <xhtml:div id="body">
        <h1>
          <xsl:value-of select="openSearch:totalResults"/> hit(s) on search engine
          <a href="{link}"><xsl:value-of select="title"/></a>
          <xsl:apply-templates select="nutch:query"/>
	</h1>
	
	<p><xsl:value-of select="description"/></p>
        <ul>
          <xsl:apply-templates select="item"/>
        </ul>
        <p><a href="{$root}{$url}">Return to document</a></p>
      </xhtml:div>
    </cmsbody>
  </xsl:template>

  <xsl:template match="nutch:query">
    with query <em><xsl:value-of select="."/></em>
  </xsl:template>

  <xsl:template match="item">
    <li>
      <h2><a href="{link}"><xsl:value-of select="title"/></a></h2>
      <p><xsl:value-of select="description"/></p>
    </li>
  </xsl:template>

<xsl:template match="@*|node()" priority="-1">
<xsl:apply-templates/>
</xsl:template>

</xsl:stylesheet>

