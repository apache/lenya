
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
    sendPage("user-admin.flow");
}

//
// Modify a user.
//
function modifyUser() {

	initAccessController();

	var userId = cocoon.request.get("user-id");

    var user = accessController.getUserManager().getUser(userId);
	var fullName = user.fullName;
	var email = user.email;
	var password = "";
	var confirmPassword = "";
	
	var errorMessageProfile = "";
	var errorMessagePassword = "";
	var successMessageProfile = "";
	var successMessagePassword = "";
	var check = false;

    while (true) {
	    sendPageAndWait("user-modify.flow", {
	    	"check" : check,
	    	"error-message-profile" : errorMessageProfile,
	    	"error-message-password" : errorMessagePassword,
	    	"success-message-profile" : successMessageProfile,
	    	"success-message-password" : successMessagePassword,
	    	"user-id" : userId,
	    	"fullname" : fullName,
	    	"password" : password,
	    	"confirm-password" : confirmPassword,
	    	"email" : email
	    });
	    
	    errorMessageProfile = "";
	    errorMessagePassword = "";
	    successMessageProfile = "";
	    successMessagePassword = "";
	    	
		var groupName = cocoon.request.get("group");
		if (cocoon.request.get("add_group") && groupName != "") {
			var group = accessController.getGroupManager().getGroup(groupName);
			if (!group.contains(user)) {
				group.add(user);
			}
		}
	    
		var userGroupName = cocoon.request.get("user_group");
		if (cocoon.request.get("remove_group") && userGroupName != "") {
			var group = accessController.getGroupManager().getGroup(userGroupName);
			if (group.contains(user)) {
				group.remove(user);
			}
		}

	    if (cocoon.request.get("save_profile")) {
		    fullName = cocoon.request.get("fullname");
	       	user.setFullName(fullName);
		    email = cocoon.request.get("email");
	       	user.setEmail(email);
	   		user.save();
	   		successMessageProfile = "The user data were saved.";
	    }

		if (cocoon.request.get("save_password")) {	    
		    password = cocoon.request.get("password");
		    confirmPassword = cocoon.request.get("confirm-password");
            if (password.equals(confirmPassword)) {
            	successMessagePassword = "The password was saved.";
	        	user.setFullName(fullName);
	    		user.save();
		    }
		    else {
		    	errorMessagePassword = "Password and confirmed password are not equal!";
		    	check = true;
		    }
		}
    }
    
}