<?xml version="1.0" encoding="UTF-8"?>
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

<!-- $Id: asset.xsl,v 1.13 2004/08/23 16:30:47 andreas Exp $ -->

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
    
<xsl:import href="../authoring/asset-upload.xsl"/>


<xsl:template name="pre-body">
  <script>
    function insertAsset(src, size) {
    
      var title = document.forms["assetlibrary"].title.value;
      <![CDATA[
      window.opener.bxe_insertContent('<asset xmlns="http://apache.org/cocoon/lenya/page-envelope/1.0" src="'+src+'" size="'+size+'" type="">'+title+'</asset>',window.opener.BXE_SELECTION,window.opener.BXE_SPLIT_IF_INLINE);
      ]]>
      window.close();
    }
    
    function check(fileinput) {
      var i = 0;
      var ext = '<xsl:value-of select="$extensions"/>';
      var delimiter = ' ';
      var thefile = fileinput["properties.asset.data"].value;
      var _tempArray = new Array();
      _tempArray = ext.split(delimiter);
      for(i in _tempArray)
      {
        if(thefile.indexOf('.' + _tempArray[i]) != -1)
        {
         // file has one of the accepted extensions.
         return true;
         }
      }
       // file does not have one of the accepted extensions.
       alert("<i18n:translate><i18n:text key="upload-with-invalid-extension"/><i18n:param>:\n\n<xsl:value-of select="$extensions"/></i18n:param></i18n:translate>");
       return false;
    }
  </script>
</xsl:template>

</xsl:stylesheet>  
