
//
// Modify a user.
//
function user_change_profile(userId) {

	resolve();

	try {
	    var user = getAccreditableManager().getUserManager().getUser(userId);
		var fullName = user.getFullName();
		var email = user.getEmail();
		var description = user.getDescription();
			
		var ldapId;
		var ldap = false;
		if (user.getClass().getName().endsWith("LDAPUser")) {
			ldapId = user.getLdapId();
		ldap = true;
		}
		
		// at the moment the loop is executed only once (no form validation)
		
	    while (true) {
		    cocoon.sendPageAndWait("users/" + userId + "/profile.xml", {
		    	"user-id" : userId,
		    	"fullname" : fullName,
		    	"email" : email,
		    	"description" : description,
		    	"page-title" : "Edit Profile",
		    	"ldap" : ldap
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
	    
	   	cocoon.sendPage("redirect.html", { "url" : "index.html" });
   	}
   	finally  {
	   	release();
   	}
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

	resolve();
	try {
	
	    var user = getAccreditableManager().getUserManager().getUser(userId);
	    var oldPassword = "";
	    var newPassword = "";
	    var confirmPassword = "";
	    var message = "";
	    
	    while (true) {
		    cocoon.sendPageAndWait("users/" + userId + "/password.xml", {
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
	    
	   	cocoon.sendPage("redirect.html", { "url" : "index.html" });
   	}
   	finally {
   		release();
   	}
    
}

//
// Change the group affiliation of a user.
//
function user_change_groups(userId) {

	resolve();
	try {
		var groupManager = getAccreditableManager().getGroupManager();
	    var user = getAccreditableManager().getUserManager().getUser(userId);
	    
	    var userGroupArray = user.getGroups();
	    var userGroups = new java.util.ArrayList(java.util.Arrays.asList(userGroupArray));
	    
	    var iterator = getAccreditableManager().getGroupManager().getGroups();
	    var groups = new java.util.ArrayList();
	    while (iterator.hasNext()) {
	    	var group = iterator.next();
	    	if (!userGroups.contains(group)) {
	    		groups.add(group);
	    	}
	    }
	    
	    while (true) {
		    cocoon.sendPageAndWait("users/" + userId + "/groups.xml", {
		    	"user-id" : userId,
		    	"groups" : groups,
		    	"user-groups" : userGroups
		    });
		    
			var groupId = cocoon.request.get("group");
			if (cocoon.request.get("add_group") && groupId != "") {
				var group = groupManager.getGroup(groupId);
				if (!userGroups.contains(group)) {
					userGroups.add(group);
					groups.remove(group);
				}
			}
		    
			var userGroupId = cocoon.request.get("user_group");
			if (cocoon.request.get("remove_group") && userGroupId != "") {
				var group = groupManager.getGroup(userGroupId);
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
	   	cocoon.sendPage("redirect.html", { "url" : "index.html" });
   	}
   	finally {
   		release();
   	}
}

function user_add_user_ldap() {
	add_user(true);
}

function user_add_user() {
	add_user(false);
}

function validate(userManager, ldap, userId, email, password, confirmPassword, messages, ldapId, configDir) {
    
	messages = new Packages.java.util.ArrayList();
	
    userid = new Packages.java.lang.String(email);
    email = new Packages.java.lang.String(email);
    
    var existingUser = userManager.getUser(userId);
			
	if (existingUser != null) {
		messages.add("This user already exists.");
	}
			
	if (!Packages.org.apache.lenya.cms.ac.AbstractItem.isValidId(userId)) {
		messages.add("This is not a valid user ID.");
	}
			
    if (email.length() == 0) {
        messages.add("Please enter an e-mail address.");
    }
    
	if (ldap) {
	    configDir = new Packages.java.util.File(configDir);
	    var ldapUser = new Packages.org.apache.lenya.cms.ac.LDAPUser(configDir);
	    if (!ldapUser.existsUser(ldapId)) {
	    	messages.add("This LDAP user ID does not exist.");
	    }
	}
	
	else {
    	password = new Packages.java.lang.String(password);
	    confirmPassword = new Packages.java.lang.String(confirmPassword);
    
		if (!password.equals(confirmPassword)) {
		   	messages.add("Password and confirmed password are not equal.");
		}
			
	    if (password.length() < 6) {
    	    messages.add("The password must be at least six characters long.");
	    }
    
	    if (!password.matches(".*\\d.*")) {
    	    messages.add("The password must contain at least one number.");
	    }
    }
    
    
    return messages;
}

//
// Add a user.
//
function add_user(ldap) {

	resolve();
	try {
	
		var userId = "";
		
		var ldapId = null;
		if (ldap) {
			ldapId = "";
		}
		
		var email = "";
		var fullName = "";
		var description = "";
		var messages = new Packages.java.util.ArrayList();
		var password = "";
		var confirmPassword = "";
		var userManager = getAccreditableManager().getUserManager();
		
		while (true) {
			cocoon.sendPageAndWait("users/profile.xml", {
				"page-title" : "Add User: Profile",
				"user-id" : userId,
		    	"fullname" : fullName,
		    	"email" : email,
		    	"description" : description,
		    	"messages" : messages,
		    	"ldap-id" : ldapId,
	    		"password" : password,
	    		"confirm-password" : confirmPassword,
	    		"new-user" : true,
	    		"ldap" : ldap
			});
			
		    if (cocoon.request.get("cancel")) {
		    	break;
		    }
		    
			userId = cocoon.request.get("userid");
			email = cocoon.request.get("email");
			fullName = cocoon.request.get("fullname");
			description = cocoon.request.get("description");
			ldapId = cocoon.request.get("ldapid");
			password = cocoon.request.get("new-password");
			confirmPassword = cocoon.request.get("confirm-password");
			
			var configDir = userManager.getConfigurationDirectory();
			messages = validate(userManager, ldap, userId, email, password, confirmPassword, ldapId, configDir);
			
			if (messages.isEmpty()) {
				var user;
				if (ldap) {
					user = new Packages.org.apache.lenya.cms.ac.LDAPUser(configDir, userId, email, ldapId);
				}
				else {
					user = new Packages.org.apache.lenya.cms.ac.FileUser(configDir, userId, fullName, email, "");
					user.setName(fullName);
					user.setPassword(password);
				}
				
				user.setDescription(description);
				user.save();
				userManager.add(user);
				break;
			}
		}
	   	cocoon.sendPage("redirect.html", { "url" : "../users.html" });
   	}
   	finally {
   		release();
   	}
}

//
// Delete user.
//
function user_delete_user() {

	resolve();
	try {
	
		var userManager = getAccreditableManager().getUserManager();
		var userId = cocoon.request.get("user-id");
		var user = userManager.getUser(userId);
		var name = user.getName();
		var showPage = true;
		
		while (showPage) {
			cocoon.sendPageAndWait("users/confirm-delete-common.xml", {
				"id" : userId,
				"name" : name,
				"type" : user
			});
			
			if (cocoon.request.get("cancel")) {
				break;
			}
			
			if (cocoon.request.get("submit")) {
				userManager.remove(user);
				user['delete']();
				showPage = false;
			}
		}
	
	   	cocoon.sendPage("redirect.html", { "url" : "../users.html" });
   	}
   	finally {
   		release();
   	}
}
