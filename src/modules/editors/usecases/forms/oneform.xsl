<?xml version="1.0"?>
<!--
  Copyright 1999-2004 The Apache Software Foundation

  Licensed under the Apache License, Version 2.0 (the "License");
  you may not use this file except in compliance with the License.
  You may obtain a copy of the License at

      http://www.apache.org/licenses/LICENSE-2.0

  Unless required by applicable law or agreed to in writing, software
  distributed under the License is distributed on an "AS IS" BASIS,
  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  See the License for the specific language governing permissions and
  limitations under the License.
-->

<!-- $Id: oneform.xsl 42908 2004-04-26 14:57:25Z michi $ -->

<xsl:stylesheet version="1.0"
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:page="http://apache.org/cocoon/lenya/cms-page/1.0"
  xmlns:i18n="http://apache.org/cocoon/i18n/2.1"
  >

  <xsl:output indent="no" />
  <xsl:param name="docid" />
  <xsl:param name="language" />

  <xsl:include href="copy-mixed-content.xsl" />
  
  <xsl:template match="/">
    <div>
        <div class="lenya-box">
          <div class="lenya-box-title">
            <a href="http://www.w3.org/TR/REC-xml#syntax">Predefined Entities</a>
          </div>
          <div class="lenya-box-body">
            <ul>
              <li>&amp;lt; instead of &lt; (left angle bracket <strong>must</strong> be escaped)</li>
              <li>&amp;amp; instead of &amp; (ampersand <strong>must</strong> be escaped)</li>
              <li>&amp;gt; instead of &gt; (right angle bracket)</li>
              <li>&amp;apos; instead of ' (single-quote)</li>
              <li>&amp;quot; instead of " (double-quote)</li>
            </ul>
          </div>
        </div>
        <div class="lenya-box">
          <div class="lenya-box-body">
    	      <input type="hidden" name="namespaces"><xsl:attribute name="value"><xsl:apply-templates mode="namespaces" /></xsl:attribute></input>
              <table border="0">
                <tr>
                  <td align="right">
                    <input type="submit" value="Save" name="submit" />
                    <input type="submit" value="Cancel" name="cancel" />
                  </td>
                </tr>
                <tr>
                  <td>
                    <textarea name="content" cols="120" rows="80">
                      <xsl:apply-templates mode="mixedcontent" />
                    </textarea>
                  </td>
                </tr>
                <tr>
                  <td align="right">
                    <input type="submit" value="Save" name="submit" />
                    <input type="submit" value="Cancel" name="cancel" />
                  </td>
                </tr>
              </table>
          </div>
        </div>
    </div>
  </xsl:template>
</xsl:stylesheet>
