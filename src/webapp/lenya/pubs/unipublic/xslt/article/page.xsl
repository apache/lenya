<?xml version="1.0"?>

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0" xmlns:up="http://www.unipublic.unizh.ch/2002/up">

<xsl:output method="html" version="1.0" indent="yes" encoding="ISO-8859-1"/>

<xsl:template match="up:Page">
  <xsl:apply-templates select="Content/NewsML"/>
</xsl:template>

<xsl:template match="NewsML">
  <html>
    <head>
      <title>unipublic - <xsl:value-of select="string($section)"/></title>
      <xsl:call-template name="styles"/>
      <xsl:call-template name="jscript"/>
    </head>

    <body text="black" link="#333399" alink="#CC0000" vlink="#666666" bgcolor="#F5F5F5" background="{$img-unipub}/bg.gif">
      <div align="center">
      <xsl:call-template name="Searchbox"/>

          <table cellspacing="0" cellpadding="0" border="0" width="585" bordercolor="green">

            <tr>
       	      <td width="187"></td>
              <td colspan="2"><a href="../"><img height="13" src="{$img-unipub}/r_{$section}.gif" alt="{$section}" border="0"/></a>
              </td>
            </tr>

            <tr>
              <td valign="top" width="187">

<!--
                <xsl:apply-templates select="NewsItem" mode="RelatedContents"/>
-->
                <xsl:apply-templates select="NewsItem/NewsComponent/ContentItem/DataContent/related-content"/>

              </td>
              <xsl:apply-templates select="NewsItem/NewsComponent/ContentItem/DataContent/nitf"/>
           </tr>

           <!-- Footer -->
	   <tr>
             <td width="187"></td>
             <td width="5" bgcolor="white"></td>
	     <td width="393" bgcolor="white">
               <xsl:call-template name="footer">
                 <xsl:with-param name="footer_date"><xsl:apply-templates select="NewsItem/NewsManagement/RevisionDate" /></xsl:with-param>
               </xsl:call-template>
             </td>
           </tr>
         </table>
       </div>
     </body>
  </html>
</xsl:template>

<xsl:template match="RevisionDate">
  <xsl:value-of select="@day"/>.<xsl:value-of select="@month"/>.<xsl:value-of select="@year"/>
</xsl:template>

</xsl:stylesheet>
