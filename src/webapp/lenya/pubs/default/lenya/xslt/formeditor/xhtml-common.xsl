<?xml version="1.0" encoding="iso-8859-1"?>

<xsl:stylesheet version="1.0"
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:xhtml="http://www.w3.org/1999/xhtml"
  xmlns:lenya="http://apache.org/cocoon/lenya/page-envelope/1.0"
  xmlns:dc="http://purl.org/dc/elements/1.1/"
  xmlns:unizh="http://unizh.ch/doctypes/elements/1.0"
>

<xsl:template match="xhtml:body">
<node name="Body" />
<xsl:apply-templates mode="body"/>
</xsl:template>

<xsl:template name="insertmenu">
<xsl:param name="path"/>
<xsl:variable name="ns">namespace=&quot;http://www.w3.org/1999/xhtml&quot;</xsl:variable>
<insert-after select="{$path}[@tagID='{@tagID}']">
  <element name="Paragraph" xupdate="&lt;xupdate:insert-after select=&quot;{$path}[@tagID='{@tagID}']&quot;&gt;&lt;xupdate:element name=&quot;xhtml:p&quot; {$ns}&gt;New Paragraph&lt;/xupdate:element&gt;&lt;/xupdate:insert-after&gt;"/>
  <element name="Table" xupdate="&lt;xupdate:insert-after select=&quot;{$path}[@tagID='{@tagID}']&quot;&gt;&lt;xupdate:element name=&quot;xhtml:table&quot; {$ns}&gt;&lt;tr&gt;&lt;td&gt;New Table&lt;/td&gt;&lt;/tr&gt;&lt;/xupdate:element&gt;&lt;/xupdate:insert-after&gt;"/>
  <element name="Unordered List" xupdate="&lt;xupdate:insert-after select=&quot;{$path}[@tagID='{@tagID}']&quot;&gt;&lt;xupdate:element name=&quot;xhtml:ul&quot; {$ns}&gt;&lt;li&gt;New Unordered List&lt;/li&gt;;&lt;/xupdate:element&gt;&lt;/xupdate:insert-after&gt;"/>
  <element name="Ordered List" xupdate="&lt;xupdate:insert-after select=&quot;{$path}[@tagID='{@tagID}']&quot;&gt;&lt;xupdate:element name=&quot;xhtml:ol&quot; {$ns}&gt;&lt;li&gt;New Ordered List&lt;/li&gt;&lt;/xupdate:element&gt;&lt;/xupdate:insert-after&gt;"/>
  <element name="Headline 2" xupdate="&lt;xupdate:insert-after select=&quot;{$path}[@tagID='{@tagID}']&quot;&gt;&lt;xupdate:element name=&quot;xhtml:h2&quot; {$ns}&gt;New Headline 2&lt;/xupdate:element&gt;&lt;/xupdate:insert-after&gt;"/>
  <element name="Headline 3" xupdate="&lt;xupdate:insert-after select=&quot;{$path}[@tagID='{@tagID}']&quot;&gt;&lt;xupdate:element name=&quot;xhtml:h3&quot; {$ns}&gt;New Headline 3&lt;/xupdate:element&gt;&lt;/xupdate:insert-after&gt;"/>
  <element name="Headline 4" xupdate="&lt;xupdate:insert-after select=&quot;{$path}[@tagID='{@tagID}']&quot;&gt;&lt;xupdate:element name=&quot;xhtml:h4&quot; {$ns}&gt;New Headline 4&lt;/xupdate:element&gt;&lt;/xupdate:insert-after&gt;"/>
</insert-after>
</xsl:template>

<xsl:template match="xhtml:p" mode="body">
<node name="Paragraph" select="/*/xhtml:body/xhtml:p[@tagID='{@tagID}']">
  <action><delete name="&lt;xupdate:remove select=&quot;/*/xhtml:body/xhtml:p[@tagID='{@tagID}']&quot;/&gt;"/></action>
  <content>
    <textarea name="&lt;xupdate:update select=&quot;/*/xhtml:body/xhtml:p[@tagID='{@tagID}']&quot;&gt;" cols="40" rows="30">
      <xsl:copy-of select="node()"/>
    </textarea>
  </content>
</node>

<xsl:call-template name="insertmenu"><xsl:with-param name="path">/*/xhtml:body/xhtml:p</xsl:with-param></xsl:call-template>

</xsl:template>

<xsl:template match="xhtml:table" mode="body">
<node name="Table" select="/*/xhtml:body/xhtml:table[@tagID='{@tagID}']">
  <action><delete name="&lt;xupdate:remove select=&quot;/*/xhtml:body/xhtml:table[@tagID='{@tagID}']&quot;/&gt;"/></action>
  <content>
    <textarea name="&lt;xupdate:update select=&quot;/*/xhtml:body/xhtml:table[@tagID='{@tagID}']&quot;&gt;" cols="40" rows="30">
      <xsl:copy-of select="."/>
    </textarea>
  </content>
