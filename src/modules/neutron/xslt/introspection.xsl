<?xml version="1.0"?>
<!--
  Copyright 1999-2006 The Apache Software Foundation

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

<!-- $Id: publication.xsl 388509 2006-03-24 13:07:10Z michi $ -->

<xsl:stylesheet
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0"
    xmlns="http://www.wyona.org/neutron/1.0"
>

<xsl:param name="context" select="'context-null'"/>
<xsl:param name="publication" select="'publication-null'"/>
<xsl:param name="area" select="'area-null'"/>
<xsl:param name="page-id" select="'page-id-null'"/>
<xsl:param name="uri" select="'/foo/bar.xml'"/>

<xsl:template match="/">
  <introspection>
    <edit mime-type="application/xml" name="Body Content">

      <open url="{$context}/{$publication}/authoring/{$page-id}.xml" method="GET"/>

      <checkout url="{$context}/{$publication}/authoring/{$page-id}.xml?lenya.module=neutron&amp;lenya.step=checkout" method="GET"/>

      <save url="{$context}/{$publication}/authoring/{$page-id}.xml?lenya.module=neutron&amp;lenya.step=checkin" method="PUT"/>



<!--
      <schemas>
        <schema href="http://foo.bar.com/lenya/modules/docbook/schemas/simple.rng" type="RelaxNG"/>
        <schema href="http://foo.bar.com/lenya/modules/docbook/schemas/default.rng" type="RelaxNG"/>
      </schemas>
      <styles>
        <style href="http://foo.bar.com/lenya/modules/xhtml/styles/default.xsl"/>
        <style href="http://foo.bar.com/lenya/modules/xhtml/styles/simple.xsl"/>
      </styles>
-->
    </edit>
  </introspection>
</xsl:template>

</xsl:stylesheet>
