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
// Modify a user.
//
function userChangeProfile(userId) {
	resolve();
	try {
	    var redirectUri = getRequestUri();
        var userId = getAccreditableId();

	    var user = getAccreditableManager().getUserManager().getUser(userId);
		
		var fullName = user.getName();
		var email = user.getEmail();
		var description = user.getDescription();

		var ldapId;
		var ldap = false;
		if (user.getClass().getName().endsWith("LDAPUser")) {
			ldapId = user.getLdapId();
            ldap = true;
		}
		
		// at the moment the loop is executed only once (no form validation)
		
		var ready = false;
	    while (!ready) {
	    
		    cocoon.sendPageAndWait("users/profile.xml", {
		    	"user-id" : userId,
		    	"fullname" : fullName,
		    	"email" : email,
		    	"description" : description,
		    	"page-title" : "Edit Profile",
		    	"ldap" : ldap
		    });
		    
		    if (cocoon.request.get("submit")) {
			    fullName = cocoon.request.get("fullname");
		       	user.setName(fullName);
			    email = cocoon.request.get("email");
		       	user.setEmail(email);
		       	description = cocoon.request.get("description");
		       	user.setDescription(description);
		   		user.save();
		    	ready = true;
		    }
		    else {
		    	ready = true;
		    }
	
	    }
	   	cocoon.sendPage("redirect.html", { "url" : redirectUri });
   	}
   	finally  {
	   	release();
   	}
}

//
// Change password as admin (don't check the old password)
//
function userChangePasswordAdmin() {
	userChangePassword(false);
}

//
// Change password as user (check the old password)
//
function userChangePasswordUser() {
	userChangePassword(true);
}

//
// Change the password.
// checkPassword: (boolean) if the old password shall be checked
//
function userChangePassword(checkPassword) {
	resolve();
	try {

	    var redirectUri = getRequestUri();
        var userId = getAccreditableId();
	
	    var user = getAccreditableManager().getUserManager().getUser(userId);
	    var oldPassword = "";
	    var newPassword = "";
	    var confirmPassword = "";
	    var message = "";
	    var enabled = "true";
	    
	    if (user.getClass().getName().endsWith("LDAPUser")) {
	        message = "You cannot change the password of this user.";
	        enabled = false;
	    }
	    
	    var ready = false;
	    
	    while (!ready) {
		    cocoon.sendPageAndWait("users/password.xml", {
		    	"user-id" : userId,
		    	"new-password" : newPassword,
		    	"confirm-password" : confirmPassword,
		    	"message" : message,
		    	"check-password" : checkPassword,
		    	"enabled" : enabled
		    });
		    
			if (cocoon.request.get("submit")) {	    
			    oldPassword = cocoon.request.get("old-password");
			    newPassword = cocoon.request.get("new-password");
                password = new Packages.java.lang.String(newPassword);
			    confirmPassword = cocoon.request.get("confirm-password");
			    
			    if (checkPassword && !user.authenticate(oldPassword)) {
			    	message = "You entered a wrong password.";
			    }
	            else if (password.length() == 0) {
			    	message = "New password cannot be empty.";
			    }
	            else if (!newPassword.equals(confirmPassword)) {
			    	message = "New password and confirmed password are not equal.";
			    }
			    else {
		        	user.setPassword(newPassword);
		    		user.save();
				    ready = true;
			    }
			}
			else {
			    ready = true;
			}
	
	    }
	    
	   	cocoon.sendPage("redirect.html", { "url" : redirectUri });
   	}
   	finally {
   		release();
   	}
    
}

