/* These are the parameters to define the appearance of the ToC. */
var
	showNumbers = true, 		// display the ordering strings: yes=true | no=false
	backColor = "#003366",		// background color of the ToC 
	normalColor = "#FFFFFF",	// text color of the ToC headlines
	currentColor = "#F0FFFF",	// text color of the actual line just clicked on
	titleColor = "#FFFFFF",		// text color of the title "Table of Contents"
	mLevel = 1,					// number of levels minus 1 the headlines of which are presentet with large and bold fonts   
	
	textSizes = new Array(1, 0.7, 0.6, 0.8, 0.7),			// font-size factors for: [0] the title "Table of Contents", [1] larger and bold fonts [2] smaller fonts if MS Internet Explorer [3] larger and bold fonts [4] smaller fonts if Netscape Navigator.
	
	fontTitle = "Helvetica,Arial", // font-family of the title "Table of Contents"
	fontLines = "Helvetica,Arial"; // font-family of the headlines  
