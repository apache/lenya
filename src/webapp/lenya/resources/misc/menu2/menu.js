function LUI() {
  //see if already instanciated
  if (lui) return lui;
  else lui = this;

  if (document.all) window.attachEvent('onload', function() { lui.init(); });
  else window.addEventListener('load', function() { lui.init(); }, false);
}


LUI.prototype = {
  _activeMenu:null,
  init:function() {
    //internal variables
    this._menurow = document.getElementById('menurow');
    this._page = document.getElementById('page');
    this._visibility = false;
    this._selTabID = 'Authoring';

    //create tabs
    this._tabs = document.getElementById('tabs');
    var tabTplStr = document.getElementById('tabTemplate').innerHTML;
    var s = '';
    for (var i=0; i<tabDef.length; i++) {
      var hash = {
        name:tabDef[i].name,
        selected:((this._selTabID==tabDef[i].name)?'selected':''),
        icon:(tabDef[i].icon?tabDef[i].icon:'/lenya/lenya/menu2/media/nix.gif'),
        href:tabDef[i].href
      };
      s += this._replaceHash(tabTplStr, hash);
    }
    this._tabs.innerHTML = s;

    //create menubar
    this._menubar = document.getElementById('menubar');
    var menubuttonTplStr = document.getElementById('menubuttonTemplate').innerHTML;
    var s = '';
    for (var i=0; i<menubarDef.length; i++) {
      var hash = {
        name:menubarDef[i].name,
        icon:(menubarDef[i].icon?menubarDef[i].icon:'/lenya/lenya/menu2/media/nix.gif')
      };
      s += this._replaceHash(menubuttonTplStr, hash);
    }
    this._menubar.innerHTML = s;

    //actions
    this.showMenu();

    //attach onclick handler
    if (document.all) document.body.attachEvent('onclick', function() { lui._doClickBody(window.event); });
    else document.body.addEventListener('click', function(evt) { lui._doClickBody(evt); }, false);
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
  _doClickMenuButton:function(menuID) {
    //find menu definition
    var menuDef = null;
    for (var i=0; i<menubarDef.length; i++) {
      if (menubarDef[i].name == menuID) {
        menuDef = menubarDef[i];
        break;
      }
    }
    if (!menuDef) return;

    //create menu
    var s = '';
    var menuitemTplStr = document.getElementById('menuitemTemplate').innerHTML;
    for (var i=0; i< menuDef.items.length; i++) {
      var hash = {
        name:menuDef.items[i].name,
        href:menuDef.items[i].href
      };
      s += this._replaceHash(menuitemTplStr, hash);
    }

    //draw the menu
    var menuEl = document.getElementById('menu_' + menuID);
    menuEl.innerHTML = s;
    this._showMenu(menuEl);
  },
  
  _replaceHash:function(s, hash) {
    for (var n in hash) s = s.replace(new RegExp('\\$' + n, 'g'), hash[n]);
    return s;
  },
  _doClickMenuItem:function(menuitemID, href) {
    this._hideMenu();
    window.frames['page'].location = href;
  },
  _showMenu:function(menuEl) {
    this._hideMenu();
    menuEl.className = 'menuvisible';
    menuEl.style.display = 'block';
    this._activeMenu = menuEl;
    document.getElementById('mask').style.display = 'block';
  },
  _hideMenu:function() {
    if (this._activeMenu) {
      this._activeMenu.style.display = 'none';
      this._activeMenu = null;
    }
    document.getElementById('mask').style.display = 'none';
  },
  _doClickBody:function(evt) {
    if (!this._activeMenu) return;
    var el = evt.srcElement || evt.target;
    while (el && (el.id != 'menurow')) el = el.parentNode;
    if (el) return;

    this._hideMenu();
  }
}

//instantiate lui
var lui = new LUI();
