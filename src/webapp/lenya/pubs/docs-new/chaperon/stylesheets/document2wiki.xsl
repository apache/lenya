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

<!-- $Id: document2wiki.xsl,v 1.2 2004/03/13 12:49:05 gregor Exp $ -->
    
<xsl:stylesheet version="1.0"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform" 
                xmlns:wiki="http://chaperon.sourceforge.net/grammar/wiki/1.0">

 <xsl:output indent="yes" method="xml"/>

 <xsl:template match="wiki:wiki">
    <xsl:apply-templates/>
 </xsl:template>

 <xsl:template match="//wiki">
  <div style="background: #b9d3ee; border: thin; border-color: black; border-style: solid; padding-left: 0.8em; 
               padding-right: 0.8em; padding-top: 0px; padding-bottom: 0px; margin: 0.5ex 0px; clear: both;">
   <xsl:apply-templates select="paragraphs/paragraph"/>
  </div>
 </xsl:template>

 <xsl:template match="wiki:paragraphs" >
  <xsl:apply-templates/>
 </xsl:template>
 
 <xsl:template match="wiki:paragraph" >
  <xsl:apply-templates select="wiki:bulletedlist|wiki:numberedlist1|wiki:numberedlist2|wiki:numberedlist3|wiki:headitem|wiki:footnote|wiki:textitem|wiki:LINE"/>
 </xsl:template>

 <xsl:template match="wiki:textitem" >
  <p>
   <xsl:apply-templates select="wiki:firstblock|wiki:textblock"/>
  </p>
 </xsl:template>

 <xsl:template match="wiki:textblock" >
  <xsl:apply-templates select="wiki:LINK|wiki:boldblock|wiki:italicblock|wiki:underlineblock|wiki:TEXT|wiki:note"/>
 </xsl:template>

 <xsl:template match="wiki:firstblock" >
  <xsl:apply-templates select="wiki:LINK|wiki:boldblock|wiki:italicblock|wiki:underlineblock|wiki:TEXT"/>
 </xsl:template>

 <xsl:template match="wiki:LINE" >
  <hr/>
 </xsl:template>

 <xsl:template match="wiki:bulletedlist" >
  <ul>
   <xsl:apply-templates select="wiki:bulletedlistitem"/>
  </ul>
 </xsl:template>

 <xsl:template match="wiki:bulletedlistitem" >
  <li>
   <xsl:apply-templates select="wiki:textblock"/>
  </li>
 </xsl:template>

 <xsl:template match="wiki:numberedlist1" >
  <ol>
   <xsl:apply-templates select="wiki:numberedlistitem1|wiki:numberedlist2"/>
  </ol>
 </xsl:template>

 <xsl:template match="wiki:numberedlistitem1" >
  <li>
   <xsl:apply-templates select="wiki:textblock"/>
  </li>
 </xsl:template>

 <xsl:template match="wiki:numberedlist2" >
  <ol>
   <xsl:apply-templates select="wiki:numberedlistitem2|wiki:numberedlist3"/>
  </ol>
 </xsl:template>
    
 <xsl:template match="wiki:numberedlistitem2" >
  <li>
   <xsl:apply-templates select="wiki:textblock"/>
  </li>
 </xsl:template>

 <xsl:template match="wiki:numberedlist3" >
  <ol>
   <xsl:apply-templates select="wiki:numberedlistitem3"/>
  </ol>
 </xsl:template>
    
 <xsl:template match="wiki:numberedlistitem3" >
  <li>
   <xsl:apply-templates select="wiki:textblock"/>
  </li>
 </xsl:template>

 <xsl:template match="wiki:headitem" >
  <xsl:choose>
   <xsl:when test="string-length(wiki:HEAD)=2">
    <h2>
     <xsl:apply-templates select="wiki:textblock"/>
    </h2>
   </xsl:when>
   <xsl:when test="string-length(wiki:HEAD)=3">
    <h3>
     <xsl:apply-templates select="wiki:textblock"/>
    </h3>
   </xsl:when>
   <xsl:otherwise>
    <h1>
     <xsl:apply-templates select="wiki:textblock"/>
    </h1>
   </xsl:otherwise>
  </xsl:choose>
 </xsl:template>


 <xsl:template match="wiki:footnote" >
  <a name="{normalize-space(wiki:note/wiki:TEXT|wiki:note/wiki:LINK)}">
   [<xsl:apply-templates select="wiki:note/wiki:TEXT|wiki:note/wiki:LINK"/>]
   <xsl:apply-templates select="wiki:textblock"/>
  </a>
 </xsl:template>

 <xsl:template match="wiki:LINK" >
  <a href="{normalize-space(.)}">
   <xsl:value-of select="."/>
  </a>
 </xsl:template>

 <xsl:template match="wiki:boldblock" >
  <b>
   <xsl:value-of select="wiki:TEXT"/>
  </b>
 </xsl:template>

 <xsl:template match="wiki:italicblock" >
  <i>
   <xsl:value-of select="wiki:TEXT"/>
  </i>
 </xsl:template>

 <xsl:template match="wiki:underlineblock" >
  <u>
   <xsl:value-of select="wiki:TEXT"/>
  </u><xsl:text> </xsl:text>
 </xsl:template>

 <xsl:template match="wiki:note" >
  <a href="#{normalize-space(wiki:TEXT|wiki:LINK)}">
   [<xsl:apply-templates select="wiki:TEXT|wiki:LINK"/>]
  </a>
 </xsl:template>

 <xsl:template match="wiki:TEXT" >
  <xsl:value-of select="."/>
 </xsl:template>

 <xsl:template match="@*|*|text()|processing-instruction()" priority="-1">
  <xsl:copy>
   <xsl:apply-templates select="@*|*|text()|processing-instruction()"/>
  </xsl:copy>
 </xsl:template>

</xsl:stylesheet>
