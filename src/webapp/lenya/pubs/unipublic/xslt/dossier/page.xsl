<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0"
                xmlns:up="http://www.unipublic.unizh.ch/2002/up">

<xsl:output method="html" version="1.0" indent="yes" encoding="ISO-8859-1"/>

<xsl:template match="dossier">
  <html>
    <head>
      <title>unipublic - Dossier: <xsl:value-of select="head/title" /></title>
      <xsl:call-template name="styles"/>
      <xsl:call-template name="jscript"/>
    </head>

    <body text="black" link="#333399" alink="#CC0000" vlink="#666666" bgcolor="#F5F5F5" background="{$img-unipub}/bg.gif">
      <div align="center">
      <xsl:call-template name="Searchbox"/>

          <table cellspacing="0" cellpadding="0" border="0" width="585" bordercolor="green">

            <tr>
	      <!-- Start Left Column  -->
              <td valign="top" align="right" width="187" rowspan="2">
                <a href="../"><img height="28" alt="" src="{$img-unipub}/dossiers/doss_rub_title.gif" width="112" border="0"/></a>

                <xsl:apply-templates select="related-content"/>

              </td>
	      <!-- End Left Column   -->

 	      <!-- Draws Dossier title and teaser image  -->
              <xsl:call-template name="dossier_head"/>

           </tr>

	   <!-- Lead -->
           <tr>
             <td valign="top" width="10" bgcolor="white">&#160;</td>
             <td valign="top" width="388" bgcolor="white" class="tsr-text">
               <p class="art-lead">
               <br/>
                <xsl:value-of select="lead" />
               </p>

	       <xsl:apply-templates select="articles" />

             </td>
          </tr>

           <!-- Footer -->
	   <tr>
             <td width="187"></td>
             <td width="5" bgcolor="white"></td>
	     <td width="393" bgcolor="white">
               <xsl:call-template name="footer">
                 <xsl:with-param name="footer_date"></xsl:with-param>
               </xsl:call-template>
             </td>
           </tr>
         </table>
       </div>
     </body>
  </html>
</xsl:template>



<!-- Draws Dossier title and teaser image  -->
<xsl:template name="dossier_head">
 <td valign="top" width="398" bgcolor="{head/color}" colspan="2">
              <table cellspacing="0" cellpadding="0" width="398" bgcolor="{head/color}" border="0" bordercolor="blue">
                <tbody>
                  <tr height="28">
                    <td align="left" width="402" colspan="2" height="28">&#160;&#160;&#160;&#160;&#160;
	              <a href="../"><img height="5" alt="Alle Dossiers" src="{$img-unipub}/dossiers/doss_nav_list.gif" width="94" border="0"/></a>
                    </td>
                  </tr>
                  <tr>
                    <td align="left" width="90">
                      <img height="60" alt="" src="{head/image/@href}" width="80" border="0"/>
                    </td>
                    <td class="dos-title1" valign="top" width="308">Dossier:<br/>
                    <xsl:value-of select="head/title" />
                    </td>
                  </tr>
                </tbody>
              </table>
            </td>
</xsl:template>          

</xsl:stylesheet>

