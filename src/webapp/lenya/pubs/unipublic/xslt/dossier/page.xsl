<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">

<xsl:output method="html" version="1.0" indent="yes" encoding="ISO-8859-1"/>

<xsl:template match="dossier">
  <html>
    <head>
      <title>unipublic - Dossier</title>
<!--
      <xsl:call-template name="styles"/>
      <xsl:call-template name="jscript"/>
-->
    </head>

    <body text="black" link="#333399" alink="#CC0000" vlink="#666666" bgcolor="#F5F5F5" background="{$img-unipub}/bg.gif">

      <xsl:call-template name="Searchbox"/>

RELATED CONTENT: <xsl:apply-templates select="related-content"/>
MAIN: <xsl:apply-templates select="title"/>

<!--
        <center>
          <table cellspacing="0" cellpadding="0" border="0" width="585">

            <tr height="16">
              <td height="16" width="187" align="center" valign="top">
                <center><a href="../../../../"><img height="52" width="108" src="{$img-unipub}/t_unipublic_ALT.gif" alt="Unipublic" border="0"/></a></center>
              </td>
              <td height="16" align="right" width="10"></td>
              <td width="388" height="16"></td>
            </tr>

            <xsl:call-template name="slider_image"/>

            <tr>
              <td valign="top" width="187">

RELATED CONTENT: <xsl:apply-templates select="related-content"/>
                <xsl:apply-templates select="NewsItem/NewsComponent/ContentItem/DataContent/related-content"/>

              </td>
MAIN: <xsl:apply-templates select="title"/>
              <xsl:apply-templates select="NewsItem/NewsComponent/ContentItem/DataContent/nitf"/>
           </tr>

           <xsl:apply-templates select="NewsItem/NewsComponent/NewsLines" mode="Article_copyright"/>

         </table>
       </center>
-->
     </body>
  </html>
</xsl:template>

</xsl:stylesheet>
