/*
$Id: Identity.java,v 1.11 2003/08/15 13:09:26 andreas Exp $
<License>

 ============================================================================
                   The Apache Software License, Version 1.1
 ============================================================================

 Copyright (C) 1999-2003 The Apache Software Foundation. All rights reserved.

 Redistribution and use in source and binary forms, with or without modifica-
 tion, are permitted provided that the following conditions are met:

 1. Redistributions of  source code must  retain the above copyright  notice,
    this list of conditions and the following disclaimer.

 2. Redistributions in binary form must reproduce the above copyright notice,
    this list of conditions and the following disclaimer in the documentation
    and/or other materials provided with the distribution.

 3. The end-user documentation included with the redistribution, if any, must
    include  the following  acknowledgment:  "This product includes  software
    developed  by the  Apache Software Foundation  (http://www.apache.org/)."
    Alternately, this  acknowledgment may  appear in the software itself,  if
    and wherever such third-party acknowledgments normally appear.

 4. The names "Apache Lenya" and  "Apache Software Foundation"  must  not  be
    used to  endorse or promote  products derived from  this software without
    prior written permission. For written permission, please contact
    apache@apache.org.

 5. Products  derived from this software may not  be called "Apache", nor may
    "Apache" appear  in their name,  without prior written permission  of the
    Apache Software Foundation.

 THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED WARRANTIES,
 INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 FITNESS  FOR A PARTICULAR  PURPOSE ARE  DISCLAIMED.  IN NO  EVENT SHALL  THE
 APACHE SOFTWARE  FOUNDATION  OR ITS CONTRIBUTORS  BE LIABLE FOR  ANY DIRECT,
 INDIRECT, INCIDENTAL, SPECIAL,  EXEMPLARY, OR CONSEQUENTIAL  DAMAGES (INCLU-
 DING, BUT NOT LIMITED TO, PROCUREMENT  OF SUBSTITUTE GOODS OR SERVICES; LOSS
 OF USE, DATA, OR  PROFITS; OR BUSINESS  INTERRUPTION)  HOWEVER CAUSED AND ON
 ANY  THEORY OF LIABILITY,  WHETHER  IN CONTRACT,  STRICT LIABILITY,  OR TORT
 (INCLUDING  NEGLIGENCE OR  OTHERWISE) ARISING IN  ANY WAY OUT OF THE  USE OF
 THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

 This software  consists of voluntary contributions made  by many individuals
 on  behalf of the Apache Software  Foundation and was  originally created by
 Michael Wechner <michi@apache.org>. For more information on the Apache Soft-
 ware Foundation, please see <http://www.apache.org/>.

 Lenya includes software developed by the Apache Software Foundation, W3C,
 DOM4J Project, BitfluxEditor, Xopus, and WebSHPINX.
</License>
*/
package org.apache.lenya.cms.ac2;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.apache.cocoon.environment.Session;
import org.apache.lenya.cms.ac.AccessControlException;
import org.apache.lenya.cms.ac.Machine;
import org.apache.lenya.cms.ac.User;


/**
 * @author andreas
 * @author Michael Wechner
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
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

}
