<?xml version="1.0" encoding="iso-8859-1"?>

<xsl:stylesheet version="1.0"
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
>

<xsl:template match="form">
<html>
<body>
<p>
Edit Document <b><xsl:value-of select="docid"/></b> (Form: <xsl:value-of select="ftype"/>)
</p>

<xsl:if test="message">
<p>
<font color="red"><b>Message:</b></font>
<br /><xsl:value-of select="message"/>
<br />(Check log files for more details: lenya/WEB-INF/logs/*)
</p>
</xsl:if>

<form method="post" action="?lenya.usecase=edit&amp;lenya.step=close&amp;form={ftype}">
<table border="1">
<tr>
  <td colspan="3" align="right"><input type="submit" value="SAVE" name="save"/><input type="submit" value="CANCEL" name="cancel"/></td>
</tr>
<xsl:apply-templates select="node"/>
<tr>
  <td colspan="3" align="right"><input type="submit" value="SAVE" name="save"/><input type="submit" value="CANCEL" name="cancel"/></td>
</tr>
</table>
</form>

<a href="http://www.w3.org/TR/REC-xml#syntax">Predefined Entities</a>:
<ul>
<li>&amp;lt; instead of &lt; (left angle bracket <b>must</b> be escaped)</li>
<li>&amp;amp; instead of &amp; (ampersand <b>must</b> be escaped)</li>
<li>&amp;gt; instead of > (right angle bracket)</li>
<li>&amp;apos; instead of ' (single-quote)</li>
<li>&amp;quot; instead of " (double-quote)</li>
</ul>
</body>
</html>
</xsl:template>

<xsl:template match="node">
<tr>
  <td valign="top"><xsl:apply-templates select="action"/><xsl:if test="not(action)">&#160;</xsl:if></td>
  <xsl:choose>
    <xsl:when test="content">
      <td valign="top"><xsl:apply-templates select="@name"/></td>
      <td valign="top"><xsl:apply-templates select="content"/></td>
    </xsl:when>
    <xsl:otherwise>
      <td colspan="2" valign="top"><xsl:apply-templates select="@name"/></td>
    </xsl:otherwise>
  </xsl:choose>
</tr>
</xsl:template>

<xsl:template match="action">
<xsl:apply-templates/>
</xsl:template>

<xsl:template match="content">
<xsl:copy-of select="node()"/>
</xsl:template>

<xsl:template match="insert">
<input type="image" src="/lenya/lenya/images/insert.gif" name="{@name}" value="LENYA"/>
</xsl:template>


<xsl:template match="delete">
<input type="image" src="/lenya/lenya/images/delete.gif" name="{@name}" value="true"/>
</xsl:template>
</xsl:stylesheet>  
