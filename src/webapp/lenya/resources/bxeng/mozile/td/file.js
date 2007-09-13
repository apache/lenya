
function mozileTransportDriver_file() {
	
}

//filename is not supported yet..
mozileTransportDriver_file.prototype.save = function (filename, content, td) {
	
	
	var mfp = mozilla.createFilePicker(MozFilePicker.MODE_SAVE, "save to local file");
	var reqObj  = new Object();

	if(mfp)
	{
		mfp.addFilter(MozFilePicker.FILTER_HTML);
		if(mfp.promptUser())
		{
			var mf = mfp.file;
			mf.write(content); 
			reqObj.isError = false;
			reqObj.status = 200;
			reqObj.statusText = "OK";
			
		} else {
		}
		
	}
	else
	{	
		reqObj.isError = true;
		reqObj.status = 404;
		reqObj.statusText = "mozileSave: can't save-to-file because Mozilla doesn't allow remote scripts to launch its native file picker dialog. Either run Mozile locally or wait until it is packaged as an extension. For more information, see http://mozile.mozdev.org/use.html."; 
	}
	td.saveCallback(reqObj);
	
}


