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
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:fi="http://apache.org/cocoon/forms/1.0#instance"
                exclude-result-prefixes="fi">

  <xsl:template match="head" mode="forms-dojoarea">
    <script type="text/javascript">
        dojo.require("dojo.widget.Editor");
    </script>
  </xsl:template>

  <xsl:template match="body" mode="forms-dojoarea"/>

  <!--+
      | fi:field with @type 'dojoarea'
      +-->
  <xsl:template match="fi:field[fi:styling[@type='dojoarea']]">
    <textarea dojoType="Editor" id="{@id}" name="{@id}" title="{fi:hint}"
      items="bold;italic;underline;strikethrough;">
      <!-- remove carriage-returns (occurs on certain versions of IE and doubles linebreaks at each submit) -->
      <xsl:apply-templates select="fi:value/node()" mode="dojoarea-copy"/>
    </textarea>
  </xsl:template>

  <xsl:template match="@*|*" mode="dojoarea-copy">
    <xsl:copy>
      <xsl:apply-templates select="@*|node()" mode="dojoarea-copy"/>
    </xsl:copy>
  </xsl:template>

  <xsl:template match="text()" mode="dojoarea-copy">
    <xsl:copy-of select="translate(., '&#13;', '')"/>
  </xsl:template>

</xsl:stylesheet>
