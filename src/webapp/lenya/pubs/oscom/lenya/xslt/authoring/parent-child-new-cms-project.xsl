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

<!-- $Id: parent-child-new-cms-project.xsl,v 1.8 2004/03/13 12:42:21 gregor Exp $ -->

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
 
<xsl:output version="1.0" indent="yes" encoding="ISO-8859-1"/>

<xsl:template match="/">
<html>
<head>
<link rel="stylesheet" type="text/css" href="/lenya/lenya/css/default.css" />
</head>
<body>
  <xsl:apply-templates/>
</body>
</html>
</xsl:template>

<xsl:template match="parent-child">
<h1>New CMS Project</h1>

<xsl:apply-templates select="exception"/>

<xsl:if test="not(exception)">
<p>
<form action="create">
<input type="hidden" name="parentid" value="{/parent-child/parentid}"/>
<input type="hidden" name="childtype" value="leaf"/>
<table>
  <tr>
    <td>project id:</td><td><input type="text" name="childid"/> (e.g. lenya)</td>
  </tr>
  <tr>
    <td>project name:</td><td><input type="text" name="childname"/> (e.g. Lenya)</td>
  </tr>
  <tr>
    <td valign="top">doc type:</td>
    <td>
      <select name="doctype" size="1">
        <option value="CMSProject"><xsl:attribute name="selected"/>CMS Project</option>
        <option value="CMFProject">CMFProject</option>
      </select>
    </td>
  </tr>
</table>
<input type="submit" value="create"/>&#160;&#160;&#160;<a href="{referer}">CANCEL</a>
</form>
</p>
</xsl:if>
</xsl:template>

<xsl:template match="exception">
<font color="red">EXCEPTION</font><br />
Go <a href="{../referer}">back</a> to page.<br />
<p>
Exception handling isn't very good at the moment. 
For further details please take a look at the log-files
of Cocoon. In most cases it's one of the two possible exceptions:
<ol>
  <li>The id is not allowed to have whitespaces</li>
  <li>The id is already in use</li>
</ol>
Exception handling will be improved in the near future.
</p>
</xsl:template>
 
</xsl:stylesheet>  
