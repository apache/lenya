
//
// Modify a group.
//
function group_change_profile(groupId) {

	resolve();
	try {
	
	    var groupManager = getAccreditableManager().getGroupManager();
	    var group = groupManager.getGroup(groupId);
		var name = group.getName();
		var description = group.getDescription();
		
		// at the moment the loop is executed only once (no form validation)
		
	    while (true) {
		    cocoon.sendPageAndWait("groups/" + groupId + "/profile.xml", {
		    	"group-id" : groupId,
		    	"name" : name,
		    	"description" : description,
		    	"page-title" : "Edit Group"
		    });
		    
		    if (cocoon.request.get("cancel")) {
		    	break;
		    }
		    
		    if (cocoon.request.get("submit")) {
			    name = cocoon.request.get("name");
		       	group.setName(name);
		       	description = cocoon.request.get("description");
		       	group.setDescription(description);
		   		group.save();
		    	break;
		    }
	
	    }
	    
	   	cocoon.sendPage("redirect.html", { "url" : "index.html" });
   	}
   	finally {
	   	release();
   	}
}


//
// Change the members of a group.
//
function group_change_members(groupId) {

	resolve();
	try {
	
	    var userManager = getAccreditableManager().getUserManager();
	    var groupManager = getAccreditableManager().getGroupManager();
	    var group = groupManager.getGroup(groupId);
	    
	    var memberArray = group.getMembers();
	    
	    var groupUsers = new java.util.ArrayList();
	    var otherUsers = new java.util.ArrayList();
	    var groupMachines = new java.util.ArrayList();
	    var otherMachines = new java.util.ArrayList();
	    
	    for (var i = 0; i < memberArray.length; i++) {
	    	var member = memberArray[i];
	    	if (member instanceof Packages.org.apache.lenya.cms.ac.User) {
	    		groupUsers.add(member);
	    	}
	    	if (member instanceof Packages.org.apache.lenya.cms.ac.Machine) {
	    		groupMachines.add(machine);
	    	}
	    }
	    
	    var userIterator = userManager.getUsers();
	    while (userIterator.hasNext()) {
	    	var user = userIterator.next();
	    	if (!groupUsers.contains(user)) {
	    		otherUsers.add(user);
	    	}
	    }
	    
	    while (true) {
		    cocoon.sendPageAndWait("groups/" + groupId + "/members.xml", {
		    	"group-id" : groupId,
		    	"users" : otherUsers,
		    	"group-users" : groupUsers,
		    	"machines" : otherMachines,
		    	"group-machines" : groupMachines,
		    });
		    
			var otherUserId = cocoon.request.get("user");
			if (cocoon.request.get("add_user") && otherUserId != "") {
				var user = userManager.getUser(otherUserId);
				if (!groupUsers.contains(user)) {
					groupUsers.add(user);
					otherUsers.remove(user);
				}
			}
		    
			var groupUserId = cocoon.request.get("group_user");
			if (cocoon.request.get("remove_user") && groupUserId != "") {
				var user = userManager.getUser(groupUserId);
				if (groupUsers.contains(user)) {
					groupUsers.remove(user);
					otherUsers.add(user);
				}
			}
	
		    if (cocoon.request.get("cancel")) {
		    	break;
		    }
		    
			if (cocoon.request.get("submit")) {
				group.removeAllMembers();
				
				var userIterator = groupUsers.iterator();
				while (userIterator.hasNext()) {
					var user = userIterator.next();
					group.add(user);
					user.save();
				}
				
				break;
			}
		}
	   	cocoon.sendPage("redirect.html", { "url" : "index.html" });
   	}
   	finally {
	   	release();
   	}
}

//
// Add a group.
//
function group_add_group() {

	resolve();
	try {
	    var groupManager = getAccreditableManager().getGroupManager();
		var groupId = "";
		var name = "";
		var description = "";
		var message = "";
		
		while (true) {
			cocoon.sendPageAndWait("groups/profile.xml", {
				"page-title" : "Add Group",
				"group-id" : groupId,
		    	"name" : name,
		    	"description" : description,
		    	"message" : message,
		    	"new-group" : true
			});
			
		    if (cocoon.request.get("cancel")) {
		    	break;
		    }
		    
			message = "";
			groupId = cocoon.request.get("group-id");
			name = cocoon.request.get("name");
			description = cocoon.request.get("description");
			
			var existingGroup = groupManager.getGroup(groupId);
			if (existingGroup != null) {
				message = "This group already exists.";
			}
			else if (!Packages.org.apache.lenya.cms.ac.AbstractItem.isValidId(groupId)) {
	          	message = "This is not a valid group ID.";
			}
			else {
				var configDir = groupManager.getConfigurationDirectory();
				var group = new Packages.org.apache.lenya.cms.ac.FileGroup(configDir, groupId);
				group.setName(name);
				group.setDescription(description);
				group.save();
				groupManager.add(group);
				break;
			}
		}
	   	cocoon.sendPage("redirect.html", { "url" : "../groups.html" });
   	}
   	finally {
	   	release();
   	}
}

//
// Delete group.
//
function group_delete_group() {

	resolve();
	try {
	
	    var groupManager = getAccreditableManager().getGroupManager();
		var groupId = cocoon.request.get("group-id");
		var group = groupManager.getGroup(groupId);
		var name = group.getName();
		var showPage = true;
		
		while (showPage) {
			cocoon.sendPageAndWait("groups/confirm-delete-common.xml", {
				"type" : "group",
				"id" : groupId,
				"name" : name
			});
			
			if (cocoon.request.get("cancel")) {
				break;
			}
			
			if (cocoon.request.get("submit")) {
				groupManager.remove(group);
				var members = group.getMembers();
				group['delete']();
				for (var i = 0; i < members.length; i++) {
				    members[i].save();
				}
				showPage = false;
			}
		}
	
	   	cocoon.sendPage("redirect.html", { "url" : "../groups.html" });
   	}
   	finally {
   		release();
   	}
}

