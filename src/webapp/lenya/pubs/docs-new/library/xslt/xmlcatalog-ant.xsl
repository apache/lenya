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

<!-- $Id: xmlcatalog-ant.xsl,v 1.2 2004/03/13 12:42:08 gregor Exp $ -->
    
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

<xsl:template match="/">
  <xsl:apply-templates select="//catalog"/>
</xsl:template>
 
<xsl:template match="catalog">
  <xmlcatalog id="forrest-schema">
    <xsl:apply-templates/>
<!-- now append entries for the old document-v10 DTDs -->
<dtd publicId="-//APACHE//DTD Documentation V1.0//EN"
     location="dtd/v10/document-v10.dtd"/>
<dtd publicId="-//APACHE//DTD Changes V1.0//EN"
     location="dtd/v10/changes-v10.dtd"/>
<dtd publicId="-//APACHE//DTD FAQ V1.0//EN"
     location="dtd/v10/faq-v10.dtd"/>
<dtd publicId="-//APACHE//DTD Todo V1.0//EN"
     location="dtd/v10/todo-v10.dtd"/>
  </xmlcatalog>
</xsl:template>
 
<xsl:template match="public">
  <dtd publicId="{@publicId}" location="{@uri}"/>
</xsl:template>

</xsl:stylesheet>
