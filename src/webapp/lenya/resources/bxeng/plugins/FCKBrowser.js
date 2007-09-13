var dbforms2_fBrowserLastLocation = '';


var FCKConfig = new Object();
var FCK = new Object();
var FCKLanguageManager = new Object(); 

function BxeFCKBrowser() {
	
	this.init = function (options) {
		bx_webroot = options['webroot'];
		FCKConfig.SkinPath = bx_webroot + 'webinc/fck/editor/skins/default/';
		FCKConfig.ImageUpload = true;
		FCKConfig.LinkUploadAllowedExtensions    = "" ;                  // empty for all
		FCKConfig.ImageUploadAllowedExtensions   = ".(jpg|gif|jpeg|png)$" ;
		FCKConfig.ImageBrowser = true ;
		FCKConfig.ImageBrowserURL = bx_webroot+ 'webinc/fck/editor/filemanager/browser/default/browser.html?Type=files&Connector=connectors/php/connector.php';

		//FCK.Language = FCKLanguageManager ;

		
	}
	
	this.start = function() {
		//register event
		//document.eDOMaddEventListener( "ClipboardPasteDialog" , BxeTextClipboard_OpenDialog , false);
		
	}
	this.getCss = function() {
		return new Array();
	}
	
	this.getScripts = function() {
		FCKLanguageManager.DefaultLanguage = 'en' ;

		FCKLanguageManager.ActiveLanguage = new Object() ;
		FCKLanguageManager.ActiveLanguage.Code = FCKLanguageManager.GetActiveLanguage() ;
		//FCKLanguageManager.ActiveLanguage.Name = FCKLanguageManager.AvailableLanguages[ FCKLanguageManager.ActiveLanguage.Code ] ;

		return new Array('../fck/editor/lang/' + FCKLanguageManager.ActiveLanguage.Code + '.js');
	}
	
	
}


function BxeFCKBrowser_Open(e) {
	var fBrowserUrl = bx_webroot + 'webinc/fck/editor/filemanager/browser/default/browser.html?Type=files&amp;Connector=connectors/php/connector.php';

    BxeFCKBrowser.cssr = window.getSelection().getEditableRange();
	
	var currentFile = '';
    if (dbforms2_fBrowserLastLocation) {
        currentFile = dbforms2_fBrowserLastLocation;
    }
    var filesDir = '/files';
    sParentFolderPath = currentFile.substring(filesDir.length, currentFile.lastIndexOf('/', currentFile.length - 2) + 1);

    if(sParentFolderPath != '' && (sParentFolderPath.indexOf('/') != -1)) {
        fBrowserUrl += '&RootPath=' + escape(sParentFolderPath);
    }
	fBrowserUrl = fBrowserUrl.replace(/&amp;/,"&");
    if(typeof fBrowserWindow != 'undefined' && !fBrowserWindow.closed) {
        fBrowserWindow.location.href = fBrowserUrl;
    } else {
		
        fBrowserWindow = window.open(fBrowserUrl, 'fBrowser', 'width=800,height=600,location=no,menubar=no');
    }

    fBrowserWindow.focus();

    
    SetUrl = function(url) {
		     
        var sel = window.getSelection();
        sel.selectEditableRange(BxeFCKBrowser.cssr);
        sel.linkText(url);
		sel.anchorNode.parentNode.updateXMLNode();
		return true;
        //old method, maybe needed one day for inserting other stuff than links :)
		//make string
		var te = "" + sel;
		te = te.replace(/</,"&lt;");
		if (typeof target == "object") {
			var xml = "<a xmlns='"+ XHTMLNS + "' href='"+url+"'>"+te+"</a>";
		} else {
			var xml = "<a xmlns='"+ XHTMLNS + "' href='"+url+"' target='"+target+"'>"+te+"</a>";
		}
        return bxe_insertContent(xml, BXE_SELECTION);
    }
    
}

function BxeFCKBrowser_OpenImageDialog(e) {
	 var w= new FCKDialogCommand( 'Image'		, 'Image'			, 'dialog/fck_image.html'		, 450, 400 ) ; 
	 
	 w.Execute();
}

