<?xml version="1.0" encoding="UTF-8" ?>
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

<!-- $Id: search.xsl 76019 2004-11-16 20:13:32Z gregor $ -->


<xsl:stylesheet version="1.0"
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:nav="http://apache.org/cocoon/lenya/navigation/1.0"
  xmlns="http://www.w3.org/1999/xhtml"
  exclude-result-prefixes="nav"
  >
  
  <xsl:template match="nav:site">
    <div id="search">
      <form action="?" id="form-search">
        <p>
          <input class="searchfield" type="text" name="queryString" alt="Search field"/>
          <select name="lenya.usecase">
            <option selected="true" value="lucene.search">Internal</option>
            <option value="lucene.externalOpensearch">External</option>
          </select>
          <input type="hidden" value="20" name="pageLength"/>
          <input class="searchsubmit" type="submit" value="Go" name="input-go"/>
        </p>
      </form>
    </div>
  </xsl:template>
  
  <xsl:template match="@*|node()">
    <xsl:copy>
      <xsl:apply-templates select="@*|node()"/>
    </xsl:copy>
  </xsl:template>
  
</xsl:stylesheet> 
