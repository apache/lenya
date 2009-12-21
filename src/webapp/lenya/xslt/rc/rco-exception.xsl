<?xml version="1.0" encoding="UTF-8"?>
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
  xmlns:rc="http://apache.org/cocoon/lenya/rc/1.0"
  xmlns:page="http://apache.org/cocoon/lenya/cms-page/1.0"
  xmlns:i18n="http://apache.org/cocoon/i18n/2.1"
  xmlns="http://www.w3.org/1999/xhtml"
  >
  
  <xsl:import href="../util/page-util.xsl"/>
  
  <xsl:template match="/">
    <xsl:apply-templates/>
  </xsl:template>
  
  <xsl:template match="rc:exception">
    <xsl:apply-templates/>
  </xsl:template>
  
  <xsl:template match="rc:file-reserved-checkout-exception">
    <page:page>
      <page:title><i18n:text>lenya.rc.nocheckout</i18n:text></page:title>
      <page:body>
        <div class="lenya-box">
          <div class="lenya-box-title"><i18n:text>lenya.rc.nocheckout</i18n:text></div>
          <div class="lenya-box-body">
            <p><i18n:text>lenya.rc.checkedoutalready</i18n:text></p>
            <table>
              <tr>
                <td class="lenya-entry-caption"><i18n:text>User</i18n:text>:</td>
                <td><xsl:value-of select="rc:user"/></td>
              </tr>
              <tr>
                <td class="lenya-entry-caption"><i18n:text>Date</i18n:text>:</td>
                <td><xsl:value-of select="rc:date"/></td>
              </tr>
              <tr>
                <td class="lenya-entry-caption"><i18n:text>Filename</i18n:text>:</td>
                <td><xsl:value-of select="rc:filename"/></td>
              </tr>
            </table>
            <form id="form-back">
              <p>
                <input type="button" value="OK" onClick="history.go(-1)" name="input-ok"/>
              </p>
            </form>		
          </div>
        </div>
      </page:body>
    </page:page>
  </xsl:template>
  
  <xsl:template match="rc:file-reserved-checkin-exception">
    <page:page>
      <page:title><i18n:text>lenya.rc.nocheckin</i18n:text></page:title>
      <page:body>
        <div class="lenya-box">
          <div class="lenya-box-title"><i18n:text>lenya.rc.nocheckin</i18n:text></div>
          <div class="lenya-box-body">
            <p><i18n:text>lenya.rc.checkedoutalready</i18n:text></p>
            <table>
              <tr>
                <td class="lenya-entry-caption"><i18n:text>User</i18n:text>:</td>
                <td><xsl:value-of select="rc:user"/></td>
              </tr>
              <tr>
                <td class="lenya-entry-caption"><i18n:text>Date</i18n:text>:</td>
                <td><xsl:value-of select="rc:date"/></td>
              </tr>
              <tr>
                <td class="lenya-entry-caption"><i18n:text>Filename</i18n:text>:</td>
                <td><xsl:value-of select="rc:filename"/></td>
              </tr>
            </table>
            <form id="form-back">
              <p>
                <input type="button" value="OK" onClick="history.go(-1)" name="input-ok"/>
              </p>
            </form>		
          </div>
        </div>
      </page:body>
    </page:page>
  </xsl:template>
  
  
  <xsl:template match="rc:generic-exception">
    <page:page>
      <page:title><i18n:text>Generic Exception</i18n:text></page:title>
      <page:body>
        <div class="lenya-box">
          <div class="lenya-box-title"><i18n:text>Generic Exception</i18n:text></div>
          <div class="lenya-box-body">
            <p><i18n:text>Reason</i18n:text>: <xsl:value-of select="rc:message"/><br />
              <i18n:text>Check the log files.</i18n:text>
            </p>
            <table>
              <tr><td><i18n:text>Filename</i18n:text>:</td><td><xsl:value-of select="rc:filename"/></td></tr>
            </table>
            <form id="form-back">
              <input type="button" value="OK" onClick="history.go(-1)" name="input-ok"/>
            </form>		
          </div>
        </div>
      </page:body>
    </page:page>
  </xsl:template>
  
</xsl:stylesheet>
