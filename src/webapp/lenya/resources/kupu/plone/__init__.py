##############################################################################
#
# Copyright (c) 2003-2004 Kupu Contributors. All rights reserved.
#
# This software is distributed under the terms of the Kupu
# License. See LICENSE.txt for license text. For a list of Kupu
# Contributors see CREDITS.txt.
#
##############################################################################
"""Kupu Plone integration

This package is a python package and contains a filesystem-based skin
layer containing the necessary UI customization to integrate Kupu as a
wysiwyg editor in Plone.

$Id: __init__.py 6741 2004-09-27 09:52:44Z duncan $
"""
from Products.CMFCore.DirectoryView import registerDirectory
from Products.CMFCore import utils
from Products.kupu.plone.plonelibrarytool import PloneKupuLibraryTool

registerDirectory('kupu_plone_layer', globals())

def initialize(context):
    utils.ToolInit("kupu Library Tool",
                   tools=(PloneKupuLibraryTool,),
                   product_name='kupu',
                   icon="kupu_icon.gif",
                   ).initialize(context)
