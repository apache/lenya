<?xml version="1.0" encoding="iso-8859-1"?>

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0"
                xmlns:up="http://www.unipublic.unizh.ch/2002/up">


<xsl:template match="up:Page">
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
        <xsl:call-template name="Searchbox"/>
        <!--ENDE kopf.html-->

        <table border="0" cellpadding="0" cellspacing="0" width="585">
          <tr>
            <td width="135" height="50" valign="top" align="right">
            </td>
            <td width="315" rowspan="2" valign="bottom">
              <table border="0" cellpadding="0" cellspacing="0" width="315">
                <tr>
                  <td valign="bottom" width="19"><img height="9" width="19" src="{$img-unipub}/eck.gif"/></td>
                  <td width="150" align="center" valign="bottom"></td>
                  <td width="108"><img height="63" width="108" src="{$img-unipub}/t_publogo.gif" alt="unipublic"/></td>
                  <td width="38"><img height="1" width="1" src="{$img-unipub}/1.gif"/></td>
                </tr>
              </table>
            </td>
            <td width="135" valign="top" align="left">
            </td>
          </tr>
          <tr>
            <td width="135" align="right" valign="top" rowspan="2">
              <!-- Navigation und Dossier -->
              <xsl:apply-templates select="FirstColumn"/>
            </td>
            <td width="135" align="left" valign="bottom" >
              <img height="21" width="120" src="{$img-unipub}/t_service.gif" alt= "service"/>
            </td>
          </tr>

          <tr>
            <td width="315" valign="top">

              <!-- Artikel in headlines.xsl-->
              <xsl:apply-templates select="MainColumn"/>
            </td>
            <td width="135" valign="top">
              <!--Service, Newsletter und Webperlen-->
              <xsl:apply-templates select="LastColumn"/>

            </td>
          </tr>

          <tr>
            <td width="135"></td>
            <td width="315"></td>
            <td width="135"></td>
          </tr>
          
         <xsl:apply-templates select="MainColumn/Articles" mode="Front_copyright"/> 

      
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
