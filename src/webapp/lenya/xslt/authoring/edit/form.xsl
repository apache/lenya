<?xml version="1.0" encoding="iso-8859-1"?>

<xsl:stylesheet version="1.0"
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
>

<xsl:param name="docid"/>
<xsl:param name="form"/>
<xsl:param name="message"/>


<xsl:template match="/">
<form>
<docid><xsl:value-of select="$docid"/></docid>
<ftype><xsl:value-of select="$form"/></ftype>
<xsl:if test="$message">
<message><xsl:value-of select="$message"/></message>
</xsl:if>
<xsl:apply-templates/>
</form>
</xsl:template>




<!-- Copy mixed content -->
<!-- also see oneform.xsl -->

<!-- FIXME: There seems to be a problem with xalan-2.5.1 (endorsed) re including XSLT files -->
<!--
<xsl:include href="copy-mixed-content.xsl"/>
-->
<xsl:template match="//*" mode="mixedcontent">
<xsl:variable name="prefix"><xsl:if test="contains(name(),':')">:<xsl:value-of select="substring-before(name(),':')"/></xsl:if></xsl:variable>

<xsl:choose>
<xsl:when test="node()">
<xsl:text>&lt;</xsl:text><xsl:value-of select="name()"/>

<xsl:apply-templates select="@*[local-name()!='tagID']" mode="mixedcontent"/>

<xsl:for-each select="namespace::*">
<xsl:variable name="prefix"><xsl:if test="local-name() != ''">:<xsl:value-of select="local-name()"/></xsl:if></xsl:variable>
<xsl:if test=". != 'http://www.w3.org/XML/1998/namespace'">
<xsl:text> </xsl:text>xmlns<xsl:value-of select="$prefix"/>="<xsl:value-of select="."/><xsl:text>"</xsl:text>
</xsl:if>
</xsl:for-each>

<xsl:text>&gt;</xsl:text>

<xsl:apply-templates select="node()" mode="mixedcontent"/>

<xsl:text>&lt;/</xsl:text><xsl:value-of select="name()"/><xsl:text>&gt;</xsl:text>

</xsl:when>

<xsl:otherwise>

<xsl:text>&lt;</xsl:text><xsl:value-of select="name()"/>

<xsl:apply-templates select="@*[local-name()!='tagID']" mode="mixedcontent"/>

<xsl:for-each select="namespace::*">
<xsl:variable name="prefix"><xsl:if test="local-name() != ''">:<xsl:value-of select="local-name()"/></xsl:if></xsl:variable>
<xsl:if test=". != 'http://www.w3.org/XML/1998/namespace'">
<xsl:text> </xsl:text>xmlns<xsl:value-of select="$prefix"/>="<xsl:value-of select="."/><xsl:text>"</xsl:text>
</xsl:if>
</xsl:for-each>

<xsl:text>/&gt;</xsl:text></xsl:otherwise>
</xsl:choose>
</xsl:template>

<xsl:template match="@*" mode="mixedcontent"><xsl:text> </xsl:text><xsl:value-of select="name()"/>="<xsl:value-of select="."/>"</xsl:template>



<!-- FIXME: namespaces occur multiple times, e.g. xlink:show="" xlink:href="" xmlns:xlink="" xmlns:xlink="" -->
<!--
<xsl:template match="//*" mode="mixedcontent">
<xsl:variable name="prefix"><xsl:if test="contains(name(),':')">:<xsl:value-of select="substring-before(name(),':')"/></xsl:if></xsl:variable>

<xsl:choose>
<xsl:when test="node()">
&lt;<xsl:value-of select="name()"/><xsl:if test="namespace-uri()"><xsl:text> </xsl:text>xmlns<xsl:value-of select="$prefix"/>="<xsl:value-of select="namespace-uri()"/>"</xsl:if><xsl:apply-templates select="@*[local-name()!='tagID']" mode="mixedcontent"/>&gt;
<xsl:apply-templates select="node()" mode="mixedcontent"/>
&lt;/<xsl:value-of select="name()"/>&gt;
</xsl:when>
<xsl:otherwise>
&lt;<xsl:value-of select="name()"/><xsl:if test="namespace-uri()"><xsl:text> </xsl:text>xmlns<xsl:value-of select="$prefix"/>="<xsl:value-of select="namespace-uri()"/>"</xsl:if><xsl:apply-templates select="@*[local-name()!='tagID']" mode="mixedcontent"/> /&gt;
</xsl:otherwise>
</xsl:choose>
</xsl:template>

<xsl:template match="@*" mode="mixedcontent">
<xsl:variable name="prefix"><xsl:if test="contains(name(),':')">:<xsl:value-of select="substring-before(name(),':')"/></xsl:if></xsl:variable>

<xsl:text> </xsl:text><xsl:value-of select="name()"/>="<xsl:value-of select="."/>"<xsl:if test="namespace-uri()"><xsl:text> </xsl:text>xmlns<xsl:value-of select="$prefix"/>="<xsl:value-of select="namespace-uri()"/>"</xsl:if></xsl:template>
-->
 
</xsl:stylesheet>  
