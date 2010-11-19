<xsl:stylesheet version="1.0"
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns="http://www.w3.org/1999/xhtml"
  xmlns:xhtml="http://www.w3.org/1999/xhtml"
  xmlns:i18n="http://apache.org/cocoon/i18n/2.1"
  exclude-result-prefixes="#default i18n"
>

<!--
  this stylesheet inserts the tinymce javascript code into the
  rendered page after the last header tag, and puts a form and 
  textarea into the div#body tag.
-->

<xsl:param name="proxyPath" select="'tinymce.ERROR'"/>
<xsl:param name="continuationId" select="'tinymce.ERROR'"/>
<xsl:param name="usecaseName" select="'tinymce.ERROR'"/>
<xsl:param name="publicationId" select="'tinymce.ERROR'" />
<xsl:param name="locale" select="'tinymce.ERROR'"/>
<xsl:param name="content" select="''"/>


<!--
  check sitemap parameters.
  the <tinymceWrapper/> is the document element set by the aggregator.
-->
<xsl:template match="tinymceWrapper">
  <xsl:if test="$proxyPath='tinymce.ERROR'">
    <xsl:message terminate="yes">
      <i18n:text>Missing proxyPath parameter!</i18n:text>
      <i18n:text>Check your tinymce sitemap.</i18n:text>
    </xsl:message>
  </xsl:if>
  <xsl:if test="$continuationId='tinymce.ERROR'">
    <xsl:message terminate="yes">
      <i18n:text>Missing continuationId parameter!</i18n:text>
      <i18n:text>Check your tinymce sitemap.</i18n:text>
    </xsl:message>
  </xsl:if>
  <xsl:if test="$usecaseName='tinymce.ERROR'">
    <xsl:message terminate="yes">
      <i18n:text>Missing usecaseName parameter!</i18n:text>
      <i18n:text>Check your tinymce sitemap.</i18n:text>
    </xsl:message>
  </xsl:if>
  <xsl:if test="$publicationId='tinymce.ERROR'">
    <xsl:message terminate="yes">
      <i18n:text>Missing publicationId parameter!</i18n:text>
      <i18n:text>Check your tinymce sitemap.</i18n:text>
    </xsl:message>
  </xsl:if>
  <xsl:if test="$locale='tinymce.ERROR'">
    <xsl:message terminate="yes">
      <i18n:text>Missing locale parameter!</i18n:text>
      <i18n:text>Check your tinymce sitemap.</i18n:text>
    </xsl:message>
  </xsl:if>

  <xsl:apply-templates select="xhtml:html"/>
</xsl:template>


<!-- 
  add tinymce code and configuration to the header.
  FIXME: can we get proper whitespace in the output
  without making the template so verbose?
  FIXME: modularize config some more so users can fallback-override more easily.
 -->
