function LUI() {
  //see if already instanciated
  if (lui) return lui;
  else lui = this;

  if (document.all) window.attachEvent('onload', function() { lui.init(); });
  else window.addEventListener('load', function() { lui.init(); }, false);
}


LUI.prototype = {
  init:function() {
    //internal variables
    this._menurow = document.getElementById('menurow');
    this._page = document.getElementById('page');
    this._visibility = false;
    this._selTabID = 'Admin';

    //create tabs
    this._tabs = document.getElementById('tabs');
    var tabTplStr = document.getElementById('tabTemplate').innerHTML;
    var s = '';
    for (var i=0; i<tabDef.length; i++) {
      var name = tabDef[i].name;
      var selected = ((this._selTabID==tabDef[i].name)?'selected':'');
      var icon = (tabDef[i].icon?tabDef[i].icon:'media/nix.gif');
      var href = tabDef[i].href;
      s += tabTplStr.replace(/\$name/g, name).replace(/\$selected/g, selected).replace(/\$icon/g, icon).replace(/\$href/g, href);
    }
    this._tabs.innerHTML = s;

    //create menubar
    this._menubar = document.getElementById('menucontent');
    var menuTplStr = document.getElementById('menuTemplate').innerHTML;
    var s = '';
    for (var i=0; i<menubarDef.length; i++) {
      var name = menubarDef[i].name;
      var icon = (menubarDef[i].icon?menubarDef[i].icon:'media/nix.gif');
      s += menuTplStr.replace(/\$name/g, name).replace(/\$icon/g, icon);
    }
    this._menubar.innerHTML = s;

    //actions
    this.showMenu();
  },
  showMenu:function() {
    this._setMenuVisibility(true);
  },
  hideMenu:function() {
    this._setMenuVisibility(false);
  },
  _setMenuVisibility:function(newVis) {
    //set new visibility
    this._visibility = newVis;

    //reflect visibility
    this._menurow.style.display = (this._visibility?'':'none');

    //force a resize of the iframe for Mozilla
    if (!document.all) {
      this._page.style.height = '101%';
      this._page.style.height = '100%';
    }
  },

  _doClickTab:function(tabID, tabHref) {
    //deselect current tab
    document.getElementById(this._selTabID).className = 'tab';

    //set new selected tab
    this._selTabID = tabID;

    //select the new tab
    document.getElementById(this._selTabID).className = 'tabselected';

    //set the body class to reflect the selected tab
    document.body.className = this._selTabID;

    //show/hide menu according to whether the live tab has been choosen or not
    if (this._selTabID.id == 'live') this.hideMenu();
    else this.showMenu();

    if (tabHref) window.frames['page'].location = tabHref;
  },
  _doClickMenu:function(menuID) {
alert('we should now open the menu having id ' + menuID);
  }
}

//instantiate lui
var lui = new LUI();
