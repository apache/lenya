<?xml version="1.0" encoding="UTF-8"?>

<xsl:stylesheet version="1.0"
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns="http://www.w3.org/1999/xhtml"
  xmlns:office="urn:oasis:names:tc:opendocument:xmlns:office:1.0"
  xmlns:text="urn:oasis:names:tc:opendocument:xmlns:text:1.0"
>

  <!-- default parameter value -->
  <xsl:param name="rendertype" select="''"/>

<xsl:template match="office:document-content">
  <div id="body">
    <h1>OpenDocument Content</h1>
    <p>
    <xsl:apply-templates select="office:body/office:text/text:p"/>
    </p>
  </div>
</xsl:template>

<xsl:template match="text:p">
  <p>
    <xsl:apply-templates/>
  </p>
</xsl:template>



<!--
  <xsl:template match="default:links" xmlns:default="http://apache.org/lenya/pubs/default/1.0">
    <div id="body">
      <h1><xsl:value-of select="default:title"/></h1>
      <ul>
        <xsl:apply-templates select="default:link"/>
      </ul>
    </div>
  </xsl:template>

  <xsl:template match="default:link">
    <li>
      <a href="{@href}"><xsl:value-of select="."/></a>
    </li>
  </xsl:template>
-->
  
</xsl:stylesheet>
