<?xml version="1.0"?>

<!--                                                                                -->
<!--  Default XSL stylesheet for use by com.lotus.xsl.server#DefaultapplyXSL.       -->
<!--                                                                                -->
<!--  This stylesheet mimics the default behavior of IE when XML data is displayed  -->
<!--  without a corresponding XSL stylesheet.  This stylesheet uses JavaScript      -->
<!--  to accommodate node expansion and contraction.                                -->
<!--                                                                                -->

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0"
                xmlns="http://www.w3.org/1999/xhtml">
               
<xsl:output method="html" indent="no"/>
<xsl:strip-space elements="*"/>

<xsl:template match="/">
    <div id="st"><xsl:apply-templates/></div>

</xsl:template>

<!-- Templates for each node type follows.  The output of each template has a similar structure
  to enable script to walk the result tree easily for handling user interaction. -->
  
<!-- Template for pis not handled elsewhere -->
<xsl:template match="processing-instruction()">
  <div id="e">
  <span id="b">&#160;</span>
  <span id="m">&lt;?</span><span id="pi"><xsl:value-of select="name(.)"/>&#160; <xsl:value-of select="."/></span><span id="m">?&gt;</span>
  </div>
</xsl:template>

<!-- Template for the XML declaration.  Need a separate template because the pseudo-attributes
    are actually exposed as attributes instead of just element content, as in other pis 
<xsl:template match="processing-instruction('xml')">
  <div id="e">
  <span id="b">&#160;</span>
  <span id="m">&lt;?</span><span id="pi">xml <xsl:for-each select="@*"><xsl:value-of select="name(.)"/>="<xsl:value-of select="."/>" </xsl:for-each></span><span id="m">?&gt;</span>
  </div>
</xsl:template>
-->

<!-- Template for attributes not handled elsewhere -->
<xsl:template match="@*"><span id="t"><xsl:text> </xsl:text><xsl:value-of select="name(.)"/></span><span id="m">="</span><B><xsl:value-of select="."/></B><span id="m">"</span></xsl:template>

<!-- Template for attributes in the xmlns or xml namespace-->
<xsl:template match="@xmlns:*|@xmlns|@xml:*"><span id="ns"> <xsl:value-of select="name(.)"/></span><span id="m">="</span><B id="ns"><xsl:value-of select="."/></B><span id="m">"</span></xsl:template>

<!-- Template for text nodes -->
<xsl:template match="text()">
  <xsl:choose><xsl:when test="name(.) = '#cdata-section'"><xsl:call-template name="cdata"/></xsl:when>
  <xsl:otherwise><div id="e">
  <span id="b">&#160;</span>
  <span id="tx"><xsl:value-of select="."/></span>
  </div></xsl:otherwise></xsl:choose>
</xsl:template>
  
<!-- Template for comment nodes -->
<xsl:template match="comment()">
  <div id="k">
  <span><a id="b" onclick="return false" onfocus="h()" style="visibility:hidden">-</a> <span id="m">&lt;!--</span></span>
  <span id="clean" class="ci"><PRE><xsl:value-of select="."/></PRE></span>
  <span id="b">&#160;</span> <span id="m">--&gt;</span>
  <SCRIPT>f(clean);</SCRIPT></div>
</xsl:template>

<!-- Template for cdata nodes -->
<xsl:template name="cdata">
  <div id="k">
  <span><a id="b" onclick="return false" onfocus="h()" style="visibility:hidden">-</a> <span id="m">&lt;![CDaTa[</span></span>
  <span id="clean" class="di"><PRE><xsl:value-of select="."/></PRE></span>
  <span id="b">&#160;</span> <span id="m">]]&gt;</span>
  <SCRIPT>f(clean);</SCRIPT></div>
</xsl:template>

<!-- Template for elements not handled elsewhere (leaf nodes) -->
<xsl:template match="*">
  <div id="e"><div style="margin-left:1em;text-indent:-2em">
  <span id="b">&#160;</span>
  <span id="m">&lt;</span><span id="t"><xsl:value-of select="name(.)"/></span> <xsl:apply-templates select="@*"/><span id="m"> /&gt;</span>
  </div></div>
</xsl:template>
  
<!-- Template for elements with comment, pi and/or cdata children
<xsl:template match="*[comment() or processing-instruction() or cdata()]">
  <div id="e">
  <div id="c"><a href="#" onclick="return false" onfocus="h()" id="b">-</a> <span id="m">&lt;</span><span><xsl:attribute name="class"><xsl:if test="xsl:*">x</xsl:if>t</xsl:attribute><xsl:value-of select="name(.)"/></span><xsl:apply-templates select="@*"/> <span id="m">&gt;</span></div>
  <div><xsl:apply-templates/>
  <div><span id="b">&#160;</span> <span id="m">&lt;/</span><span><xsl:attribute name="class"><xsl:if test="xsl:*">x</xsl:if>t</xsl:attribute><xsl:value-of select="name(.)"/></span><span id="m">&gt;</span></div>
  </div></div>
</xsl:template> -->

<!-- Template for elements with only text children -->
<xsl:template match="*[text() and not(comment() or processing-instruction() or *)]">
  <div id="e"><div style="margin-left:1em;text-indent:-2em">
  <span id="b">&#160;</span> <span id="m">&lt;</span><span id="t"><xsl:value-of select="name(.)"/></span><xsl:apply-templates select="@*"/>
  <span id="m">&gt;</span><span id="tx"><xsl:value-of select="."/></span><span id="m">&lt;/</span><span id="t"><xsl:value-of select="name(.)"/></span><span id="m">&gt;</span>
  </div></div>
</xsl:template>

<!-- Template for elements with element children -->
<xsl:template match="*[*]">
  <div id="e">
  <div id="c" style="margin-left:1em;text-indent:-2em"><a href="#" onclick="return false" onfocus="h()" id="b">-</a> <span id="m">&lt;</span><span id="t"><xsl:value-of select="name(.)"/>
<!--  <xsl:if test="namespace-uri()">
  	xmlns="<xsl:value-of select="namespace-uri()"/>"
	</xsl:if>-->
  
  </span><xsl:apply-templates select="@*"/><span id="m">&gt;</span></div>
  <div><xsl:apply-templates/>
  <div><span id="b">&#160;</span><span id="m">&lt;/</span><span id="t"><xsl:value-of select="name(.)"/></span><span id="m">&gt;</span></div>
  </div></div>
</xsl:template>

</xsl:stylesheet>
