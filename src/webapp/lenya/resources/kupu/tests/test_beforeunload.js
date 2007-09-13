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

// Test form beforeUnload processing

// The handler is careful not to use any global variables, so we have
// to work a bit to find out its class.
var BeforeUnloadHandler = window.onbeforeunload.tool.constructor;

function KupuBeforeUnloadTestCase() {
    this.name = 'KupuBeforeUnloadTestCase';

    function Submit(index) { return '<input type="submit" id="SUBMIT'+index+'" value="submit" />'; }
    // Field types to test
    this.INPUTTEXT = '<input type="text" value="42" id="INPUTTEXT" name="i1" />';
    this.INPUTCLIENT = '<input type="text" value="42" id="INPUTTEXT2" />';
    this.TEXTAREA = '<textarea id="TEXTAREA" name="i2">42</textarea>';
    this.RADIO = '<INPUT type="radio" id="radio1" name="radio" CHECKED>1-10 years old \
        <INPUT type="radio" id="radio2" name="radio">11 years old\
        <INPUT type="radio" id="radio3" name="radio">12-120 years old';
    this.BUTTON = '<input type="button" value="click me" id="BUTTON" name="b1" />';
    this.CHECKBOX = '<input type="checkbox" checked id="chk1" name="c1">Uncheck me<br>\
        <input type="checkbox" id="chk2" name="c2">check me';

    this.FILE = '<input type="file" id="FILE" value="hello" name="f1" />';
    
    this.HIDDEN = '<input type="hidden" id="HIDDEN" value="42" name="h1" />';
    this.HIDDEN = '<form id="FORMHIDDEN">'+this.HIDDEN+'</form>';
    
    this.IMAGE = '<input type="image" id="IMAGE" name="im1" />';
    this.PASSWORD = '<input type="password" value="secret" id="PASSWORD" name="pass1" />';
    this.RESET = '<input type="reset" id="RESET" value="reset" "name="reset1" />';
    this.SUBMIT = Submit('');
    this.SELECTONE = '<select id="SELECTONE" id="SELECT" name="select1">\
        <OPTION VALUE="1">Red\
        <OPTION VALUE="2">Green\
        <OPTION VALUE="3">Blue</SELECT>';
    this.SELECTONEA = '<select id="SELECTONE" id="SELECT" name="select2">\
        <OPTION VALUE="1">Red\
        <OPTION VALUE="2" SELECTED>Green\
        <OPTION VALUE="3">Blue</SELECT>';
    this.SELECTMULTIPLE = '<select id="SELECTMULTIPLE" id="SELECT" MULTIPLE name="select3">\
        <OPTION VALUE="1">Red\
        <OPTION VALUE="2" SELECTED>Green\
        <OPTION VALUE="3">Blue</SELECT>';
    this.FORM1 = '<form id="FORM1">'+this.INPUTTEXT+Submit(1)+'</form>';
    this.FORM2 = '<form id="FORM2">'+this.RADIO+Submit(2)+'</form>';
    this.FORM3 = '<form id="FORM3">'+this.SELECTMULTIPLE+Submit(3)+'</form>';
    this.FORMS = '<div id="DIV1">'+this.FORM1+this.FORM2+'</div>'+this.FORM3;
}

KupuBeforeUnloadTestCase.prototype = new TestCase;
Class = KupuBeforeUnloadTestCase.prototype;
var BeforeUnloadHandler = window.onbeforeunload.tool.constructor;

Class.setUp = function() {
    
    this.bu = new BeforeUnloadHandler();
};

Class.tearDown = function() {
    window.onbeforeunload = null;
};

Class.setHtml = function(fragment) {
    var testdiv = document.getElementById('testdiv');
    testdiv.innerHTML = fragment;
};

Class.assertNotChanged = function(id) {
    var field = document.getElementById(id);
    //this.debug("element "+id+" is "+field+" type "+field.type);
    this.assertFalse(this.bu.isElementChanged(field), "field not changed");
}

Class.assertChanged = function(id, newvalue) {
    var field = document.getElementById(id);
    //this.debug("element "+id+" is "+field+" type "+field.type);
    if (newvalue) field.value = newvalue;
    this.assertTrue(this.bu.isElementChanged(field), "field changed");
}

Class.simpleFieldTest = function(fragment, id, value) {
    this.setHtml(fragment);
    if (value) {
        this.assertChanged(id, value);
    } else {
        this.assertNotChanged(id);
    }
}

Class.testInputField = function() {
    this.simpleFieldTest(this.INPUTTEXT, "INPUTTEXT");
    this.simpleFieldTest(this.INPUTTEXT, "INPUTTEXT", 37);
}

Class.testClientIgnored = function() {
    var id = "INPUTTEXT2";
    this.setHtml(this.INPUTCLIENT);
    this.assertNotChanged(id);
    var field = document.getElementById(id);
    field.value = 25;
    this.assertNotChanged(id);
    // Give the field a name and then we pick up the change
    field.name = "ANINPUT";
    this.assertChanged(id);
}

Class.testTextArea = function() {
    this.simpleFieldTest(this.TEXTAREA, "TEXTAREA");
    this.simpleFieldTest(this.TEXTAREA, "TEXTAREA", 37);
}

Class.testRadio = function() {
    this.setHtml(this.RADIO);
    this.assertNotChanged("radio1");
    this.assertNotChanged("radio2");
    this.assertNotChanged("radio3");
    document.getElementById("radio3").checked = true;
    this.assertChanged("radio1");
    this.assertNotChanged("radio2");
    this.assertChanged("radio3");
}

Class.testButton = function() {
    this.simpleFieldTest(this.BUTTON, "BUTTON");
}

