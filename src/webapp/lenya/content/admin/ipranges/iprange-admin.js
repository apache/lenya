
//
// Modify an IP range.
//
function iprange_change_profile(iprangeId) {

    var ipRangeManager = getIPRangeManager();
    var range = ipRangeManager.getIPRange(iprangeId);
	var name = range.getName();
	var description = range.getDescription();
	
	// at the moment the loop is executed only once (no form validation)
	
    while (true) {
	    sendPageAndWait("ipranges/profile.xml", {
	    	"iprange-id" : iprangeId,
	    	"name" : name,
	    	"description" : description,
	    	"page-title" : "Edit IP Range"
	    });
	    
	    if (cocoon.request.get("cancel")) {
	    	break;
	    }
	    
	    if (cocoon.request.get("submit")) {
		    name = cocoon.request.get("name");
	       	range.setName(name);
	       	description = cocoon.request.get("description");
	       	range.setDescription(description);
	   		range.save();
	    	break;
	    }

    }
    
   	sendPage("redirect.html", { "url" : "index.html" });
}


//
// Add an IP range.
//
function iprange_add_iprange() {

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

