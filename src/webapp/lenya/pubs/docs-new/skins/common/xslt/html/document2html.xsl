<?xml version="1.0"?>
<!--
This stylesheet contains the majority of templates for converting documentv11
to HTML.  It renders XML as HTML in this form:

  <div class="content">
   ...
  </div>

..which site2xhtml.xsl then combines with HTML from the index (book2menu.xsl)
and tabs (tab2menu.xsl) to generate the final HTML.

Section handling
  - <a name/> anchors are added if the id attribute is specified

$Id: document2html.xsl,v 1.1 2003/04/02 09:00:25 andreas Exp $
-->
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

  <!-- the skinconf file -->
  <xsl:param name="config-file" select="'../../../../skinconf.xml'"/>
  <xsl:variable name="config" select="document($config-file)/skinconfig"/>
  
  <!-- If true, a PDF link for this page will not be generated -->
  <xsl:variable name="disable-pdf-link" select="$config/disable-pdf-link"/>
  <!-- If true, a "print" link for this page will not be generated -->
  <xsl:variable name="disable-print-link" select="$config/disable-print-link"/>
  <!-- If true, an XML link for this page will not be generated -->
  <xsl:variable name="disable-xml-link" select="$config/disable-xml-link"/>  

    
  <xsl:param name="notoc"/>
  <xsl:param name="path"/>
  <!-- <xsl:include href="split.xsl"/> -->
  <xsl:include href="dotdots.xsl"/>
  <xsl:include href="pathutils.xsl"/>

  <!-- Path to site root, eg '../../' -->
  <xsl:variable name="root">
    <xsl:call-template name="dotdots">
      <xsl:with-param name="path" select="$path"/>
    </xsl:call-template>
  </xsl:variable>

  <xsl:variable name="filename-noext">
    <xsl:call-template name="filename-noext">
      <xsl:with-param name="path" select="$path"/>
    </xsl:call-template>
  </xsl:variable>
 
  <xsl:variable name="skin-img-dir" select="concat(string($root), 'skin/images')"/>

  <xsl:template match="document">
    <div class="content">
      <xsl:if test="normalize-space(header/title)!=''">
        <table summary="" class="title">
          <tr> 
            <td valign="middle"> 
              <h1>
                <xsl:value-of select="header/title"/>
              </h1>
            </td>
            <xsl:call-template name="printlink"/> 
            <xsl:call-template name="pdflink"/>
            <xsl:call-template name="xmllink"/>
          </tr>
        </table>
      </xsl:if>
      <xsl:if test="normalize-space(header/subtitle)!=''">
        <h3>
          <xsl:value-of select="header/subtitle"/>
        </h3>
      </xsl:if>
      <xsl:if test="header/authors">
        <p>
          <font size="-2">
            <xsl:for-each select="header/authors/person">
              <xsl:choose>
                <xsl:when test="position()=1">by&#160;</xsl:when>
                <xsl:otherwise>,&#160;</xsl:otherwise>
              </xsl:choose>
              <xsl:value-of select="@name"/>
            </xsl:for-each>
          </font>
        </p>
      </xsl:if>
      <xsl:apply-templates select="body"/>
    </div>
  </xsl:template>

  <!-- Generates the "printer friendly version" link -->
  <xsl:template name="printlink">
    <xsl:if test="$disable-print-link = 'false'"> 
    
<script Language="Javascript">

function printit(){  
if (window.print) {
    window.print() ;  
} else {
    var WebBrowser = '<OBJECT ID="WebBrowser1" WIDTH="0" HEIGHT="0" CLASSID="CLSID:8856F961-340A-11D0-A96B-00C04FD705A2"></OBJECT>';
document.body.insertAdjacentHTML('beforeEnd', WebBrowser);
    WebBrowser1.ExecWB(6, 2);//Use a 1 vs. a 2 for a prompting dialog box    WebBrowser1.outerHTML = "";  
}
}
</script>
            

