/*
* Copyright 1999-2004 The Apache Software Foundation
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*     http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/

//
// Modify an IP range.
//
function iprangeChangeProfile() {
    iprangeChangeProfile(false);
}

//
// Converts a 0..255 integer value to a -128..127 byte value.
//
function getByte(intValue) {
   var byteValue;
   if (intValue <= 127) {
       byteValue = intValue;
   }
   else {
       byteValue = intValue - 256;
   }
   return byteValue;
}

//
// Converts a -128..127 byte value to a 0..255 integer value.
//
function getInt(byteValue) {
   var intValue;
   if (byteValue >= 0) {
       intValue = "" + byteValue;
   }
   else {
       intValue = "" + (256 + byteValue);
   }
   return intValue;
}

//
// Checks if value is a valid IP number.
//
function checkIPNumber(value) {
    var parsedValue = parseInt(value);
    var result = !isNaN(parsedValue) && 0 <= parsedValue && parsedValue <= 255;
    return result;
}

//
// Converts a byte array to an IP address string.
//
function getIPString(byteArray) {
	return byteArray[0] + "." + byteArray[1] + "." + byteArray[2] + "." + byteArray[3];
}

//
// Modify an IP range.
//
function iprangeChangeProfile(newRange) {

	resolve();
	try {
	    
	    var iprangeId = "";
	    if (!(newRange == true)) {
	    	iprangeId = getAccreditableId();
	    }
	    var redirectUri = getRequestUri();
	    var ipRangeManager = getAccreditableManager().getIPRangeManager();
	    var range;
	    if (newRange == true) {
	        range = new Packages.org.apache.lenya.ac.file.FileIPRange();
	    }
	    else {
	        range = ipRangeManager.getIPRange(iprangeId);
	    }
		var name = range.getName();
		var description = range.getDescription();
		
		var net = new Array(4);
		var mask = new Array(4);
	
		for (i = 0; i < 4; i++) {
	        net[i] = getInt(range.getNetworkAddress().getAddress()[i]);
	        mask[i] = getInt(range.getSubnetMask().getAddress()[i]);
		}
		
		var netErrors = new Array(4);
		var maskErrors = new Array(4);
		
	    var message = "";
		
	  	for (i = 0; i < 4; i++) {
	        netErrors[i] = "false";
	        maskErrors[i] = "false";
	   	}
		
	    while (true) {
	    
	    	var pageTitle;
	    	if (newRange == true) {
	    		pageTitle = "Add IP Range";
	    	}
	    	else {
	    		pageTitle = "Edit IP Range";	    		
	    	}
	        
		    cocoon.sendPageAndWait("ipranges/profile.xml", {
		    	"iprange-id" : iprangeId,
		    	"name" : name,
		    	"description" : description,
		    	"page-title" : pageTitle,
		    	"net" : java.util.Arrays.asList(net),
		    	"net-errors" : java.util.Arrays.asList(netErrors),
		    	"mask" : java.util.Arrays.asList(mask),
		    	"mask-errors" : java.util.Arrays.asList(maskErrors),
		    	"new-iprange" : newRange,
		    	"message" : message
		    });
		    
		  	for (i = 0; i < 4; i++) {
		        netErrors[i] = "false";
		        maskErrors[i] = "false";
		   	}
		   	message = "";
		
		    if (cocoon.request.get("cancel")) {
		    	break;
		    }
		    
		    if (cocoon.request.get("submit")) {
	
		        var ok = true;
		        
	            // get values from request
			    name = cocoon.request.get("name");
		       	description = cocoon.request.get("description");
		       	for (i = 0; i < 4; i++) {
		       	    net[i] = cocoon.request.get("net-" + (i+1));
		       	    if (!checkIPNumber(net[i])) {
		       	        netErrors[i] = "true";
		       	        ok = false;
		       	        message = "Please correct the errors.";
		       	    }
		       	    
		       	    mask[i] = cocoon.request.get("mask-" + (i+1));
		       	    if (!checkIPNumber(mask[i])) {
		       	        maskErrors[i] = "true";
		       	        ok = false;
		       	        message = "Please correct the errors.";
		       	    }
		       	}
		       	
		       	// initialize new IP range
		        if (newRange == true) {
				    iprangeId = cocoon.request.get("iprange-id");
			        if (ok) {
		                var existingIPRange = ipRangeManager.getIPRange(iprangeId);
		                if (existingIPRange != null) {
		                    message = "This IP range already exists.";
		                    ok = false;
		                }
						else if (!Packages.org.apache.lenya.ac.impl.AbstractItem.isValidId(iprangeId)) {
		                	message = "This is not a valid IP range ID. [" + iprangeId + "]";
		                	ok = false;
		                }
		                else {
		                    range = new Packages.org.apache.lenya.ac.file.FileIPRange(
		                        ipRangeManager.getConfigurationDirectory(), iprangeId);
		                    ipRangeManager.add(range);
		                }
		            }
		        }
	
	            // save IP range	    
		       	if (ok == true) {
	                range.setName(name);
	                range.setDescription(description);
	                range.setNetworkAddress(getIPString(net));
	                range.setSubnetMask(getIPString(mask));
	                range.save();
	                break;
		       	}
		    }
	
	    }
	    
	   	cocoon.redirectTo(redirectUri);
   	}
   	finally {
   		release();
   	}
}

//
// Add an IP range.
//
function iprangeAddIPRange() {
    iprangeChangeProfile(true);
}

//
// Delete IP range.
//
function iprangeDeleteIPRange() {

	resolve();
	try {
		var redirectUri = getRequestUri();
	    var ipRangeManager = getAccreditableManager().getIPRangeManager();
		var ipRangeId = cocoon.request.get("iprange-id");
		var range = ipRangeManager.getIPRange(ipRangeId);
		var name = range.getName();
		var showPage = true;
		
		while (showPage) {
			cocoon.sendPageAndWait("ipranges/confirm-delete-common.xml", {
				"type" : "IP Range",
				"id" : ipRangeId,
				"name" : name
			});
			
			if (cocoon.request.get("cancel")) {
				break;
			}
			
			if (cocoon.request.get("submit")) {
				ipRangeManager.remove(range);
				range['delete']();
				showPage = false;
			}
		}
	
	   	cocoon.redirectTo(redirectUri);
   	}
   	finally {
   		release();
   	}
}

//
// Change the group affiliation of an IP range.
//
function iprangeChangeGroups() {

	var redirectUri = getRequestUri();
	var iprangeId = getAccreditableId();
    var range = getAccreditableManager().getIPRangeManager().getIPRange(iprangeId);
    
    var rangeGroupArray = range.getGroups();
    var rangeGroups = new java.util.ArrayList(java.util.Arrays.asList(rangeGroupArray));
    
    var groupArray = getAccreditableManager().getGroupManager().getGroups();
    var groups = new java.util.ArrayList();
    for (var i = 0; i < groupArray.length; i++) {
    	if (!rangeGroups.contains(groupArray[i])) {
    		groups.add(groupArray[i]);
    	}
    }
    
    while (true) {
	    cocoon.sendPageAndWait("ipranges/" + iprangeId + "/groups.xml", {
	    	"iprange-id" : iprangeId,
	    	"groups" : groups,
	    	"iprange-groups" : rangeGroups
	    });
	    
		var groupId = cocoon.request.get("group");
		if (cocoon.request.get("add_group") && groupId != "") {
			var group = getAccreditableManager().getGroupManager().getGroup(groupId);
			if (!rangeGroups.contains(group)) {
				rangeGroups.add(group);
				groups.remove(group);
			}
		}
	    
		var rangeGroupId = cocoon.request.get("iprange_group");
		if (cocoon.request.get("remove_group") && rangeGroupId != "") {
			var group = getAccreditableManager().getGroupManager().getGroup(rangeGroupId);
			if (rangeGroups.contains(group)) {
				rangeGroups.remove(group);
				groups.add(group);
			}
		}

	    if (cocoon.request.get("cancel")) {
	    	break;
	    }
	    
		if (cocoon.request.get("submit")) {
			range.removeFromAllGroups();
			var iterator = rangeGroups.iterator();
			while (iterator.hasNext()) {
				var group = iterator.next();
				group.add(range);
			}
			range.save();
			break;
		}
	}
   	cocoon.redirectTo(redirectUri);
}

