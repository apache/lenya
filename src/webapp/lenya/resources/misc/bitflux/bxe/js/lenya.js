// +----------------------------------------------------------------------+
// | Bitflux Editor                                                       |
// +----------------------------------------------------------------------+
// | Copyright (c) 2001,2002 Bitflux GmbH                                 |
// +----------------------------------------------------------------------+
// | This software is published under the terms of the Apache Software    |
// | License a copy of which has been included with this distribution in  |
// | the LICENSE file and is available through the web at                 |
// | http://bitflux.ch/editor/license.html                                |
// +----------------------------------------------------------------------+
// | Author: Christian Stocker <chregu@bitflux.ch>                        |
// +----------------------------------------------------------------------+
//
// $Id: lenya.js,v 1.2 2002/11/11 18:09:15 felixcms Exp $
var p = new XMLHttpRequest();

function BX_wyona_save()
{
/*	document.forms.poster.action = BX_posturl;
	document.forms.poster.method = "POST";
    document.forms.poster.content.value = calculateMarkup(BX_getResultXML(),true);
    document.forms.poster.submit();*/
// wyona way

	p.onload = BX_responseXML;
	var WyonaRequest = BX_xml.createElement("request");
	WyonaRequest.setAttribute("type","checkin");
	var dataEle = BX_xml.createElement("data");
	dataEle.setAttribute("type","xml");
	dataEle.appendChild(BX_getResultXML().firstChild);

	WyonaRequest.appendChild(dataEle);
	p.open("POST",BX_posturl);
	// BX_show_xml(WyonaRequest);
	p.send(calculateMarkup(WyonaRequest,true));
}

function BX_responseXML(e) {
	var alerttext="";
	if (p.responseXML) {
	   if (p.responseXML.firstChild.nodeName == 'parsererror')
		{
		alerttext="Something went wrong during parsing of the response:\n\n";
		alerttext+=BX_show_xml(p.responseXML);
		}
		else if (p.responseXML.documentElement.getAttribute("status") == "ok")
		{
			alerttext = "Document successfully saved";
		}
		else
		{
		alerttext="Something went wrong during saving:\n\n";
		alerttext += (calculateMarkup(p.responseXML.documentElement,true));
		}
		alert(alerttext +p.responseXML);
	}
	else {
		alerttext="Something went wrong during saving:\n\n";
		alert(alerttext + p.responseText) ;
	}
}

