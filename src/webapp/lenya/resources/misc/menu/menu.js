function initialize()
        //runs at onload of window
        {
        if (document.getElementById("articleBody") != null) initArticle(); //lays out the article in columns(if one exists)
        userEventsInit(); //set up all user events
        document.onmousemove = mousePosition; //moz
        }

var mouseX = mouseY =0;
menus = new Array("File","Search","Help","Debug")
var allEvents = new Array()
var activeMenu = null; //defines which menu is currently open

function screenObject()
        {

        this.bottom = function(){
                if (document.body.scrollHeight) return document.body.scrollHeight};

        this.height = function() {
                if (document.body.offsetHeight) return document.body.offsetHeight;}

        this.visHeight = function() {
                if (window.innerHeight) return window.innerHeight;
                if (document.body.clientHeight) return document.body.clientHeight;}

        this.width = function() {
                if (document.body.offsetWidth) return document.body.offsetWidth;}

        this.scrollTop = function() {
                if(document.body.scrollTop) return document.body.scrollTop
                if (window.pageYOffset) return window.pageYOffset;
                else return 0;};


        }

function mousePosition(e)
        {
        if (e) event = e;   //for Netscape
        mouseX = event.clientX;
        mouseY = event.clientY;
        }

function menuOver()
        {
        document.onclick = null;
        if (document.all) event.cancelBubble=true
        }
function menuOut()
        {
        document.onclick = eventHideMenu;
        if (document.all) event.cancelBubble=true
        }

function eventShowPopUp(target)
        //this will display info pop ups, for example art event details

        {
        if (activeMenu != null) eventHideMenu()
        activeMenu = target;
        obj = document.getElementById(activeMenu)
        obj.style.display = "block"
        if (event) event.cancelBubble=true
        }

function eventShowArtPopUp(target)
        //this will display info pop ups, for example art event details

        {

        if (activeMenu != null) eventHideMenu()
        activeMenu = target;
        obj = document.getElementById(activeMenu)
        obj.style.display = "block"
        obj.style.visibility = "visible"

        obj.style.top = -20+mouseY+ihtScreen.scrollTop()+"px"
        if (event) event.cancelBubble=true
        }

function eventShowMenu(e)
        //turns on the display for nav menus
        {
        if (activeMenu != null) eventHideMenu()
        activeMenu = "menu"+this.id.substring(3,this.id.length)
        obj = document.getElementById(activeMenu)
        if (obj) obj.style.visibility = "visible"
        if (document.all) document.onclick = eventHideMenu;
        event.cancelBubble=true

        }

function eventHideMenu()
        //hides nav menus and pop ups
        {
        document.onclick = null;
        if (activeMenu != null)
                {
                obj = document.getElementById(activeMenu)
                obj.style.visibility = "hidden"
                }
        }

function userEventsInit()
        {
        //nav events
                for (i=0; i < menus.length; i++)
                {
                obj = document.getElementById("nav"+menus[i])
                obj.onclick = eventShowMenu;

                obj = document.getElementById("menu"+menus[i])
                obj.onmouseover = menuOver;
                obj.onmouseout = menuOut;
                }

 }