// ### General Dialog Box Commands.
var FCKDialogCommand = function( name, title, url, width, height, getStateFunction, getStateParam )
{
	this.Name	= name ;
	this.Title	= title ;
	this.Url	= url ;
	this.Width	= width ;
	this.Height	= height ;

	this.GetStateFunction	= getStateFunction ;
	this.GetStateParam		= getStateParam ;
}

FCKDialogCommand.prototype.Execute = function()
{
	FCKDialog.OpenDialog( 'FCKDialog_' + this.Name , this.Title, this.Url, this.Width, this.Height ) ;
}

FCKDialogCommand.prototype.GetState = function()
{
	if ( this.GetStateFunction )
		return this.GetStateFunction( this.GetStateParam ) ;
	else
		return FCK_TRISTATE_OFF ;
}


var FCKDialog = new Object() ;

// This method opens a dialog window using the standard dialog template.
FCKDialog.OpenDialog = function( dialogName, dialogTitle, dialogPage, width, height, customValue, parentWindow, resizable )
{
	// Setup the dialog info.
	var oDialogInfo = new Object() ;
	oDialogInfo.Title = dialogTitle ;
	oDialogInfo.Page = dialogPage ;
	oDialogInfo.Editor = window ;
	oDialogInfo.CustomValue = customValue ;		// Optional
	
	var sUrl = bx_webroot + 'webinc/fck/editor/fckdialog.html' ;
	this.Show( oDialogInfo, dialogName, sUrl, width, height, parentWindow, true ) ;
}



FCKDialog.Show = function( dialogInfo, dialogName, pageUrl, dialogWidth, dialogHeight, parentWindow, resizable )
{
	var iTop  = 0 // (FCKConfig.ScreenHeight - dialogHeight) / 2 ;
	var iLeft = 0 // (FCKConfig.ScreenWidth  - dialogWidth)  / 2 ;

	var sOption  = "location=no,menubar=no,toolbar=no,dependent=yes,dialog=yes,minimizable=no,modal=yes,alwaysRaised=yes" +
		",resizable="  + ( resizable ? 'yes' : 'no' ) +
		",width="  + dialogWidth +
		",height=" + dialogHeight +
		",top="  + iTop +
		",left=" + iLeft ;

	if ( !parentWindow )
		parentWindow = window ;
	
	var oWindow = parentWindow.open( '', 'FCKeditorDialog_' + dialogName, sOption, true ) ;
	oWindow.moveTo( iLeft, iTop ) ;
	oWindow.resizeTo( dialogWidth, dialogHeight ) ;
	oWindow.focus() ;
	oWindow.location.href = pageUrl 
	
	oWindow.bx_webroot = bx_webroot;
	oWindow.dialogArguments = dialogInfo ;
	
	// On some Gecko browsers (probably over slow connections) the 
	// "dialogArguments" are not set to the target window so we must
	// put it in the opener window so it can be used by the target one.
	parentWindow.FCKLastDialogInfo = dialogInfo ;
	
	this.Window = oWindow ;
	
	// Try/Catch must be used to avoit an error when using a frameset 
	// on a different domain: 
	// "Permission denied to get property Window.releaseEvents".
	try
	{
		window.top.captureEvents( Event.CLICK | Event.MOUSEDOWN | Event.MOUSEUP | Event.FOCUS ) ;
		window.top.parent.addEventListener( 'mousedown', this.CheckFocus, true ) ;
		window.top.parent.addEventListener( 'mouseup', this.CheckFocus, true ) ;
		window.top.parent.addEventListener( 'click', this.CheckFocus, true ) ;
		window.top.parent.addEventListener( 'focus', this.CheckFocus, true ) ;
	}
	catch (e)
	{}
}

