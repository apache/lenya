package org.apache.lenya.cms.ac.usecases;


/**
 * Display group information.
 *
 * @version $Id: Group.java 407305 2006-05-17 16:21:49Z andreas $
 */
public class Group extends AccessControlUsecase {

    protected static final String GROUP_ID = "groupId";
    protected static final String GROUP = "group";

    /**
     * @see org.apache.lenya.cms.usecase.Usecase#setParameter(java.lang.String, java.lang.Object)
     */
    public void setParameter(String name, Object value) {
        super.setParameter(name, value);

        if (name.equals(GROUP_ID)) {
            String groupId = (String) value;
            org.apache.lenya.ac.Group group = getGroupManager().getGroup(groupId);
            if (group == null) {
                addErrorMessage("group_no_such_entry", new String[]{groupId});
            } else {
                setParameter(GROUP, group);
            }
        }
    }
}
