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

<!-- $Id: xopus.xsl,v 1.8 2004/03/13 12:42:14 gregor Exp $ -->

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0" xmlns:t="http://www.q42.nl/t">

<xsl:output version="1.0" indent="yes"/>

<xsl:variable name="xopus_path">/xopus2</xsl:variable>

<xsl:template name="xopus_html_attribute">
</xsl:template>


<xsl:template name="xopus_top">
</xsl:template>

<xsl:template name="xopus_head">
	<script language="javascript" src="/lenya/xopus/xopus/xopus.js">;</script>
	<script language="javascript">
		//xopus_globs.LENYA_CMS_URL="<xsl:value-of select="$context_prefix" />";
 
                // Xopus 2.0.0.1
		//xopus_consts.LENYA_CMS_URL="<xsl:value-of select="$context_prefix" />/xopus/XopusInterface";

                // Xopus 2.0.0.8
		xopus_globs.WYONA_CMS_URL="<xsl:value-of select="$context_prefix" />/xopus/XopusInterface";
		<!-- e.g. xopus_globs.WYONA_CMS_URL="/lenya/oscom/xopus/XopusInterface"; -->
		xopus_globs.WYONA_CMS_EXIT_URL="<xsl:value-of select="substring-before(/lenya/menu/request_uri,'/xopus2')" /><xsl:value-of select="substring-after(/lenya/menu/request_uri,'/xopus2')"/>";
		<!-- e.g. xopus_globs.WYONA_CMS_EXIT_URL="/lenya/oscom/authoring/matrix/cocoon.html"; -->
	</script>
</xsl:template>



<xsl:template name="xopus_body">
</xsl:template>





</xsl:stylesheet>