//
// Change the group affiliation of a user.
//
function userChangeGroups() {

	resolve();
	try {
	    var redirectUri = getRequestUri();
        var userId = getAccreditableId();
	
		var groupManager = getAccreditableManager().getGroupManager();
	    var user = getAccreditableManager().getUserManager().getUser(userId);
	    
	    var userGroupArray = user.getGroups();
	    var userGroups = new java.util.ArrayList(java.util.Arrays.asList(userGroupArray));
	    
	    var groupArray = getAccreditableManager().getGroupManager().getGroups();
	    var groups = new java.util.ArrayList(java.util.Arrays.asList(groupArray));
	    groups.removeAll(userGroups);

	    var ready = false;
	    while (!ready) {
		    cocoon.sendPageAndWait("users/groups.xml", {
		    	"user-id" : userId,
		    	"groups" : groups,
		    	"user-groups" : userGroups
		    });
		    
			var groupId = cocoon.request.get("group");
			if (cocoon.request.get("add_group") && groupId != null) {
				var group = groupManager.getGroup(groupId);
				if (!userGroups.contains(group)) {
					userGroups.add(group);
					groups.remove(group);
				}
			}
		    
			var userGroupId = cocoon.request.get("user_group");
			if (cocoon.request.get("remove_group") && userGroupId != null) {
				var group = groupManager.getGroup(userGroupId);
				if (userGroups.contains(group)) {
					userGroups.remove(group);
					groups.add(group);
				}
			}
	
			if (cocoon.request.get("submit")) {
				user.removeFromAllGroups();

		var testGroups = groupManager.getGroups();
        for (i = 0; i < testGroups.length; i++) {
            if (testGroups[i].contains(user)) throw new Packages.java.lang.Exception(group + ":" + user);
        }

				var iterator = userGroups.iterator();
				while (iterator.hasNext()) {
					var group = iterator.next();
					
					if (group.contains(user)) throw new Packages.java.lang.Exception(group + "-" + user);
					
					group.add(user);
				}
				user.save();
				ready = true;
			}
			else if (cocoon.request.get("cancel")) {
			    ready = true;
			}
		}
	   	cocoon.sendPage("redirect.html", { "url" : redirectUri });
   	}
   	finally {
   		release();
   	}
}

function userAddUserLdap() {
	addUser(true);
}

function userAddUser() {
	addUser(false);
}

function validate(userManager, ldap, userId, email, password, confirmPassword, ldapId, configDir) {
    
	messages = new Packages.java.util.ArrayList();
	
    userid = new Packages.java.lang.String(email);
    email = new Packages.java.lang.String(email);
    
    var existingUser = userManager.getUser(userId);
			
	if (existingUser != null) {
		messages.add("This user already exists.");
	}
			
	if (!Packages.org.apache.lenya.ac.impl.AbstractItem.isValidId(userId)) {
		messages.add("This is not a valid user ID.");
	}
			
    if (email.length() == 0) {
        messages.add("Please enter an e-mail address.");
    }
    
	if (ldap) {
	    var ldapUser = new Packages.org.apache.lenya.ac.ldap.LDAPUser(configDir);
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
function addUser(ldap) {

	resolve();
	try {
        var redirectUri = getRequestUri();
	
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
		
		var ready = false;
		while (!ready) {
			cocoon.sendPageAndWait("users/profile.xml", {
				"page-title" : "Add User",
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
			
		    if (cocoon.request.get("submit")) {
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
						user = new Packages.org.apache.lenya.ac.ldap.LDAPUser(configDir, userId, email, ldapId);
					}
					else {
						user = new Packages.org.apache.lenya.ac.file.FileUser(configDir, userId, fullName, email, "");
						user.setName(fullName);
						user.setPassword(password);
					}
					
					user.setDescription(description);
					user.save();
					userManager.add(user);
					ready = true;
				}
		    }
		    else {
		    	ready = true;
		    }
		}
	   	cocoon.sendPage("redirect.html", { "url" : redirectUri });
   	}
   	finally {
   		release();
   	}
}

//
// Delete user.
//
function userDeleteUser() {

	resolve();
    var redirectUri = getRequestUri();
	
	var userId = cocoon.request.get("user-id");
	var user;
	
	try {
		var userManager = getAccreditableManager().getUserManager();
		user = userManager.getUser(userId);
   	}
   	finally {
   		release();
   	}
		
	var name = user.getName();
	var ready = false;
		
	while (!ready) {
		cocoon.sendPageAndWait("users/confirm-delete-common.xml", {
			"id" : userId,
			"name" : name,
			"type" : "user"
		});
			
		if (cocoon.request.get("submit")) {
			resolve();
			try {
				var userManager = getAccreditableManager().getUserManager();
				userManager.remove(user);
			}
		   	finally {
   				release();
		   	}
			user['delete']();
			ready = true;
		}
		else {
			ready = true;
		}
	}
	
   	cocoon.sendPage("redirect.html", { "url" : redirectUri });
}
