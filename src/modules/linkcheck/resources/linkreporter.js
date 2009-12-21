/*
* Licensed to the Apache Software Foundation (ASF) under one or more
* contributor license agreements.  See the NOTICE file distributed with
* this work for additional information regarding copyright ownership.
* The ASF licenses this file to You under the Apache License, Version 2.0
* (the "License"); you may not use this file except in compliance with
* the License.  You may obtain a copy of the License at
*
*     http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/

var req;

function processReqChange() {
    // only if req shows "loaded"
    if (req.readyState == 4) {
        // only if "OK"
        if (req.status == 200) {
            // ...processing statements go here...
            //parse link report for broken links
            var rptLinks = req.responseXML.getElementsByTagName("link");
            var brokenLinks = new Array(rptLinks.length);
            brokenCount=0;
            for(var i = 0; i < rptLinks.length; i++) {  // Loop through the returned links
                if (rptLinks[i].hasAttribute("status") && rptLinks[i].getAttribute("status") == "404") {
                    brokenLinks[brokenCount++] = rptLinks[i].getAttribute("href");
                }
            } 
            if (brokenCount > 0) {
                //get link elements from dom
                var links = document.getElementById("page").getElementsByTagName("a");
                for (var i = 0; i < links.length; i++) {  // Loop through the links in the doc
                    //for each link, check to see if it is in broken list
                    for (var j = 0; j < brokenLinks.length; j++) {
                        if (brokenLinks[j] == links[i]) {
                            //if it is, give it class attribute with value "brokenlink"
                            links[i].setAttribute("class", "brokenlink")
                        }
                    }
                }
            }
        } else {
            alert("There was a problem retrieving the XML data:\n" +
                req.statusText);
        }
    }
}

function loadXMLDoc(url) {
	req = false;
    // branch for native XMLHttpRequest object
    if(window.XMLHttpRequest) {
    	try {
			req = new XMLHttpRequest();
        } catch(e) {
			req = false;
        }
    // branch for IE/Windows ActiveX version
    } else if(window.ActiveXObject) {
       	try {
        	req = new ActiveXObject("Msxml2.XMLHTTP");
      	} catch(e) {
        	try {
          		req = new ActiveXObject("Microsoft.XMLHTTP");
        	} catch(e) {
          		req = false;
        	}
		}
    }
	if(req) {
		req.onreadystatechange = processReqChange;
		req.open("GET", url, true);
		req.send("");
	}
}

reportlinks = function() {
  //get link report
  loadXMLDoc("?lenya.usecase=linkcheck.getLinks&asXML=true");
}

//  assign reportlinks function to onload

function addOnLoad(newFunction) { 
    var oldOnload = window.onload; 
    if (typeof window.onload != 'function') { 
      window.onload = newFunction; 
    } else { 
      window.onload = function() { oldOnload(); newFunction(); } 
    } 
} 

addOnLoad(reportlinks); 