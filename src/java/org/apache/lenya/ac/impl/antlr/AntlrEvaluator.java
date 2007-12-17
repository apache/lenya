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
package org.apache.lenya.ac.impl.antlr;

import org.antlr.runtime.ANTLRStringStream;
import org.antlr.runtime.CharStream;
import org.antlr.runtime.CommonTokenStream;
import org.antlr.runtime.RecognitionException;
import org.apache.avalon.framework.logger.AbstractLogEnabled;
import org.apache.avalon.framework.logger.Logger;
import org.apache.lenya.ac.AttributeDefinition;
import org.apache.lenya.ac.AttributeDefinitionRegistry;
import org.apache.lenya.ac.AttributeOwner;
import org.apache.lenya.ac.AttributeRuleEvaluator;
import org.apache.lenya.ac.ErrorHandler;
import org.apache.lenya.ac.Message;
import org.apache.lenya.ac.SimpleErrorHandler;
import org.apache.lenya.ac.impl.ValidationResult;
import org.apache.lenya.util.Assert;

/**
 * ANTLR-based attribute rule evaluator.
 */
public class AntlrEvaluator extends AbstractLogEnabled implements AttributeRuleEvaluator {
    
    /**
     * @param logger The logger.
     */
    public AntlrEvaluator(Logger logger) {
        enableLogging(logger);
    }

    protected static final String UNDEFINED_VALUE = "undefined";

    public boolean isComplied(AttributeOwner user, String rule) {
        ErrorHandler handler = new SimpleErrorHandler();
        ExpressionsParser parser = getParser(rule, handler);
        try {
            String[] names = getAttributeNames();
            for (int i = 0; i < names.length; i++) {
                String[] values = user.getAttributeValues(names[i]);
                if (values == null) {
                    parser.memory.put(names[i], UNDEFINED_VALUE);
                }
                else {
                    if (values.length == 1) {
                        parser.memory.put(names[i], values[0]);
                    } else {
                        parser.memory.put(names[i], values);
                    }
                }
            }
            boolean result = parser.prog();
            Message[] errors = handler.getErrors();
            if (errors.length == 0) {
                return result;
            }
            else {
                getLogger().error("Invalid rule: " + rule);
                for (int i = 0; i < errors.length; i++) {
                    getLogger().error(errors[i].getText());
                }
                return false;
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public ValidationResult validate(String rule) {
        ErrorHandler handler = new SimpleErrorHandler();
        ExpressionsParser parser = getParser(rule, handler);
        ValidationResult result;
        try {
            String[] names = getAttributeNames();
            for (int i = 0; i < names.length; i++) {
                parser.memory.put(names[i], UNDEFINED_VALUE);
            }
            parser.prog();
            result = new ValidationResult(handler.getErrors());
        } catch (RecognitionException e) {
            throw new RuntimeException(e);
        }
        return result;
    }

    protected String[] getAttributeNames() {
        AttributeDefinition attributesDef = AttributeDefinitionRegistry
        .getAttributeDefinition();
        String[] names = attributesDef.getAttributeNames();
        return names;
    }

    protected ExpressionsParser getParser(String rule, ErrorHandler handler) {
        Assert.notNull("rule", rule);
        CharStream stream = new ANTLRStringStream(rule);
        ExpressionsLexer lexer = new ExpressionsLexer(stream);
        lexer.setErrorHandler(handler);
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        ExpressionsParser parser = new ExpressionsParser(tokens);
        parser.setErrorHandler(handler);
        return parser;
    }

}
