package org.apache.lenya.utils;

import static org.junit.Assert.assertEquals;

import org.apache.lenya.utils.test.SpringEnv;
import org.junit.Before;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

public class URLInformationTest {
	
	String mockURL = "/test/authoring/DocumentTest.html";
	
	@Before
	public void setMockURL(){
		SpringEnv.setMockRequestContextHolder(mockURL);
	}
	
	@Test
	public void getWebappUrl(){
		URLInformation urli = new URLInformation();
		String pubId = urli.getWebappUrl();
		assertEquals(pubId,mockURL);
		
	}
	
	@Test
	public void getPublicationIdTest(){
		URLInformation urli = new URLInformation();
		String pubId = urli.getPublicationId();
		assertEquals(pubId,"test");
	}
	
	@Test
	public void getAreaTest(){
		URLInformation urli = new URLInformation();
		String area = urli.getArea();
		assertEquals(area, "authoring");
	}
	
	@Test
	public void getCompleteAreaTest(){
		URLInformation urli = new URLInformation();
		String area = urli.getCompleteArea();
		assertEquals(area, "authoring");
	}
	
	@Test
	public void getDocumentURL(){
		URLInformation urli = new URLInformation();
		String durl = urli.getDocumentUrl();
		assertEquals(durl, "/DocumentTest.html");
	}
	
}
