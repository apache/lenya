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
package org.apache.lenya.ac;

import org.apache.lenya.ac.impl.ValidationResult;

/**
 * Service to evaluate attribute rules.
 */
public interface AttributeRuleEvaluator {

    /**
     * @param user The user.
     * @param rule The rule.
     * @return if the rule is complied.
     */
    public boolean isComplied(AttributeOwner user, String rule);

    /**
     * @param rule The rule to validate.
     * @return If the rule is valid.
     */
    public ValidationResult validate(String rule);

}
