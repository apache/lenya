<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0"
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:i18n="http://apache.org/cocoon/i18n/2.1"    
    xmlns:lenya-info="http://apache.org/cocoon/lenya/info/1.0"
    xmlns:wf="http://apache.org/cocoon/lenya/workflow/1.0"
    xmlns:rc="http://apache.org/cocoon/lenya/rc/1.0"
    xmlns:dc="http://purl.org/dc/elements/1.1/"
    xmlns="http://www.w3.org/1999/xhtml"
    xmlns:page="http://apache.org/cocoon/lenya/cms-page/1.0"
    xmlns:usecase="http://apache.org/cocoon/lenya/usecase/1.0"
    exclude-result-prefixes="lenya-info wf rc dc usecase page i18n"
    >

<xsl:param name="lenya.usecase" select="'asset'"/>
<xsl:param name="lenya.step"/>
<xsl:param name="error"/>
<xsl:param name="extensions" select="'doc dot rtf txt asc ascii xls xlw xlt ppt pot gif jpg png tif eps pct m3u kar mid smf mp3 swa mpg mpv mp4 mov bin sea hqx sit zip jmx jcl qz jbc jmt cfg pdf'"/>

<xsl:template match="/lenya-info:info">
  <page:page>
    <page:title><i18n:text key="lenya.assetupload.title"/></page:title>
    <page:body>
      <xsl:apply-templates select="lenya-info:assets"/>
    </page:body>
  </page:page>
</xsl:template>


<xsl:template match="lenya-info:assets">
  <xsl:call-template name="pre-body"/>
  <xsl:call-template name="upload-form"/>
  <xsl:call-template name="library-form"/>
</xsl:template>


<!--
Override this template to add scripts etc.
-->
<xsl:template name="pre-body"/>


<xsl:template name="upload-form">
  <div class="lenya-box">
    <div class="lenya-box-title"><i18n:text key="lenya.assetupload.subtitle"/></div>
    <div class="lenya-box-body">
      <form name="fileinput" action="" method="post" enctype="multipart/form-data" onsubmit="return check(fileinput)">
        <input type="hidden" name="lenya.usecase" value="{$lenya.usecase}"/>
        <input type="hidden" name="lenya.step" value="asset-upload"/>
        <input type="hidden" name="task-id" value="insert-asset"/>
        <input type="hidden" name="uploadtype" value="asset"/>
        <input type="hidden" name="properties.asset.date" value="{/lenya-info:info/lenya-info:assets/lenya-info:date}"/>
        <input type="hidden" name="properties.insert.asset.document-id" value="{/lenya-info:info/lenya-info:assets/lenya-info:document-id}"/>
        <input type="hidden" name="properties.insert.asset.language" value="{/lenya-info:info/lenya-info:assets/lenya-info:language}"/>
        <table class="lenya-table-noborder">
          <xsl:if test="$error = 'true'">
            <tr>
              <td colspan="2" class="lenya-form-caption">
                <span class="lenya-form-message-error"><i18n key="filename-format-exception"/></span>
              </td>
            </tr>
          </xsl:if>
          <tr>
            <td class="lenya-form-caption"><i18n:text>Select File</i18n:text>:</td><td><input class="lenya-form-element" type="file" name="properties.asset.data"/><br/>(<i18n:text>No whitespace, no special characters</i18n:text>)</td>
          </tr>
          <tr><td>&#160;</td></tr>
          <tr>
            <td class="lenya-form-caption"><i18n:text>Title</i18n:text>:</td><td><input class="lenya-form-element" type="text" name="properties.asset.title"/></td>
          </tr>
          <tr>
            <td class="lenya-form-caption"><i18n:text>Creator</i18n:text>:</td><td><input class="lenya-form-element" type="text" name="properties.asset.creator" value="{/lenya-info:info/lenya-info:assets/lenya-info:creator}"/></td>
          </tr>
          <tr>
            <td class="lenya-form-caption"><i18n:text>Rights</i18n:text>:</td><td><input class="lenya-form-element" type="text" name="properties.asset.rights" value="All rights reserved."/></td>
          </tr>
          <tr><td>&#160;</td></tr>
          <tr>
            <td/>
            <td>
              <input i18n:attr="value" type="submit" value="Add" />&#160;
              <input i18n:attr="value" type="button" onClick="location.href='javascript:window.close();';" value="Cancel"/>
            </td>
          </tr>
        </table>
      </form>
    </div>
  </div>
</xsl:template>


<xsl:template name="library-form">
  <div class="lenya-box">
    <div class="lenya-box-title"><i18n:text>Asset Library</i18n:text></div>
    <div class="lenya-box-body">
      <form name="assetlibrary" action="">
        <table class="lenya-table-noborder">
          <xsl:if test="not(lenya-info:asset)">
            <tr><td colspan="5" class="lenya-form-caption"><i18n:text>No assets available</i18n:text></td></tr>
          </xsl:if>
          <tr>
            <td class="lenya-form-caption"><i18n:text>Title</i18n:text>:</td>
            <td colspan="4"><input class="lenya-form-element" type="text" name="title" value="{dc:title}"/></td>
          </tr>
          <xsl:for-each select="lenya-info:asset">
            <tr>
              <td/>
              <td>
                <input type="hidden" name="source" value=""/>
                <input type="hidden" name="extent" value=""/>
                <input type="radio" name="asset"
                  onclick="document.forms[&quot;assetlibrary&quot;].title.value = '{dc:title}';
                           document.forms[&quot;assetlibrary&quot;].source.value = '{dc:source}';
                           document.forms[&quot;assetlibrary&quot;].extent.value = '{dc:extent}';"/>
              </td>
              <td><xsl:value-of select="dc:title"/></td>
              <td><xsl:value-of select="dc:extent"/> KB</td>
              <td><xsl:value-of select="dc:date"/></td>
              <!--
              <td><a href="javascript:insertAsset('{dc:source}','{dc:extent}');"><i18n:text>Insert</i18n:text></a></td>
              -->
            </tr>
          </xsl:for-each>
          <tr>
            <td/>
            <td colspan="4">
              <input type="button" value="Insert" i18n:attr="value"
                     onclick="insertAsset(document.forms[&quot;assetlibrary&quot;].source.value,
                                          document.forms[&quot;assetlibrary&quot;].extent.value);"/>
            </td>
          </tr>
        </table>
      </form>
    </div>
  </div>
</xsl:template>

</xsl:stylesheet>