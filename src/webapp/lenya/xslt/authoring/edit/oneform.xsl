<?xml version="1.0" encoding="iso-8859-1"?>
<xsl:stylesheet version="1.0"
xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
xmlns:page="http://apache.org/cocoon/lenya/cms-page/1.0">
  <xsl:output indent="no" />
  <xsl:param name="docid" />
  <xsl:param name="message" />
  <xsl:template match="/">
    <page:page>
      <page:title>Edit Document</page:title>
      <page:body>
        <div class="lenya-box">
          <div class="lenya-box-title">
            <a href="http://www.w3.org/TR/REC-xml#syntax">Predefined
            Entities</a>
          </div>
          <div class="lenya-box-body">
            <ul>
              <li>&amp;lt; instead of &lt; (left angle bracket 
              <b>must</b>
              be escaped)</li>
              <li>&amp;amp; instead of &amp; (ampersand 
              <b>must</b>
              be escaped)</li>
              <li>&amp;gt; instead of &gt; (right angle bracket)</li>
              <li>&amp;apos; instead of ' (single-quote)</li>
              <li>&amp;quot; instead of " (double-quote)</li>
            </ul>
            <table class="lenya-table-noborder">
              <xsl:if test="$message">
                <tr>
                  <td valign="top" class="lenya-entry-caption">
                    <span class="lenya-error">Message:</span>
                  </td>
                  <td>
                  <font color="red">
                    <xsl:value-of select="$message" />
                  </font>
                  <br />
                  <br />
                  (Check log files for more details:
                  lenya/WEB-INF/logs/*)</td>
                </tr>
              </xsl:if>
            </table>
          </div>
        </div>
        <div class="lenya-box">
          <div class="lenya-box-body">
            <form method="post"
            action="?lenya.usecase=1formedit&amp;lenya.step=close">
              <table border="0">
                <tr>
                  <td align="right">
                    <input type="submit" value="Save" name="save" />
                    <input type="submit" value="Cancel"
                    name="cancel" />
                  </td>
                </tr>
                <tr>
                  <td>
                    <textarea name="content" cols="80" rows="80">
                      <xsl:apply-templates mode="mixedcontent" />
                    </textarea>
                  </td>
                </tr>
                <tr>
                  <td align="right">
                    <input type="submit" value="Save" name="save" />
                    <input type="submit" value="Cancel"
                    name="cancel" />
                  </td>
                </tr>
              </table>
            </form>
          </div>
        </div>
      </page:body>
    </page:page>
  </xsl:template>
  <xsl:include href="copy-mixed-content.xsl" />
<!-- Copy mixed content -->
<!--
<xsl:template match="//*" mode="mixedcontent">
<xsl:variable name="prefix"><xsl:if test="contains(name(),':')">:<xsl:value-of select="substring-before(name(),':')"/></xsl:if></xsl:variable>

<xsl:choose>
<xsl:when test="node()">
<xsl:text>&lt;</xsl:text><xsl:value-of select="name()"/>

<xsl:apply-templates select="@*" mode="mixedcontent"/>

<xsl:for-each select="namespace::*">
<xsl:variable name="prefix"><xsl:if test="local-name() != ''">:<xsl:value-of select="local-name()"/></xsl:if></xsl:variable>
<xsl:if test=". != 'http://www.w3.org/XML/1998/namespace'">
<xsl:text> </xsl:text>xmlns<xsl:value-of select="$prefix"/>="<xsl:value-of select="."/><xsl:text>"</xsl:text>
</xsl:if>
</xsl:for-each>

<xsl:text>&gt;</xsl:text>

<xsl:apply-templates select="node()" mode="mixedcontent"/>

<xsl:text>&lt;/</xsl:text><xsl:value-of select="name()"/><xsl:text>&gt;</xsl:text>

</xsl:when>

<xsl:otherwise>

<xsl:text>&lt;</xsl:text><xsl:value-of select="name()"/>

<xsl:apply-templates select="@*" mode="mixedcontent"/>

<xsl:for-each select="namespace::*">
<xsl:variable name="prefix"><xsl:if test="local-name() != ''">:<xsl:value-of select="local-name()"/></xsl:if></xsl:variable>
<xsl:if test=". != 'http://www.w3.org/XML/1998/namespace'">
<xsl:text> </xsl:text>xmlns<xsl:value-of select="$prefix"/>="<xsl:value-of select="."/><xsl:text>"</xsl:text>
</xsl:if>
</xsl:for-each>

<xsl:text>/&gt;</xsl:text></xsl:otherwise>
</xsl:choose>
</xsl:template>




<xsl:template match="@*" mode="mixedcontent"><xsl:text> </xsl:text><xsl:value-of select="name()"/>="<xsl:value-of select="."/>"</xsl:template>
-->
</xsl:stylesheet>

