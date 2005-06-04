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

<!-- $Id$ -->

<xsl:stylesheet version="1.0"
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:page="http://apache.org/cocoon/lenya/cms-page/1.0"
  xmlns:error="http://apache.org/cocoon/error/2.1"
  xmlns:i18n="http://apache.org/cocoon/i18n/2.1">
  
  <xsl:import href="../util/page-util.xsl"/>
  
  <xsl:param name="documentid"/>
  <xsl:param name="documenturl"/>
  <xsl:param name="contextPath"/>

  <xsl:template match="error:notify">
    
    <page:page>
      <page:title>Apache Lenya: <i18n:text i18n:key="error-generic" /></page:title>
        <style>
          h1 { color: #336699; text-align: left; margin: 0px 0px 30px 0px; padding: 0px; border-width: 0px 0px 1px 0px; border-style: solid; border-color: #336699;}
          p.message { padding: 10px 30px 10px 30px; font-weight: bold; font-size: 130%; border-width: 1px; border-style: dashed; border-color: #336699; }
          p.description { padding: 10px 30px 20px 30px; border-width: 0px 0px 1px 0px; border-style: solid; border-color: #336699;}
          p.topped { padding-top: 10px; border-width: 1px 0px 0px 0px; border-style: solid; border-color: #336699; }
          pre { font-size: 120%; }
        </style>
      <page:body>
        <xsl:apply-templates select="." mode="onload"/>
        <script>
function toggle(id) {
    var element = document.getElementById(id);
    with (element.style) {
        if ( display == "none" ){
            display = ""
        } else{
            display = "none"
        }
    }
    var text = document.getElementById(id + "-switch").firstChild;
    if (text.nodeValue == "[<i18n:text i18n:key="show" />]") {
        text.nodeValue = "[<i18n:text i18n:key="hide" />]";
    } else {
        text.nodeValue = "[<i18n:text i18n:key="show" />]";
    }
}
</script>
	<div class="lenya-box">
	  <div class="lenya-box-title"><i18n:text i18n:key="error-generic" /></div>
	  <div class="lenya-box-body">
	    <p>
          <xsl:value-of select="error:message"/>
	    </p>
	    <p>
          <xsl:value-of select="error:description"/>
	    </p>
        <xsl:apply-templates select="error:extra"/>
	  </div>
	</div>
      </page:body>
    </page:page>
  </xsl:template>

  <xsl:template match="error:extra">
    <xsl:choose>
     <xsl:when test="contains(@error:description,'stacktrace')">
      <p class="stacktrace">
       <span class="description"><xsl:value-of select="@error:description"/></span>
       <span class="switch" id="{@error:description}-switch" onclick="toggle('{@error:description}')">[<i18n:text i18n:key="hide" />]</span>
       <pre id="{@error:description}">
         <xsl:value-of select="translate(.,'&#13;','')"/>
       </pre>
      </p>
     </xsl:when>
     <xsl:otherwise>
      <p class="extra">
       <span class="description"><xsl:value-of select="@error:description"/>:&#160;</span>
       <xsl:value-of select="."/>
      </p>
     </xsl:otherwise>
    </xsl:choose>
  </xsl:template>

  <xsl:template match="error:notify" mode="onload">
    <xsl:attribute name="onload">
      <xsl:for-each select="error:extra[contains(@error:description,'stacktrace')]">
        <xsl:text>toggle('</xsl:text>
        <xsl:value-of select="@error:description"/>
        <xsl:text>');</xsl:text>
      </xsl:for-each>
    </xsl:attribute>
  </xsl:template>
  
</xsl:stylesheet>
