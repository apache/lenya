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

<!--
  Removes blocks and items which will not be displayed in the current view.
-->

<xsl:stylesheet version="1.0"
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:i18n="http://apache.org/cocoon/i18n/2.1"
    xmlns:uc="http://apache.org/cocoon/lenya/usecase/1.0"
    xmlns:menu="http://apache.org/cocoon/lenya/menubar/1.0"
    xmlns="http://www.w3.org/1999/xhtml">

  <xsl:param name="usecase"/>

  <xsl:variable name="currentTab">
    <xsl:choose>
      <xsl:when test="starts-with($usecase, 'admin.')">admin</xsl:when>
      <xsl:when test="starts-with($usecase, 'tab.')">site</xsl:when>
      <xsl:otherwise>authoring</xsl:otherwise>
    </xsl:choose>
  </xsl:variable>

  <xsl:template match="menu:block[(@info = 'false' and $currentTab = 'site') or (@*[local-name() = $currentTab] = 'false' and $currentTab != 'site')]"/>

  <xsl:template match="menu:item[(@info = 'false' and $currentTab = 'site') or (@*[local-name() = $currentTab] = 'false' and $currentTab != 'site')]"/>

  <xsl:template match="@*|node()">
    <xsl:copy>
      <xsl:apply-templates select="@*|node()"/>
    </xsl:copy>
  </xsl:template>

</xsl:stylesheet>
