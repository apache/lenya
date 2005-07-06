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

<xsl:stylesheet version="1.0"
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns="http://www.w3.org/1999/xhtml"
    xmlns:xhtml="http://www.w3.org/1999/xhtml"
    xmlns:i18n="http://apache.org/cocoon/i18n/2.1"    
    >

  <xsl:template name="wait_script">
    <script src="{$contextprefix}/lenya/javascript/wait.js" type="text/javascript">
    <xsl:comment>  
    </xsl:comment>
    </script>
  </xsl:template>
   
  <xsl:template name="wait_screen">
    <div id="pleasewaitScreen" style="position:absolute;z-index:5;top:30%;left:42%;visibility:hidden">
      <table bgcolor="#CCCCCC" border="1" bordercolor="#CCCCCC" cellpadding="0" cellspacing="0" height="100" width="200">
        <tr>
          <td width="100%" height="100%" bgcolor="#E5F5F8" align="center" valign="middle">
            <b><i18n:text>please_wait</i18n:text></b> <br/> <br/>
            <img src="{$contextprefix}/lenya/images/wait.gif" alt="Please wait"/>
          </td>
        </tr>
      </table>
    </div>
  </xsl:template>

</xsl:stylesheet> 
