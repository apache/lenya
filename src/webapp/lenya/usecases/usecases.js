/*
 * Copyright  1999-2004 The Apache Software Foundation
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
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
 
/* Helper method to add all request parameters to a usecase */
function passRequestParameters(flowHelper, usecase) {
	var names = cocoon.request.getParameterNames();
	while (names.hasMoreElements()) {
	    var name = names.nextElement();
		if (!name.equals("lenya.usecase")
			&& !name.equals("lenya.continuation")
			&& !name.equals("submit")) {
			
			var value = flowHelper.getRequest(cocoon).get(name);
			
			var string = new Packages.java.lang.String();
			if (string.getClass().isInstance(value)) {
				usecase.setParameter(name, value);
			}
			else {
				usecase.setPart(name, value);
			}
			
		}
	}
}

/* Helper method to choose the appropriate view pipeline. The usecases displayed in a tab in the site
   area need a more complex view, so they have their own pipeline. */
function selectView(usecaseName) { 
	var usecaseView = new Packages.java.lang.String(usecaseName).replace('.', '/');
	var isTabUsecase = new Packages.java.lang.String(usecaseName).startsWith('tab');
	if (isTabUsecase) {
		var view = "view-tab/" + usecaseView;
	} else {
		var view = "view/" + usecaseView;
	}

	return view;
}

function executeUsecase() {
	var usecaseName = cocoon.request.getParameter("lenya.usecase");
	var usecaseResolver = cocoon.getComponent("org.apache.lenya.cms.usecase.UsecaseResolver");
	var usecase = usecaseResolver.resolve(usecaseName);
	
	var flowHelper = new Packages.org.apache.lenya.cms.cocoon.flow.FlowHelper();
	var envelope = flowHelper.getPageEnvelope(cocoon);
	var document = envelope.getDocument();
	
	passRequestParameters(flowHelper, usecase);
	
	var view = selectView(usecaseName);

	var ready = false;
	var success = false;

	while (!ready) {
	
		cocoon.sendPageAndWait(view, {
		    "usecase" : usecase
		});
		
		passRequestParameters(flowHelper, usecase);
		usecase.advance();
		
		if (cocoon.request.getParameter("submit")) {
			usecase.checkExecutionConditions();
			if (usecase.getErrorMessages().isEmpty()) {
				usecase.execute();
				if (usecase.getErrorMessages().isEmpty()) {
					usecase.checkPostconditions();
					if (usecase.getErrorMessages().isEmpty()) {
						ready = true;
						success = true;
					}
				}
			}
		}
		else if (cocoon.request.getParameter("cancel")) {
			ready = true;
		}
	
	}
	
	var url = envelope.getContext() + usecase.getTargetURL(success);
	cocoon.redirectTo(url);
	
}
