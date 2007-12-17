package org.apache.lenya.ac;

public interface ManagedUser extends User, Accreditable {

    /**
     * Delete a user
     * 
     * @throws AccessControlException if the delete failed
     */
    void delete() throws AccessControlException;

    /**
     * Authenticate a user. This is done by encrypting the given password and
     * comparing this to the encryptedPassword.
     * 
     * @param password to authenticate with
     * @return true if the given password matches the password for this user
     */
    boolean authenticate(String password);

    /**
     * Sets the password.
     * @param plainTextPassword The plain text password.
     */
    void setPassword(String plainTextPassword);

    /**
     * Save the user
     * 
     * @throws AccessControlException if the save failed
     */
    void save() throws AccessControlException;

}
