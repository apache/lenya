<?xml version="1.0"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">

  <xsl:template match="/">
    <div id="xopus_treeview">
      <xsl:apply-templates/>
    </div>
  </xsl:template>

  <!-- Template for attributes not handled elsewhere -->
  <xsl:template match="@*" xml:space="preserve">
    <xsl:if test="not(namespace-uri()='http://www.xopus.org/xmlns/id')">
      <SPAN><xsl:attribute name="class">xopus_treeview_<xsl:if test="xsl:*/@*">x</xsl:if>t</xsl:attribute>&#160;<xsl:value-of select="string(name())"/></SPAN><SPAN class="xopus_treeview_m">="</SPAN><SPAN class="xopus_treeview_tx"><xsl:value-of select="."/></SPAN><SPAN class="xopus_treeview_m">"</SPAN>
    </xsl:if>
  </xsl:template>

  <!-- Template for attributes in the xmlns or xml namespace -->
  <xsl:template match="@xmlns:*|@xmlns|@xml:*"><SPAN class="xopus_treeview_ns">&#160;<xsl:value-of select="string(name())"/></SPAN><SPAN class="xopus_treeview_m">="</SPAN><SPAN class="xopus_treeview_ns"><xsl:value-of select="."/></SPAN><SPAN class="xopus_treeview_m">"</SPAN></xsl:template>

  <!-- Template for text nodes -->
  <xsl:template match="text()">
    <xsl:if test="string-length(normalize-space(.))>0">
      <DIV class="xopus_treeview_e">
      <SPAN class="xopus_treeview_b">&#160;</SPAN>
      <SPAN class="xopus_treeview_tx"><xsl:value-of select="."/></SPAN>
      </DIV>
    </xsl:if>
  </xsl:template>

  <!-- Template for comment nodes -->
  <xsl:template match="comment()">
    <DIV class="xopus_treeview_k">
    <SPAN><A class="xopus_treeview_b" onclick="return false" onfocus="h()" STYLE="visibility:hidden">-</A> <SPAN class="xopus_treeview_m">&lt;!--</SPAN></SPAN>
    <SPAN id="clean" class="xopus_treeview_ci"><PRE><xsl:value-of select="."/></PRE></SPAN>
    <SPAN class="xopus_treeview_b">&#160;</SPAN> <SPAN class="xopus_treeview_m">--&gt;</SPAN>
    <SCRIPT>f(clean);</SCRIPT></DIV>
  </xsl:template>

  <!-- Template for elements not handled elsewhere (leaf nodes) -->
  <xsl:template match="*">
    <xsl:if test="not(namespace-uri()='http://www.xopus.org/xmlns/id')">
      <DIV class="xopus_treeview_e"><DIV STYLE="margin-left:1em;text-indent:-2em">
      <SPAN class="xopus_treeview_b">&#160;</SPAN>
      <SPAN class="xopus_treeview_m">&lt;</SPAN><SPAN><xsl:attribute name="class">xopus_treeview_<xsl:if test="xsl:*">x</xsl:if>t</xsl:attribute><xsl:value-of select="string(name())"/></SPAN> <xsl:apply-templates select="@*"/><SPAN class="xopus_treeview_m"> /&gt;</SPAN>
      </DIV></DIV>
    </xsl:if>
  </xsl:template>

  <!-- Template for elements with comment, pi and/or cdata children -->
  <xsl:template match="*[node()]">
    <xsl:if test="not(namespace-uri()='http://www.xopus.org/xmlns/id')">
      <DIV class="xopus_treeview_e">
      <DIV class="xopus_treeview_c"><A href="#" onclick="return false" onfocus="h()" class="xopus_treeview_b">-</A> <SPAN class="xopus_treeview_m">&lt;</SPAN><SPAN><xsl:attribute name="class">xopus_treeview_<xsl:if test="xsl:*">x</xsl:if>t</xsl:attribute><xsl:value-of select="string(name())"/></SPAN><xsl:apply-templates select="@*"/> <SPAN class="xopus_treeview_m">&gt;</SPAN></DIV>
      <DIV><xsl:apply-templates/>
      <DIV><SPAN class="xopus_treeview_b">&#160;</SPAN> <SPAN class="xopus_treeview_m">&lt;/</SPAN><SPAN><xsl:attribute name="class">xopus_treeview_<xsl:if test="xsl:*">x</xsl:if>t</xsl:attribute><xsl:value-of select="string(name())"/></SPAN><SPAN class="xopus_treeview_m">&gt;</SPAN></DIV>
      </DIV></DIV>
    </xsl:if>
  </xsl:template>

  <!-- Template for elements with only text children -->
  <xsl:template match="*[text() and not(comment())]">
    <xsl:if test="not(namespace-uri()='http://www.xopus.org/xmlns/id')">
      <DIV class="xopus_treeview_e"><DIV STYLE="margin-left:1em;text-indent:-2em">
      <SPAN class="xopus_treeview_b">&#160;</SPAN> <SPAN class="xopus_treeview_m">&lt;</SPAN><SPAN><xsl:attribute name="class">xopus_treeview_<xsl:if test="xsl:*">x</xsl:if>t</xsl:attribute><xsl:value-of select="string(name())"/></SPAN><xsl:apply-templates select="@*"/>
      <SPAN class="xopus_treeview_m">&gt;</SPAN><SPAN class="xopus_treeview_tx"><xsl:value-of select="text()"/></SPAN><SPAN class="xopus_treeview_m">&lt;/</SPAN><SPAN><xsl:attribute name="class">xopus_treeview_<xsl:if test="xsl:*">x</xsl:if>t</xsl:attribute><xsl:value-of select="string(name())"/></SPAN><SPAN class="xopus_treeview_m">&gt;</SPAN>
      </DIV></DIV>
    </xsl:if>
  </xsl:template>

  <!-- Template for elements with element children -->
  <xsl:template match="*[*]">
    <xsl:if test="not(namespace-uri()='http://www.xopus.org/xmlns/id')">
      <DIV class="xopus_treeview_e">
      <DIV class="xopus_treeview_c" STYLE="margin-left:1em;text-indent:-2em"><SPAN class="xopus_treeview_b">-</SPAN> <SPAN class="xopus_treeview_m">&lt;</SPAN><SPAN><xsl:attribute name="class">xopus_treeview_<xsl:if test="xsl:*">x</xsl:if>t</xsl:attribute><xsl:value-of select="string(name())"/></SPAN><xsl:apply-templates select="@*"/> <SPAN class="xopus_treeview_m">&gt;</SPAN></DIV>
      <DIV><xsl:apply-templates/>
      <DIV><SPAN class="xopus_treeview_b">&#160;</SPAN> <SPAN class="xopus_treeview_m">&lt;/</SPAN><SPAN><xsl:attribute name="class">xopus_treeview_<xsl:if test="xsl:*">x</xsl:if>t</xsl:attribute><xsl:value-of select="string(name())"/></SPAN><SPAN class="xopus_treeview_m">&gt;</SPAN></DIV>
      </DIV></DIV>
    </xsl:if>
  </xsl:template>

</xsl:stylesheet>
