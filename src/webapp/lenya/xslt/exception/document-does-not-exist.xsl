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

<!-- $Id: document-does-not-exist.xsl 473841 2006-11-12 00:46:38Z gregor $ -->

<xsl:stylesheet version="1.0"
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:page="http://apache.org/cocoon/lenya/cms-page/1.0"
  >
  
  <xsl:import href="../util/page-util.xsl"/>
  
  <xsl:param name="documentid"/>
  <xsl:param name="documenturl"/>

  <xsl:template match="/">
    
    <page:page>
      <page:title>404: Page not found</page:title>
      <page:body>
	<div class="lenya-box">
	  <div class="lenya-box-title">The requested document does not exist</div>
	  <div class="lenya-box-body">
	    <p>
	      The requested document '<xsl:value-of select="$documenturl"/>' with
	      document-id '<xsl:value-of select="$documentid"/>' does not exist.
	    </p>
	  </div>
	</div>
      </page:body>
    </page:page>
  </xsl:template>
  
</xsl:stylesheet>
