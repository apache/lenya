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

/* $Id:$  */

package org.apache.lenya.cms.cocoon.components.modules.input;

import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.avalon.framework.parameters.ParameterException;
import org.apache.avalon.framework.parameters.Parameters;

/**
 * Input module parameter class.
 * Parses input module parameters and offers accessors for the parameters.
 * The parameters are expected to be separated by ":".
 */
public class InputModuleParameters {
    
    protected final static char PARAM_SEPARATOR = ':';
    
    protected Parameters params;
    protected final String[] paramList;
    
    /**
     * Parse input module parameters. 
     * @param parameters Parameter to be parsed.
     * @param paramList List of expected parameters.
     * @param minParams Minimum number of parameters expected.
     * @throws ConfigurationException if there is an error parsing the parameters.
     */
    public InputModuleParameters(String parameters, final String[] paramList, int minParams)
    throws ConfigurationException
    {
        params = new Parameters();
        this.paramList = (String[])paramList.clone();
        parseParameters(parameters, minParams);
    }
    
    /**
     * Parse parameters according to the parameter list passed.
     * @param parameters
     * @param minParams Minimum number of parameters.
     * @return Parameters object initialized with parsed parameters.
     * @throws ConfigurationException
     */
    protected Parameters parseParameters(String parameters, int minParams)
    throws ConfigurationException
    {
        // Parse parameters
        int start = 0;
        int end = parameters.indexOf(PARAM_SEPARATOR);
        for (int i=0; i<paramList.length; i++) {
            if (end != -1) {
                String paramToken = parameters.substring(start, end);
                params.setParameter(paramList[i], paramToken);
                start = end+1;
                end = parameters.indexOf(PARAM_SEPARATOR, start+1);
            } else {
                if ((i+1) < minParams) {
                    // A mandatory parameter is missing.
                    throw new ConfigurationException("Error parsing parameters: mandatory parameter '"
                            + paramList[i] + "' not found [" + parameters + "]");
                } else if (i == 0) {
                    // Zero or one parameter passed.
                    if (parameters.length() != 0) {
                        params.setParameter(paramList[i], parameters);
                    }
                    break;
                } else {
                    // All parameters parsed except the last one.
                    String paramToken = parameters.substring(start);
                    if (paramToken.length() != 0) {
                        params.setParameter(paramList[i], paramToken);
                    }
                    break;
                }
            }
        }
        return params;
    }
    
    /**
     * Get a parameter.
     * @param param Name of requested parameter.
     * @return Requested parameter.
     * @throws ParameterException if the specified parameter cannot be found
     */
    public String getParameter(String param) throws ParameterException
    {
        return params.getParameter(param);
    }
    
    /**
     * Does a parameter with given name exists?
     * @param param Parameter name.
     * @return True if parameters exists, otherwise false.
     */
    public boolean isParameter(String param) {
        return params.isParameter(param);
    }
}
