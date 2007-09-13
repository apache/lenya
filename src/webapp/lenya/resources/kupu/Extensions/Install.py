##############################################################################
#
# Copyright (c) 2003-2004 Kupu Contributors. All rights reserved.
#
# This software is distributed under the terms of the Kupu
# License. See LICENSE.txt for license text. For a list of Kupu
# Contributors see CREDITS.txt.
#
##############################################################################
"""Install kupu in CMF and, if available, Plone

This is best executed using CMFQuickInstaller

$Id: Install.py 13680 2005-06-22 08:13:45Z yuppie $
"""
import os.path
import sys
from StringIO import StringIO

from App.Common import package_home

from Products.CMFCore.utils import getToolByName, minimalpath
from Products.CMFCore.DirectoryView import createDirectoryView
from Products.kupu import kupu_globals
from OFS.ObjectManager import BadRequestException
from zExceptions import BadRequest

try:
    from Products.MimetypesRegistry import MimeTypeItem
except ImportError:
    pass # Plone not available

PROJECTNAME = 'Kupu'

kupu_package_dir = 'kupu'

def register_layer(self, relpath, name, out):
    """Register a file system directory as skin layer
    """
    skinstool = getToolByName(self, 'portal_skins')
    if name not in skinstool.objectIds():
        kupu_plone_skin_dir = os.path.join(kupu_package_dir, relpath)
        createDirectoryView(skinstool, kupu_plone_skin_dir, name)
        print >>out, "The layer '%s' was added to the skins tool" % name

    # put this layer into all known skins
    for skinName in skinstool.getSkinSelections():
        path = skinstool.getSkinPath(skinName) 
        path = [i.strip() for i in path.split(',')]
        try:
            if name not in path:
                path.insert(path.index('custom')+1, name)
        except ValueError:
            if name not in path:
                path.append(name)

        path = ','.join(path)
        skinstool.addSkinSelection(skinName, path)

def install_plone(self, out):
    """Install with plone
    """
    # register the plone skin layer
    register_layer(self, 'plone/kupu_plone_layer', 'kupu_plone', out)

    # register as editor
    portal_props = getToolByName(self, 'portal_properties')
    site_props = getattr(portal_props,'site_properties', None)
    attrname = 'available_editors'
    if site_props is not None:
        editors = list(site_props.getProperty(attrname)) 
        if 'Kupu' not in editors:
            editors.append('Kupu')
            site_props._updateProperty(attrname, editors)        
            print >>out, "Added 'Kupu' to available editors in Plone."
    install_libraries(self, out)
    install_configlet(self, out)
    install_transform(self, out)
    install_customisation(self, out)

def install_libraries(self, out):
    """Install everything necessary to support Kupu Libraries
    """
    # add the library tool
    addTool = self.manage_addProduct['kupu'].manage_addTool
    try:
        addTool('Kupu Library Tool')
        print >>out, "Added the Kupu Library Tool to the plone Site"
    except BadRequest:
        print >>out, "Kupu library Tool already added"    
    except: # Older Zopes
        #heuristics for testing if an instance with the same name already exists
        #only this error will be swallowed.
        #Zope raises in an unelegant manner a 'Bad Request' error
        e=sys.exc_info()
        if e[0] != 'Bad Request':
            raise
        print >>out, "Kupu library Tool already added"    

def install_configlet(self, out):
    try:
        portal_conf=getToolByName(self,'portal_controlpanel')
    except AttributeError:
        print >>out, "Configlet could not be installed"
        return
    try:
        portal_conf.registerConfiglet( 'kupu'
               , 'Kupu'      
               , 'string:${portal_url}/kupu_library_tool/kupu_config' 
               , ''                 # a condition   
               , 'Manage portal'    # access permission
               , 'Products'         # section to which the configlet should be added: 
                                    #(Plone,Products,Members) 
               , 1                  # visibility
               , PROJECTNAME
               , 'kupuimages/kupu_icon.gif' # icon in control_panel
               , 'Kupu Library Tool'
               , None
               )
    except KeyError:
        pass # Get KeyError when registering duplicate configlet.

def install_transform(self, out):
    try:
        print >>out, "Adding new mimetype"
        mimetypes_tool = getToolByName(self, 'mimetypes_registry')
        newtype = MimeTypeItem.MimeTypeItem('HTML with captioned images',
            ('text/x-html-captioned',), ('html-captioned',), 0)
        mimetypes_tool.register(newtype)

        print >>out,"Add transform"
        transform_tool = getToolByName(self, 'portal_transforms')
        try:
            transform_tool.manage_delObjects(['html-to-captioned'])
        except: # XXX: get rid of bare except
            pass
        transform_tool.manage_addTransform('html-to-captioned', 'Products.kupu.plone.html2captioned')
    except (NameError,AttributeError):
        print >>out, "No MimetypesRegistry, captioning not supported."

def install_customisation(self, out):
    """Default settings may be stored in a customisation policy script so
    that the entire setup may be 'productised'"""

    # Skins are cached during the request so we (in case new skin
    # folders have just been added) we need to force a refresh of the
    # skin.
    self.changeSkin(None)

    scriptname = '%s-customisation-policy' % PROJECTNAME.lower()
    cpscript = getattr(self, scriptname, None)
    if cpscript:
        cpscript = cpscript.__of__(self)

    if cpscript:
        print >>out,"Customising %s" % PROJECTNAME
        print >>out,cpscript()
    else:
        print >>out,"No customisation policy"

def install(self):
    out = StringIO()

    # register the core layer
    register_layer(self, 'common', 'kupu', out)

    # try for plone
    try:
        import Products.CMFPlone
    except ImportError:
        pass
    else:
        install_plone(self, out)

    print >>out, "kupu successfully installed"
    return out.getvalue()

def uninstall(self):
    out = StringIO()

    # remove the configlet from the portal control panel
    configTool = getToolByName(self, 'portal_controlpanel', None)
    if configTool:
        configTool.unregisterConfiglet('kupu')
        out.write('Removed kupu configlet\n')

    print >> out, "Successfully uninstalled %s." % PROJECTNAME
    return out.getvalue()