<xsl:template match="xhtml:html/xhtml:head/xhtml:*[last()]">
  <xsl:call-template name="identity"/>
  <xsl:text>
  </xsl:text>
  <xsl:comment>special code for tinymce.edit usecase view</xsl:comment>
  <xsl:text>
  </xsl:text>
  <xsl:comment>without the space between opening and closing script tag, Firefox tends to interpret the whole page as a comment or something :(</xsl:comment>
  <xsl:text>
  </xsl:text>
  <xsl:comment>the tinymce code:</xsl:comment>
  <xsl:text>
  </xsl:text>
  <script language="javascript" 
          type="text/javascript" 
          src="{$proxyPath}/{$publicationId}/modules/tinymce/tinymce/jscripts/tiny_mce/tiny_mce.js"
  >&#160;</script>
  <xsl:text>
  </xsl:text>
  <xsl:comment>custom event handlers for lenya:</xsl:comment>
  <xsl:text>
  </xsl:text>
  <xsl:comment>the main configuration of tinymce:</xsl:comment>
  <xsl:text>
  </xsl:text>
  <script language="javascript" 
          type="text/javascript"
          src="{$proxyPath}/{$publicationId}/modules/tinymce/javascript/tiny_config.js"
  >&#160;</script>
  <xsl:text>
  </xsl:text>
  <xsl:comment>allowed XHTML elements in tinymce:</xsl:comment>
  <xsl:text>
  </xsl:text>
  <script language="javascript" 
          type="text/javascript"
          src="{$proxyPath}/{$publicationId}/modules/tinymce/javascript/tiny_valid_elements.js"
  >&#160;</script>
  <xsl:text>
  </xsl:text>
  <script language="javascript" 
          type="text/javascript" 
          src="{$proxyPath}/{$publicationId}/modules/editors/javascript/org.apache.lenya.editors.js"
  >&#160;</script>
  <xsl:text>
  </xsl:text>
  <xsl:comment>catch backspace key - it maps to "back in history" in some popular 
  browsers under windows, which can confuse users and lead to data loss</xsl:comment>
  <xsl:text>
  </xsl:text>
  <script language="javascript" 
          type="text/javascript" 
          src="{$proxyPath}/{$publicationId}/modules/editors/javascript/disablebackspace.js"
  >&#160;</script>
  <xsl:text>
  </xsl:text>
  <script language="javascript" 
          type="text/javascript" 
          src="{$proxyPath}/{$publicationId}/modules/tinymce/javascript/tiny_lenya_glue.js"
  >&#160;</script>
  <xsl:text>
  </xsl:text>
  <script language="javascript" type="text/javascript">
  <xsl:text>

/* 
   include valid elements from tiny_valid_elements.js (extra file so that you can override it per-doctype)
   FIXME: not yet done, this needs to be resolved via a special pipeline!

 */

   config['valid_elements'] = lenya_valid_elements;


/* some dynamic configuration that depends on pipeline information */

    /* pass the locale of the current request to TinyMCE, so that it is properly localized */

    config['language'] = "</xsl:text>
    <xsl:value-of select="$locale"/>
    <xsl:text>";

    /* pass all the stylesheets of the current page (except for those specific
       to the Lenya authoring GUI) to TinyMCE for true WYSIWYG editing */

    config['content_css'] = "</xsl:text>
    <xsl:value-of select="$proxyPath"/>
    <xsl:text>/modules/tinymce/css/editor_content.css</xsl:text>
    <xsl:for-each select="../xhtml:link[@rel='stylesheet' and not(contains(@href, '/lenya/css/'))]">
        <xsl:text>,</xsl:text>
        <xsl:value-of select="@href"/>
    </xsl:for-each>

    <xsl:text>";

    tinyMCE.init(config);

    </xsl:text>

    </script>
</xsl:template>

<!--
  insert lenya cms default.css before the first header element, so that 
  publication-specific styles will override it. (used to format the usecase
 messages.)
-->
<xsl:template match="xhtml:html/xhtml:head/xhtml:*[1]">
  <link rel="stylesheet" type="text/css" href="/modules/gui/css/default.css"/>
  <xsl:call-template name="identity"/>
</xsl:template>

<!-- 
  a textarea is inserted into the <div id="body"/> element. 
  it will become the editor instance. the contents of <div id="body"/>
  are then copied into the textarea.
  before the textarea, we insert any usecase info and error messages.
-->
<xsl:template match="xhtml:html/xhtml:body//xhtml:div[@id='body']">
<!-- copy the div#body and all attributes that might be in it -->
<xsl:copy>
  <xsl:for-each select="@*">
    <xsl:copy/>
  </xsl:for-each>
    <!-- insert usecase messages, if any -->
    <xsl:apply-templates select="/tinymceWrapper/xhtml:div[@class='lenyaInfoBox']"/>
<!--    <xsl:if test="not(/tinymceWrapper/xhtml:div[@class = 'lenyaInfoBox']/*)">-->
    <!-- insert tinymce form hook -->
      <xsl:comment>special code for the tinymce.edit usecase view</xsl:comment>
      <xsl:text>
      </xsl:text>
      <form method="post" enctype="multipart/form-data">
          <xsl:text>
          </xsl:text>
          <input type="hidden" name="lenya.continuation" value="{$continuationId}"/>
          <xsl:text>
          </xsl:text>
          <input type="hidden" name="lenya.usecase" value="{$usecaseName}"/>		
          <xsl:text>
          </xsl:text>
          <input type="hidden" name="tinymce.namespaces" value=""/>
          <xsl:text>
          </xsl:text>
          <textarea id="tinymce.content" name="tinymce.content" style="width:100%">
            <xsl:choose>
              <!-- firefox bug workaround: prevent <textarea/> from collapsing if empty -->
              <xsl:when test="$content!='' and /tinymceWrapper/xhtml:div[@class = 'lenyaInfoBox']/*">
                <xsl:value-of select="$content" />
              </xsl:when>
              <xsl:when test=".//*">
                <xsl:apply-templates/>
              </xsl:when>
              <xsl:otherwise>
                <xsl:text>&#160;</xsl:text>
              </xsl:otherwise>
            </xsl:choose>
          </textarea>
          <xsl:text>
          </xsl:text>
          <input i18n:attr="value" type="submit" name="submit" value="Save" onclick="tinymce.DOM.win.onbeforeunload = null;"/>        
          <xsl:text>
          </xsl:text>
          <input i18n:attr="value" type="submit" name="cancel" value="Cancel" onclick="tinymce.DOM.win.onbeforeunload = null;"/>      
          <xsl:text>
      </xsl:text>
      </form>
<!--    </xsl:if>-->
    <xsl:text>
    </xsl:text>
</xsl:copy>
</xsl:template>

<xsl:template match="/tinymceWrapper/xhtml:div[@class='lenyaInfoBox']">
  <xsl:copy>
    <xsl:for-each select="@*">
      <xsl:copy/>
    </xsl:for-each>
    <div>
       <p><strong>Usecase messages for "<xsl:value-of select="$usecaseName"/>":</strong></p>
       <xsl:apply-templates/>
    </div>
  </xsl:copy>
</xsl:template>


  <xsl:template match="xhtml:textarea">
    <xsl:text>&lt;textarea</xsl:text>
     <xsl:for-each select="@*">
       <xsl:text> </xsl:text>
       <xsl:value-of select="name()"/>
       <xsl:text>="</xsl:text>
       <xsl:value-of select="."/>
       <xsl:text>"</xsl:text>
     </xsl:for-each>
    <xsl:text>&gt;</xsl:text>
    <xsl:value-of select="."/>
    <xsl:text>&lt;/textarea&gt;</xsl:text>
  </xsl:template>
  
<xsl:template match="@*|node()" name="identity">
  <xsl:copy>
    <xsl:apply-templates select="@*|node()"/>
  </xsl:copy>
</xsl:template>


</xsl:stylesheet>
