
var accessController = undefined;

function initAccessController() {
	if (accessController == undefined) {

	    var publication = Packages.org.apache.lenya.cms.publication.PublicationFactory
	                          .getPublication(cocoon.request, cocoon.context);
	    var configDir = new java.io.File(publication.getDirectory(),
	                       "config" + java.io.File.separator + "ac");
	    accessController = new Packages.org.apache.lenya.cms.ac2.file.FileAccessController(configDir);
    }
}

//
// The user admin main screen.
//
function userAdmin() {
    sendPage("users.flow");
}

//
// Modify a user.
//
function user_change_profile(userId) {

	initAccessController();
    var user = accessController.getUserManager().getUser(userId);
	var fullName = user.fullName;
	var email = user.email;
	
	// at the moment the loop is executed only once (no form validation)
	
    while (true) {
	    sendPageAndWait("users/lenya.usecase.change_profile/profile.xml", {
	    	"user-id" : userId,
	    	"fullname" : fullName,
	    	"email" : email
	    });
	    
	    if (cocoon.request.get("cancel")) {
	    	break;
	    }
	    
	    if (cocoon.request.get("submit")) {
		    fullName = cocoon.request.get("fullname");
	       	user.setFullName(fullName);
		    email = cocoon.request.get("email");
	       	user.setEmail(email);
	   		user.save();
	    	break;
	    }

    }
    
   	sendPage("redirect.html", { "url" : "index.html" });
}


function user_change_password_admin(userId) {
	user_change_password(false, userId);
}

function user_change_password_user(userId) {
	user_change_password(true, userId);
}

function user_change_password(checkPassword, userId) {

	initAccessController();
    var user = accessController.getUserManager().getUser(userId);
    var oldPassword = "";
    var newPassword = "";
    var confirmPassword = "";
    var message = "";
    
    while (true) {
	    sendPageAndWait("users/lenya.usecase.change_password/password.xml", {
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


function user_change_groups(userId) {
	initAccessController();
    var user = accessController.getUserManager().getUser(userId);
    
    var userGroupArray = user.getGroups();
    var userGroups = new java.util.ArrayList(java.util.Arrays.asList(userGroupArray));
    
    var iterator = accessController.getGroupManager().getGroups();
    var groups = new java.util.ArrayList();
    while (iterator.hasNext()) {
    	var group = iterator.next();
    	if (!userGroups.contains(group)) {
    		groups.add(group);
    	}
    }
    
    while (true) {
	    sendPageAndWait("users/lenya.usecase.change_groups/groups.xml", {
	    	"user-id" : userId,
	    	"groups" : groups,
	    	"user-groups" : userGroups
	    });
	    
		var groupName = cocoon.request.get("group");
		if (cocoon.request.get("add_group") && groupName != "") {
			var group = accessController.getGroupManager().getGroup(groupName);
			if (!userGroups.contains(group)) {
				userGroups.add(group);
				groups.remove(group);
			}
		}
	    
		var userGroupName = cocoon.request.get("user_group");
		if (cocoon.request.get("remove_group") && userGroupName != "") {
			var group = accessController.getGroupManager().getGroup(userGroupName);
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
			break;
		}
	}
   	sendPage("redirect.html", { "url" : "index.html" });
}

