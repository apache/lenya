
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

function executeUsecase() {
	var usecaseName = cocoon.request.getParameter("lenya.usecase");
	var usecaseResolver = cocoon.getComponent("org.apache.lenya.cms.usecase.UsecaseResolver");
	var usecase = usecaseResolver.resolve(usecaseName);
	
	var flowHelper = new Packages.org.apache.lenya.cms.cocoon.flow.FlowHelper();
	var situation = flowHelper.getSituation(cocoon);
	var envelope = flowHelper.getPageEnvelope(cocoon);
	var document = envelope.getDocument();
	
	passRequestParameters(flowHelper, usecase);
	
	var ready = false;
	var success = false;
	
	var usecaseView = new Packages.java.lang.String(usecaseName).replace('.', '/');
	
	while (!ready) {
	
		cocoon.sendPageAndWait("view/" + usecaseView, {
		    "usecase" : usecase
		});
		
		if (cocoon.request.getParameter("submit")) {
		
		    passRequestParameters(flowHelper, usecase);
			
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
