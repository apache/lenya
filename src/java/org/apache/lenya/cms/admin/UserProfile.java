/*
 * Created on 20.09.2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.apache.lenya.cms.admin;

import org.apache.lenya.ac.User;

/**
 * @author nobby
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class UserProfile extends AccessControlUsecase {

    protected static final String USER_ID = "userId";
    protected static final String FULL_NAME = "fullName";
    protected static final String EMAIL = "email";
    protected static final String DESCRIPTION = "description";
    
    /**
     * Ctor.
     */
    public UserProfile() {
        super();
    }

    /**
     * @see org.apache.lenya.cms.usecase.AbstractUsecase#doCheckExecutionConditions()
     */
    protected void doCheckExecutionConditions() throws Exception {
        
        String email = getParameter(UserProfile.EMAIL);
        if (email.length() == 0) {
            addErrorMessage("Please enter an e-mail address.");
        }
    }
    
    /**
     * @see org.apache.lenya.cms.usecase.AbstractUsecase#doExecute()
     */
    protected void doExecute() throws Exception {
        super.doExecute();
        
        String fullName = getParameter(UserProfile.FULL_NAME);
        String description = getParameter(UserProfile.DESCRIPTION);
        String email = getParameter(UserProfile.EMAIL);
        
        getUser().setEmail(email);
        getUser().setName(fullName);
        getUser().setDescription(description);
        getUser().save();
        
    }
    
    private User user;
    
    /**
     * Returns the currently edited user.
     * @return A user.
     */
    protected User getUser() {
        return this.user;
    }
    
    /**
     * @see org.apache.lenya.cms.usecase.Usecase#setParameter(java.lang.String, java.lang.String)
     */
    public void setParameter(String name, String value) {
        super.setParameter(name, value);
        
        if (name.equals(USER_ID)) {
            String userId = value;
            this.user = getUserManager().getUser(userId);
            if (user == null) {
                throw new RuntimeException("User [" + userId + "] not found.");
            }
            
            setParameter(EMAIL, user.getEmail());
            setParameter(DESCRIPTION, user.getDescription());
            setParameter(FULL_NAME, user.getName());
        }
    }

}
