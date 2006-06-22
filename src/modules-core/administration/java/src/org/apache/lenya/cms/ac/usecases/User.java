package org.apache.lenya.cms.ac.usecases;


/**
 * Display user information.
 * 
 * @version $Id: User.java 407305 2006-05-17 16:21:49Z andreas $
 */
public class User extends AccessControlUsecase {

    protected static final String USER_ID = "userId";
    protected static final String USER = "user";

    /**
     * @see org.apache.lenya.cms.usecase.Usecase#setParameter(java.lang.String, java.lang.Object)
     */
    public void setParameter(String name, Object value) {
        super.setParameter(name, value);

        if (name.equals(USER_ID)) {
            String userId = (String) value;
            org.apache.lenya.ac.User user = getUserManager().getUser(userId);
            if (user == null) {
                addErrorMessage("user_no_such_user", new String[]{userId});
            } else {
                setParameter(USER, user);
            }
        }
    }
}
