/*
 * Copyright  1999-2004 The Apache Software Foundation
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 */

/* $Id: Identity.java,v 1.2 2004/03/03 12:56:31 gregor Exp $  */

package org.apache.lenya.ac;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.apache.cocoon.environment.Session;


public class Identity implements Identifiable {
    private Set identifiables = new HashSet();
    
    /**
     * Ctor.
     */
    public Identity() {
        addIdentifiable(World.getInstance());
    }

    /**
     * Returns the identifiables of this identity.
     * @return An array of identifiables.
     */
    public Identifiable[] getIdentifiables() {
        return (Identifiable[]) identifiables.toArray(new Identifiable[identifiables.size()]);
    }

    /**
     * Adds a new identifiable to this identity.
     * @param identifiable The identifiable to add.
     */
    public void addIdentifiable(Identifiable identifiable) {
        assert identifiable != null;
        assert identifiable != this;
        assert !identifiables.contains(identifiable);
        identifiables.add(identifiable);
    }

    /**
     * @see org.apache.lenya.cms.ac.Accreditable#getAccreditables()
     */
    public Accreditable[] getAccreditables() {
        Set accreditables = new HashSet();
        Identifiable[] identifiables = getIdentifiables();

        for (int i = 0; i < identifiables.length; i++) {
            Accreditable[] groupAccreditables = identifiables[i].getAccreditables();
            accreditables.addAll(Arrays.asList(groupAccreditables));
        }

        return (Accreditable[]) accreditables.toArray(new Accreditable[accreditables.size()]);
    }

    /**
     * @see java.lang.Object#toString()
     */
    public String toString() {
        String accrString = "";
        Accreditable[] accreditables = getAccreditables();

        for (int i = 0; i < accreditables.length; i++) {
            accrString += (" " + accreditables[i]);
        }

        String string = "[identity:" + accrString + "]";

        return string;
    }
    
    /**
     * Checks if this identity belongs to a certain accreditable manager.
     * @param manager The accreditable manager to check for.
     * @return A boolean value.
     * 
     * @throws AccessControlException if an error occurs
     */
    public boolean belongsTo(AccreditableManager manager) throws AccessControlException {
        
        boolean belongs = true;
        
        Identifiable identifiables[] = getIdentifiables();
        int i = 0;
        while (belongs && i < identifiables.length) {
            if (identifiables[i] instanceof User) {
                User user = (User) identifiables[i];
                User otherUser = manager.getUserManager().getUser(user.getId());
                belongs = belongs && user == otherUser;
            }
            i++;
        }
        
        return belongs;
    }

    /**
     * Returns the user of this identity.
     * @return A user.
     */
    public User getUser() {
        User user = null;
        Identifiable[] identifiables = getIdentifiables();
        int i = 0;
        while (user == null && i < identifiables.length) {
            if (identifiables[i] instanceof User) {
                user = (User) identifiables[i];
            }
            i++;
        }
        return user;
     }

    /**
     * Returns the machine of this identity.
     * @return A machine.
     */
    public Machine getMachine() {
        Machine machine = null;
        Identifiable[] identifiables = getIdentifiables();
        int i = 0;
        while (machine == null && i < identifiables.length) {
            if (identifiables[i] instanceof Machine) {
                machine = (Machine) identifiables[i];
            }
            i++;
        }
        return machine;
     }
     
     /**
      * Checks if this identity contains a certain identifiable.
      * @param identifiable The identifiable to look for.
      * @return A boolean value.
      */
     public boolean contains(Identifiable identifiable) {
         return identifiables.contains(identifiable);
     }
     
     /**
      * Fetches the identity from a session.
      * @param session The session.
      * @return An identity.
      */
     public static Identity getIdentity(Session session) {
         Identity identity = (Identity) session.getAttribute(Identity.class.getName());
         return identity;
     }
     
     /**
      * Removes a certain identifiable from the idenity.
      * @param identifiable An identifiable.
      */
     public void removeIdentifiable(Identifiable identifiable) {
         assert identifiables.contains(identifiable);
         identifiables.remove(identifiable);
     }

}
