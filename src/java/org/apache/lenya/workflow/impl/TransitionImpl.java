/*
$Id
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
package org.apache.lenya.workflow.impl;

import org.apache.lenya.workflow.Action;
import org.apache.lenya.workflow.Condition;
import org.apache.lenya.workflow.Event;
import org.apache.lenya.workflow.Situation;
import org.apache.lenya.workflow.Transition;

import java.util.ArrayList;
import java.util.List;


/**
 *
 * @author  andreas
 */
public class TransitionImpl implements Transition {
    /** Creates a new instance of TransitionImpl */
    protected TransitionImpl(StateImpl sourceState, StateImpl destinationState) {
        assert sourceState != null;
        assert destinationState != null;

        source = sourceState;
        destination = destinationState;
    }

    private List actions = new ArrayList();

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public Action[] getActions() {
        return (Action[]) actions.toArray(new Action[actions.size()]);
    }

    /**
     * DOCUMENT ME!
     *
     * @param action DOCUMENT ME!
     */
    public void addAction(Action action) {
        assert action != null;
        actions.add(action);
    }

    private List conditions = new ArrayList();

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public Condition[] getConditions() {
        return (Condition[]) conditions.toArray(new Condition[conditions.size()]);
    }

    /**
     * DOCUMENT ME!
     *
     * @param condition DOCUMENT ME!
     */
    public void addCondition(Condition condition) {
        assert condition != null;
        conditions.add(condition);
    }

    private Event event;

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public Event getEvent() {
        return event;
    }

    /**
     * DOCUMENT ME!
     *
     * @param anEvent DOCUMENT ME!
     */
    public void setEvent(Event anEvent) {
        assert anEvent != null;
        event = anEvent;
    }

    private StateImpl source;

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public StateImpl getSource() {
        return source;
    }

    private StateImpl destination;

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public StateImpl getDestination() {
        return destination;
    }

    /** Returns if the transition can fire in a certain situation.
     *
     */
    public boolean canFire(Situation situation) {
        Condition[] conditions = getConditions();
        boolean canFire = true;

        for (int i = 0; i < conditions.length; i++) {
            if (!conditions[i].isComplied(situation)) {
                canFire = false;
            }
        }

        return canFire;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    public String toString() {
        String string = getEvent().getName() + " [";
        Condition[] conditions = getConditions();

        for (int i = 0; i < conditions.length; i++) {
            if (i > 0) {
                string += ", ";
            }

            string += conditions[i].toString();
        }

        string += "]";

        Action[] actions = getActions();

        if (actions.length > 0) {
            string += " / ";

            for (int i = 0; i < actions.length; i++) {
                if (i > 0) {
                    string += ", ";
                }

                string += actions[i].toString();
            }
        }

        return string;
    }
}
