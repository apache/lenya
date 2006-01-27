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
<xsl:stylesheet  xmlns:blog="http://apache.org/cocoon/blog/1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:xi="http://www.w3.org/2001/XInclude" version="1.0">
  <xsl:param name="feedid"/>
  <xsl:param name="area"/>
  <xsl:template match="/">
    <xsl:apply-templates select="blog:blog"/>
  </xsl:template>
  <xsl:template match="blog:blog">
    <echo:feed xmlns:echo="http://purl.org/atom/ns#" xmlns="http://www.w3.org/1999/xhtml" version="0.3" xml:lang="en">
      <xi:include href="lenya://lenya/pubs/blog/content/{$area}/feeds/{$feedid}/index.xml#xmlns(atom=http://purl.org/atom/ns#)xpointer(/atom:feed/atom:title)"/>
      <xi:include href="lenya://lenya/pubs/blog/content/{$area}/feeds/{$feedid}/index.xml#xmlns(atom=http://purl.org/atom/ns#)xpointer(/atom:feed/atom:link)"/>
      <xi:include href="lenya://lenya/pubs/blog/content/{$area}/feeds/{$feedid}/index.xml#xmlns(atom=http://purl.org/atom/ns#)xpointer(/atom:feed/atom:modified)"/>
      <xsl:for-each select="blog:entry">
        <xi:include href="lenya://lenya/pubs/blog/content/{$area}{@docid}.xml"/>
      </xsl:for-each>
    </echo:feed>
  </xsl:template>
</xsl:stylesheet>
