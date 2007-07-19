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

<!-- $Id: publication.xsl 388509 2006-03-24 13:07:10Z michi $ -->

<xsl:stylesheet
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0"
    xmlns="http://www.wyona.org/neutron/1.0"
>

<xsl:param name="uri"/>
  
<xsl:template match="/">
  <introspection>

    <navigation>
      <sitetree href="{$uri}?lenya.module=neutron&amp;lenya.action=sitetree" method="GET"/>
    </navigation>
    
    <!--
    TODO: In order to get the WYSIWYG view one needs to change the mime-type
    to xhtml+xml, but somehow this causes problems re saving ...
    See missing mime-type within
    cocoon_2_1_x/src/java/org/apache/cocoon/generation/StreamGenerator.java
    -->
    <edit mime-type="application/xhtml+xml" name="Body Content">

      <checkout url="{$uri}?lenya.usecase=neutron.checkout" method="GET"/>

<!-- Save without releasing the lock, e.g. for "global" temporary saving -->
<!--
      <save url="{$uri}?lenya.module=neutron&amp;lenya.step=save" method="PUT"/>
-->
      <checkin url="{$uri}?lenya.usecase=bxe.close" method="PUT"/>

<!--
      <schemas>
        <schema href="http://foo.bar.com/lenya/modules/docbook/schemas/simple.rng" type="RelaxNG"/>
        <schema href="http://foo.bar.com/lenya/modules/docbook/schemas/default.rng" type="RelaxNG"/>
      </schemas>
      <styles>
        <style href="{$uri}.xsl"/>
      </styles>
-->
    </edit>
  </introspection>
</xsl:template>

</xsl:stylesheet>
