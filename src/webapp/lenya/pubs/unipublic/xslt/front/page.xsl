<?xml version="1.0" encoding="iso-8859-1"?>

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0"
                xmlns:up="http://www.unipublic.unizh.ch/2002/up">

<xsl:include href="../navigation.xsl"/>

<!--
<xsl:template match="up:Page">
-->
<xsl:template match="Page">
  <xsl:apply-templates select="Content"/>
</xsl:template>

<xsl:template match="Content">
  <html>
    <head>
     <title>unipublic - Das Online-Magazin der Universit&#228;t Z&#252;rich</title>

     <script type="text/javascript" language="JavaScript1.2">
       <xsl:comment>
         function aboWindow() {
           newWind = open("newsletter.html","Display", "toolbar='no', statusbar='no', height='280', width='225'" );
         }
       </xsl:comment>        
     </script>

     <xsl:call-template name="styles"/>

     <xsl:call-template name="jscript"/>

     <style type="text/css">
       <xsl:comment>
        .tsr-title { font-weight: bold; font-size: 10px; font-family: Geneva, Verdana, Helvetica, Arial, Swiss, "Nu Sans Regular"; text-transform: uppercase }
        .tsr-text { font-size: 10px; font-family: Geneva, Verdana, Helvetica, Arial, Swiss, "Nu Sans Regular" }
        .webperlen { font-size: 10px; font-family: Geneva, Verdana, Helvetica, Arial, Swiss, "Nu Sans Regular" }
        .top-title { font-size: 18px; font-family: Geneva, Verdana, Helvetica, Arial, Swiss, "Nu Sans Regular"; text-transform: uppercase }
       </xsl:comment>
     </style>
    </head>	

    <body text="#333333" link="#333399" alink="#993300" vlink="#666666" bgcolor="#FFFFFF" background="{$img-unipub}/bg.gif">
      <center>

        <!--START kopf.html-->
        <xsl:call-template name="Searchbox">
	  <xsl:with-param name="is-front">true</xsl:with-param>
	</xsl:call-template>
        <!--ENDE kopf.html-->

        <table border="0" cellpadding="0" cellspacing="0" width="585" bordercolor="red">
          <tr>
            <td width="135" align="right" valign="top" >
              <!-- Navigation und Dossier -->
              <xsl:apply-templates select="FirstColumn"/>
            </td>
            <td width="315" valign="top">

              <!-- Artikel in headlines.xsl-->
              <img height="9" width="19" src="{$img-unipub}/eck.gif"/><img src="{$img-unipub}/spacer.gif" height="21" alt=" " />
              <xsl:apply-templates select="MainColumn"/>
            </td>
            <td width="135" valign="top">
              <!--Service, Newsletter und Webperlen-->
	      <img height="21" width="120" src="{$img-unipub}/t_service.gif" alt= "service"/>
              <xsl:apply-templates select="LastColumn"/>

            </td>
          </tr>

          <tr>
            <td width="135"></td>
            <td colspan="2">
	      <!-- Footer -->
              <xsl:call-template name="footer">
                <xsl:with-param name="footer_date" select="/wyona/cmsbody/Page/Content/MainColumn/Articles/Article[1]/body.head/dateline/story.date/@norm" />
              </xsl:call-template>

            </td>
          </tr>
          
      
        </table>
      </center>
    </body>
  </html>
</xsl:template>

<xsl:template match="LastColumn">
  <xsl:apply-templates select="Services"/>
  <xsl:apply-templates select="Webperls"/>
</xsl:template>

<xsl:template match="FirstColumn">
  <xsl:apply-templates select="MainNavigation"/>
</xsl:template>

</xsl:stylesheet>
