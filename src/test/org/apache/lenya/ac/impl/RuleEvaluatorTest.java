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

/* $Id: GroupManagerTest.java 473841 2006-11-12 00:46:38Z gregor $  */
package org.apache.lenya.ac.impl;

import junit.framework.TestCase;

import org.apache.avalon.framework.logger.ConsoleLogger;
import org.apache.lenya.ac.attr.AttributeSet;
import org.apache.lenya.ac.attr.AttributeSetRegistry;
import org.apache.lenya.ac.attr.AttributeRuleEvaluator;
import org.apache.lenya.ac.attr.antlr.AntlrEvaluator;
import org.apache.lenya.ac.file.FileUser;

public class RuleEvaluatorTest extends TestCase {
    
    protected static final String ATTR_NAME = "attr";

    public void testRuleEvaluation() throws Exception {
        
        AttributeSet def = new AttributeSet() {
            public String[] getAttributeNames() {
                String[] names = { ATTR_NAME };
                return names;
            }
        };
        AttributeSetRegistry.register(def);
        
        AttributeRuleEvaluator evaluator = new AntlrEvaluator(new ConsoleLogger());
        
        FileUser user = new FileUser();
        String[] values = { "foo", "bar" };
        user.setAttributeValues(ATTR_NAME, values);
        
        assertTrue(evaluator.isComplied(user, ATTR_NAME + " == \"bar\""));
        assertFalse(evaluator.isComplied(user, ATTR_NAME + " == \"baz\""));
        
        String[] value = { "foo" };
        user.setAttributeValues(ATTR_NAME, value);
        assertTrue(evaluator.isComplied(user, ATTR_NAME + " == \"foo\""));
        assertFalse(evaluator.isComplied(user, ATTR_NAME + " == \"bar\""));
        

    }

}
