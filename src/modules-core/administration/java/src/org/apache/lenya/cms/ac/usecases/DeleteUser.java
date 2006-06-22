package org.apache.lenya.cms.ac.usecases;


import org.apache.lenya.ac.User;

/**
 * Usecase to delete a user.
 *
 * @version $Id: DeleteUser.java 407305 2006-05-17 16:21:49Z andreas $
 */
public class DeleteUser extends AccessControlUsecase {

    /**
     * @see org.apache.lenya.cms.usecase.AbstractUsecase#doExecute()
     */
    protected void doExecute() throws Exception {
        super.doExecute();

        String userId = getParameterAsString(UserProfile.USER_ID);
        User user = getUserManager().getUser(userId);
        if (user == null) {
            throw new RuntimeException("User [" + userId + "] not found.");
        }
        
        getUserManager().remove(user);
        user.delete();
    }
}
