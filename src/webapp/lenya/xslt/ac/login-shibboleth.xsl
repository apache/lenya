<?xml version="1.0"?>
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

<xsl:stylesheet version="1.0"
  xmlns:i18n="http://apache.org/cocoon/i18n/2.1"      
  xmlns:page="http://apache.org/cocoon/lenya/cms-page/1.0"
  xmlns:session="http://www.apache.org/xsp/session/2.0"
  xmlns="http://www.w3.org/1999/xhtml"
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
  
  <xsl:import href="login-user.xsl"/>
  
  <xsl:param name="homepageUrl"/>
  
  <xsl:template name="loginFormWrapper">
    <xsl:if test="not(/page/body/login/errors/error[normalize-space() = 'shibboleth-delete-cookies'])">
      <xsl:call-template name="loginForm"/>
      <br/>
    </xsl:if>
    <p>
      <a href="{$homepageUrl}"><i18n:text>link-to-publication-homepage</i18n:text></a>
    </p>
    <p>
      <a href="?lenya.usecase=shibboleth&amp;lenya.step=wayf">Login via Shibboleth</a>
    </p>
  </xsl:template>
  
  
</xsl:stylesheet>

