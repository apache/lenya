<?xml version="1.0" encoding="iso-8859-1"?>

<xsl:stylesheet version="1.0"
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:page="http://apache.org/cocoon/lenya/cms-page/1.0"
>

<xsl:param name="contextPrefix" select="'/lenya'"/>
<xsl:param name="edit" select="'No node selected yet'"/>

<xsl:variable name="imagesPath"><xsl:value-of select="$contextPrefix"/>/lenya/images</xsl:variable>

<xsl:include href="copy-mixed-content.xsl"/>

<xsl:template match="form">
<page:page>
<page:title>Edit Document</page:title>
<page:body>
  
<div style="float: left">
  
<div class="lenya-box">
  <div class="lenya-box-title">Information</div>
  <div class="lenya-box-body">
  
<table class="lenya-table-noborder">
  <tr>
    <td class="lenya-entry-caption">Document:</td>
    <td><xsl:value-of select="docid"/></td>
  </tr>
  <tr>
    <td class="lenya-entry-caption">Form:</td>
    <td><xsl:value-of select="ftype"/></td>
  </tr>
  <tr>
    <td class="lenya-entry-caption">Node:</td>
    <td><xsl:value-of select="$edit"/></td>
  </tr>

  <xsl:if test="message">
    <tr>
      <td valign="top" class="lenya-entry-caption"><span class="lenya-error">Message:</span></td>
      <td>
<font color="red">
        <xsl:value-of select="message"/>
</font>
        <br/><br/>
        (Check log files for more details: lenya/WEB-INF/logs/*)
      </td>
    </tr>
  </xsl:if>

</table>

</div>
</div>

<form method="post" action="?lenya.usecase=edit&amp;lenya.step=close&amp;form={ftype}">
  
<div class="lenya-box">
  <div class="lenya-box-title" style="text-align: right">
    <input type="submit" value="SAVE" name="save"/>&#160;<input type="submit" value="CANCEL" name="cancel"/>
  </div>
  <div class="lenya-box-body">
  
  <table class="lenya-table">
    <xsl:apply-templates select="node"/>
  </table>

  </div>
  <div class="lenya-box-title" style="text-align: right">
    <input type="submit" value="SAVE" name="save"/>&#160;<input type="submit" value="CANCEL" name="cancel"/>
  </div>
</div>
</form>

<div class="lenya-box">
  <div class="lenya-box-title"><a href="http://www.w3.org/TR/REC-xml#syntax">Predefined Entities</a></div>
  <div class="lenya-box-body">
<ul>
<li>&amp;lt; instead of &lt; (left angle bracket <b>must</b> be escaped)</li>
<li>&amp;amp; instead of &amp; (ampersand <b>must</b> be escaped)</li>
<li>&amp;gt; instead of > (right angle bracket)</li>
<li>&amp;apos; instead of ' (single-quote)</li>
<li>&amp;quot; instead of " (double-quote)</li>
</ul>
</div>
</div>

</div>

</page:body>
</page:page>
</xsl:template>

<xsl:template match="node">
<tr>
  <td valign="top"><xsl:apply-templates select="action"/><xsl:if test="not(action)">&#160;</xsl:if><xsl:apply-templates select="@select"/></td>
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
<xsl:choose>
<xsl:when test="$edit = ../@select">
  <xsl:apply-templates select="textarea"/>
  <xsl:copy-of select="input"/>
</xsl:when>
<xsl:otherwise>
  <p>
    <xsl:value-of select="input/@value"/>
    <xsl:copy-of select="textarea/node()"/>
  </p>
</xsl:otherwise>
</xsl:choose>
</xsl:template>

<xsl:template match="textarea">
<xsl:copy>
  <xsl:copy-of select="@*"/>
  <xsl:apply-templates mode="mixedcontent"/>
</xsl:copy>
</xsl:template>

<xsl:template match="insert">
<input type="image" src="{$imagesPath}/insert.gif" name="{@name}" value="LENYA"/>
</xsl:template>

<xsl:template match="delete">
<input type="image" src="{$imagesPath}/delete.gif" name="{@name}" value="true"/>
</xsl:template>

<xsl:template match="@select">
<input type="image" src="{$imagesPath}/util/reddot.gif" name="edit" value="{.}"/>
</xsl:template>

</xsl:stylesheet>  
