## Script (Python) "kupuInfoForBrains"
##title=Provide dictionaries with information about a list of catalog brains
##bind container=container
##bind context=context
##bind namespace=
##bind script=script
##bind subpath=traverse_subpath
##parameters=values
from Products.CMFCore.utils import getToolByName

request = context.REQUEST
response = request.RESPONSE
response.setHeader('Cache-Control', 'no-cache')

types_tool = getToolByName(context, 'portal_types')
kupu_tool = getToolByName(context, 'kupu_library_tool')
linkbyuid = kupu_tool.getLinkbyuid()
coll_types = kupu_tool.queryPortalTypesForResourceType('collection', ())
preview_action = 'kupupreview'

# The redirecting url must be absolute otherwise it won't work for
# preview when the page is using portal_factory
# The absolute to relative conversion when the document is saved
# should strip the url right back down to resolveuid/whatever.
base = context.absolute_url()

def info(brain):
    # It would be nice to do everything from the brain, but
    # unfortunately we need to get the object to calculate a UID
    # based URL, and also for the preview size.
    obj = brain.getObject()
    id = brain.getId
    url = brain.getURL()
    portal_type = brain.portal_type
    collection = portal_type in coll_types

    if linkbyuid and not collection and hasattr(obj, 'UID'):
        url = base+'/resolveuid/%s' % obj.UID()
    else:
        url = brain.getURL()

    icon = "%s/%s" % (context.portal_url(), brain.getIcon)
    width = height = size = None
    preview = types_tool.getTypeInfo(brain.portal_type).getActionById(preview_action, None)

    if hasattr(obj, 'get_size'):
        size = context.getObjSize(obj)
    width = getattr(obj, 'width', None)
    height = getattr(obj, 'height', None)
    if callable(width): width = width()
    if callable(height): height = height()
        
    title = brain.Title or brain.getId
    description = brain.Description

    return {'id': id, 'url': url, 'portal_type': portal_type,
          'collection':  collection, 'icon': icon, 'size': size,
          'width': width, 'height': height,
          'preview': preview, 'title': title, 'description': description,
          }
          
return [info(brain) for brain in values]
