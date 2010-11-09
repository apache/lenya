package org.apache.lenya.cms.ac.usecases;


import static org.junit.Assert.assertEquals;

import org.apache.lenya.utils.test.SpringEnv;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

	public class LoginTest {
		
		String mockURL = "/test/authoring";
		@Before
		public void setUp(){
			SpringEnv.setMockRequestContextHolder(mockURL);
		}
		
		
		//Ignore("Find a way to test usecase") 
		@Ignore("Find a way to test usecase") @Test
    public void testInitParameters(){
		Login login = new Login();
		login.initParameters();
		String pubName = login.getParameterAsString("Publication");
    	assertEquals("test", pubName);
    }

}
