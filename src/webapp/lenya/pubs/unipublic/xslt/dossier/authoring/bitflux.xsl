<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0"
                xmlns:xhtml="http://www.w3.org/1999/xhtml" >

<xsl:output method="xml" version="1.0" indent="yes" encoding="ISO-8859-1"/>

<xsl:param name="documentid"/>
<xsl:param name="authoring"/>

<!-- Variables apparently not supported by Bitflux
<xsl:variable name="unipublic">/lenya/unipublic</xsl:variable>
<xsl:variable name="img-uni"><xsl:value-of select="string($unipublic)"/>/img_uni</xsl:variable>
<xsl:variable name="img-unipub"><xsl:value-of select="string($unipublic)"/>/img_unipublic</xsl:variable>
-->

<xsl:template match="/">
  <xsl:apply-templates />
</xsl:template>

<xsl:template match="dossier">
  <html>
    <head>
      <!-- <title>unipublic - Dossier: <xsl:value-of select="head/title" /></title>  -->
      <!-- <xsl:call-template name="styles"/> -->
      <!-- <xsl:call-template name="jscript"/> -->
    </head>

    <body text="black" link="#333399" alink="#CC0000" vlink="#666666" bgcolor="#F5F5F5" background="/lenya/unipublic/img_unipublic/bg.gif">
      <center>
      <div align="center">
      <xsl:call-template name="Searchbox"/>

          <table cellspacing="0" cellpadding="0" border="0" width="585" bordercolor="green">

            <tr>
	      <!-- Start Left Column  -->
              <td valign="top" align="right" width="187" rowspan="2">
                <a href="../"><img height="28" alt="" src="/lenya/unipublic/img_unipublic/dossiers/doss_rub_title.gif" width="112" border="0"/></a>
  <table width="180" border="0" cellspacing="0" cellpadding="0">
    <tr valign="top">
      <td width="180">
        <table width="180" border="0" cellspacing="0" cellpadding="0">
          <tr valign="top">
            <td width="180"><img height="19" width="187" src="/lenya/unipublic/img_unipublic/t_teil7.gif" alt="Muscheln1"/></td>
          </tr>
          <tr valign="top">
            <td width="180" valign="middle" bgcolor="#CCCC99">
                <xsl:apply-templates select="related-content"/>
            </td>
          </tr>
          <tr valign="top">
            <td width="180"><img height="27" width="181" src="/lenya/unipublic/img_unipublic/t_teil8.gif" align="right"/></td>
          </tr>
        </table>
     </td>
   </tr>
 </table>



              </td>
	      <!-- End Left Column   -->

 	      <!-- Draws Dossier title and teaser image  -->
	      <!-- <xsl:apply-templates select="dossier/head"/> -->
              <xsl:call-template name="dossier_head"/> 

           </tr>

	   <!-- Lead -->
           <tr>
             <td valign="top" width="10" bgcolor="white">&#160;</td>
             <td valign="top" width="388" bgcolor="white" class="tsr-text">
               <p class="art-lead">
               <br/>
    <abstract contentEditable="true">
      <xsl:for-each select="head/abstract">
        <xsl:apply-templates/>
      </xsl:for-each>
    </abstract>

               </p>

    <table cellpadding="1" border="0" width="100%" bgcolor="#cccccc"><tr><td>
    <table cellpadding="3" border="0" width="100%" bgcolor="white">
      <tr>
        <td class="tsr-text"><b>Dossier-Farbe (Hex-Code)</b></td>
        <td class="tsr-text">
          <color contentEditable="true">
            <xsl:for-each select="head/color">
              <xsl:apply-templates/>
            </xsl:for-each>
          </color>
        </td>
      </tr>
      <tr>
        <td class="tsr-text"><b>Teaser-Text</b></td>
        <td class="tsr-text">
          <teasertext contentEditable="true">
            <xsl:for-each select="head/teasertext">
              <xsl:apply-templates/>
            </xsl:for-each>
          </teasertext>
        </td>
      </tr> 
    </table>
    </td></tr></table>

	       <!-- <xsl:apply-templates select="articles" /> -->

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
     </center>
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
	              <a href="../"><img height="5" alt="Alle Dossiers" src="/lenya/unipublic/img_unipublic/dossiers/doss_nav_list.gif" width="94" border="0"/></a>
                    </td>
                  </tr>
                  <tr>
                    <td align="left" width="90" class="rel-text">
<!--
		      <xsl:if test="$authoring">
    		        <a href="index.html?usecase=uploadimage&amp;step=showteaserscreen&amp;documentid={$documentid}&amp;xpath=/dossiers/head/title">
      		        <xsl:choose>
        		  <xsl:when test="head/media/media-reference">
                            <img height="60" alt="" src="{head/media/media-reference/@source}" width="80" border="0"/>
        		  </xsl:when>
        		  <xsl:otherwise>
         		  <img src="{$context_prefix}/images/wyona/cms/util/reddot.gif" alt="Upload Teaser Image" border="0"/> Upload Teaser Image
        		  </xsl:otherwise>
      		        </xsl:choose>
    			</a>
  		      </xsl:if>
-->
                    </td>
                    <td class="dos-title1" valign="top" width="308">Dossier:<br/>
    <dos_title contentEditable="true">
      <xsl:for-each select="head/dos_title">
        <xsl:apply-templates/>
      </xsl:for-each>
    </dos_title>
                    <!-- <xsl:value-of select="head/title" /> -->
                    </td>
                  </tr>
                </tbody>
              </table>
            </td>
