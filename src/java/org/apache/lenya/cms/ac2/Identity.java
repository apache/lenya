/*
 * $Id
 * <License>
 * The Apache Software License
 *
 * Copyright (c) 2002 lenya. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice, this
 *    list of conditions and the following disclaimer in the documentation and/or
 *    other materials provided with the distribution.
 *
 * 3. All advertising materials mentioning features or use of this software must
 *    display the following acknowledgment: "This product includes software developed
 *    by lenya (http://www.lenya.org)"
 *
 * 4. The name "lenya" must not be used to endorse or promote products derived from
 *    this software without prior written permission. For written permission, please
 *    contact contact@lenya.org
 *
 * 5. Products derived from this software may not be called "lenya" nor may "lenya"
 *    appear in their names without prior written permission of lenya.
 *
 * 6. Redistributions of any form whatsoever must retain the following acknowledgment:
 *    "This product includes software developed by lenya (http://www.lenya.org)"
 *
 * THIS SOFTWARE IS PROVIDED BY lenya "AS IS" WITHOUT ANY WARRANTY EXPRESS OR IMPLIED,
 * INCLUDING THE WARRANTY OF NON-INFRINGEMENT AND THE IMPLIED WARRANTIES OF MERCHANTI-
 * BILITY AND FITNESS FOR A PARTICULAR PURPOSE. lenya WILL NOT BE LIABLE FOR ANY DAMAGES
 * SUFFERED BY YOU AS A RESULT OF USING THIS SOFTWARE. IN NO EVENT WILL lenya BE LIABLE
 * FOR ANY SPECIAL, INDIRECT OR CONSEQUENTIAL DAMAGES OR LOST PROFITS EVEN IF lenya HAS
 * BEEN ADVISED OF THE POSSIBILITY OF THEIR OCCURRENCE. lenya WILL NOT BE LIABLE FOR ANY
 * THIRD PARTY CLAIMS AGAINST YOU.
 *
 * Lenya includes software developed by the Apache Software Foundation, W3C,
 * DOM4J Project, BitfluxEditor and Xopus.
 * </License>
 */
package org.apache.lenya.cms.ac2;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;


/**
 * @author andreas
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class Identity implements Identifiable {
    
    private Set identifiables = new HashSet();
    
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
        Identifiable identifiables[] = getIdentifiables();
        for (int i = 0; i < identifiables.length; i++) {
            Accreditable groupAccreditables[] = identifiables[i].getAccreditables();
            accreditables.addAll(Arrays.asList(groupAccreditables));
        }
        return (Accreditable[]) accreditables.toArray(new Accreditable[accreditables.size()]);
    }
    
    /**
     * @see java.lang.Object#toString()
     */
    public String toString() {
        String accrString = "";
        Accreditable accreditables[] = getAccreditables();
        for (int i = 0; i < accreditables.length; i++) {
            accrString += " " + accreditables[i];
        }
        String string = "[identity:" + accrString + "]";
        return string;
    }

}
