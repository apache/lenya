<?xml version="1.0" encoding="iso-8859-1"?>
<!--
  Licensed to the Apache Software Foundation (ASF) under one or more
  contributor license agreements.  See the NOTICE file distributed with
  this work for additional information regarding copyright ownership.
  The ASF licenses this file to You under the Apache License, Version 2.0
  (the "License"); you may not use this file except in compliance with
  the License.  You may obtain a copy of the License at

      http://www.apache.org/licenses/LICENSE-2.0

  Unless required by applicable law or agreed to in writing, software
  distributed under the License is distributed on an "AS IS" BASIS,
  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  See the License for the specific language governing permissions and
  limitations under the License.
-->

<!-- $Id$ -->

<xsl:stylesheet version="1.0"
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:page="http://apache.org/cocoon/lenya/cms-page/1.0"
  xmlns:i18n="http://apache.org/cocoon/i18n/2.1"    
  >
  
  <xsl:import href="../util/page-util.xsl"/>
  
  <xsl:variable name="separator" select="','"/>
  
  <xsl:template match="/">
    
    <page:page>
      <page:title><i18n:text>Parent document not found</i18n:text></page:title>
      <page:body>
	<div class="lenya-box">
	  <div class="lenya-box-title"><i18n:text>Error while publishing</i18n:text></div>
	  <div class="lenya-box-body">
	    <p>
	      <i18n:text>An error occurred while publishing. Most likely you are trying
	      to publish a document whose parent document hasn't been
	      published yet.</i18n:text></p>
	    <p><i18n:text>Try to publish the parent document first.</i18n:text></p>
	  </div>
	</div>
      </page:body>
    </page:page>
  </xsl:template>
  
</xsl:stylesheet>
