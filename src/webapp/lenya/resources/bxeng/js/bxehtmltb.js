/* ***** BEGIN LICENSE BLOCK *****
 * Licensed under Version: MPL 1.1/GPL 2.0/LGPL 2.1
 * Full Terms at http://mozile.mozdev.org/license.html
 *
 * Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License
 * for the specific language governing rights and limitations under the
 * License.
 *
 * The Original Code is Playsophy code.
 *
 * The Initial Developer of the Original Code is Playsophy
 * Portions created by the Initial Developer are Copyright (C) 2002-2003
 * the Initial Developer. All Rights Reserved.
 *
 * Contributor(s):
 *		Karl Guertin <grayrest@grayrest.com>
 *
 * ***** END LICENSE BLOCK ***** */

// $Id: bxehtmltb.js 879 2004-11-19 10:01:00Z chregu $

/*******************************************************************************************************
 * Simple, html-based editing toolbar for mozile: it appears once an editable area is 
 * selected: V0.46
 *
 * POST04: 
 * - make OO - too hacky (see mozileModify's button disable!)
 * - experiment with "select" events after reselection (cp) to indent/dedent buttons
 * - make invisible if not in editable area [partially done: have to work on arrow in/out]
 * - print: hide this toolbar (call disable)
 * - move bar to bottom or side of screen if trying to edit the top?
 * (see: http://devedge.netscape.com/toolbox/examples/2002/xb/xbPositionableElement/)
 * - preload images
 * - support for a "read-writetext" value that only allows text editing and
 * does not allow ANY formatting. Should be bar appear or be greyed out?
 * - HTML bar as divs and spans and not a table: use moz-outline on imgs ...
 * - do equivalent in XUL
 *   - add as proper toolbar: http://devedge.netscape.com/viewsource/2002/toolbar/
 *******************************************************************************************************/

// image should be in the same directory as this file. This file is in mozile_root_dir. The loader
// sets this constant.


var ptbStyles = new Array(
			"height", "30px",
			"background-color", "#ff6600",
			"border", "0px",
			"position", "fixed",
			"z-index", "999", // important to be higher than all else on page
			"-moz-user-select", "none",
			"-moz-user-modify", "read-only",
			"-moz-user-input", "enabled", // means enabled: overrides page default which could be "disabled"
			"top", "16px",
			"width", "100%",
			"margin-left", "0px");


var buttonStyles = new Array(
	"height", "20px", 
	"width", "20px", 
	"border", "solid 1px #C0C0C0",
	"background-color", "##ff6600",
	"-moz-user-modify", "read-only",
	"-moz-user-input", "disabled",
	"-moz-user-select", "none",
	"background-image","url("+buttonImgLoc+")");

var ptbActive = false;
var ptbEnabled = false;

function ptbenable()
{
	var ptb = document.getElementById("playtoolbar");
	if(!ptb)
		return;
	ptb.style.setProperty("display", 'table', '');
	ptbEnabled = true;
}

function ptbdisable()
{

}



