/*
 * Created on 27.05.2003
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package org.apache.lenya.cms.workflow;

import java.util.ArrayList;
import java.util.List;

import org.apache.lenya.cms.workflow.*;
import org.apache.lenya.workflow.Situation;
import org.apache.lenya.workflow.Transition;
import org.apache.lenya.workflow.WorkflowInstance;

/**
 * @author andreas
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class CommandFilterImpl implements CommandFilter {

    /* (non-Javadoc)
     * @see org.apache.lena.cms.menu.MenuFilter#getExecutableItems(org.apache.lenya.cms.workflow.Situation)
     */
    public String[] getExecutableCommands(
        WorkflowInstance instance,
        Situation situation) {

        List commands = new ArrayList();

        Transition transitions[] = instance.getExecutableTransitions(situation);
        for (int i = 0; i < transitions.length; i++) {
            String command = transitions[i].getEvent().getCommand();
            commands.add(command);
        }

        return (String[]) commands.toArray(new String[commands.size()]);

    }

}
