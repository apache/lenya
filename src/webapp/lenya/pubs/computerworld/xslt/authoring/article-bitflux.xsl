<?xml version="1.0" encoding="utf-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                              xmlns:xslout="Can be anything, doesn't matter">
<xsl:output type="xml"/>
<xsl:namespace-alias stylesheet-prefix="xslout" result-prefix="xsl"/>


<!-- Copies everything else to the result tree -->
<xsl:template match="@* | node()">
  <xsl:copy>
    <xsl:apply-templates select="@* | node()"/>
  </xsl:copy>
</xsl:template>


<!-- Replaces the html code of the editable section by the bitflux specific code -->
<xsl:template match="*[@bxe-editable='headlines']">
	<articles contentEditable="true">
                <xslout:for-each select="article">
  			<xslout:apply-templates/>
                </xslout:for-each>
	</articles>
</xsl:template>

<xsl:template match="article">
	<table border="0" cellpadding="2" cellspacing="0" bgcolor="#CCCCFF">
		<span bxe-editable="article">
		<snip>Here goes the xslt/xhtml code that displays this part of the page.</snip>
		</span>
	</table>
</xsl:template>


<!-- Adds the stylesheet specific elements -->
<xsl:template match="/">
  <xslout:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
    <xslout:output type="xml"/>
    <xslout:template match="/">
      <xsl:apply-templates/>
    </xslout:template>

	Template used by Bitfluxeditor to make things editable 
        <xslout:template match="*">
                <xslout:copy>
                        <xslout:for-each select="@*">
                                <xslout:copy/>
                        </xslout:for-each>
                        <xslout:apply-templates select="node()"/>
                </xslout:copy>
        </xslout:template>

  </xslout:stylesheet>
</xsl:template>  

</xsl:stylesheet>



<!--
<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
  <xsl:output method="xml" encoding="iso-8859-1"/>
  <xsl:template match="/">

  </xsl:template>
  <xsl:template match="body">



 
    <hl1 xmlns="http://www.w3.org/1999/xhtml" contentEditable="true">
      <xsl:for-each select="body.head/hedline/hl1">
        <xsl:apply-templates/>
      </xsl:for-each>
    </hl1>
    <abstract contentEditable="true">
      <xsl:for-each select="body.head/abstract">
        <xsl:apply-templates/>
      </xsl:for-each>
    </abstract>
    <byline class="art-author" contentEditable="true">
      <xsl:for-each select="body.head/byline">
        <xsl:apply-templates/>
      </xsl:for-each>
    </byline>
    <article contentEditable="true">
      <xsl:for-each select="body.content">
        <xsl:apply-templates/>
      </xsl:for-each>
    </article>
  </xsl:template>

  <xsl:template match="*">
    <xsl:copy>
      <xsl:for-each select="@*">
        <xsl:copy/>
      </xsl:for-each>
      <xsl:apply-templates select="node()"/>
    </xsl:copy>
  </xsl:template>
</xsl:stylesheet>
-->

