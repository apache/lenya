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

package org.apache.lenya.cms.cocoon.selection;

import java.util.Arrays;
import java.util.Map;

import org.apache.avalon.framework.logger.AbstractLogEnabled;
import org.apache.avalon.framework.parameters.Parameters;
import org.apache.cocoon.environment.ObjectModelHelper;
import org.apache.cocoon.environment.Request;
import org.apache.cocoon.selection.Selector;
import org.apache.lenya.modules.resource.MediaTypeParser;

import com.google.common.base.Optional;

/**
 * <p>
 * Compares the media type passed as expression to the Accept header of the HTTP request.
 * Returns true if the media type matches.
 * </p>
 * <p>
 * If the Accept header contains specific media types and additional the double-wildcard
 * (universal) media type, and only the universal media type is matched, this match is
 * ignored.
 * </p>
 */
public class MediaTypeSelector extends AbstractLogEnabled implements Selector {

    @Override
    public boolean select(
            final String expression,
            @SuppressWarnings("rawtypes") final Map objectModel,
            final Parameters parameters) {
        
        final Request request = ObjectModelHelper.getRequest(objectModel);
        final Optional<String> type = MediaTypeParser.bestMatch(
                Arrays.asList(expression.split("\\s,\\s")),
                request.getHeader("Accept"),
                true);
        getLogger().debug(
                  request.getRequestURI()
                + "\n  Accepted: " + expression
                + "\n  Header:   " + request.getHeader("Accept")
                + "\n  Detected: " + type.or("-"));
        return type.isPresent() && !type.equals("*/*");
    }

}
