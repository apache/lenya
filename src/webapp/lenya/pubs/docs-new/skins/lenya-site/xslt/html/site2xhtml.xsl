<?xml version="1.0"?>
<!--
site2xhtml.xsl is the final stage in HTML page production.  It merges HTML from
document2html.xsl, tab2menu.xsl and book2menu.xsl, and adds the site header,
footer, searchbar, css etc.  As input, it takes XML of the form:

<site>
  <div class="menu">
    ...
  </div>
  <div class="tab">
    ...
  </div>
  <div class="content">
    ...
  </div>
</site>

$Id: site2xhtml.xsl,v 1.7 2003/05/13 15:48:23 andreas Exp $
-->

<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

  <xsl:import href="../../../common/xslt/html/site2xhtml.xsl"/>

  <xsl:variable name="header-color" select="'#FFFFFF'"/>
  <xsl:variable name="header-color2" select="'#a5b6c6'"/>
  <xsl:variable name="menu-border" select="'#F7F7F7'"/>
  <xsl:variable name="background-bars" select="'#CFDCED'"/>


  <xsl:template match="site">
    <html>
      <head>
        <title><xsl:value-of select="div[@class='content']/table/tr/td/h1"/></title>
        <link rel="stylesheet" href="{$root}skin/page.css" type="text/css"/>
      </head>
      <body bgcolor="#FFFFFF" text="#000000">

        <!-- ================================= top bar with logo's and search box ===================================  -->

        <xsl:comment>================= start Banner ==================</xsl:comment>

        <table cellspacing="0" cellpadding="0" border="0" width="100%" summary="header with logos">
          <tr>

            <xsl:comment>================= start Group Logo ==================</xsl:comment>
            <td bgcolor="{$header-color}" valign="bottom">
              <xsl:if test="$config/group-url">
                <div class="headerlogo">
                <xsl:call-template name="renderlogo">
                  <xsl:with-param name="name" select="$config/group-name"/>
                  <xsl:with-param name="url" select="$config/group-url"/>
                  <xsl:with-param name="logo" select="$config/group-logo"/>
                  <xsl:with-param name="root" select="$root"/>
                </xsl:call-template>
                </div>
                <span class="textheader"><xsl:value-of select="$config/group-name"/></span>
              </xsl:if>
              <xsl:comment>================= start Tabs ==================</xsl:comment>
              <xsl:apply-templates select="div[@class='tab']"/>
              <xsl:comment>================= end Tabs ==================</xsl:comment>
            </td>
            <xsl:comment>================= end Group Logo ==================</xsl:comment>
            
            <xsl:comment>================= start Project Logo ==================</xsl:comment>
            <td bgcolor="{$header-color}" align="right" valign="bottom" width="100%">
             <div class="headerlogo" style="padding: 10px">
              <xsl:call-template name="renderlogo">
                <xsl:with-param name="name" select="$config/project-name"/>
                <xsl:with-param name="url" select="$config/project-url"/>
                <xsl:with-param name="logo" select="$config/project-logo"/>
                <xsl:with-param name="root" select="$root"/>
              </xsl:call-template>
              </div>
              <!-- border for tabs -->
              <div class="tab-separator"/>
            </td>
            <xsl:comment>================= end Project Logo ==================</xsl:comment>

          </tr>
        </table>
        <xsl:comment>================= end Banner ==================</xsl:comment>
        <div class="tab-bar"><img src="{$spacer}" height="5" alt=""/></div>

        <xsl:comment>================= start Menu, NavBar, Content ==================</xsl:comment>
        <table cellspacing="0" cellpadding="0" border="0" width="100%" bgcolor="#ffffff" summary="page content">
          <tr>
            <td valign="top">
              <table cellpadding="0" cellspacing="0" border="0" summary="menu">
                <tr>
                  <xsl:comment>================= start left top NavBar ==================</xsl:comment>
                  <xsl:comment>================= end left top NavBar ==================</xsl:comment>

                  <td valign="top"><div class="tab-subbar"><img src="{$spacer}" alt="" height="5" width="10" /></div></td>

                  <td valign="top">
                  <!--<td bgcolor="{$menu-border}" valign="top" nowrap="nowrap">-->

                    <xsl:comment>================= start Menu items ==================</xsl:comment>
                    <!-- original: <xsl:apply-templates select="div[@class='menu']"/>    -->

                    
                    <div class="menu">
                        <xsl:for-each select = "div[@class='menu']/ul/li">
                          <xsl:call-template name = "innermenuli" />
                        </xsl:for-each>
                    </div>

                    <xsl:comment>================= end Menu items ==================</xsl:comment>
                  </td>
                  <td valign="top"><div class="tab-subbar">&#160;&#160;</div></td>
                </tr>

                <tr>
                  <td/>
                  <td height="5">
                    <img src="{$spacer}" height="1" width="150" alt=""/>
                  </td>
                  <td/>
                </tr>                
        
                
              <xsl:if test="$filename = 'index.html' and $config/credits">
                <xsl:for-each select="$config/credits/credit[not(@role='pdf')]">
                  <xsl:variable name="name" select="name"/>
                  <xsl:variable name="url" select="url"/>
                  <xsl:variable name="image" select="image"/>
                  <xsl:variable name="width" select="width"/>
                  <xsl:variable name="height" select="height"/>
                  <tr>
                    <td height="5"><img src="{$spacer}" alt="" height="5" width="1" /></td>
                  </tr> 
                  <tr> 
                  <td><img src="{$spacer}" alt="" height="1" width="1" /></td>
                  <td colspan="4" height="5" class="logos">
                  <a href="{$url}">
                    <img alt="{$name} logo" border="0">
                      <xsl:attribute name="src">
                        <xsl:if test="not(starts-with($image, 'http://'))"><xsl:value-of select="$root"/></xsl:if>
                        <xsl:value-of select="$image"/>
                      </xsl:attribute>
                      <xsl:if test="$width"><xsl:attribute name="width"><xsl:value-of select="$width"/></xsl:attribute></xsl:if>
                      <xsl:if test="$height"><xsl:attribute name="height"><xsl:value-of select="$height"/></xsl:attribute></xsl:if>
                    </img>
                    <img src="{$spacer}" border="0" alt="" width="5" height="1" />
                  </a>
                  </td>
                  </tr> 
                </xsl:for-each>
              </xsl:if>
              
              </table>
            </td>

            <td width="100%" valign="top">
              <table cellspacing="0" cellpadding="0" border="0" width="100%" summary="content">

                <tr>
                  <td colspan="3" valign="top"><div class="tab-subbar">&#160;&#160;</div></td>
                </tr>
                  
                <xsl:comment>================= start Content==================</xsl:comment>
                <tr>
                  <td width="10" align="left"><img src="{$spacer}" alt="" height="1" width="10" /></td>
                  <td width="100%" align="left">
                    <xsl:apply-templates select="div[@class='content']"/>
                  </td>
                  <td width="10"><img src="{$spacer}" alt="" height="1" width="10" /></td>
                </tr>
                <xsl:comment>================= end Content==================</xsl:comment>

              </table>
            </td>
          </tr>
          <tr>
           <td><!-- using breaks so it scales with font size -->
             <br/><br/>
           </td>
          </tr>
        </table>
        <xsl:comment>================= end Menu, NavBar, Content ==================</xsl:comment>

        <xsl:comment>================= start Footer ==================</xsl:comment>
        <div class="footer">
        <table border="0" width="100%" cellpadding="0" cellspacing="0" summary="footer">
          <tr>
            <td height="1" colspan="2">
              <img src="{$spacer}" alt="" width="1" height="1" />
              <a href="{$skin-img-dir}/label.gif"/>
              <a href="{$skin-img-dir}/page.gif"/>
              <a href="{$skin-img-dir}/chapter.gif"/>
              <a href="{$skin-img-dir}/chapter_open.gif"/>
              <a href="{$skin-img-dir}/current.gif"/>
            </td>
          </tr>
          <tr>
            <xsl:if test="$config/host-logo and not($config/host-logo = '')">
              <div class="host">
                <img src="{$root}skin/images/spacer.gif" width="10" height="1" alt=""/>
                <xsl:call-template name="renderlogo">
                  <xsl:with-param name="name" select="$config/host-name"/>
                  <xsl:with-param name="url" select="$config/host-url"/>
                  <xsl:with-param name="logo" select="$config/host-logo"/>
                  <xsl:with-param name="root" select="$root"/>
                </xsl:call-template>
              </div>
            </xsl:if>
            <td width="90%" align="center" class="copyright" colspan="2">
              <span class="footnote">Copyright &#169;
                <xsl:value-of select="$config/year"/>&#160;<xsl:value-of
                  select="$config/vendor"/> All rights reserved.
                <br/><script language="JavaScript" type="text/javascript"><![CDATA[<!--
                  document.write(" - "+"Last Published: " + document.lastModified);
                  //  -->]]></script></span>
            </td>
            <td class="logos" align="right" nowrap="nowrap">

              <xsl:call-template name="compliancy-logos"/>
              <!-- old place where to put credits icons-->
              <!--
              <xsl:if test="$filename = 'index.html' and $config/credits">
                <xsl:for-each select="$config/credits/credit[not(@role='pdf')]">
                  <xsl:variable name="name" select="name"/>
                  <xsl:variable name="url" select="url"/>
                  <xsl:variable name="image" select="image"/>
                  <xsl:variable name="width" select="width"/>
                  <xsl:variable name="height" select="height"/>
                  <a href="{$url}">
                    <img alt="{$name} logo" border="0">
                      <xsl:attribute name="src">
                        <xsl:if test="not(starts-with($image, 'http://'))"><xsl:value-of select="$root"/></xsl:if>
                        <xsl:value-of select="$image"/>
                      </xsl:attribute>
                      <xsl:if test="$width"><xsl:attribute name="width"><xsl:value-of select="$width"/></xsl:attribute></xsl:if>
                      <xsl:if test="$height"><xsl:attribute name="height"><xsl:value-of select="$height"/></xsl:attribute></xsl:if>
                    </img>
                    <img src="{$spacer}" border="0" alt="" width="5" height="1" />
                  </a>
                </xsl:for-each>
              </xsl:if>
              -->
            </td>
          </tr>
        </table>
        </div>
        <xsl:comment>================= end Footer ==================</xsl:comment>
      </body>
    </html>
  </xsl:template>


  <xsl:template name="innermenuli">
    <div class="menutitle"><xsl:value-of select="font"/></div>
      <div class="menuitemgroup">
        <xsl:for-each select= "ul/li">

          <xsl:choose>
            <xsl:when test="a">
              <div class="menuitem"><a href="{a/@href}"><xsl:value-of select="a" /></a></div>
            </xsl:when>
            <xsl:when test="span/@class='sel'">
              <div class="menupage">
                <div class="menupagetitle"><xsl:value-of select="span" /></div>
                <xsl:if test="//toc/tocc"> 
                  <div class="menupageitemgroup">
                      <xsl:for-each select = "//toc/tocc">
                        <div class="menupageitem">
<!-- ah@wyona.org: not needed because nowrap not used                       
                          <xsl:choose>
                            <xsl:when test="string-length(toca)>15">
                              <a href="{toca/@href}" title="{toca}"><xsl:value-of select="substring(toca,0,20)" />...</a>
                            </xsl:when>
                            <xsl:otherwise>
-->                            
                              <a href="{toca/@href}"><xsl:value-of select="toca" /></a>
<!--                              
                            </xsl:otherwise>
                          </xsl:choose>
-->                          

                          <xsl:if test="toc2/tocc">
                          <!-- nicolaken: this enables double-nested page links-->
                          <!--
                            <ul>
                              <xsl:for-each select = "toc2/tocc">

                                <xsl:choose>
                                  <xsl:when test="string-length(toca)>15">
                                    <li><a href="{toca/@href}" title="{toca}"><xsl:value-of select="substring(toca,0,20)" />...</a></li>
                                  </xsl:when>
                                  <xsl:otherwise>
                                    <li><a href="{toca/@href}"><xsl:value-of select="toca" /></a></li>
                                  </xsl:otherwise>
                                </xsl:choose>

                              </xsl:for-each>
                            </ul> 
                            -->
                          </xsl:if>
                        </div>
                      </xsl:for-each>
                  </div>
                </xsl:if>
              </div>
            </xsl:when>
            <xsl:otherwise>
              <xsl:call-template name = "innermenuli" />
            </xsl:otherwise>
          </xsl:choose>

        </xsl:for-each>
      </div>
  </xsl:template>


  <xsl:template match="toc|toc2|tocc|toca">
  </xsl:template>


  <xsl:template match="node()|@*" priority="-1">
    <xsl:copy>
      <xsl:apply-templates select="@*"/>
      <xsl:apply-templates/>
    </xsl:copy>
  </xsl:template>

</xsl:stylesheet>
