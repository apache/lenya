<?php
include("bitlib/xml/xml2db.php");
include("bitlib/functions/debug.php");
include("../../../../../inc/config.inc.php");

$xml2db = new xml_xml2db($config["dsn"]);
$xml2db->idField ="ID";
$xml2db->useDumpNode = True;

/* Mediaobject insert and upload */
$MOFields = array("textobject_phrase","caption_main","ID");
/* imageobject stuff */

/*Make Mediaobject */


$xml = "<?xml version='1.0' encoding='iso-8859-1' ?>
       <iba>
       <Mediaobject>
       ";
foreach ($MOFields as $field)
{
    $xml .= "  <$field>".($_POST["Mediaobject_".$field])."</$field>\n";
}
$xml .= "</Mediaobject>\n";
$xml .= "</iba>";
//debug::print_xml($xml);

$xml2db->insert($xml);


Header("Location: /editor/originalsize?ID=".$_POST["Mediaobject_ID"]."&ok=1");


?>
