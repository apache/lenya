<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                              xmlns:xslout="Can be anything, doesn't matter">
<xsl:output type="xml"/>
<xsl:namespace-alias stylesheet-prefix="xslout" result-prefix="xsl"/>

<!-- Copies everything else to the result tree  -->
<xsl:template match="@* | node()">
  <xsl:copy>
    <xsl:apply-templates select="@* | node()"/>
  </xsl:copy>
</xsl:template>

<xsl:template match="*[@bxe-editable='true']">
	<!--          <newsletter contentEditable="true">
            <xsl:for-each select="newsletter">
              <xsl:apply-templates/>
            </xsl:for-each>
            </newsletter>
   	-->
    <table cellpadding="3" border="0" width="100%" bgcolor="white">
      <tr>
        <td class="tsr-text">To:</td>
        <td class="tsr-text">
          <to contentEditable="true">
            <xslout:for-each select="newsletter/email/to">
              <xslout:apply-templates/>
            </xslout:for-each>
           </to>
        </td>
      </tr> 
      <tr>
        <td class="tsr-text">Cc:</td>
        <td class="tsr-text">
          <cc contentEditable="true">
            <xslout:for-each select="newsletter/email/cc">
              <xslout:apply-templates/>
            </xslout:for-each>
          </cc>
        </td>
      </tr> 
      <tr>
        <td class="tsr-text">Bcc:</td>
        <td class="tsr-text">
          <bcc contentEditable="true">
            <xslout:for-each select="newsletter/email/bcc">
              <xslout:apply-templates/>
            </xslout:for-each>
          </bcc>
        </td>
      </tr>
      <tr>
        <td class="tsr-text">Subject:</td>
        <td class="tsr-text">
          <subject contentEditable="true">
            <xslout:for-each select="newsletter/email/subject">
              <xslout:apply-templates/>
            </xslout:for-each>
          </subject>
        </td>
      </tr> 
    </table>
     <title contentEditable="true">
      <xslout:for-each select="newsletter/title">
        <xslout:apply-templates/>
      </xslout:for-each>
    </title>
     <abstract contentEditable="true">
      <xslout:for-each select="newsletter/abstract">
        <xslout:apply-templates/>
      </xslout:for-each>
    </abstract>
    <articles contentEditable="true">
            <xslout:for-each select="newsletter/articles">
              <xslout:apply-templates/>
            </xslout:for-each>
    </articles>
    <br/><br/>
     <footer contentEditable="true">
            <xslout:for-each select="newsletter/footer">
              <xslout:apply-templates/>
            </xslout:for-each>
    </footer>
    

</xsl:template>

<!-- Adds the stylesheet specific elements -->
<xsl:template match="/">
  <xslout:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
    <xslout:output type="xml"/>
    <xslout:template match="/">
      <xsl:apply-templates/>
    </xslout:template>

	<!-- Template used by Bitfluxeditor to make things editable -->
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
