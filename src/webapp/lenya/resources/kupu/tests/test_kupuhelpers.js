/*****************************************************************************
 *
 * Copyright (c) 2003-2004 Kupu Contributors. All rights reserved.
 *
 * This software is distributed under the terms of the Kupu
 * License. See LICENSE.txt for license text. For a list of Kupu
 * Contributors see CREDITS.txt.
 *
 *****************************************************************************/

// $Id: test_kupuhelpers.js 9384 2005-02-21 15:11:54Z duncan $

function KupuHelpersTestCase() {
    this.name = 'KupuHelpersTestCase';

    this.setUp = function() {
        this.doc = document.getElementById('iframe').contentWindow.document;
        var head = this.doc.createElement('head');
        var title = this.doc.createElement('title');
        var titletext = this.doc.createTextNode('test');
        this.body = this.doc.createElement('body');

        title.appendChild(titletext);
        head.appendChild(title);
        var html = this.doc.documentElement;
        while (html.childNodes.length > 0)
            html.removeChild(html.childNodes[0]);
        html.appendChild(head);
        html.appendChild(this.body);
    };
        
    this.testSelectSelectItem = function() {
        var select = this.doc.createElement('select');
        this.body.appendChild(select);
        var option = this.doc.createElement('option');
        option.value = 'foo';
        select.appendChild(option);
        var option2 = this.doc.createElement('option');
        option2.value = 'bar';
        select.appendChild(option2);

        this.assertEquals(select.selectedIndex, 0);
        var ret = selectSelectItem(select, 'bar');
        this.assertEquals(select.selectedIndex, 1);
        var ret = selectSelectItem(select, 'baz');
        this.assertEquals(select.selectedIndex, 0);
    };

    this.testArrayContains = function() {
        var array = new Array(1, 2, 3);
        this.assert(array.contains(1));
        this.assert(array.contains(2));
        this.assertFalse(array.contains(4));
        this.assert(array.contains('1'));
        this.assertFalse(array.contains('1', 1));
    };

    this.testStringStrip = function() {
        // an empty string
        var str = "";
        this.assertEquals(str.strip(), str);
        // a string only containg whitespace
        str = " \n  \t ";
        this.assertEquals(str.strip(), "");
        // a string not containg any whitespaces
        str = "foo"
        this.assertEquals(str.strip(), str);
        // a word wrapped around whitespace
        str = "\n  foo \t  ";
        // a single character wrapped in whitespace
        str = "\n\t a \t\n";
        this.assertEquals(str.strip(), "a");
        // a string containing whitespace in the middle
        str = "foo bar baz";
        this.assertEquals(str.strip(), str);
        // a string containing spaces around it and in it
        str = " \t  foo bar\n baz  ";
        this.assertEquals(str.strip(), "foo bar\n baz");
        str = "  tu quoque Brute filie mee  ";
        this.assertEquals(str.strip(), "tu quoque Brute filie mee");
    };

    this.testLoadDictFromXML = function() {
        var dict = loadDictFromXML(document, 'xmlisland');
        this.assertEquals(dict['foo'], 'bar');
        this.assertEquals(dict['sna'], 'fu');
        for (var attr in dict) {
            this.assert(attr == 'foo' || attr == 'sna' || 
                            attr == 'some_int' || attr == 'nested' ||
                            attr == 'list');
        };
        this.assertEquals(dict['some_int'], 1);
        this.assertEquals(dict['nested']['foo'], 'bar');
        this.assertEquals(dict['list'][0], 0);
        this.assertEquals(dict['list'].length, 2);
    };
};

KupuHelpersTestCase.prototype = new TestCase;

function KupuSelectionTestCase() {
    this.setUp = function() {
        this.main_body = document.getElementById('body');
        this.iframe = this.main_body.appendChild(document.createElement('iframe'));
        this.kupudoc = new KupuDocument(this.iframe);
        this.document = this.iframe.contentWindow.document;
        var doc = this.document;
        doc.designMode = 'on';
        var docel = doc.documentElement ? doc.documentElement : doc;
        this.body = docel.appendChild(doc.createElement('body'));
        this.kupudoc.getWindow().focus();
    };

    this.testReplaceWithNode = function() {
        var node = this.document.createElement('p');
        var nbsp = this.document.createTextNode('\xa0');
        node.appendChild(nbsp);
        this.body.appendChild(node);
        var selection = _SARISSA_IS_IE ? new IESelection(this.kupudoc) : new MozillaSelection(this.kupudoc);
        selection.selectNodeContents(node);
        this.assertEquals(selection.getSelectedNode(), node);
    };

    this.tearDown = function() {
        this.main_body.removeChild(this.iframe);
    };
};

KupuSelectionTestCase.prototype = new TestCase;
