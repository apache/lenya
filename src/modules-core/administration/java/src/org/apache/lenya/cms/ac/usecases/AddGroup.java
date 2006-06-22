package org.apache.lenya.cms.ac.usecases;


import org.apache.lenya.ac.Group;
import org.apache.lenya.ac.ItemUtil;
import org.apache.lenya.cms.usecase.UsecaseException;

/**
 * Usecase to add a group.
 *
 * @version $Id: AddGroup.java 407305 2006-05-17 16:21:49Z andreas $ 
 */
public class AddGroup extends AccessControlUsecase {

    /**
     * @see org.apache.lenya.cms.usecase.AbstractUsecase#doCheckExecutionConditions()
     */
    protected void doCheckExecutionConditions() throws Exception {
        validate();
    }

    /**
     * Validates the request parameters.
     * @throws UsecaseException if an error occurs.
     */
    void validate() throws UsecaseException {

        String groupId = getParameterAsString(GroupProfile.ID);

        Group existingGroup = getGroupManager().getGroup(groupId);

        if (existingGroup != null) {
            addErrorMessage("This group already exists.");
        }

        if (!ItemUtil.isValidId(groupId)) {
            addErrorMessage("This is not a valid group ID.");
        }

    }

    /**
     * @see org.apache.lenya.cms.usecase.AbstractUsecase#doExecute()
     */
    protected void doExecute() throws Exception {
        super.doExecute();

        String id = getParameterAsString(GroupProfile.ID);
        String name = getParameterAsString(GroupProfile.NAME);
        String description = getParameterAsString(GroupProfile.DESCRIPTION);

        Group group = getGroupManager().add(id);
        group.setName(name);
        group.setDescription(description);
        
        group.save();
        
        setExitParameter(GroupProfile.ID, id);
    }

}