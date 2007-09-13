function BxeDrawer() {
	
	this.getCss = function() {
		var ar = new Array();
		ar.push("css/kupudrawerstyles.css");
		return ar;
	}
	
	this.init = function() {
	}
	
	this.start = function(options) {
		
		var body = document.getElementsByTagName("body")[0];
		var div = document.createElement("div");
		div.setAttribute("id","kupu-librarydrawer");
		body.appendChild(div);
		//var librariesURI = mozile_root_dir + "kupu/kupudrawers/demolibraries.xml"; 
	    var imageLibrariesURI = mozile_root_dir + "kupu/kupudrawers/demolibraries.xml"; 
	    var linkLibrariesURI = mozile_root_dir + "kupu/kupudrawers/demolibraries.xml"; 
	    var assetLibrariesURI = mozile_root_dir + "kupu/kupudrawers/demolibraries.xml"; 
        if (options["librariesURI"]) {
			imageLibrariesURI = options["librariesURI"];
		}
        if (options["imageLibrariesURI"]) {
			imageLibrariesURI = options["imageLibrariesURI"];
		}
		
        if (options['linkLibrariesURI']) {
            linkLibrariesURI = options['linkLibrariesURI'];
        }
        
        if (options['assetLibrariesURI']) {
            assetLibrariesURI = options['assetLibrariesURI'];
        }
        
        drawertool = new DrawerToolBxe();
        /* create and register the LinkLibrary-Drawer */
		var linktool = new LinkToolBxe();
        
        var liblinkdrawer = new LinkLibraryDrawer(linktool, mozile_root_dir+"/kupu/kupudrawers/librarydrawer.xsl", linkLibrariesURI, "kupu/kupudrawers/demolibrary1.xml");
        
        
        /* create and register the ImageDrawer */
        var imagetool = new ImageToolBxe();
        var imagedrawer = new ImageLibraryDrawer(imagetool, mozile_root_dir +"/kupu/kupudrawers/imagedrawer.xsl", imageLibrariesURI, "kupu/kupudrawers/demolibrary1.xml");
		
        
        /** 
        * create and register the Asset Drawer 
        * the asset Drawer is only called when the
        * `ShowAssetDrawer` is explicitly configured (config.xml)
        * for the asset button, otherwise default behaviours
        * are expected
        */
        var assettool = new AssetToolBxe();
        var assetdrawer = new AssetLibraryDrawer(assettool, mozile_root_dir+"/kupu/kupudrawers/assetdrawer.xsl", assetLibrariesURI, "kupu/kupudrawers/demolibrary1.xml");
        
        
        drawertool.registerDrawer('imagedrawer', imagedrawer);
		drawertool.registerDrawer('liblinkdrawer', liblinkdrawer);
	    drawertool.registerDrawer('assetdrawer', assetdrawer);	
         
        liblinkdrawer.editor = new Object();
		liblinkdrawer.editor.getBrowserName = function () {
			return "Mozilla";
		}
		
		imagedrawer.editor = new Object();
        imagedrawer.editor.getBrowserName = function() {
            return "Mozilla";
        }

        assetdrawer.editor = new Object();
        assetdrawer.editor.getBrowserName = function() {
            return "Mozilla";
        }
	}
	
	this.getScripts = function() {
		var ar = new Array();
		ar.push("kupu/sarissa.js");
		ar.push("kupu/kupuhelpers.js");
		ar.push("kupu/kupubasetools.js");
		//ar.push("kupu/kupudrawertool.js");
		//ar.push("kupu/kupulibrarydrawer.js");
		ar.push("kupu/kupudrawers.js");
		
		ar.push("plugins/Drawer/classes.js");
		return ar;
	}
}


