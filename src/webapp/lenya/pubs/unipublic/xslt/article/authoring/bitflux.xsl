<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns="http://www.w3.org/1999/xhtml" version="1.0">
  <xsl:output method="html" encoding="iso-8859-1"/>
  <xsl:template match="/">
    <head>
      <meta name="generator" content="HTML Tidy for Linux/x86 (vers 1st March 2002), see www.w3.org"/>
      <meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1"/>
      <title>Authoring</title>
      <style type="text/css">
/*<![CDATA[*/
<!--
        .awyona {
            color: #0066FF;
            text-decoration: none;
        }

        .awyona:visited {
            
            color: #0066FF;
            text-decoration: none;
        }
      -->
/*]]>*/
</style>
      <meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1"/>
      <title>unipublic -</title>
      <link href="http://212.71.98.102:9999/wyona-cms/unipublic/unipublic.css" rel="stylesheet" type="text/css"/>
      <link href="http://212.71.98.102:9999/wyona-cms/unipublic/unipublic.mac.css" type="text/css" rel="stylesheet"/>
      <base href="http://212.71.98.102:9999/"/>
    </head>
    <body background="http://212.71.98.102:9999/wyona-cms/unipublic/img_unipublic/bg.gif" bgcolor="#FFFFFF" vlink="#666666" alink="#993300" link="#333399" text="#333333">
      <center>
        <form method="post" action="http://www.unizh.ch/cgi-bin/unisearch">
          <input name="url" value="www.unipublic.unizh.ch" type="hidden"/>
          <a id="topofpage" name="topofpage"> </a>
          <table bgcolor="#666699" cellpadding="0" cellspacing="0" border="0" width="585">
            <tr>
              <td align="left" valign="middle" bgcolor="#999966">
                <img alt=" " src="http://212.71.98.102:9999/wyona-cms/unipublic/img_uni/1.gif" width="3" height="20"/>
                <a href="http://www.unizh.ch/index.html">
                  <img width="31" height="17" border="0" alt="Home" src="http://212.71.98.102:9999/wyona-cms/unipublic/img_uni/oliv_home.gif"/>
                </a>
              </td>
              <td width="1" bgcolor="#999966"> </td>
              <td align="right" valign="middle" bgcolor="#999966">
                <a href="http://www.unipublic.unizh.ch/ssi_unipublic/impressum.html">
                  <img align="middle" width="41" height="17" border="0" alt="Kontakt" src="http://212.71.98.102:9999/wyona-cms/unipublic/img_uni/oliv_kontakt.gif"/>
                </a>
                <img align="middle" width="7" height="17" alt="|" src="http://212.71.98.102:9999/wyona-cms/unipublic/img_uni/oliv_strich.gif"/>
                <img align="middle" width="37" height="17" alt="Suchen" src="http://212.71.98.102:9999/wyona-cms/unipublic/img_uni/oliv_suchen.gif"/>
                <input size="18" name="keywords" type="text"/>
                <input align="middle" name="search" border="0" type="image" src="http://212.71.98.102:9999/wyona-cms/unipublic/img_uni/oliv_go.gif"/>
              </td>
              <td width="57" bgcolor="#F5F5F5"> </td>
            </tr>
            <tr height="39">
              <td height="39" align="right"> </td>
              <td colspan="2" valign="top" height="39" align="right">
                <a href="http://www.unizh.ch/index.html">
                  <img border="0" alt="Universität Zürich" src="http://212.71.98.102:9999/wyona-cms/unipublic/img_uni/unilogoklein.gif" width="235" height="29"/>
                </a>
              </td>
              <td height="39" width="57"> </td>
            </tr>
          </table>
        </form>
      </center>
      <center>
        <table width="585" border="0" cellpadding="0" cellspacing="0">
          <tr height="16">
            <td valign="top" align="center" width="187" height="16">
              <center>
                <a href="../../../../">
                  <img border="0" alt="Unipublic" src="http://212.71.98.102:9999/wyona-cms/unipublic/img_unipublic/t_unipublic_ALT.gif" width="108" height="52"/>
                </a>
              </center>
            </td>
            <td width="10" align="right" height="16">
          </td>
            <td height="16" width="388">
          </td>
          </tr>
          <tr>
            <td width="187" align="right">
          </td>
            <td width="10">
          </td>
            <td width="388">
              <a href="../">
                <img border="0" alt="gesundheit" src="http://212.71.98.102:9999/wyona-cms/unipublic/img_unipublic/r_gesundheit.gif" width="80" height="13"/>
              </a>
            </td>
          </tr>
          <tr>
            <td width="187" valign="top">
              <table cellpadding="0" cellspacing="0" border="0" width="180">
                <tr valign="top">
                  <td width="180">
                    <table cellpadding="0" cellspacing="0" border="0" width="180">
                      <tr valign="top">
                        <td width="180">
                          <img alt="Muscheln1" src="http://212.71.98.102:9999/wyona-cms/unipublic/img_unipublic/t_teil7.gif" width="187" height="19"/>
                        </td>
                      </tr>
                      <tr valign="top">
                        <td bgcolor="#CCCC99" valign="middle" width="180">
                          <xsl:apply-templates select="/newsml/newsitem/relatedcontent"/>
                        </td>
                      </tr>
                      <tr valign="top">
                        <td width="180">
                          <img align="right" src="http://212.71.98.102:9999/wyona-cms/unipublic/img_unipublic/t_teil8.gif" width="181" height="27"/>
                        </td>
                      </tr>
                    </table>
                  </td>
                </tr>
              </table>
            </td>
            <td valign="top" bgcolor="white" width="10">
              

            <p> </p>
          </td>
            <td xsd_xopus="article.xsd" xsl_xopus="Page/Article/Authoring/xopus.xsl" xml_xopus="magazin/gesundheit/articles/2002/0508/forum.xml" id_xopus="body" class="art-text" width="388" bgcolor="white" valign="top">
              <dossier class="art-pretitle" contentEditable="true">
                <xsl:for-each select="/newsml/newsitem/newscomponent/contentitem/datacontent/nitf/head/hedline/dossier">
                  <xsl:apply-templates/>
                </xsl:for-each>
              </dossier>
              <xsl:apply-templates select="/newsml/newsitem/newscomponent/contentitem/datacontent/nitf/body"/>

            </td>
          </tr>
          <tr>
            <td width="187">
          </td>
            <td bgcolor="white" width="10"> </td>
            <td width="388" bgcolor="white">
              <br/>
              <div align="left">
                <a href="#topofpage">
                  <font size="1">zum Anfang<br/>
              <br/>
              </font>
                </a>
                <img alt=" " src="http://212.71.98.102:9999/wyona-cms/unipublic/img_unipublic/999999.gif" width="390" height="1"/>
                <br/>
                <font size="1">©Universität Zürich, , <a href="http://www.unipublic.unizh.ch/ssi_unipublic/impressum.html">Impressum</a></font>
              </div>
            </td>
          </tr>
        </table>
      </center>
    </body>
  </xsl:template>
  <xsl:template match="relatedcontent">
    <RelatedContent contentEditable="true">
      <xsl:for-each select=".">
        <xsl:apply-templates/>
      </xsl:for-each>
    </RelatedContent>
  </xsl:template>
  <xsl:template match="body">
    <hl1 xmlns="http://www.w3.org/1999/xhtml" contentEditable="true">
      <xsl:for-each select="body.head/hedline/hl1">
        <xsl:apply-templates/>
      </xsl:for-each>
    </hl1>
    <abstract contentEditable="true">
      <xsl:for-each select="body.head/abstract">
        <xsl:apply-templates/>
      </xsl:for-each>
    </abstract>
    <byline class="art-author" contentEditable="true">
      <xsl:for-each select="body.head/byline">
        <xsl:apply-templates/>
      </xsl:for-each>
    </byline>
    <article contentEditable="true">
      <xsl:for-each select="body.content">
        <xsl:apply-templates/>
      </xsl:for-each>
    </article>
    <tagline contentEditable="true">
      <xsl:for-each select="body.end/tagline">
        <xsl:apply-templates/>
      </xsl:for-each>
    </tagline>
  </xsl:template>

  <xsl:template match="*">
    <xsl:copy>
      <xsl:for-each select="@*">
        <xsl:copy/>
      </xsl:for-each>
      <xsl:apply-templates select="node()"/>
    </xsl:copy>
  </xsl:template>
</xsl:stylesheet>
