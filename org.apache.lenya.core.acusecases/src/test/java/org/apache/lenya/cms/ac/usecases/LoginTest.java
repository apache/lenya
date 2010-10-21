package org.apache.lenya.cms.ac.usecases;


import static org.junit.Assert.assertEquals;

import org.junit.Ignore;
import org.junit.Test;

public class LoginTest {

	@Ignore("Find a way to retrieve all the test context") @Test
    public void testParametersInit(){
		Login login = new Login();
		login.initParameters();
		String pubName = login.getParameterAsString("Publication");
    	assertEquals("test", pubName);
    }

}
