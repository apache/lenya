<?xml version="1.0" encoding="iso-8859-1"?>
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

<!-- $Id: not-published-yet.xsl,v 1.2 2004/03/13 12:42:12 gregor Exp $ -->

<xsl:stylesheet version="1.0"
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:page="http://apache.org/cocoon/lenya/cms-page/1.0"
  >
  
  <xsl:import href="../util/page-util.xsl"/>
  
  <xsl:output version="1.0" indent="yes" encoding="ISO-8859-1"/>
  
  <xsl:template match="/">
    
    <page:page>
      <page:title>Page not published yet</page:title>
      <page:body>
	<div class="lenya-box">
	  <div class="lenya-box-title">Page not published yet</div>
	  <div class="lenya-box-body">
	    <p>
	      An error occured. Most likely you are trying access a
	      page which has not been published yet.</p>
	      
	    <p>To publish this page click on the <b>File</b> menu
	      within the Authoring area and then click on the
	      <b>Publish</b> menu item.</p>
	  </div>
	</div>
      </page:body>
    </page:page>
  </xsl:template>
  
</xsl:stylesheet>
