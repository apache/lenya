
//
// Modify an IP range.
//
function iprange_change_profile(iprangeId) {
    iprange_change_profile(iprangeId, true);
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
function iprange_change_profile(iprangeId, newRange) {

    var ipRangeManager = getIPRangeManager();
    var range;
    if (newRange) {
        range = new Packages.org.apache.lenya.cms.ac.FileIPRange();
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
    
	    sendPageAndWait("ipranges/profile.xml", {
	    	"iprange-id" : iprangeId,
	    	"name" : name,
	    	"description" : description,
	    	"page-title" : "Edit IP Range",
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
	        if (newRange && ok) {
			    iprangeId = cocoon.request.get("iprange-id");
                var existingIPRange = ipRangeManager.getIPRange(iprangeId);
                if (existingIPRange != null) {
                    message = "This IP range already exists.";
                    ok = false;
                }
                else {
                    range = new Packages.org.apache.lenya.cms.ac.FileIPRange(
                        ipRangeManager.getConfigurationDirectory(), iprangeId);
                    ipRangeManager.add(range);
                    ok = true;
                }
	        }

            // save IP range	    
	       	if (ok) {
                range.setName(name);
                range.setDescription(description);
                range.setNetworkAddress(getIPString(net));
                range.setSubnetMask(getIPString(mask));
                range.save();
                break;
	       	}
	    }

    }
    
    var url;
    if (newRange) {
    	url = "../ipranges.html";
    }
    else {
    	url = "index.html";
    }
   	sendPage("redirect.html", { "url" : url });
}

//
// Add an IP range.
//
function iprange_add_iprange() {
    iprange_change_profile("", true);
}

function temp() {
    var ipRangeManager = getIPRangeManager();
	var ipRangeId = "";
	var name = "";
	var description = "";
	var message = "";
	
	while (true) {
		sendPageAndWait("ipranges/profile.xml", {
			"page-title" : "Add IP range",
			"iprange-id" : ipRangeId,
	    	"name" : name,
	    	"description" : description,
	    	"message" : message,
	    	"new-iprange" : true
		});
		
	    if (cocoon.request.get("cancel")) {
	    	break;
	    }
	    
		message = "";
		ipRangeId = cocoon.request.get("iprange-id");
		name = cocoon.request.get("name");
		description = cocoon.request.get("description");
		
		var existingIPRange = ipRangeManager.getIPRange(ipRangeId);
		if (existingIPRange != null) {
			message = "This IP range already exists.";
		}
		else {
			var configDir = ipRangeManager.getConfigurationDirectory();
			var range = new Packages.org.apache.lenya.cms.ac.FileIPRange(configDir, ipRangeId);
			range.setName(name);
			range.setDescription(description);
			range.save();
			ipRangeManager.add(range);
			break;
		}
	}
   	sendPage("redirect.html", { "url" : "../ipranges.html" });
}

//
// Delete IP range.
//
function iprange_delete_iprange() {

    var ipRangeManager = getIPRangeManager();
	var ipRangeId = cocoon.request.get("iprange-id");
	var range = ipRangeManager.getIPRange(ipRangeId);
	var name = range.getName();
	var showPage = true;
	
	while (showPage) {
		sendPageAndWait("ipranges/confirm-delete-common.xml", {
			"type" : "IP range",
			"id" : ipRangeId,
			"name" : name
		});
		
		if (cocoon.request.get("submit")) {
			ipRangeManager.remove(range);
			range['delete']();
			showPage = false;
		}
	}

   	sendPage("redirect.html", { "url" : "../ipranges.html" });
}

//
// Change the group affiliation of an IP range.
//
function iprange_change_groups(iprangeId) {
    var range = getIPRangeManager().getIPRange(iprangeId);
    
    var rangeGroupArray = range.getGroups();
    var rangeGroups = new java.util.ArrayList(java.util.Arrays.asList(rangeGroupArray));
    
    var iterator = getGroupManager().getGroups();
    var groups = new java.util.ArrayList();
    while (iterator.hasNext()) {
    	var group = iterator.next();
    	if (!rangeGroups.contains(group)) {
    		groups.add(group);
    	}
    }
    
    while (true) {
	    sendPageAndWait("ipranges/groups.xml", {
	    	"iprange-id" : iprangeId,
	    	"groups" : groups,
	    	"iprange-groups" : rangeGroups
	    });
	    
		var groupId = cocoon.request.get("group");
		if (cocoon.request.get("add_group") && groupId != "") {
			var group = getGroupManager().getGroup(groupId);
			if (!rangeGroups.contains(group)) {
				rangeGroups.add(group);
				groups.remove(group);
			}
		}
	    
		var rangeGroupId = cocoon.request.get("iprange_group");
		if (cocoon.request.get("remove_group") && rangeGroupId != "") {
			var group = getGroupManager().getGroup(rangeGroupId);
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
   	sendPage("redirect.html", { "url" : "index.html" });
}

