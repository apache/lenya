<?xml version="1.0" encoding="utf-8"?>

<!-- 
   xml prettyprinter for apache cocoon/lenya, contributed by <nettings@apache.org>
   everything that is non-trivial in this script has been borrowed from somewhere.
   this script is in the public domain.
-->

<xsl:stylesheet version="1.0"
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns="http://www.w3.org/1999/xhtml"
>

<xsl:output method="xml"/>

<xsl:variable name="indent" select="'&#160;&#160; '"/>

<xsl:template match="/">
  <html>
    <head>
      <style type="text/css">
         
.highlight-elem {
  color:green;
}
.highlight-attr {
}
.highlight-attrName {
  color:blue;
}
.highlight-attrValue {
  color:red;
}
.highlight-text {
  color:grey;
}

.highlight-pi {
  color:darkgrey;
  font-weight:bold;
  font-style:italic;
}

.highlight-comment {
  color:#bbbbbb;
  font-style:italic;
}

      </style>
    </head>
    <body>
      <h1>XML Source</h1>
      <xsl:apply-templates />
      <p>
         Note: this source viewer is not very well tested, does not currently handle namespace nodes and mixed content formatting is very ugly.
      </p>

    </body>
  </html>
</xsl:template>

<xsl:template match="*">
  <xsl:param name="indentation"/>
  <xsl:value-of select="$indentation"/>
  <span class="highlight-elem">
    <xsl:text>&lt;</xsl:text>
    <xsl:value-of select="local-name()"/>
    <xsl:apply-templates select="@*"/>
    <xsl:text>&gt;</xsl:text>
  </span>
  <xsl:if test="*">
    <br />
  </xsl:if>
  <xsl:apply-templates>
    <xsl:with-param name="indentation" select="concat($indentation, $indent)"/>
  </xsl:apply-templates>
  <xsl:if test="*">
  <xsl:value-of select="$indentation"/>
  </xsl:if>
  <span class="highlight-elem">
  <xsl:text>&lt;/</xsl:text>
  <xsl:value-of select="local-name()"/>
  <xsl:text>&gt;</xsl:text>
  </span>
  <br />
</xsl:template>

<xsl:template match="*[not(node())]">
  <xsl:param name="indentation"/>
  <xsl:value-of select="$indentation"/>
  <span class="highlight-elem">
    <xsl:text>&lt;</xsl:text>
    <xsl:value-of select="local-name()"/>
    <xsl:apply-templates select="@*"/>
    <xsl:text>/&gt;</xsl:text>
  </span>
  <br />
</xsl:template>

<xsl:template match="@*">
  <xsl:text> </xsl:text>
  <span class="highlight-attr">
    <span class="highlight-attrName">
      <xsl:value-of select="name()"/>
    </span>
    <xsl:text>="</xsl:text>
    <span class="highlight-attrValue">
      <xsl:value-of select="."/>
    </span>
    <xsl:text>"</xsl:text>
  </span>
</xsl:template>

<xsl:template match="text()">
  <xsl:param name="indentation"/>
  <xsl:value-of select="$indentation"/>
  <span class="highlight-text">
    <xsl:value-of select="normalize-space(.)"/>
  </span>
</xsl:template>

<xsl:template match="processing-instruction()">
  <span class="highlight-pi">
    <xsl:text>&lt;?</xsl:text>
    <xsl:value-of select="name()"/>
    <xsl:text> </xsl:text>
    <xsl:value-of select="."/>
    <xsl:text>?&gt;</xsl:text>
  </span>
  <br />
</xsl:template>

<xsl:template match="comment()">
  <xsl:param name="indentation"/>
  <xsl:value-of select="$indentation"/>
  <span class="highlight-comment">
    <xsl:text>&lt;!-- </xsl:text>
    <xsl:value-of select="."/>
    <xsl:text> --&gt;</xsl:text>
  </span>
  <br />
</xsl:template>


</xsl:stylesheet>