</node>

<xsl:call-template name="insertmenu"><xsl:with-param name="path">/*/xhtml:body/xhtml:table</xsl:with-param></xsl:call-template>

</xsl:template>

<xsl:template match="xhtml:ul" mode="body">
<node name="Unordered List" select="/*/xhtml:body/xhtml:ul[@tagID='{@tagID}']">
  <action><delete name="&lt;xupdate:remove select=&quot;/*/xhtml:body/xhtml:ul[@tagID='{@tagID}']&quot;/&gt;"/></action>
  <content>
    <textarea name="&lt;xupdate:update select=&quot;/*/xhtml:body/xhtml:ul[@tagID='{@tagID}']&quot;&gt;" cols="40" rows="30">
      <xsl:copy-of select="node()"/>
    </textarea>
  </content>
</node>

<xsl:call-template name="insertmenu"><xsl:with-param name="path">/*/xhtml:body/xhtml:ul</xsl:with-param></xsl:call-template>

</xsl:template>

<xsl:template match="xhtml:ol" mode="body">
<node name="Ordered List" select="/*/xhtml:body/xhtml:ol[@tagID='{@tagID}']">
  <action><delete name="&lt;xupdate:remove select=&quot;/*/xhtml:body/xhtml:ol[@tagID='{@tagID}']&quot;/&gt;"/></action>
  <content>
    <textarea name="&lt;xupdate:update select=&quot;/*/xhtml:body/xhtml:ol[@tagID='{@tagID}']&quot;&gt;" cols="40" rows="30">
      <xsl:copy-of select="node()"/>
    </textarea>
  </content>
</node>

<xsl:call-template name="insertmenu"><xsl:with-param name="path">/*/xhtml:body/xhtml:ol</xsl:with-param></xsl:call-template>

</xsl:template>

<xsl:template match="xhtml:h2" mode="body">
<node name="Headline 2" select="/*/xhtml:body/xhtml:h2[@tagID='{@tagID}']">
  <action><delete name="&lt;xupdate:remove select=&quot;/*/xhtml:body/xhtml:h2[@tagID='{@tagID}']&quot;/&gt;"/></action>
  <content>
    <textarea name="&lt;xupdate:update select=&quot;/*/xhtml:body/xhtml:h2[@tagID='{@tagID}']&quot;&gt;" cols="40" rows="3">
      <xsl:copy-of select="node()"/>
    </textarea>
  </content>
</node>

<xsl:call-template name="insertmenu"><xsl:with-param name="path">/*/xhtml:body/xhtml:h2</xsl:with-param></xsl:call-template>

</xsl:template>

<xsl:template match="xhtml:h3" mode="body">
<node name="Headline 3" select="/*/xhtml:body/xhtml:h3[@tagID='{@tagID}']">
  <action><delete name="&lt;xupdate:remove select=&quot;/*/xhtml:body/xhtml:h3[@tagID='{@tagID}']&quot;/&gt;"/></action>
  <content>
    <textarea name="&lt;xupdate:update select=&quot;/*/xhtml:body/xhtml:h3[@tagID='{@tagID}']&quot;&gt;" cols="40" rows="3">
      <xsl:copy-of select="node()"/>
    </textarea>
  </content>
</node>

<xsl:call-template name="insertmenu"><xsl:with-param name="path">/*/xhtml:body/xhtml:h3</xsl:with-param></xsl:call-template>

</xsl:template>

<xsl:template match="xhtml:h4" mode="body">
<node name="Headline 4" select="/*/xhtml:body/xhtml:h4[@tagID='{@tagID}']">
  <action><delete name="&lt;xupdate:remove select=&quot;/*/xhtml:body/xhtml:h4[@tagID='{@tagID}']&quot;/&gt;"/></action>
  <content>
    <textarea name="&lt;xupdate:update select=&quot;/*/xhtml:body/xhtml:h4[@tagID='{@tagID}']&quot;&gt;" cols="40" rows="3">
      <xsl:copy-of select="node()"/>
    </textarea>
  </content>
</node>

<xsl:call-template name="insertmenu"><xsl:with-param name="path">/*/xhtml:body/xhtml:h4</xsl:with-param></xsl:call-template>

</xsl:template>

<xsl:template match="xhtml:hr" mode="body">
<node name="Horizontal Rule" select="/*/xhtml:body/xhtml:hr[@tagID='{@tagID}']">
  <action><delete name="&lt;xupdate:remove select=&quot;/*/xhtml:body/xhtml:hr[@tagID='{@tagID}']&quot;/&gt;"/></action>
</node>

<xsl:call-template name="insertmenu"><xsl:with-param name="path">/*/xhtml:body/xhtml:hr</xsl:with-param></xsl:call-template>

</xsl:template>

</xsl:stylesheet>  