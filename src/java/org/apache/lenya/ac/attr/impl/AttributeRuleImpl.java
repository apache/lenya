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
package org.apache.lenya.ac.attr.impl;

import org.apache.lenya.ac.AccessControlException;
import org.apache.lenya.ac.Message;
import org.apache.lenya.ac.attr.AttributeOwner;
import org.apache.lenya.ac.attr.AttributeRule;
import org.apache.lenya.ac.attr.AttributeRuleEvaluator;
import org.apache.lenya.ac.attr.AttributeSet;
import org.apache.lenya.ac.impl.ValidationResult;
import org.apache.lenya.util.Assert;

/**
 * Attribute rule implementation.
 */
public class AttributeRuleImpl implements AttributeRule {
    
    /**
     * @param rule
     * @param attrSet
     * @param evaluator
     * @throws AccessControlException if the rule is not valid.
     */
    public AttributeRuleImpl(String rule, AttributeSet attrSet, AttributeRuleEvaluator evaluator)
            throws AccessControlException {
        Assert.notNull("rule", rule);
        Assert.notNull("attribute set", attrSet);
        Assert.notNull("evaluator", evaluator);
        
        ValidationResult result = evaluator.validate(rule, attrSet);
        if (!result.succeeded()) {
            StringBuffer msg = new StringBuffer();
            Message[] messages = result.getMessages();
            for (int i = 0; i < messages.length; i++) {
                if (i > 0) {
                    msg.append("; ");
                }
                msg.append(messages[i].getText());
            }
            throw new AccessControlException("The rule is not valid: " + msg.toString());
        }
        
        this.rule = rule;
        this.attributeSet = attrSet;
        this.evaluator = evaluator;
    }
    
    private String rule;
    private AttributeSet attributeSet;
    private AttributeRuleEvaluator evaluator;

    public AttributeSet getAttributeSet() {
        return this.attributeSet;
    }

    public String getRule() {
        return this.rule;
    }

    public boolean matches(AttributeOwner owner) {
        return this.evaluator.isComplied(owner, getRule());
    }

    public String toString() {
        return getRule();
    }
    
}