<script Language="Javascript">  
var NS = (navigator.appName == "Netscape");
var VERSION = parseInt(navigator.appVersion);
if (VERSION > 3) {
    document.write('<td align="center" width="40" nowrap="nowrap">');     
    document.write('  <a href="javascript:printit()" class="dida">');        
    document.write('    <img border="0" src="{$skin-img-dir}/printer.gif" alt="Print this Page"/><br/>');   
    document.write('  print</a>');   
    document.write('</td>');           
}
</script>

    </xsl:if>
  </xsl:template>

  <!-- Generates the PDF link -->
  <xsl:template name="pdflink">
    <xsl:if test="$disable-pdf-link = 'false'"> 
      <td align="center" width="40" nowrap="nowrap"><a href="{$filename-noext}.pdf" class="dida">
          <img border="0" src="{$skin-img-dir}/pdfdoc.gif" alt="PDF"/><br/>
          PDF</a>
      </td>
    </xsl:if>
  </xsl:template>
  

  <!-- Generates the XML link -->
  <xsl:template name="xmllink">
    <xsl:if test="$disable-xml-link = 'false'">
      <td align="center" width="40" nowrap="nowrap"><a href="{$filename-noext}.xml" class="dida">
          <img border="0" src="{$skin-img-dir}/xmldoc.gif" alt="xml"/><br/>
          xml</a>
      </td>
    </xsl:if>
  </xsl:template>
  
  <xsl:template match="body">
    <xsl:if test="section and not($notoc='true')">
      <ul class="minitoc">
        <xsl:for-each select="section">
          <li>
            <xsl:call-template name="toclink"/>
            <xsl:if test="section">
              <ul class="minitoc">
                <xsl:for-each select="section">
                  <li>
                    <xsl:call-template name="toclink"/>
                  </li>
                </xsl:for-each>
              </ul>
            </xsl:if>
          </li>
        </xsl:for-each>
      </ul>
    </xsl:if>
    <xsl:apply-templates/>
  </xsl:template>


  <!-- Generate a <a name="..."> tag for an @id -->
  <xsl:template match="@id">
    <xsl:if test="normalize-space(.)!=''">
      <a name="{.}"/>
    </xsl:if>
  </xsl:template>

  <xsl:template match="section">
    <!-- count the number of section in the ancestor-or-self axis to compute
         the title element name later on -->
    <xsl:variable name="sectiondepth" select="count(ancestor-or-self::section)"/>
    <a name="{generate-id()}"/>
    <xsl:apply-templates select="@id"/>
    <!-- generate a title element, level 1 -> h3, level 2 -> h4 and so on... -->
    <xsl:element name="{concat('h',$sectiondepth + 2)}">
      <xsl:value-of select="title"/>
      <xsl:if test="$notoc='true' and $sectiondepth = 3">
        <span style="float: right"><a href="#{@id}-menu">^</a></span>
      </xsl:if>
    </xsl:element>

    <!-- Indent FAQ entry text 15 pixels -->
    <xsl:variable name="indent">
      <xsl:choose>
        <xsl:when test="$notoc='true' and $sectiondepth = 3">
          <xsl:text>15</xsl:text>
        </xsl:when>
        <xsl:otherwise>
          <xsl:text>0</xsl:text>
        </xsl:otherwise>
      </xsl:choose>
    </xsl:variable>

    <div style="margin-left: {$indent} ; border: 2px">
          <xsl:apply-templates select="*[not(self::title)]"/>
    </div>
  </xsl:template>

  <xsl:template match="note | warning | fixme">
    <xsl:apply-templates select="@id"/>
    <div class="frame {local-name()}">
      <div class="label">
        <xsl:choose>
          <xsl:when test="local-name() = 'note'">Note</xsl:when>
          <xsl:when test="local-name() = 'warning'">Warning</xsl:when>
          <xsl:otherwise>Fixme (
               <xsl:value-of select="@author"/>

               )</xsl:otherwise>
        </xsl:choose>
      </div>
      <div class="content">
        <xsl:apply-templates/>
      </div>
    </div>
  </xsl:template>

  <xsl:template match="link">
    <xsl:apply-templates select="@id"/>
    <xsl:choose>
      <xsl:when test="starts-with(@href, 'mailto:') and contains(@href, '@')">
        <xsl:variable name="mailto-1" select="substring-before(@href,'@')"/>
        <xsl:variable name="mailto-2" select="substring-after(@href,'@')"/>
          <a href="{$mailto-1}.at.{$mailto-2}">
            <xsl:apply-templates/>
          </a>
       </xsl:when>
       <xsl:otherwise>
          <a href="{@href}">
            <xsl:apply-templates/>
          </a>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>

  <xsl:template match="jump">
    <xsl:apply-templates select="@id"/>
    <a href="{@href}" target="_top">
      <xsl:apply-templates/>
    </a>
  </xsl:template>

  <xsl:template match="fork">
    <xsl:apply-templates select="@id"/>
    <a href="{@href}" target="_blank">
      <xsl:apply-templates/>
    </a>
  </xsl:template>

  <xsl:template match="p[@xml:space='preserve']">
    <xsl:apply-templates select="@id"/>
    <div class="pre">
      <xsl:apply-templates/>
    </div>
  </xsl:template>

  <xsl:template match="source">
    <xsl:apply-templates select="@id"/>
    <pre class="code">
