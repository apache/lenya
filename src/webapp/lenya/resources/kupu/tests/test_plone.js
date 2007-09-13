/*****************************************************************************
 *
 * Copyright (c) 2003-2004 Kupu Contributors. All rights reserved.
 *
 * This software is distributed under the terms of the Kupu
 * License. See LICENSE.txt for license text. For a list of Kupu
 * Contributors see CREDITS.txt.
 *
 *****************************************************************************/

// $Id$

// Various tests for html -> xhtml processing.

function KupuPloneTestCase() {
    this.name = 'KupuPloneTestCase';

    this.incontext = function(s) {
        return '<html><head><title>test</title></head><body>'+s+'</body></html>';
    }
    this.verifyResult = function(actual, expected) {
        //var expected = this.incontext(exp);

        if (actual == expected)
            return;

        var context = /<html><head><title>test<\/title><\/head><body>(.*)<\/body><\/html>/;
        if (context.test(actual) && context.test(expected)) {
            var a = context.exec(actual)[1];
            var e = context.exec(expected)[1];
            throw('Assertion failed: ' + a + ' != ' + e);
        }
        throw('Assertion failed: ' + actual + ' != ' + expected);
    }

    this.setUp = function() {
        this.editor = new KupuEditor(null, {}, null);
        this.doc = document.getElementById('iframe').contentWindow.document;
        var head = this.doc.createElement('head');
        var title = this.doc.createElement('title');
        var titletext = this.doc.createTextNode('test');this
        this.body = this.doc.createElement('body');

        title.appendChild(titletext);
        head.appendChild(title);
        var html = this.doc.documentElement;
        while (html.childNodes.length > 0)
            html.removeChild(html.childNodes[0]);
        html.appendChild(head);
        html.appendChild(this.body);
    };

    this.testRelativeLinks1 = function() {
        var data =  '<a href="http://localhost/cms/folder/emptypage#_ftnref1">[1]</a>';
        var expected = '<a href="#_ftnref1">[1]</a>';
        var base = 'http://localhost/cms/folder/';

        var actual = this.editor.makeLinksRelative(data, base);
        this.verifyResult(actual, expected);
    }
    this.testRelativeLinks2 = function() {
        var data =  '<a href="http://localhost/cms/folder/otherdoc#key">[1]</a>';
        var expected = '<a href="otherdoc#key">[1]</a>';
        var base = 'http://localhost/cms/folder/';

        var actual = this.editor.makeLinksRelative(data, base);
        this.verifyResult(actual, expected);
    }
    this.testRelativeLinks3 = function() {
        var data =  '<a href="http://localhost/cms/otherfolder/otherdoc">[1]</a>';
        var expected = '<a href="../otherfolder/otherdoc">[1]</a>';
        var base = 'http://localhost/cms/folder/';

        var actual = this.editor.makeLinksRelative(data, base);
        this.verifyResult(actual, expected);
    }
    this.testRelativeLinks4 = function() {
        var data =  '<a href="http://localhost:9080/plone/Members/admin/art1">[1]</a>';
        var expected = '<a href="art1">[1]</a>';
        var base = 'http://localhost:9080/plone/Members/admin/art1';

        var actual = this.editor.makeLinksRelative(data, base);
        this.verifyResult(actual, expected);
    }
    this.testRelativeLinks5 = function() {
        var data =  '<a href="http://localhost:9080/plone/Members/admin/art1/subitem">[1]</a>';
        var expected = '<a href="art1/subitem">[1]</a>';
        var base = 'http://localhost:9080/plone/Members/admin/art1';

        var actual = this.editor.makeLinksRelative(data, base);
        this.verifyResult(actual, expected);
    }

    this.testRelativeLinks6 = function() {
        var data =  '<a href="http://localhost:9080/plone/Members/admin">[1]</a>';
        var expected = '<a href=".">[1]</a>';
        var base = 'http://localhost:9080/plone/Members/admin/art1';

        var actual = this.editor.makeLinksRelative(data, base);
        this.verifyResult(actual, expected);
    }

}

KupuPloneTestCase.prototype = new TestCase;
