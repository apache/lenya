
//
// Add a group.
//
function group_add_group() {

	var accessController = getAccessController();
	var groupId = "";
	var name = "";
	var description = "";
	var message = "";
	
	while (true) {
		sendPageAndWait("groups/lenya.usecase.add_group/profile.xml", {
			"page-title" : "Add Group: Profile",
			"group-id" : groupId,
	    	"name" : name,
	    	"description" : description,
	    	"message" : message,
	    	"new-group" : true
		});
		
		message = "";
		groupId = cocoon.request.get("user-id");
		name = cocoon.request.get("name");
		description = cocoon.request.get("description");
		
		var existingGroup = accessController.getGroupManager().getGroup(groupId);
		
		if (existingGroup != null) {
			message = "This group already exists.";
		}
		else {
			var configDir = accessController.getGroupManager().getConfigurationDirectory();
			var group = new Packages.org.apache.lenya.cms.ac.FileGroup(configDir, groupId, fullName, email, "");
			user.setDescription(description);
			user.save();
			accessController.getUserManager().add(user);
			break;
		}
	}
   	sendPage("redirect.html", { "url" : "../users.html" });
}

//
// Delete group.
//
function group_delete_group() {

	var accessController = getAccessController();
	var userId = cocoon.request.get("user-id");
	var user = accessController.getUserManager().getUser(userId);
	var fullName = user.getFullName();
	var showPage = true;
	
	while (showPage) {
		sendPageAndWait("users/lenya.usecase.delete_user/confirm-delete.xml", {
			"user-id" : userId,
			"fullname" : fullName
		});
		
		if (cocoon.request.get("submit")) {
			accessController.getUserManager().remove(user);
			user['delete']();
			showPage = false;
		}
	}

   	sendPage("redirect.html", { "url" : "../users.html" });
}

//
// Modify a group.
//
function group_change_profile(groupId) {

	var accessController = getAccessController();
    var group = accessController.getGroupManager().getGroup(groupId);
	var name = group.getName();
	var description = group.getDescription();
	
	// at the moment the loop is executed only once (no form validation)
	
    while (true) {
	    sendPageAndWait("groups/lenya.usecase.change_profile/profile.xml", {
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
    
   	sendPage("redirect.html", { "url" : "index.html" });
}


//
// Change the members of a group.
//
function group_change_members(groupId) {
	var accessController = getAccessController();
    var group = accessController.getGroupManager().getGroup(groupId);
    
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
    
    var userIterator = accessController.getUserManager().getUsers();
    while (userIterator.hasNext()) {
    	var user = userIterator.next();
    	if (!groupUsers.contains(user)) {
    		otherUsers.add(user);
    	}
    }
    
    while (true) {
	    sendPageAndWait("groups/lenya.usecase.change_members/members.xml", {
	    	"group-id" : groupId,
	    	"users" : otherUsers,
	    	"group-users" : groupUsers,
	    	"machines" : otherMachines,
	    	"group-machines" : groupMachines,
	    });
	    
		var otherUserId = cocoon.request.get("user");
		if (cocoon.request.get("add_user") && otherUserId != "") {
			var user = accessController.getUserManager().getUser(otherUserId);
			if (!groupUsers.contains(user)) {
				groupUsers.add(user);
				otherUsers.remove(user);
			}
		}
	    
		var groupUserId = cocoon.request.get("group_user");
		if (cocoon.request.get("remove_user") && groupUserId != "") {
			var user = accessController.getUserManager().getUser(groupUserId);
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
			}
			
			group.save();
			break;
		}
	}
   	sendPage("redirect.html", { "url" : "index.html" });
}

