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

function customLoopFlow(view,proxy,generic) {
   // load some helper functions
   // cocoon.load("fallback://lenya/modules/cforms/flow/lenyadoc-utils.js");
    try {
        var formDef = "fallback://lenya/modules/cforms/usecases/dynamicrepeater.xml";
        var formBind = "fallback://lenya/modules/cforms/usecases/dynamicrepeater_binding.xml";
        var formView = "usecases-view/menu/modules/cforms/usecases/dynamicrepeater_template.xml";
        generic.form = new Form(formDef);
        generic.form.setAttribute("counter", new java.lang.Integer(0));
        generic.form.createBinding(formBind);
        
        try {
                var parser = cocoon.getComponent(Packages.org.apache.excalibur.xml.dom.DOMParser.ROLE);
                var resolver = cocoon.getComponent(Packages.org.apache.cocoon.environment.SourceResolver.ROLE);
                var source = resolver.resolveURI(proxy.getParameter('sourceUri'));
                var is = new Packages.org.xml.sax.InputSource(source.getInputStream());
                is.setSystemId(source.getURI());
                generic.doc = parser.parseDocument(is);
        } finally {
                if (source != null)
                resolver.release(source);
                cocoon.releaseComponent(parser);
                cocoon.releaseComponent(resolver);
        }
        
        generic.form.load(generic.doc);
        generic.form.showForm(formView, {"usecase" : proxy});
    } catch (exception) {
        // if an exception was thrown by the view, allow the usecase to rollback the transition
        log("error", "Exception during customLoopFlow: " + exception);
        throw exception;
    }
}

function customSubmitFlow(usecase, generic) {
    generic.form.save(generic.doc);
    usecase.setParameter("xml", generic.doc);
    defaultSubmitFlow(usecase);
}
