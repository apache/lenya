<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0"
                xmlns:up="http://www.unipublic.unizh.ch/2002/up">

<xsl:output method="html" version="1.0" indent="yes" encoding="ISO-8859-1"/>

<xsl:template match="dossierlist">
  <html>
    <head>
      <title>unipublic - Dossiers</title>
      <xsl:call-template name="styles"/>
      <xsl:call-template name="jscript"/>
    </head>

    <body text="black" link="#333399" alink="#CC0000" vlink="#666666" bgcolor="#F5F5F5" background="{$img-unipub}/bg.gif">
      <div align="center">
      <xsl:call-template name="Searchbox"/>

          <table cellspacing="0" cellpadding="0" border="0" width="585" bordercolor="green">

            <tr>
              <td align="right" width="186"/>
              <td width="399">
                <img height="13" width="39" src="{$img-unipub}/jahr/2002_ein.gif" alt="2002" border="0"/>
                <!-- <img height="13" width="39" src="../../img/jahr/2001_aus.gif" alt="2001" border="0"/> -->
              </td>
            </tr>

            <tr>
	      <!-- Start Left Column  -->
              <td valign="top" align="right" width="186" >
                <img height="28" alt="" src="{$img-unipub}/dossiers/doss_rub_title.gif" width="112" border="0"/>
                <table width="100%" border="0" cellspacing="0" cellpadding="5" bgcolor="#CCCCCC">
                  <tr>
                    <td class="rel-text">Alle Artikel zu einem Thema finden Sie versammelt in unseren Dossiers.</td>
                  </tr>
                </table>
              </td>
	      <!-- End Left Column   -->

	      <td class="art-text" valign="top" bgcolor="white" width="399">

                <xsl:apply-templates select="dossiers" />

	      </td>
           </tr>

           <!-- Footer -->
	   <tr>
             <td width="186"></td>
	     <td width="399" bgcolor="white">
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


</xsl:stylesheet>

