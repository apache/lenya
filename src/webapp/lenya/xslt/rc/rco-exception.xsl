<?xml version="1.0" encoding="ISO-8859-1"?>
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

<!-- $Id: rco-exception.xsl,v 1.11 2004/03/13 12:42:18 gregor Exp $ -->

<xsl:stylesheet version="1.0"
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:rc="http://apache.org/cocoon/lenya/rc/1.0"
  xmlns:page="http://apache.org/cocoon/lenya/cms-page/1.0"
  >
  <xsl:import href="../util/page-util.xsl"/>
  
  <xsl:output version="1.0" indent="yes" encoding="ISO-8859-1"/>

  <xsl:template match="/">
    <xsl:apply-templates/>
  </xsl:template>

  <xsl:template match="rc:exception">
          <xsl:apply-templates/>
  </xsl:template>

  <xsl:template match="rc:file-reserved-checkout-exception">
    <page:page>
      <page:title>File Reserved Checkout Exception</page:title>
      <page:body>
 	    <div class="lenya-box">
    	  <div class="lenya-box-title"> Could not check out the file </div>
	      <div class="lenya-box-body">
	        <p> Reason : The resource has already been checked out by another user
            </p>
            <table>
              <tr><td>User:</td><td><xsl:value-of select="rc:user"/></td></tr>
              <tr><td>Date:</td><td><xsl:value-of select="rc:date"/></td></tr>
              <tr><td>Filename:</td><td><xsl:value-of select="rc:filename"/></td></tr>
            </table>
  	      </div>
  	    </div>
      </page:body>
    </page:page>
  </xsl:template>
  
  <xsl:template match="rc:file-reserved-checkin-exception">
    <page:page>
      <page:title>File Reserved Checkin Exception</page:title>
      <page:body>
 	    <div class="lenya-box">
          <div class="lenya-box-title">File not checked in yet</div>
    	  <div class="lenya-box-body">
	        <p> The resource has already been checked out:
            </p>
            <table>
              <tr><td>User:</td><td><xsl:value-of select="rc:user"/></td></tr>
              <tr><td>Date:</td><td><xsl:value-of select="rc:date"/></td></tr>
              <tr><td>Filename:</td><td><xsl:value-of select="rc:filename"/></td></tr>
            </table>
  	      </div>
  	    </div>
      </page:body>
    </page:page>
  </xsl:template>


  <xsl:template match="rc:generic-exception">
    <page:page>
      <page:title>Generic Exception</page:title>
      <page:body>
 	    <div class="lenya-box">
	    <div class="lenya-box-title">Generic Exception</div>
	      <div class="lenya-box-body">
            <p>
            Check the log files.
            </p>
            <table>
              <tr><td>Filename:</td><td><xsl:value-of select="rc:filename"/></td></tr>
            </table>
       	  </div>
  	    </div>
      </page:body>
    </page:page>
  </xsl:template>

</xsl:stylesheet>
