
//
// Modify a user.
//
function user_change_profile(userId) {

    var user = getUserManager().getUser(userId);
	var fullName = user.getFullName();
	var email = user.getEmail();
	var description = user.getDescription();
	
	// at the moment the loop is executed only once (no form validation)
	
    while (true) {
	    sendPageAndWait("users/profile.xml", {
	    	"user-id" : userId,
	    	"fullname" : fullName,
	    	"email" : email,
	    	"description" : description,
	    	"page-title" : "Edit Profile"
	    });
	    
	    if (cocoon.request.get("cancel")) {
	    	break;
	    }
	    
	    if (cocoon.request.get("submit")) {
		    fullName = cocoon.request.get("fullname");
	       	user.setFullName(fullName);
		    email = cocoon.request.get("email");
	       	user.setEmail(email);
	       	description = cocoon.request.get("description");
	       	user.setDescription(description);
	   		user.save();
	    	break;
	    }

    }
    
   	sendPage("redirect.html", { "url" : "index.html" });
}

//
// Change password as admin (don't check the old password)
//
function user_change_password_admin(userId) {
	user_change_password(false, userId);
}

//
// Change password as user (check the old password)
//
function user_change_password_user(userId) {
	user_change_password(true, userId);
}

//
// Change the password.
// checkPassword: (boolean) if the old password shall be checked
//
function user_change_password(checkPassword, userId) {

    var user = getUserManager().getUser(userId);
    var oldPassword = "";
    var newPassword = "";
    var confirmPassword = "";
    var message = "";
    
    while (true) {
	    sendPageAndWait("users/password.xml", {
	    	"user-id" : userId,
	    	"new-password" : newPassword,
	    	"confirm-password" : confirmPassword,
	    	"message" : message,
	    	"check-password" : checkPassword,
	    });
	    
	    if (cocoon.request.get("cancel")) {
	    	break;
	    }
	    
		if (cocoon.request.get("submit")) {	    
		    oldPassword = cocoon.request.get("old-password");
		    newPassword = cocoon.request.get("new-password");
		    confirmPassword = cocoon.request.get("confirm-password");
		    
		    if (checkPassword && !user.authenticate(oldPassword)) {
		    	message = "Wrong password!";
		    }
            else if (!newPassword.equals(confirmPassword)) {
		    	message = "New password and confirmed password are not equal!";
		    }
		    else {
	        	user.setPassword(newPassword);
	    		user.save();
	    		break;
		    }
		}

    }
    
   	sendPage("redirect.html", { "url" : "index.html" });
    
}

//
// Change the group affiliation of a user.
//
function user_change_groups(userId) {
    var user = getUserManager().getUser(userId);
    
    var userGroupArray = user.getGroups();
    var userGroups = new java.util.ArrayList(java.util.Arrays.asList(userGroupArray));
    
    var iterator = getGroupManager().getGroups();
    var groups = new java.util.ArrayList();
    while (iterator.hasNext()) {
    	var group = iterator.next();
    	if (!userGroups.contains(group)) {
    		groups.add(group);
    	}
    }
    
    while (true) {
	    sendPageAndWait("users/groups.xml", {
	    	"user-id" : userId,
	    	"groups" : groups,
	    	"user-groups" : userGroups
	    });
	    
		var groupId = cocoon.request.get("group");
		if (cocoon.request.get("add_group") && groupId != "") {
			var group = getGroupManager().getGroup(groupId);
			if (!userGroups.contains(group)) {
				userGroups.add(group);
				groups.remove(group);
			}
		}
	    
		var userGroupId = cocoon.request.get("user_group");
		if (cocoon.request.get("remove_group") && userGroupId != "") {
			var group = getGroupManager().getGroup(userGroupId);
			if (userGroups.contains(group)) {
				userGroups.remove(group);
				groups.add(group);
			}
		}

	    if (cocoon.request.get("cancel")) {
	    	break;
	    }
	    
		if (cocoon.request.get("submit")) {
			user.removeFromAllGroups();
			var iterator = userGroups.iterator();
			while (iterator.hasNext()) {
				var group = iterator.next();
				group.add(user);
			}
			user.save();
			break;
		}
	}
   	sendPage("redirect.html", { "url" : "index.html" });
}

//
// Add a user.
//
function user_add_user() {

	var userId = "";
	var email = "";
	var fullName = "";
	var description = "";
	var message = "";
	var userManager = getUserManager();
	
	while (true) {
		sendPageAndWait("users/profile.xml", {
			"page-title" : "Add User: Profile",
			"user-id" : userId,
	    	"fullname" : fullName,
	    	"email" : email,
	    	"description" : description,
	    	"message" : message,
	    	"new-user" : true
		});
		
	    if (cocoon.request.get("cancel")) {
	    	break;
	    }
	    
		message = "";
		userId = cocoon.request.get("user-id");
		email = cocoon.request.get("email");
		fullName = cocoon.request.get("fullname");
		description = cocoon.request.get("description");
		
		var existingUser = userManager.getUser(userId);
		
		if (existingUser != null) {
			message = "This user already exists.";
		}
		else {
			var configDir = userManager.getConfigurationDirectory();
			var user = new Packages.org.apache.lenya.cms.ac.FileUser(configDir, userId, fullName, email, "");
			user.setDescription(description);
			user.save();
			userManager.add(user);
			break;
		}
	}
   	sendPage("redirect.html", { "url" : "../users.html" });
}

//
// Delete user.
//
function user_delete_user() {

	var userManager = getUserManager();
	var userId = cocoon.request.get("user-id");
	var user = userManager.getUser(userId);
	var fullName = user.getFullName();
	var showPage = true;
	
	while (showPage) {
		sendPageAndWait("users/confirm-delete.xml", {
			"user-id" : userId,
			"fullname" : fullName
		});
		
		if (cocoon.request.get("submit")) {
			userManager.remove(user);
			user['delete']();
			showPage = false;
		}
	}

   	sendPage("redirect.html", { "url" : "../users.html" });
}
