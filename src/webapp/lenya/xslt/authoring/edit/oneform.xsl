<?xml version="1.0"?>
<xsl:stylesheet version="1.0"
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:page="http://apache.org/cocoon/lenya/cms-page/1.0"
  xmlns:i18n="http://apache.org/cocoon/i18n/2.1"
  >

  <xsl:output indent="no" />
  <xsl:param name="docid" />
  <xsl:param name="language" />
  <xsl:param name="message" />
  
  <xsl:template match="/">
    <page:page>
      <page:title>Edit Document</page:title>
      <page:body>

	<div class="lenya-box">
	  <div class="lenya-box-title">Information</div>
	  <div class="lenya-box-body">
	    
	    <table class="lenya-table-noborder">
	      <tr>
		<td class="lenya-entry-caption"><i18n:text>Document ID</i18n:text>:</td>
		<td><xsl:value-of select="$docid"/></td>
	      </tr>
	      <tr>
		<td class="lenya-entry-caption"><i18n:text>Language</i18n:text>:</td>
		<td><xsl:value-of select="$language"/></td>
	      </tr>

	      <xsl:if test="$message">
		<tr>
		  <td valign="top" class="lenya-entry-caption">
		    <span class="lenya-error"><i18n:text>Message</i18n:text>:</span>
		  </td>
		  <td>
		    <font color="red">
		      <xsl:value-of select="$message" />
		    </font>
		    <br /><br />
		    (Check log files for more details: lenya/WEB-INF/logs/*)</td>
		</tr>
	      </xsl:if>
	    </table>
	    
	  </div>
	</div>
	
	
        <div class="lenya-box">
          <div class="lenya-box-title">
            <a href="http://www.w3.org/TR/REC-xml#syntax">Predefined Entities</a>
          </div>
          <div class="lenya-box-body">
            <ul>
              <li>&amp;lt; instead of &lt; (left angle bracket <b>must</b> be escaped)</li>
              <li>&amp;amp; instead of &amp; (ampersand <b>must</b> be escaped)</li>
              <li>&amp;gt; instead of &gt; (right angle bracket)</li>
              <li>&amp;apos; instead of ' (single-quote)</li>
              <li>&amp;quot; instead of " (double-quote)</li>
            </ul>
          </div>
        </div>
        <div class="lenya-box">
          <div class="lenya-box-body">
            <form method="post"
	      action="?lenya.usecase=1formedit&amp;lenya.step=close">
              <table border="0">
                <tr>
                  <td align="right">
                    <input type="submit" value="Save" name="save" />
                    <input type="submit" value="Cancel" name="cancel" />
                  </td>
                </tr>
                <tr>
                  <td>
                    <textarea name="content" cols="80" rows="80">
                      <xsl:apply-templates mode="mixedcontent" />
                    </textarea>
                  </td>
                </tr>
                <tr>
                  <td align="right">
                    <input type="submit" value="Save" name="save" />
                    <input type="submit" value="Cancel" name="cancel" />
                  </td>
                </tr>
              </table>
            </form>
          </div>
        </div>
      </page:body>
    </page:page>
  </xsl:template>
  <xsl:include href="copy-mixed-content.xsl" />
</xsl:stylesheet>