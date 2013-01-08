package org.apache.lenya.ac.file;

import java.text.DecimalFormat;
import java.util.HashSet;
import java.util.Set;

import org.apache.avalon.framework.container.ContainerUtil;
import org.apache.lenya.ac.AccessControlException;
import org.apache.lenya.ac.Group;
import org.apache.lenya.ac.User;
import org.apache.lenya.ac.UserType;
import org.apache.lenya.ac.impl.AbstractAccessControlTest;

/**
 * https://issues.apache.org/bugzilla/show_bug.cgi?id=44035
 */
public class Issue44035Test extends AbstractAccessControlTest {

    private final String ADMIN_GROUP = "adminGroup";
    private final String EDITOR_GROUP = "editorGroup";

    /**
     * Create and save a user
     * 
     * @param userName The user name
     * @return a <code>FileUser</code>
     * @throws AccessControlException if an error occurs
     */
    final public FileUser createAndSaveUser(String userName) throws AccessControlException {

        final FileUser user = new FileUser(getAccreditableManager().getUserManager(), getLogger(),
                userName, "Henry Hamster", "henry@hamster.com", "foobar");
        ContainerUtil.enableLogging(user, getLogger());

        for (final String groupName : new String[] { ADMIN_GROUP, EDITOR_GROUP }) {
            final FileGroup group = new FileGroup(getAccreditableManager().getGroupManager(),
                    getLogger(), groupName);
            ContainerUtil.enableLogging(group, getLogger());
            group.add(user);
            group.save();
        }

        user.save();

        final FileUserManager _manager = getUserManager();
        _manager.add(user);

        return user;
    }

    /**
     * Returns the file user manager.
     * @return A file user manager.
     * @throws AccessControlException if an error occurs.
     */
    protected FileUserManager getUserManager() throws AccessControlException {
        final UserType[] userTypes = { FileAccreditableManager.getDefaultUserType() };
        return FileUserManager.instance(getAccreditableManager(), getAccreditablesDirectory(),
                userTypes, getLogger());
    }

    public void testIssue44035() throws Exception {
        final int numRuns = 1;
        final int numUsers = 10;
        final Set<User> users = new HashSet<User>();
        try {
            for (int i = 0; i < numUsers; i++) {
                final User user = createAndSaveUser(username(i));
                users.add(user);
            }
            for (int n = 0; n < numRuns; n++) {
                final FileUserManager mgr = createUserManager();
                for (int i = 0; i < numUsers; i++) {
                    final User user = mgr.getUser(username(i));
                    final Set<String> groupIds = new HashSet<String>();
                    for (final Group group : user.getGroups()) {
                        groupIds.add(group.getId());
                    }
                    assertTrue(groupIds.contains(ADMIN_GROUP));
                    assertTrue(groupIds.contains(EDITOR_GROUP));
                }
            }
        } finally {
            for (final User user : users) {
                getUserManager().remove(user);
                user.delete();
            }
        }
    }

    protected String username(int i) {
        return "user_" + new DecimalFormat("000").format(i);
    }

    protected FileUserManager createUserManager() throws AccessControlException {
        final UserType[] userTypes = { FileAccreditableManager.getDefaultUserType() };
        final FileUserManager manager = new FileUserManager(getAccreditableManager(), userTypes);
        manager.enableLogging(getLogger());
        manager.configure(getAccreditablesDirectory());
        return manager;
    }

}
