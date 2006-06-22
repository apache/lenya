package org.apache.lenya.cms.ac.usecases;


import org.apache.lenya.ac.Group;

/**
 * Usecase to change the profile of a group.
 * 
 * @version $Id: GroupProfile.java 407305 2006-05-17 16:21:49Z andreas $
 */
public class GroupProfile extends AccessControlUsecase {

    protected static final String ID = "groupId";
    protected static final String NAME = "name";
    protected static final String DESCRIPTION = "description";

    private Group group;

    protected void doExecute() throws Exception {
        super.doExecute();

        String name = getParameterAsString(NAME);
        String description = getParameterAsString(DESCRIPTION);

        this.group.setName(name);
        this.group.setDescription(description);

        this.group.save();
    }

    /**
     * @see org.apache.lenya.cms.usecase.Usecase#setParameter(java.lang.String, java.lang.Object)
     */
    public void setParameter(String name, Object value) {
        super.setParameter(name, value);

        if (name.equals(ID)) {
            String id = (String) value;
            this.group = getGroupManager().getGroup(id);
            if (this.group == null) {
                throw new RuntimeException("Group [" + id + "] not found.");
            }

            setParameter(DESCRIPTION, this.group.getDescription());
            setParameter(NAME, this.group.getName());
        }
    }
}