package org.apache.lenya.cms.linking;

import java.net.URI;
import java.net.URISyntaxException;

import junit.framework.TestCase;

import org.apache.lenya.cms.linking.UriUtil;

public class UriUtilTest extends TestCase {
    
    public void testRelativePath() throws URISyntaxException {
        
        final String baseUri = "/aaa/bbb/";
        
        verify(baseUri, "/aaa/bbb/ccc", "ccc");
        verify(baseUri, "/aaa/bbb", "../bbb");
        verify(baseUri, "/aaa/bbb/", "");
        verify(baseUri, "/aaa", "../../aaa");
        verify(baseUri, "/aaa/", "../");
        verify(baseUri, "/", "../../");
        verify(baseUri, "/aaa/bbb/foo", "foo");
        verify(baseUri, "/aaa/bbb/ccc/ddd", "ccc/ddd");
        verify(baseUri, "/aaa/foo", "../foo");
        verify(baseUri, "/aaa/foo/bar", "../foo/bar");
        verify(baseUri, "/foo/bar", "../../foo/bar");
        verify(baseUri, "/aaa/foo/bar/baz", "../foo/bar/baz");
        verify(baseUri, "/aaa/bbb/?hello", "?hello");
        verify(baseUri, "/aaa/?hello", "../?hello");
        verify(baseUri, "/?hello", "../../?hello");

        assertEquals("../..", UriUtil.getRelativeUri(baseUri, ""));
    }
    
    protected void verify(final String base, final String target, final String rel) throws URISyntaxException {
        final String resolvedRel = UriUtil.getRelativeUri(base, target);
        assertEquals(rel, resolvedRel);
        final String prefix = "http://foo.com";
        final URI baseUri = new URI(prefix + base);
        assertEquals(prefix + target, baseUri.resolve(resolvedRel).toString());
    }

}
