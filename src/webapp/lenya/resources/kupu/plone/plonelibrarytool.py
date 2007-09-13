##############################################################################
#
# Copyright (c) 2003-2004 Kupu Contributors. All rights reserved.
#
# This software is distributed under the terms of the Kupu
# License. See LICENSE.txt for license text. For a list of Kupu
# Contributors see CREDITS.txt.
#
##############################################################################
"""Plone Kupu library tool

This module contains the Plone specific version of the Kupu library
tool.

$Id: plonelibrarytool.py 9723 2005-03-10 11:58:38Z duncan $
"""
import os
from ZODB.PersistentList import PersistentList
from ZODB.PersistentMapping import PersistentMapping
from AccessControl import ClassSecurityInfo
from OFS.SimpleItem import SimpleItem
import Globals
from Globals import InitializeClass

from Products.PageTemplates.PageTemplateFile import PageTemplateFile
from Products.CMFCore.utils import UniqueObject

from Products.kupu.plone.librarytool import KupuLibraryTool
from Products.kupu.plone import permissions, scanner
from Products.kupu import kupu_globals

_default_libraries = (
    dict(id="string:portal_root",
         title="string:Home",
         uri="string:${portal_url}",
         src="string:${portal_url}/kupucollection.xml",
         icon="string:${portal_url}/kupuimages/kupulibrary.png"),
    dict(id="string:${folder_url}",
         title="string:Current folder",
         uri="string:${folder_url}",
         src="string:${folder_url}/kupucollection.xml",
         icon="string:${portal_url}/kupuimages/kupulibrary.png"),
    dict(id="myitems",
         title="string:My items",
         uri="string:${portal_url}/kupumyitems.xml",
         src="string:${portal_url}/kupumyitems.xml",
         icon="string:${portal_url}/kupuimages/kupusearch_icon.gif"),
    dict(id="recentitems",
         title="string:Recent items",
         uri="string:${portal_url}/kupurecentitems.xml",
         src="string:${portal_url}/kupurecentitems.xml",
         icon="string:${portal_url}/kupuimages/kupusearch_icon.gif")
    )

_default_resource_types = {
    'collection': ('Plone Site', 'Folder', 'Large Plone Folder'),
    'mediaobject': ('Image',),
    'linkable': ('Document', 'Image', 'File', 'News Item', 'Event')
    }

# Tidy up html by exlcluding lots of things.
_excluded_html = [
  (('center', 'span', 'tt', 'big', 'small', 'u', 's', 'strike', 'basefont', 'font'), ()),
  ((), ('dir','lang','valign','halign','border','frame','rules','cellspacing','cellpadding','bgcolor')),
  (('table','th','td'),('width','height')),
]

# Default should list all styles used by Kupu
_style_whitelist = ['text-align', 'list-style-type', 'float']