</xsl:template>          


  <xsl:template match="related-content">
    <related-content contentEditable="true">
      <xsl:for-each select=".">
        <xsl:apply-templates/>
      </xsl:for-each>
    </related-content>
  </xsl:template>


<xsl:template name="Searchbox">

<xsl:param name="is-front" />

  <!--BEGINN KOPFTEIL-->
  <form action="http://www.unizh.ch/cgi-bin/unisearch" method="post">
    <input type="hidden" value="www.unipublic.unizh.ch" name="url"/>
    <a id="topofpage" name="topofpage">&#160;</a>
    <table width="585" border="0" cellspacing="0" cellpadding="0">
      <tr>
        <td valign="middle" bgcolor="#999966" width="142">
          <img src="/lenya/unipublic/img_unipublic/spacer.gif" alt=" " width="3" height="20" border="0"/>
          <a href="http://www.unizh.ch/">
            <img src="/lenya/unipublic/img_unipublic/head/home.gif" alt="Home" border="0" height="17" width="31"/>
          </a>
        </td>
        <td colspan="2" align="right" valign="middle" bgcolor="#999966">&#160;<a href="http://www.unipublic.unizh.ch/ssi_unipublic/impressum
.html">
            <img src="/lenya/unipublic/img_unipublic/head/kontakt.gif" alt="Kontakt" border="0" height="17" width="41" align="middle"/>
          </a>
          <img src="/lenya/unipublic/img_unipublic/head/strich.gif" alt="|" height="17" width="7" align="middle"/>
          <img src="/lenya/unipublic/img_unipublic/head/up_suchen.gif" alt="Suchen" height="17" width="79" align="middle"/>
          <input type="text" name="keywords" size="18"/>
          <input src="/lenya/unipublic/img_unipublic/head/go.gif" type="image" border="0" name="search4" align="middle" height="17" width="28"/>
        </td>
        <td valign="middle" bgcolor="#F5F5F5" width="57">&#160;</td>
      </tr>
      <tr>
        <td bgcolor="#666699" width="142">
          <img src="/lenya/unipublic/img_unipublic/spacer.gif" alt=" " width="10" height="39" border="0"/>
        </td>
        <td valign="bottom" bgcolor="#666699" width="96">
          <!-- No link needed on the unipublic logo if we are on the frontpage -->
          <xsl:choose>
            <xsl:when test="$is-front">
              <img src="/lenya/unipublic/img_unipublic/head/uplogo_oben.gif" alt="unipublic" width="96" height="21" border="0"/>
            </xsl:when>
            <xsl:otherwise>
              <a href="{$unipublic}{$view}/"><img src="/lenya/unipublic/img_unipublic/head/uplogo_oben.gif" alt="unipublic" width="96" height="21" border="0"
/></a>
            </xsl:otherwise>
          </xsl:choose>
        </td>
        <td align="right" valign="top" bgcolor="#666699" width="290">
          <a href="http://www.unizh.ch/">
            <img src="/lenya/unipublic/img_unipublic/head/uni_zh.gif" alt="Universit&#228;t Z&#252;rich" width="235" height="29" border="0"/>
          </a>
        </td>
        <td bgcolor="#666699" width="57"/>
      </tr>
      <tr>
        <td width="142"/>
        <td valign="top" width="96">
          <!-- No link needed on the unipublic logo if we are on the frontpage -->
          <xsl:choose>
            <xsl:when test="$is-front">
              <img src="/lenya/unipublic/img_unipublic/head/uplogo_unten.gif" alt="unipublic" width="96" height="29" border="0"/>
            </xsl:when>
            <xsl:otherwise>
              <a href="{$unipublic}{$view}/"><img src="/lenya/unipublic/img_unipublic/head/uplogo_unten.gif" alt="unipublic" width="96" height="29" border="0
"/></a>
            </xsl:otherwise>
          </xsl:choose>
        </td>
        <td width="290"/>
        <td width="57"/>
      </tr>
    </table>
  </form>
  <!--ENDE KOPFTEIL-->


</xsl:template>

<xsl:template name="footer">
  <xsl:param name="footer_date" />
      <div align="left">
                <!--BEGINN FUSSTEIL-->
                        <table width="100%" border="0" cellspacing="0" cellpadding="0">
                                <tr>
                                        <td><br/>
                                                <a href="#topofpage"><img src="/lenya/unipublic/img_unipublic/top.gif" alt="top" width="40" height="13" border="0" /></a></td>
                                </tr>
                                <tr>
                                        <td bgcolor="#666666" height="1"><img src="/lenya/unipublic/img_unipublic/spacer.gif" alt="top" width="40" height="1" border="0" /></td>
                                </tr>
                                <tr>
                                        <td><font size="1">&#169; Universit&#228;t Z&#252;rich,&#160;<!--
                                           --><xsl:if test="$footer_date and not($footer_date = '')"><xsl:value-of select="$footer_date" />,
&#160;</xsl:if><!--
                                           --><a href="http://www.unipublic.unizh.ch/ssi_unipublic/impressum.html">Impressum</a></font>
                                        </td>
                                </tr>
                        </table>
                <!--ENDE FUSSTEIL-->
      </div>
</xsl:template>

<!-- Needed for Bitflux Editor -->
  <xsl:template match="*">
    <xsl:copy>
      <xsl:for-each select="@*">
        <xsl:copy/>
      </xsl:for-each>
      <xsl:apply-templates select="node()"/>
    </xsl:copy>
  </xsl:template>


</xsl:stylesheet>

