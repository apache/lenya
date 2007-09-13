<?php
/**
 this script loads and saves XML document with the help of PHP

 in your config.xml you would then write
 
 <file name="BX_xmlfile">/path/to/your/loadsave.php?XML=path/to/content.xml</file>

 needs PHP 4.3 or PHP 5. similar is also possible with PHP < 4.3
*/

header("Content-Type: text/html");

$xmlfile = realpath($_GET['XML']);
/* very crude and simple security check

    we allow only inclusion of files, which
    are somewhere in the directory one level
    above this script. You have to change this
    rule, if you want to include from somewhere
    else, but make sure, it can't read any sensitive
    files..
*/
$basedir = dirname(dirname(__FILE__));

if (strpos($xmlfile,$basedir) !== 0) {
    die("you are not allowed to read/write this file (".$_GET['XML'].")");
}

//If saving, then REQUEST METHOD is PUT
// and we have to set the response code to 204 

if ($_SERVER['REQUEST_METHOD'] == "PUT") {
    $fd = fopen ($_GET['XML'], "w");
    if ($fd) {
        // read sent content
        $content = file_get_contents("php://input");
        //write content
        fwrite($fd,$content);
        fclose($fd);
        //sent correct HTTP header
        header("HTTP/1.1 204 No Content");
        
    } else {
        die ("couldn't write to ".$_GET['XML']);
    }
} 
//otherwise it's just a get REQUEST
else {
    print file_get_contents($_GET['XML']);
}
?>