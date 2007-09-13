##############################################################################
#
# Copyright (c) 2003-2004 Kupu Contributors. All rights reserved.
#
# This software is distributed under the terms of the Kupu
# License. See LICENSE.txt for license text. For a list of Kupu
# Contributors see CREDITS.txt.
#
##############################################################################
"""Tests for the library tool

$Id: test_resourcetypemapper.py 6741 2004-09-27 09:52:44Z duncan $
"""

import os, sys
if __name__ == '__main__':
    execfile(os.path.join(sys.path[0], 'framework.py'))

from Testing.ZopeTestCase import ZopeTestCase
from Products.kupu.plone.librarytool import KupuLibraryTool

class TestIResourceTypeMapper(ZopeTestCase):
    """Test the implementation of IResourceMapper in KupuLibraryTool"""

    def afterSetUp(self):
        self.type_map = self.prepare()

    def prepare(self):
        type_map = KupuLibraryTool()
        type_map.addResourceType("foobar", ("Foo", "", "Bar"))
        type_map.addResourceType("bonobo", ("Monkey", " Ape\n"))
        return type_map

    def test_get_portal_types(self):
        type_map = self.type_map
        self.assertEqual(type_map.getPortalTypesForResourceType("foobar"),
                         ("Foo", "Bar"))
        self.assertEqual(type_map.getPortalTypesForResourceType("bonobo"),
                         ("Monkey", "Ape"))

    def test_update(self):
        type_map = self.type_map
        type_info = [
            dict(old_type='foobar', resource_type='foobar',
                 portal_types=("Nothing In Here Anymore",)),
            dict(old_type='bonobo', resource_type='chimpanse',
                 portal_types=("Bad Monkey", "Bad Ape")),
            ]
        type_map.updateResourceTypes(type_info)
        self.assertEqual(type_map.getPortalTypesForResourceType("foobar"),
                         ("Nothing In Here Anymore",))
        self.assertEqual(type_map.getPortalTypesForResourceType("chimpanse"),
                         ("Bad Monkey", "Bad Ape"))
        self.assertRaises(KeyError, type_map.getPortalTypesForResourceType,
                          "bonobo")

    def test_delete(self):
        type_map = self.type_map
        type_map.deleteResourceTypes(["foobar", "bonobo"])
        self.assertRaises(KeyError, type_map.getPortalTypesForResourceType,
                          "foobar")
        self.assertRaises(KeyError, type_map.getPortalTypesForResourceType,
                          "bonobo")

if __name__ == '__main__':
    framework()
else:
    # While framework.py provides its own test_suite()
    # method the testrunner utility does not.
    from unittest import TestSuite, makeSuite
    def test_suite():
        suite = TestSuite()
        suite.addTest(makeSuite(TestIResourceTypeMapper))
        return suite
