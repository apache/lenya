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

<!-- $Id: page-util.xsl 42703 2004-03-13 12:57:53Z gregor $ -->

<xsl:stylesheet version="1.0"
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:i18n="http://apache.org/cocoon/i18n/2.1"
  xmlns="http://www.w3.org/1999/xhtml"
  >

  <xsl:template name="toggle-script">
    <script>
      function toggle(id) {
          var element = document.getElementById(id);
          with (element.style) {
              if ( display == "none" ){
                  display = ""
              } else{
                  display = "none"
              }
          }
          var text = document.getElementById(id + "-switch").firstChild;
          if (text.nodeValue == "<i18n:text i18n:key="show" />") {
              text.nodeValue = "<i18n:text i18n:key="hide" />";
          } else {
              text.nodeValue = "<i18n:text i18n:key="show" />";
          }
      }
    </script>
  </xsl:template>

</xsl:stylesheet>
