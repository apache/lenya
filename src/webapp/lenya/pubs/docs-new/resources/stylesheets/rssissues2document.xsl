<?xml version='1.0'?>
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

<!-- $Id: rssissues2document.xsl,v 1.2 2004/03/13 12:42:09 gregor Exp $ -->
    
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

<xsl:template match="rss">
  <document>
    <header>
      <title>Open issues</title>
    </header>
    <body>
      <note>These are the open issues in our <link href="http://issues.cocoondev.org/jira/secure/BrowseProject.jspa?id=10000">bug tracking system</link>.
      They are regenerated on each Forrest run.</note>
      <xsl:apply-templates select="channel/item"/>
    </body>
  </document>
</xsl:template>

<xsl:template match="item">
  <section>
    <title><xsl:value-of select="title" disable-output-escaping="yes"/></title>
    <p><link href="{link}"><xsl:value-of select="link"/></link></p>
    <p><xsl:value-of select="description" disable-output-escaping="yes"/></p>
  </section>
</xsl:template>

</xsl:stylesheet>
