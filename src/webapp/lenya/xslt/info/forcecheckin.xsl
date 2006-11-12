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

<!-- $Id$ -->

 <xsl:stylesheet version="1.0"
   xmlns="http://www.w3.org/1999/xhtml"
   xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
   xmlns:i18n="http://apache.org/cocoon/i18n/2.1"
   xmlns:page="http://apache.org/cocoon/lenya/cms-page/1.0"
   >
  
  <xsl:param name="user"/>
  <xsl:param name="date"/>
  <xsl:param name="message"/>
  <xsl:param name="requesturi"/>
  <xsl:param name="state"/>

  <xsl:template match="/">
    <page:page>
      <page:title><i18n:text>CheckIn a Document</i18n:text></page:title>
      <page:body>
	<div class="lenya-box">
	  <div class="lenya-box-title"><i18n:text>CheckIn a Document</i18n:text></div>
	  <div class="lenya-box-body">
            <p><i18n:text><xsl:value-of select="$message"/></i18n:text>:</p>
            <table>
              <tr><td><i18n:text>User</i18n:text>:</td><td><strong><xsl:value-of select="$user"/></strong></td></tr>
              <tr><td><i18n:text>Date</i18n:text>:</td><td><xsl:value-of select="$date"/></td></tr>
            </table>
            <xsl:if test="$state = 'co'">
               <p> Do you really want to check-in the document?</p>
            </xsl:if>
	    <form method="get" name="forcecheckin-form">
	      <xsl:attribute name="action"></xsl:attribute>
	      <input type="hidden" name="lenya.usecase" value="forcecheckin"/>
	      <input type="hidden" name="lenya.step" value="forcecheckin"/>
	      
	      <table class="lenya-table-noborder">
		<tr>
		  <td/>
		  <td>
		    <br/>
                    <xsl:if test="$state ='co'">
		      <input type="submit" value="Ok" name="Ok"/>&#160;
		      <input i18n:attr="value" type="button" onClick="location.href='{$requesturi}';" value="Cancel" name="Cancel"/>
                    </xsl:if>
                    <xsl:if test="$state = 'ci'">
		      <input type="button" onClick="location.href='{$requesturi}';" value="Ok" name="Ok"/>
                    </xsl:if>
		  </td>
		</tr>
	      </table>
	    </form>
	  </div>
	</div>
      </page:body>
    </page:page>
  </xsl:template>
  
</xsl:stylesheet>