<!-- Temporarily removed long-line-splitter ... gives out-of-memory problems -->
      <xsl:apply-templates/>
<!--
    <xsl:call-template name="format">
    <xsl:with-param select="." name="txt" /> 
     <xsl:with-param name="width">80</xsl:with-param> 
     </xsl:call-template>
-->
    </pre>
  </xsl:template>

  <xsl:template match="anchor">
    <a name="{@id}"/>
  </xsl:template>

  <xsl:template match="icon">
    <xsl:apply-templates select="@id"/>
    <img src="{@src}" alt="{@alt}">
      <xsl:if test="@height">
        <xsl:attribute name="height"><xsl:value-of select="@height"/></xsl:attribute>
      </xsl:if>
      <xsl:if test="@width">
        <xsl:attribute name="width"><xsl:value-of select="@width"/></xsl:attribute>
      </xsl:if>
    </img>
  </xsl:template>

  <xsl:template match="code">
    <xsl:apply-templates select="@id"/>
    <span class="codefrag"><xsl:value-of select="."/></span>
  </xsl:template>

  <xsl:template match="figure">
    <xsl:apply-templates select="@id"/>
    <div align="center">
      <img src="{@src}" alt="{@alt}" class="figure">
        <xsl:if test="@height">
          <xsl:attribute name="height"><xsl:value-of select="@height"/></xsl:attribute>
        </xsl:if>
        <xsl:if test="@width">
          <xsl:attribute name="width"><xsl:value-of select="@width"/></xsl:attribute>
        </xsl:if>
      </img>
    </div>
  </xsl:template>

  <xsl:template match="table">
    <xsl:apply-templates select="@id"/>
    <table cellpadding="4" cellspacing="1" class="ForrestTable">
      <xsl:if test="@cellspacing"><xsl:attribute name="cellspacing"><xsl:value-of select="@cellspacing"/></xsl:attribute></xsl:if>
      <xsl:if test="@cellpadding"><xsl:attribute name="cellpadding"><xsl:value-of select="@cellpadding"/></xsl:attribute></xsl:if>
      <xsl:if test="@border"><xsl:attribute name="border"><xsl:value-of select="@border"/></xsl:attribute></xsl:if>
      <xsl:if test="@class"><xsl:attribute name="class"><xsl:value-of select="@class"/></xsl:attribute></xsl:if>
      <xsl:if test="@bgcolor"><xsl:attribute name="bgcolor"><xsl:value-of select="@bgcolor"/></xsl:attribute></xsl:if>
      <xsl:apply-templates/>
    </table>
  </xsl:template>

  <xsl:template match="acronym/@title">
    <xsl:attribute name="title">
      <xsl:value-of select="normalize-space(.)"/>
    </xsl:attribute>
  </xsl:template>

  <xsl:template name="toclink">
    <a>
      <xsl:attribute name="href">
        <xsl:text>#</xsl:text>
        <xsl:if test="@id">
          <xsl:value-of select="@id"/>
        </xsl:if>
      </xsl:attribute>
      <xsl:value-of select="title"/>
    </a>
  </xsl:template>

  <xsl:template match="node()|@*" priority="-1">
    <xsl:copy>
      <xsl:apply-templates select="@*"/>
      <xsl:apply-templates/>
    </xsl:copy>
  </xsl:template>
</xsl:stylesheet>