FCKDialog.CheckFocus = function()
{
	// It is strange, but we have to check the FCKDialog existence to avoid a 
	// random error: "FCKDialog is not defined".
	if ( typeof( FCKDialog ) != "object" )
		return false ;
	
	if ( FCKDialog.Window && !FCKDialog.Window.closed )
		FCKDialog.Window.focus() ;
	else
	{
		// Try/Catch must be used to avoit an error when using a frameset 
		// on a different domain: 
		// "Permission denied to get property Window.releaseEvents".
		try
		{
			window.top.releaseEvents(Event.CLICK | Event.MOUSEDOWN | Event.MOUSEUP | Event.FOCUS) ;
			window.top.parent.removeEventListener( 'onmousedown', FCKDialog.CheckFocus, true ) ;
			window.top.parent.removeEventListener( 'mouseup', FCKDialog.CheckFocus, true ) ;
			window.top.parent.removeEventListener( 'click', FCKDialog.CheckFocus, true ) ;
			window.top.parent.removeEventListener( 'onfocus', FCKDialog.CheckFocus, true ) ;
		}
		catch (e)
		{}
	}
	return false ;
}
	

var NS ;

if ( !( NS = window.parent.__FCKeditorNS ) )
	NS = window.parent.__FCKeditorNS = new Object() ;


var FCKBrowserInfo ;

if ( !( FCKBrowserInfo = NS.FCKBrowserInfo ) )
{
	FCKBrowserInfo = NS.FCKBrowserInfo = new Object() ;

	var sAgent = navigator.userAgent.toLowerCase() ;

	FCKBrowserInfo.IsIE			= ( sAgent.indexOf("msie") != -1 ) ;
	FCKBrowserInfo.IsGecko		= !FCKBrowserInfo.IsIE ;
	FCKBrowserInfo.IsSafari		= ( sAgent.indexOf("safari") != -1 ) ;
	FCKBrowserInfo.IsNetscape	= ( sAgent.indexOf("netscape") != -1 ) ;
}

FCKLanguageManager.GetActiveLanguage = function()
{
	if ( FCKConfig.AutoDetectLanguage )
	{
		var sUserLang ;
		
		// IE accepts "navigator.userLanguage" while Gecko "navigator.language".
		if ( navigator.userLanguage )
			sUserLang = navigator.userLanguage.toLowerCase() ;
		else if ( navigator.language )
			sUserLang = navigator.language.toLowerCase() ;
		else
		{
			// Firefox 1.0 PR has a bug: it doens't support the "language" property.
			return FCKConfig.DefaultLanguage ;
		}
		
		// Some language codes are set in 5 characters, 
		// like "pt-br" for Brasilian Portuguese.
		if ( sUserLang.length >= 5 )
		{
			sUserLang = sUserLang.substr(0,5) ;
			if ( this.AvailableLanguages[sUserLang] ) return sUserLang ;
		}
		
		// If the user's browser is set to, for example, "pt-br" but only the 
		// "pt" language file is available then get that file.
		if ( sUserLang.length >= 2 )
		{
			sUserLang = sUserLang.substr(0,2) ;
			if ( this.AvailableLanguages[sUserLang] ) return sUserLang ;
		}
	}
	
	return this.DefaultLanguage ;
}

FCKLanguageManager.TranslateElements = function( targetDocument, tag, propertyToSet )
{
	var e = targetDocument.getElementsByTagName(tag) ;

	for ( var i = 0 ; i < e.length ; i++ )
	{
		var sKey = e[i].getAttribute( 'fckLang' ) ;
		
		if ( sKey )
		{
			var s = FCKLang[ sKey ] ;
			if ( s ) 
				eval( 'e[i].' + propertyToSet + ' = s' ) ;
		}
	}
}

FCKLanguageManager.TranslatePage = function( targetDocument )
{
	this.TranslateElements( targetDocument, 'INPUT', 'value' ) ;
	this.TranslateElements( targetDocument, 'SPAN', 'innerHTML' ) ;
	this.TranslateElements( targetDocument, 'LABEL', 'innerHTML' ) ;
	this.TranslateElements( targetDocument, 'OPTION', 'innerHTML' ) ;
}

FCK.CreateElement = function(name) {
	//var node = bxe_config.xmldoc.createElementNS(XHTMLNS,name);
	var node =  bxe_insertContent_async('<'+name.toLowerCase()+' xmlns="'+XHTMLNS+'"/>',BXE_SELECTION);
	
	return  node;
}




