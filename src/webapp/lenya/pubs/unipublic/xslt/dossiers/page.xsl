<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0"
                xmlns:up="http://www.unipublic.unizh.ch/2002/up">

<xsl:output version="1.0" indent="yes" encoding="ISO-8859-1"/>

<xsl:param name="year"/>

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
		<!-- Drawing dynamic sub-navigation for the years 2002 and after (according to tree.xml) -->
		<xsl:for-each select="../tree/branch/branch[@relURI='dossiers']/branch">
			<xsl:choose>
				<xsl:when test="@relURI=$year"><img alt="{@relURI}" src="{$img-unipub}/jahr/{@relURI}_ein.gif" height="13" 	width="39" 	border="0" /></xsl:when>
				<xsl:otherwise><a href="../{@relURI}/"><img alt="{@relURI}" src="{$img-unipub}/jahr/{@relURI}_aus.gif" height="13" 		width="39" border="0" /></a></xsl:otherwise>
			</xsl:choose>
		</xsl:for-each>
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

