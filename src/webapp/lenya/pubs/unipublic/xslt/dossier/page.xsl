<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0"
                xmlns:up="http://www.unipublic.unizh.ch/2002/up">

<xsl:output version="1.0" indent="yes" encoding="ISO-8859-1"/>

<xsl:param name="documentid"/>
<xsl:param name="authoring"/>

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
                <xsl:value-of select="head/abstract" />
               </p>

	       <xsl:apply-templates select="articles" />

	  <!-- Display of Color and Teasertext -->
	  <xsl:if test="$authoring">
	    <table cellpadding="1" border="0" width="100%" bgcolor="#cccccc"><tr><td>
	    <table cellpadding="3" border="0" width="100%" bgcolor="white">
	      <tr>
	        <td class="tsr-text"><b>Dossier-Farbe (Hex-Code)</b></td>
	        <td class="tsr-text"><xsl:value-of select="head/color" /></td>
	      </tr>
	      <tr>
	        <td class="tsr-text"><b>Teaser-Text</b></td>
	        <td class="tsr-text"><xsl:value-of select="head/teasertext"/></td>
	      </tr>
	    </table>
	    </td></tr></table>
	  </xsl:if>

             </td>
          </tr>

           <!-- Footer -->
	   <tr>
             <td width="187"></td>
             <td width="5" bgcolor="white"></td>
	     <td width="393" bgcolor="white">
               <xsl:call-template name="footer">
	         <xsl:with-param name="footer_date" select="articles/article[1]/body.head/dateline/story.date/@norm" />
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
                    <td align="left" width="90" class="rel-text">
		     <xsl:choose>
		      <xsl:when test="$authoring">
    		        <a href="index.html?lenya.usecase=uploadimage&amp;lenya.step=showteaserscreen&amp;documentid={$documentid}&amp;xpath=/dossier/head/title">
      		        <xsl:choose>
        		  <xsl:when test="head/media/media-reference">
                            <img height="60" alt="" src="{head/media/media-reference/@source}" width="80" border="0"/>
        		  </xsl:when>
        		  <xsl:otherwise>
         		  <img src="/lenya/lenya/images/util/reddot.gif" alt="Upload Teaser Image" border="0"/> Upload Teaser Image
        		  </xsl:otherwise>
      		        </xsl:choose>
    			</a>
		      </xsl:when>
		      <xsl:otherwise>
			<xsl:if test="head/media/media-reference">
                            <img height="60" alt="" src="{head/media/media-reference/@source}" width="80" border="0"/>
                        </xsl:if>
		      </xsl:otherwise>
  		     </xsl:choose>
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

