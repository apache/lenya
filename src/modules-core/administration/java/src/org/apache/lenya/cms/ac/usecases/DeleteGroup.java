package org.apache.lenya.cms.ac.usecases;


import org.apache.lenya.ac.Group;

/**
 * Usecase to delete a group.
 *
 * @version $Id: DeleteGroup.java 407305 2006-05-17 16:21:49Z andreas $
 */
public class DeleteGroup extends AccessControlUsecase {

    /**
     * @see org.apache.lenya.cms.usecase.AbstractUsecase#doExecute()
     */
    protected void doExecute() throws Exception {
        super.doExecute();

        String groupId = getParameterAsString(GroupProfile.ID);
        Group group = getGroupManager().getGroup(groupId);
        if (group == null) {
            throw new RuntimeException("Group [" + groupId + "] not found.");
        }
        
        getGroupManager().remove(group);
        group.delete();
    }
}
