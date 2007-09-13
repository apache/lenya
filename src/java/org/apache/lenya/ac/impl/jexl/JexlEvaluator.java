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
package org.apache.lenya.ac.impl.jexl;

import org.apache.commons.jexl.Expression;
import org.apache.commons.jexl.ExpressionFactory;
import org.apache.commons.jexl.JexlContext;
import org.apache.commons.jexl.JexlHelper;
import org.apache.lenya.ac.AttributeDefinition;
import org.apache.lenya.ac.AttributeDefinitionRegistry;
import org.apache.lenya.ac.AttributeRuleEvaluator;
import org.apache.lenya.ac.User;
import org.apache.lenya.ac.impl.ValidationResult;

/**
 * JEXL-based attribute rule evaluator.
 */
public class JexlEvaluator implements AttributeRuleEvaluator {

    /**
     * @param user The user.
     * @param rule The rule.
     * @return if the rule is complied.
     */
    public boolean isComplied(User user, String rule) {
        if (user.getAttributeNames().length == 0) {
            return false;
        } else {
            try {
                JexlContext context = JexlHelper.createContext();
                AttributeDefinition attributesDef = AttributeDefinitionRegistry
                        .getAttributeDefinition();
                String[] names = attributesDef.getAttributeNames();
                for (int i = 0; i < names.length; i++) {
                    String[] values = user.getAttributeValues(names[i]);
                    if (values != null) {
                        if (values.length == 1) {
                            context.getVars().put(names[i], values[0]);
                        } else {
                            context.getVars().put(names[i], values);
                        }
                    }
                }
                Expression e = ExpressionFactory.createExpression(rule);
                Object result = e.evaluate(context);
                if (result instanceof Boolean) {
                    return ((Boolean) result).booleanValue();
                }
                else {
                    throw new RuntimeException("The result [" + result + "] is not a boolean value!");
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    /**
     * @param rule The rule to validate.
     * @return If the rule is valid.
     */
    public ValidationResult validate(String rule) {
        ValidationResult result;
        try {
            JexlContext context = JexlHelper.createContext();
            AttributeDefinition attributesDef = AttributeDefinitionRegistry
                    .getAttributeDefinition();
            String[] names = attributesDef.getAttributeNames();
            for (int i = 0; i < names.length; i++) {
                context.getVars().put(names[i], null);
            }
            try {
                Expression e = ExpressionFactory.createExpression(rule);
                e.evaluate(context);
                result = new ValidationResult(true);
            } catch (Exception ex) {
                result = new ValidationResult(false);
                result.addMessage(ex.getMessage());
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return result;
    }
}