class PloneKupuLibraryTool(UniqueObject, SimpleItem, KupuLibraryTool):
    """Plone specific version of the kupu library tool"""

    id = "kupu_library_tool"
    meta_type = "Kupu Library Tool"
    title = "Kupu WYSIWYG editor configuration"
    security = ClassSecurityInfo()

    # protect methods provided by super class KupuLibraryTool
    security.declareProtected(permissions.QueryLibraries, "getLibraries",
                              "getPortalTypesForResourceType")
    security.declareProtected(permissions.ManageLibraries, "addLibrary",
                              "deleteLibraries", "updateLibraries",
                              "moveUp", "moveDown")
    security.declareProtected(permissions.ManageLibraries, "addResourceType",
                              "updateResourceTypes", "deleteResourceTypes")

    def __init__(self):
        self._libraries = PersistentList()
        self._res_types = PersistentMapping()
        self.linkbyuid = True

    def manage_afterAdd(self, item, container):
        # We load default values here, so __init__ can still be used
        # in unit tests. Plus, it only makes sense to load these if
        # we're being added to a Plone site anyway
        for lib in _default_libraries:
            self.addLibrary(**lib)
        self._res_types.update(_default_resource_types)

    security.declareProtected('View', "getLinkbyuid")
    def getLinkbyuid(self):
        """Returns 'is linking by UID enabled'?"""
        try:
            return self.linkbyuid
        except AttributeError:
            return 1

    security.declareProtected('View', "getTableClassnames")
    def getTableClassnames(self):
        """Return a list of classnames supported in tables"""
        try:
            return self.table_classnames
        except AttributeError:
            return ('plain', 'listing', 'grid', 'data')

    security.declareProtected('View', "getParagraphStyles")
    def getParagraphStyles(self):
        """Return a list of classnames supported in tables"""
        try:
            return self.paragraph_styles
        except AttributeError:
            return ()

    security.declareProtected('View', "getHtmlExclusions")
    def getHtmlExclusions(self):
        try:
            return self.html_exclusions
        except AttributeError:
            self.html_exclusions = _excluded_html
            return self.html_exclusions

    security.declareProtected('View', "getStyleWhitelist")
    def getStyleWhitelist(self):
        try:
            return self.style_whitelist
        except AttributeError:
            self.style_whitelist = _style_whitelist
            return self.style_whitelist

    security.declareProtected('View', "getClassBlacklist")
    def getClassBlacklist(self):
        return getattr(self, 'class_blacklist', [])

    security.declareProtected('View', "getClassBlacklist")
    def installBeforeUnload(self):
        return getattr(self, 'install_beforeunload', True)

    # ZMI views
    manage_options = (SimpleItem.manage_options[1:] + (
         dict(label='Config', action='kupu_config'),
         dict(label='Libraries', action='zmi_libraries'),
         dict(label='Resource types', action='zmi_resource_types'),
         dict(label='Documentation', action='zmi_docs'),
         #dict(label='Status', action='sanity_check'),
         ))


    security.declarePublic('scanIds')
    def scanIds(self):
        """Finds the relevant source files and the doller/Id/dollar strings they contain"""
        return scanner.scanIds()

    security.declarePublic('scanKWS')
    def scanKWS(self):
        """Check that kupu_wysiwyg_support is up to date"""
        return scanner.scanKWS()

    security.declarePublic('docs')
    def docs(self):
        """Returns FormController docs formatted as HTML"""
        docpath = os.path.join(Globals.package_home(kupu_globals), 'doc')
        f = open(os.path.join(docpath, 'PLONE2.txt'), 'r')
        _docs = f.read()
        return _docs

    security.declareProtected(permissions.ManageLibraries, "zmi_docs")
    zmi_docs = PageTemplateFile("zmi_docs.pt", globals())
    zmi_docs.title = 'kupu configuration'

    security.declareProtected(permissions.ManageLibraries, "sanity_check")
    sanity_check = PageTemplateFile("sanity_check.pt", globals())
    sanity_check.title = 'kupu status'

    security.declareProtected(permissions.ManageLibraries, "kupu_config")
    kupu_config = PageTemplateFile("kupu_config.pt", globals())
    kupu_config.title = 'kupu configuration'

    security.declareProtected(permissions.ManageLibraries, "zmi_libraries")
    zmi_libraries = PageTemplateFile("libraries.pt", globals())
    zmi_libraries.title = 'kupu configuration'

    security.declareProtected(permissions.ManageLibraries, "zmi_resource_types")
    zmi_resource_types = PageTemplateFile("resource_types.pt", globals())
    zmi_resource_types.title = 'kupu configuration'

    security.declareProtected(permissions.ManageLibraries,
                              "zmi_get_libraries")
    def zmi_get_libraries(self):
        """Return the libraries sequence for the ZMI view"""
        #return ()
        return [dict([(key, value.text) for key, value in lib.items()])
                for lib in self._libraries]

    security.declareProtected(permissions.ManageLibraries,
                              "zmi_add_library")
    def zmi_add_library(self, id, title, uri, src, icon, REQUEST):
        """Add a library through the ZMI"""
        self.addLibrary(id, title, uri, src, icon)
        REQUEST.RESPONSE.redirect(self.absolute_url() + '/zmi_libraries')

    security.declareProtected(permissions.ManageLibraries,
                              "zmi_update_libraries")
    def zmi_update_libraries(self, libraries, REQUEST):
        """Update libraries through the ZMI"""
        self.updateLibraries(libraries)
        REQUEST.RESPONSE.redirect(self.absolute_url() + '/zmi_libraries')

    security.declareProtected(permissions.ManageLibraries,
                              "zmi_delete_libraries")
    def zmi_delete_libraries(self, indices, REQUEST):
        """Delete libraries through the ZMI"""
        self.deleteLibraries(indices)
        REQUEST.RESPONSE.redirect(self.absolute_url() + '/zmi_libraries')

    security.declareProtected(permissions.ManageLibraries,
                              "zmi_move_up")
    def zmi_move_up(self, indices, REQUEST):
        """Move libraries up through the ZMI"""
        self.moveUp(indices)
        REQUEST.RESPONSE.redirect(self.absolute_url() + '/zmi_libraries')

    security.declareProtected(permissions.ManageLibraries,
                              "zmi_move_down")
    def zmi_move_down(self, indices, REQUEST):
        """Move libraries down through the ZMI"""
        self.moveDown(indices)
        REQUEST.RESPONSE.redirect(self.absolute_url() + '/zmi_libraries')

    security.declareProtected(permissions.ManageLibraries,
                              "zmi_get_type_mapping")
    def zmi_get_type_mapping(self):
        """Return the type mapping for the ZMI view"""
        return [(res_type, tuple(portal_type)) for res_type, portal_type
                in self._res_types.items()]

    security.declareProtected(permissions.ManageLibraries,
                              "zmi_add_resource_type")
    def zmi_add_resource_type(self, resource_type, portal_types, REQUEST):
        """Add resource type through the ZMI"""
        self.addResourceType(resource_type, portal_types)
        REQUEST.RESPONSE.redirect(self.absolute_url() + '/zmi_resource_types')

    security.declareProtected(permissions.ManageLibraries,
                              "zmi_update_resource_types")
    def zmi_update_resource_types(self, type_info, REQUEST):
        """Update resource types through the ZMI"""
        self.updateResourceTypes(type_info)
        REQUEST.RESPONSE.redirect(self.absolute_url() + '/zmi_resource_types')

    security.declareProtected(permissions.ManageLibraries,
                              "zmi_delete_resource_types")
    def zmi_delete_resource_types(self, resource_types, REQUEST):
        """Delete resource types through the ZMI"""
        self.deleteResourceTypes(resource_types)
        REQUEST.RESPONSE.redirect(self.absolute_url() + '/zmi_resource_types')

    security.declareProtected(permissions.ManageLibraries,
                              "configure_kupu")
    def configure_kupu(self,
        linkbyuid, table_classnames, html_exclusions, style_whitelist, class_blacklist,
        installBeforeUnload=None, parastyles=None,
        REQUEST=None):
        """Delete resource types through the ZMI"""
        self.linkbyuid = int(linkbyuid)
        self.table_classnames = table_classnames
        if installBeforeUnload is not None:
            self.install_beforeunload = bool(installBeforeUnload)
        if parastyles:
            self.paragraph_styles = [ line.strip() for line in parastyles if line.strip() ]

        newex = html_exclusions[-1]
            
        html_exclusions = [ (tuple(h.get('tags', ())), tuple(h.get('attributes', ())))
            for h in html_exclusions[:-1] if h.get('keep')]
        
        tags, attr = newex.get('tags', ()), newex.get('attributes', ())
        if tags or attr:
            tags = tuple(tags.replace(',',' ').split())
            attr = tuple(attr.replace(',',' ').split())
            html_exclusions.append((tags, attr))

        self.html_exclusions = html_exclusions

        self.style_whitelist = list(style_whitelist)
        self.class_blacklist = list(class_blacklist)

        if REQUEST:
            REQUEST.RESPONSE.redirect(self.absolute_url() + '/kupu_config')

InitializeClass(PloneKupuLibraryTool)
