<?xml version="1.0"?>

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0" xmlns:t="http://www.q42.nl/t">

<xsl:output method="html" version="1.0" indent="yes"/>

<!--
<xsl:variable name="xopus_path">/wyona/oscom/xopus</xsl:variable>
-->
<!--
<xsl:variable name="xopus_path">/wyona/ethz-mat/xopus</xsl:variable>
-->
<xsl:variable name="xopus_path">/xps/xopus</xsl:variable>

<xsl:template name="xopus_html_attribute">
  <xsl:attribute name="xmlns:t"><xsl:text>urn:schemas-microsoft-com:time</xsl:text></xsl:attribute>
</xsl:template>



<xsl:template name="xopus_top">
  <script language="javascript" src="{$xopus_path}/init.js">//nothing, but make sure the end tag will still be there...

</script>

</xsl:template>



<xsl:template name="xopus_head">

        <style type="text/css">

                .company,

                .person,

                .organization           { background-color:#FFFFDD; text-decoration:underline; cursor:hand; color:blue; }

                

                .xopustable td  { font-family:Tahoma,Arial; }

                .xopustable a           { text-decoration:none; }

                .xopustable a:hover     { text-decoration:underline; }

                #xopus_bar              {

                                          position:absolute; top:-100; left:0; z-index:42042; width:102%;

                                          background-color:buttonface;

                                           border-style:outset; border-width:0 0 2 0; 

                                        }

                .l_g    { font-size:18px; color:#8F8F8F; font-weight:900; letter-spacing:1px; }

                .l_b    { font-size:18px; color:#000000; font-weight:900; letter-spacing:1px; }

                .l_o    { font-size:18px; color:#FF6600; font-weight:900; letter-spacing:1px; }

                .n_g    { font-size:12px; color:#6F6F6F; letter-spacing:1px; }

                .n_b    { font-size:12px; color:#000000; letter-spacing:1px; }

                .n_o    { font-size:12px; color:#FF6600; letter-spacing:1px; }

                .s_g    { font-size:10px; color:#6F6F6F; }

                .s_b    { font-size:10px; color:#000000; }

                .s_o    { font-size:10px; color:#4f8dff; }

        </style>

</xsl:template>



<xsl:template name="xopus_body">



        <br /><br /><br />

        <div id="xopus_bar">

        <table class="xopustable" border="0" cellspacing="0" cellpadding="1" width="102%">

                <tr><td colspan="3"><img src="{$xopus_path}/logic/media/nix.gif" name="xopus_bar_spacer" width="1" height="100" hspace="0" vspace="0" border="0" alt="" /></td></tr>

                <tr><td colspan="3">

                &#160;&#160;&#160;<span class="l_g">wyona</span>

                &#160;&#160;&#160;<span class="s_g">Editor Xopus by <a class="s_o" href="http://www.q42.nl" target="_blank">Q42</a></span>

                </td></tr>

                <tr>

                        <td valign="top" align="left">

<!--                            <a href="#" onclick="if (window.xps_xopus_connection) xps_xopus_connection('save'); return false;"><span class="n_b"><b>Zwischenspeichern</b></span></a> &#160;

                <a href="#" onclick="if (window.xps_xopus_connection) xps_xopus_connection('close'); return false;"><span class="n_b"><b>Schliessen</b></span></a> &#160;

-->                



&#160;&#160;

<a href="#" id="xopus_savebutton" onclick="if (window.xps_xopus_connection) xps_xopus_connection('save'); return false;">

<span class="n_b"><b>Save</b></span>

</a>

&#160;|&#160;

<a href="#" id="xopus_closebutton" onclick="if (window.xps_xopus_connection) xps_xopus_connection('close'); return false;">

<span class="n_b"><b>Save and Close</b></span>

</a>

<!--

<a href="#" id="xopus_savebutton" onclick="if (window.xps_xopus_connection) xps_xopus_connection('save'); return false;"><img src="{$xopus_path}/square.gif" width="5" height="5" alt="" hspace="4" vspace="4" border="0" /><span class="n_b"><b>Zwischenspeichern</b></span></a> &#160;

<a href="#" id="xopus_checkbutton" onclick="if (window.xps_xopus_connection) xps_xopus_connection('check'); return false;"><img src="{$xopus_path}/square.gif" width="5" height="5" alt="" hspace="4" vspace="4" border="0" /><span class="n_b"><b>Korrigieren</b></span></a> &#160;

<a href="#" id="xopus_closebutton" onclick="if (window.xps_xopus_connection) xps_xopus_connection('close'); return false;"><img src="{$xopus_path}/square.gif" width="5" height="5" alt="" hspace="4" vspace="4" border="0" /><span class="n_b"><b>Schliessen</b></span></a> &#160;

-->



                        </td>

                        <td valign="top" align="right">

                        <span class="s_g">INFO: </span><span class="s_o" id="xopus_currentlabel"><b id="current_xopus_bar">Click on the blue gearwheel to start editing.&#160;&#160;&#160;&#160;&#160;&#160;</b></span>

                        <br/><span class="s_g">&#160;&#160;&#160;&#160;&#160;&#160;</span>

                        <!--<br/><span class="s_g">Editor Xopus by Q42&#160;&#160;&#160;&#160;&#160;&#160;</span>-->

                        </td>

                        <td width="3%">&#160;</td>

                </tr>

                

        </table>

        </div>

        

        <script language="javascript">

                function xopus_handle_scrolling(event) {

                        var r = document.all.xopus_bar_spacer.getBoundingClientRect();

                        document.all.xopus_bar.style.top = window.document.body.scrollTop - (r.bottom - r.top);

                }

                

                window.attachEvent("onscroll",xopus_handle_scrolling);

        </script>







</xsl:template>





</xsl:stylesheet>

