/*
 * Situation.java
 *
 * Created on 8. April 2003, 17:42
 */

package org.apache.lenya.cms.workflow.impl;

import org.apache.lenya.cms.ac.User;
import org.apache.lenya.cms.workflow.Situation;

/**
 *
 * @author  andreas
 */
public class CMSSituation implements Situation {

    /** Creates a new instance of Situation */
    public CMSSituation(User user) {

        assert user != null;
        this.user = user;

    }

    private User user;

    /**
     * @return
     */
    public User getUser() {
        return user;
    }

}
