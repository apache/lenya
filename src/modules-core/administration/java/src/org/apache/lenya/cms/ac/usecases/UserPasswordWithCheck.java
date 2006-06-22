package org.apache.lenya.cms.ac.usecases;


/**
 * Usecase to change a user's password. The old password is checked.
 */
public class UserPasswordWithCheck extends UserPassword {

    protected static final String OLD_PASSWORD = "oldPassword";

    protected void doCheckExecutionConditions() throws Exception {

        super.doCheckExecutionConditions();

        if (getUser() != null) {
            String oldPassword = getParameterAsString(OLD_PASSWORD);
            boolean authenticated = getUser().authenticate(oldPassword);
            if (!authenticated) {
                addErrorMessage("The old password is not correct.");
            }
        }
    }

}
