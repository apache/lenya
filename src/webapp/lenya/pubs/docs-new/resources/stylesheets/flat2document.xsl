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

<!-- $Id: flat2document.xsl,v 1.2 2004/03/13 12:42:09 gregor Exp $ -->
    
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

<xsl:key name="attlistbyname" match="attlist" use="@ename"/>
<xsl:key name="contentmodelbychildren" match="contentModel" use="descendant::element/@name"/>

<xsl:template match="dtd">
  <document>
    <header>
      <title>DTD documentation</title>
      <subtitle>
        <xsl:call-template name="trailingfilename">
          <xsl:with-param name="string" select="@sysid"/>
        </xsl:call-template>
      </subtitle>
    </header>
    <body>
      <section><title>Top-level element(s)</title>
        <ul>
          <xsl:for-each select="/dtd/contentModel[not(key('contentmodelbychildren', @ename))]">
            <li>
              <link href="#{@ename}"><xsl:value-of select="@ename"/></link>
            </li>
          </xsl:for-each>
        </ul>
      </section>
      <section><title>List of elements</title>
        <ul>
          <xsl:for-each select="contentModel">
            <xsl:sort select="@ename"/>
            <li>
              <link href="#{@ename}"><xsl:value-of select="@ename"/></link>
            </li>
          </xsl:for-each>
        </ul>
      </section>
      <section>
        <title>Element declarations</title>
        <xsl:apply-templates select="contentModel"/>
      </section>
    </body>
  </document>
</xsl:template>

<xsl:template match="contentModel">
  <section id="{@ename}">
      <table class="dtdElement" cellspacing="0" cellpadding="0" border="0">
        <tr>
          <td colspan="3"><span class="dtdTag"><xsl:value-of select="@ename"/></span></td>
        </tr>
        <tr>
         <td valign="top" nowrap="nowrap"><b>Content model&#160;</b></td>
         <td colspan="2" width="100%"><xsl:apply-templates/></td>
        </tr>
        <xsl:if test="key('attlistbyname',@ename)">
          <tr>
            <td colspan="3"><hr noshade="noshade" width="100%"/></td>
          </tr>
          <tr>
            <td valign="top"><b>Attributes</b></td>
            <td colspan="2" width="100%"><xsl:apply-templates select="key('attlistbyname',@ename)"/></td>
          </tr>
        </xsl:if>
      <xsl:if test="key('contentmodelbychildren',@ename)">
        <tr>
          <td colspan="3"><hr noshade="noshade" width="100%"/></td>
        </tr>
        <tr>
          <td valign="top" nowrap="nowrap"><b>Used inside</b></td>
          <td width="100%">
            <xsl:for-each select="key('contentmodelbychildren',@ename)">
              <link href="#{@ename}"><xsl:value-of select="@ename"/></link>
              <xsl:if test="not(position() = last())"> | </xsl:if>
            </xsl:for-each>
          </td>
        </tr>
        <tr><td colspan="3">&#160;</td></tr>
      </xsl:if>
    </table>
  </section>
</xsl:template>

<xsl:template match="empty">
  EMPTY
</xsl:template>

<xsl:template match="pcdata">
  #PCDATA
</xsl:template>

<xsl:template match="element">
  <link href="#{@name}"><xsl:value-of select="@name"/></link>
</xsl:template>

<xsl:template match="group">
  <xsl:text>( </xsl:text><xsl:apply-templates/><xsl:text> )</xsl:text>
</xsl:template>

<xsl:template match="separator">
  <xsl:text> </xsl:text><xsl:value-of select="@type"/><xsl:text> </xsl:text>
</xsl:template>

<xsl:template match="occurrence">
  <xsl:text> </xsl:text><xsl:value-of select="@type"/>
</xsl:template>

<xsl:template match="attlist">
  <table cellpadding="0" cellspacing="0" border="0" class="dtdElement">
    <xsl:apply-templates/>
  </table>
</xsl:template>

<xsl:template match="attributeDecl">
  <tr>
    <td valign="top"><xsl:if test="position() mod 2 != 0"><xsl:attribute name="bgcolor">#F1F7FF</xsl:attribute></xsl:if><xsl:value-of select="@aname"/></td>
    <td><xsl:if test="position() mod 2 != 0"><xsl:attribute name="bgcolor">#F1F7FF</xsl:attribute></xsl:if>&#160;&#160;&#160;&#160;</td>
    <td width="100%"><xsl:if test="position() mod 2 != 0"><xsl:attribute name="bgcolor">#F1F7FF</xsl:attribute></xsl:if>
      <xsl:if test="not(enumeration)">
        type: <xsl:value-of select="@atype"/><br/>
      </xsl:if>
      <xsl:if test="@required">
        required attribute<br/>
      </xsl:if>
      <xsl:if test="@default">
        default value: <xsl:value-of select="@default"/><br/>
      </xsl:if>
      <xsl:if test="@fixed">
        fixed value: <xsl:value-of select="@default"/><br/>
      </xsl:if>
      <xsl:if test="enumeration">
        possible values: <xsl:for-each select="enumeration">
          <xsl:value-of select="@value"/><xsl:text> </xsl:text>
        </xsl:for-each>
        <br/>
      </xsl:if>
    </td>
  </tr>
</xsl:template>

<xsl:template name="trailingfilename">
  <xsl:param name="string"/>
  <xsl:choose>
    <xsl:when test="contains($string,'/')">
      <xsl:call-template name="trailingfilename">
        <xsl:with-param name="string" select="substring-after($string,'/')"/>
      </xsl:call-template>
    </xsl:when>
    <xsl:otherwise>
      <xsl:value-of select="$string"/>
    </xsl:otherwise>
  </xsl:choose>
</xsl:template>

</xsl:stylesheet>
