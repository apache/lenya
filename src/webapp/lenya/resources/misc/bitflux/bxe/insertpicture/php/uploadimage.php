<?php
include("bitlib/xml/xml2db.php");
include("bitlib/functions/debug.php");
include("../../../../../inc/config.inc.php");

$xml2db = new xml_xml2db($GLOBALS["BX_config"]["dsn"]);
$xml2db->idField ="ID";
$xml2db->useDumpNode = True;

/* Mediaobject insert and upload */
$MOFields = array("objectinfo_title","textobject_phrase","caption_main");
/* imageobject stuff */
$imageUploadName = "Imageobject_imagedata_fileref";
$filerefField = "imagedata_fileref";
$widthField = "imagedata_width";
$heightField = "imagedata_height";
$formatField = "imagedata_format";
$sizeField = "imagedata_filesize";
$uploadDir = "/home/bitflux/demo/www/files/images/";

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

$MediaObjectID = $xml2db->insert($xml);

if (!$MediaObjectID)
{
	print "Error inserting Mediaobject. Please try again or report it to support@bitflux.ch";
    die();
}

/* Document2Object insert */
$xml = "<?xml version='1.0' ?>
       <iba>
       <Document2Object>
       <objectname>Mediaobject</objectname>
       <foreignobjectid>$MediaObjectID</foreignobjectid>
       <foreigndocumentid>".$_POST["DocumentID"]."</foreigndocumentid>
       </Document2Object>
		</iba>";
       
//debug::print_xml($xml);
$Doc2ObjectID = $xml2db->insert($xml);

if (!$Doc2ObjectID)
{
	print "Error inserting Document2Object. Please try again or report it to support@bitflux.ch";
    die();
}  



/* imageobject insert and upload */
if ( isset($_FILES[$imageUploadName]) &&  $_FILES[$imageUploadName]["tmp_name"] )
{
    $ImgInfo = getImageSize($_FILES[$imageUploadName]["tmp_name"]);
    $ImgFields[$widthField] = $ImgInfo[0];
    $ImgFields[$heightField] = $ImgInfo[1];
    switch ($ImgInfo[2]) {
    case 1:

        $ImgFields[$formatField]= "gif";
        break;
    case 2:
        $ImgFields[$formatField]= "jpeg";
        break;
    case 3:
        $ImgFields[$formatField]= "png";
        break;
    case 4:
        $ImgFields[$formatField]= "swf";
        break;
    default:
        $ImgFields[$formatField]= "0";
        break;
    }
}
$ImgFields[$sizeField] = filesize($_FILES[$imageUploadName]["tmp_name"]);
$ImgFields[$filerefField] = $_FILES[$imageUploadName]["name"];

// create xml out of this information

$xml = "<?xml version='1.0' encoding='iso-8859-1' ?>
       <iba>
       <Imageobject>
       ";
foreach ($ImgFields as $key => $field)
{
    $xml .= "  <$key>".($field)."</$key>\n";

}
$xml .= " <foreignmediaobjectid>$MediaObjectID</foreignmediaobjectid>";
$xml .= "</Imageobject>\n";
$xml .= "</iba>";

$ImageObjectID = $xml2db->insert($xml);

//move file
move_uploaded_file($_FILES[$imageUploadName]["tmp_name"],$uploadDir.  $ImageObjectID.".".$_FILES[$imageUploadName]["name"]);
if (!$ImageObjectID)
{
	print "Error inserting ImageObject. Please try again or report it to support@bitflux.ch";
    die();
}  

Header("Location: ../html/upload.php?ID=".$_POST["DocumentID"]."&ok=1");


?>
