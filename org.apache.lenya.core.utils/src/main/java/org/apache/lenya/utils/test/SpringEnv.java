package org.apache.lenya.utils.test;

import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

public class SpringEnv {

	public static void setMockRequestContextHolder(String mockURL){
		
		MockHttpServletRequest request = new MockHttpServletRequest("GET", mockURL);
		MockHttpSession session = new MockHttpSession();
		request.setSession(session);
		RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));
		
		
	}
	
}