Class.testCheck = function() {
    this.setHtml(this.CHECKBOX);
    this.assertNotChanged("chk1");
    this.assertNotChanged("chk2");
    document.getElementById("chk1").checked = false;
    document.getElementById("chk2").checked = true;
    this.assertChanged("chk1");
    this.assertChanged("chk2");
    document.getElementById("chk1").checked = true;
    document.getElementById("chk2").checked = false;
    this.assertNotChanged("chk1");
    this.assertNotChanged("chk2");
}

Class.testFile = function() {
    this.simpleFieldTest(this.FILE, "FILE");
    // Cannot modify file from javascript, so no way to test changed
    // field.
}

Class.testHidden = function() {
    this.simpleFieldTest(this.HIDDEN, "HIDDEN");
    var form = document.getElementById("FORMHIDDEN");
    this.bu.addForms(form);
    this.assertChanged("HIDDEN", "37");
}
Class.testImage = function() {
    this.simpleFieldTest(this.IMAGE, "IMAGE");
}
Class.testPassword = function() {
    this.simpleFieldTest(this.PASSWORD, "PASSWORD");
    this.simpleFieldTest(this.PASSWORD, "PASSWORD", "hidden");
}
Class.testReset = function() {
    this.simpleFieldTest(this.RESET, "RESET");
}
Class.testSubmit = function() {
    this.simpleFieldTest(this.SUBMIT, "SUBMIT");
}
Class.testSelectOne = function() {
    this.setHtml(this.SELECTONE);
    // select with no default starts with first element selected.
    this.assertNotChanged("SELECTONE");
    var field = document.getElementById("SELECTONE");
    field.options[1].selected = true;
    this.assertChanged("SELECTONE");
    field.options[0].selected = true;
    this.assertNotChanged("SELECTONE");
}

Class.testSelectOneA = function() {
    this.setHtml(this.SELECTONEA);
    this.assertNotChanged("SELECTONE");
    var field = document.getElementById("SELECTONE");
    field.options[2].selected = true;
    this.assertChanged("SELECTONE");
    field.options[1].selected = true;
    this.assertNotChanged("SELECTONE");
}
Class.testSelectMultiple = function() {
    this.setHtml(this.SELECTMULTIPLE);
    this.assertNotChanged("SELECTMULTIPLE");
    var field = document.getElementById("SELECTMULTIPLE");
    field.options[2].selected = true;
    this.assertChanged("SELECTMULTIPLE");
    field.options[2].selected = false;
    this.assertNotChanged("SELECTMULTIPLE");
    field.options[1].selected = false;
    this.assertChanged("SELECTMULTIPLE");
}
Class.testForm1 = function() {
    this.setHtml(this.FORM1);
    this.assertNotChanged("FORM1");
    document.getElementById("INPUTTEXT").value = "37";
    this.assertChanged("FORM1");
}
Class.testAddForm = function() {
    this.setHtml(this.FORMS);
    var form = document.getElementById("FORM1");
    this.bu.addForms(form);
    this.assertEquals(1, this.bu.forms.length);
    this.assertFalse(this.bu.isAnyFormChanged(form), "form not changed");
    document.getElementById("radio3").checked = true;
    this.assertFalse(this.bu.isAnyFormChanged(form), "form not changed");
    document.getElementById("INPUTTEXT").value = "37";
    this.assertTrue(this.bu.isAnyFormChanged(form), "form changed");
}

Class.testAddRemoveForm = function() {
    this.setHtml(this.FORMS);
    var div = document.getElementById("DIV1");
    this.bu.addForms(null);
    this.assertEquals(0, this.bu.forms.length);

    this.bu.addForms(div);
    this.assertEquals(2, this.bu.forms.length);

    var form = document.getElementById("FORM1");
    this.bu.addForms(form);
    this.assertEquals(2, this.bu.forms.length);
    this.bu.removeForms(form);
    this.assertEquals(1, this.bu.forms.length);

    var form3 = document.getElementById("FORM3");
    this.bu.addForms(form3);
    this.assertEquals(2, this.bu.forms.length);
    this.bu.removeForms(div);
    this.assertEquals(1, this.bu.forms.length);

    this.bu.removeForms(null);
    this.assertEquals(1, this.bu.forms.length);
}

Class.testSubmit = function() {
    this.setHtml(this.FORMS);
    var form1 = document.getElementById("FORM1");
    var form2 = document.getElementById("FORM3");
    var form3 = document.getElementById("FORM3");
    var div = document.getElementById("DIV1");
    this.bu.addForms(div);

    this.assertEquals(this.bu.execute(), undefined);
    document.getElementById("INPUTTEXT").value = "37";
    this.assertEquals(this.bu.execute(), this.bu.message);

    window.onbeforeunload = this.bu.execute;
    this.bu.onsubmit();

    this.assertEquals(this.bu.execute(), undefined);
}

Class.testHandlers = function() {
    this.setHtml(this.FORMS);
    var form1 = document.getElementById("FORM1");
    var form2 = document.getElementById("FORM3");
    var form3 = document.getElementById("FORM3");
    var div = document.getElementById("DIV1");
    this.bu.addForms(div);

    function Handler() {
        return "called!";
    }
    this.bu.addHandler(Handler);
    this.assertEquals(this.bu.execute(), Handler());
}

Class.testIdOverride = function() {
    this.setHtml(this.FORMS);
    var form = document.getElementById("FORM1");
    this.bu.addForms(form);
    document.getElementById("INPUTTEXT").value = "37";
    this.assertTrue(this.bu.execute());
    this.bu.chkId['INPUTTEXT'] = function() { return false; }
    this.assertFalse(this.bu.execute());
}
