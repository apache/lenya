<?xml version="1.0"? encoding="UTF-8">

<xsl:stylesheet version="1.0"
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:default="http://apache.org/lenya/pubs/default/1.0"
  xmlns="http://www.w3.org/1999/xhtml"
>

  <xsl:template match="default:links">
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
  
</xsl:stylesheet>
