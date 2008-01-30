/*
* Licensed to the Apache Software Foundation (ASF) under one or more
* contributor license agreements.  See the NOTICE file distributed with
* this work for additional information regarding copyright ownership.
* The ASF licenses this file to You under the Apache License, Version 2.0
* (the "License"); you may not use this file except in compliance with
* the License.  You may obtain a copy of the License at
*
*     http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/

/*
* Clear the URI parameterizer cache.
*/
function clearUriParameterizerCache() {
    var parameterizer;
    try {
        parameterizer = cocoon.getComponent("org.apache.lenya.cms.cocoon.uriparameterizer.URIParameterizer");
        parameterizer.clearCache();
    }
    finally {
        if (parameterizer) {
            cocoon.releaseComponent(parameterizer);
        }
    }
    cocoon.sendPage("cache/cache.xml", { "state" : "cleared" });
}