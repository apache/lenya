// $Id: tabs.js,v 1.1 2003/06/04 15:08:44 gregor Exp $

function Tab(i){
	// Variables for customisation:
	var numberOfTabs = 5;
	var colourOfInactiveTab = "#999966";
	var colourOfActiveTab = "#cccc99";
	var colourOfInactiveLink = "#333333";
	var colourOfActiveLink = "#ffffcc";
	// end variables
	if (document.getElementById){
		for (f=1;f<numberOfTabs+1;f++){
			document.getElementById('contentblock'+f).style.display='none';
			document.getElementById('link'+f).style.background=colourOfInactiveTab;
			document.getElementById('link'+f).style.color=colourOfInactiveLink;
		}
		document.getElementById('contentblock'+i).style.display='block';
		document.getElementById('link'+i).style.background=colourOfActiveTab;
		document.getElementById('link'+i).style.color=colourOfActiveLink;
	}
}
