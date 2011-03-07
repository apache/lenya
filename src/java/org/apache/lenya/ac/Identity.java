/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 *  contributor license agreements.  See the NOTICE file distributed with
 *  this work for additional information regarding copyright ownership.
 *  The ASF licenses this file to You under the Apache License, Version 2.0
 *  (the "License"); you may not use this file except in compliance with
 *  the License.  You may obtain a copy of the License at
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

/* $Id$  */

package org.apache.lenya.ac;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.apache.cocoon.environment.Session;

/**
 * Identity object. Used to store the authenticated accreditables in the session.
 */
public class Identity implements Identifiable, Serializable {
    
    private static final long serialVersionUID = 1L;
    private Set<Identifiable> identifiables = new HashSet<Identifiable>();

    public static Identity ANONYMOUS = new Identity();

    /**
     * C'tor. Adds World identifiable by default.
     */
    public Identity() {
        addIdentifiable(World.getInstance());
    }

    /**
     * In the case of Tomcat the object will be serialized to
     * TOMCAT/work/Standalone/localhost/lenya/SESSIONS.ser
     * @param out OutputStream to hold the serialized identity
     * @throws IOException
     */
    private void writeObject(ObjectOutputStream out) throws IOException {
        out.defaultWriteObject();
        out.writeObject(this.identifiables);
    }

    /**
     * In case of Tomcat the object will be restored from
     * TOMCAT/work/Standalone/localhost/lenya/SESSIONS.ser
     * @param in InputStream that holds the serialized identity
     * @throws IOException
     * @throws ClassNotFoundException
     */
    @SuppressWarnings("unchecked")
    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        this.identifiables = (Set<Identifiable>) in.readObject();
    }

    /**
     * Returns the identifiables of this identity.
     * @return An array of identifiables.
     */
    public Identifiable[] getIdentifiables() {
        return (Identifiable[]) this.identifiables.toArray(new Identifiable[this.identifiables
                .size()]);
    }

    /**
     * Adds a new identifiable to this identity.
     * @param identifiable The identifiable to add.
     */
    public void addIdentifiable(Identifiable identifiable) {
        assert identifiable != null;
        assert identifiable != this;
        assert !this.identifiables.contains(identifiable);

        this.identifiables.add(identifiable);
    }

    /**
     * @see Accreditable#getAccreditables()
     */
    public Accreditable[] getAccreditables() {
        Set<Accreditable> accreditables = new HashSet<Accreditable>();
        Identifiable[] _identifiables = getIdentifiables();

        for (int i = 0; i < _identifiables.length; i++) {
            Accreditable[] groupAccreditables = _identifiables[i].getAccreditables();
            accreditables.addAll(Arrays.asList(groupAccreditables));
        }

        return (Accreditable[]) accreditables.toArray(new Accreditable[accreditables.size()]);
    }

    /**
     * @see java.lang.Object#toString()
     */
    public String toString() {
        StringBuffer buf = new StringBuffer();
        Accreditable[] accreditables = getAccreditables();

        for (int i = 0; i < accreditables.length; i++) {
            buf.append(" " + accreditables[i]);
        }

        String string = "[identity:" + buf.toString() + "]";

        return string;
    }

    /**
     * Checks if this identity belongs to a certain accreditable manager.
     * @param manager The accreditable manager to check for.
     * @return A boolean value.
     * @throws AccessControlException if an error occurs
     */
    public boolean belongsTo(AccreditableManager manager) throws AccessControlException {
        User user = getUser();
        if (user == null) {
            return true;
        } else {
            String thisId = user.getAccreditableManager().getId();
            String otherId = manager.getId();
            return thisId.equals(otherId);
        }
    }

    /**
     * Returns the user of this identity.
     * @return A user.
     */
    public User getUser() {
        User user = null;
        Identifiable[] _identifiables = getIdentifiables();
        int i = 0;
        while (user == null && i < _identifiables.length) {
            if (_identifiables[i] instanceof User) {
                user = (User) _identifiables[i];
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
        Identifiable[] _identifiables = getIdentifiables();
        int i = 0;
        while (machine == null && i < _identifiables.length) {
            if (_identifiables[i] instanceof Machine) {
                machine = (Machine) _identifiables[i];
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
        return this.identifiables.contains(identifiable);
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
        assert this.identifiables.contains(identifiable);
        this.identifiables.remove(identifiable);
    }

}