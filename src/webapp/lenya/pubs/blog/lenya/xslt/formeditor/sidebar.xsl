<?xml version="1.0" encoding="iso-8859-1"?>

<xsl:stylesheet version="1.0"
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
>

<xsl:import href="../../../../../xslt/authoring/edit/form.xsl"/>

<xsl:template match="sidebar">
<xsl:apply-templates select="block"/>
</xsl:template>

<xsl:template match="block">
<node name="Block">
  <action><insert name="&lt;xupdate:insert-before select=&quot;/sidebar/block[@tagID='{@tagID}']&quot;&gt;&lt;xupdate:element name=&quot;block&quot;&gt;&lt;title&gt;New title&lt;/title&gt;&lt;content&gt;New content&lt;/content&gt;&lt;/xupdate:element&gt;&lt;/xupdate:insert-before&gt;"/></action>
</node>
<node name="Block">
  <action><delete name="&lt;xupdate:remove select=&quot;/sidebar/block[@tagID='{@tagID}']&quot;/&gt;"/></action>
</node>
<node name="Title" select="/sidebar/block/title[@tagID='{title/@tagID}']">
  <content type="plain"><input type="text" name="&lt;xupdate:update select=&quot;/sidebar/block/title[@tagID='{title/@tagID}']&quot;&gt;" size="40"><xsl:attribute name="value"><xsl:value-of select="title" /></xsl:attribute></input></content>
</node>
<node name="Content" select="/sidebar/block/content[@tagID='{content/@tagID}']">
  <content type="mixed">
    <textarea name="&lt;xupdate:update select=&quot;/sidebar/block/content[@tagID='{content/@tagID}']&quot;&gt;" cols="40" rows="3">
      <xsl:copy-of select="content/node()"/>
<!--
      <xsl:apply-templates select="content/node()" mode="mixedcontent"/>
-->
    </textarea>
  </content>

<!--
  <content type="non-editable"><pre><xsl:copy-of select="content/@*|content/node()" /></pre></content>
-->
</node>
</xsl:template>
 
</xsl:stylesheet>
