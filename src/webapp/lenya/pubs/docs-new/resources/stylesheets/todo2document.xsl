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

<!-- $Id: todo2document.xsl,v 1.2 2004/03/13 12:42:09 gregor Exp $ -->

<xsl:stylesheet
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    version="1.0">

 <xsl:import href="copyover.xsl"/>

 <xsl:template match="/">
  <xsl:apply-templates select="//todo"/>
 </xsl:template>

 <xsl:template match="todo">
  <document>
   <header>
    <title>Todo List</title>
   </header>
   <body>
    <xsl:apply-templates/>
   </body>
  </document>
 </xsl:template>

 <xsl:template match="actions">
  <section>
   <title><xsl:value-of select="@priority"/></title>
   <ul>
    <xsl:for-each select="action">
     <li>
      <strong><xsl:text>[</xsl:text><xsl:value-of select="@context"/><xsl:text>]</xsl:text></strong><xsl:text> </xsl:text>
      <xsl:apply-templates/>
      <xsl:text> </xsl:text>&#8594;<xsl:text> </xsl:text><xsl:value-of select="@dev"/>
     </li>
    </xsl:for-each>
   </ul>
  </section>
 </xsl:template>

</xsl:stylesheet>
