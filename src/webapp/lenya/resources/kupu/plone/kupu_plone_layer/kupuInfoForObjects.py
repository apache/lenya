## Script (Python) "kupuInfoForObjects"
##bind container=container
##bind context=context
##bind namespace=
##bind script=script
##bind subpath=traverse_subpath
##parameters=values, linkhere=False, linkparent=False
##title=Provide dictionaries with information about a list of objects
##
from Products.CMFCore.utils import getToolByName
import AccessControl
from AccessControl import Unauthorized

request = context.REQUEST
response = request.RESPONSE
response.setHeader('Cache-Control', 'no-cache')

kupu_tool = getToolByName(context, 'kupu_library_tool')
url_tool = getToolByName(context, 'portal_url')
coll_types = kupu_tool.queryPortalTypesForResourceType('collection', ())
linkbyuid = kupu_tool.getLinkbyuid()
preview_action = 'kupupreview'

# The redirecting url must be absolute otherwise it won't work for
# preview when the page is using portal_factory
# The absolute to relative conversion when the document is saved
# should strip the url right back down to resolveuid/whatever.
base = context.absolute_url()

security = AccessControl.getSecurityManager()

def info(obj, allowCollection=True):
    if not security.checkPermission('View', obj):
        return None

    try:
        id = obj.getId()
        portal_type = getattr(obj, 'portal_type','')
        collection = allowCollection and portal_type in coll_types

        if linkbyuid and not collection and hasattr(obj, 'UID'):
            url = base+'/resolveuid/%s' % obj.UID()
        else:
            url = obj.absolute_url()

        icon = "%s/%s" % (context.portal_url(), obj.getIcon())
        width = height = size = None
        preview = obj.getTypeInfo().getActionById(preview_action, None)

        try:
                size = context.getObjSize(obj)
        except:
            size = None

        width = getattr(obj, 'width', None)
        height = getattr(obj, 'height', None)
        if callable(width): width = width()
        if callable(height): height = height()

        title = obj.Title() or obj.getId()
        description = obj.Description()

        return {'id': id, 'url': url, 'portal_type': portal_type,
              'collection':  collection, 'icon': icon, 'size': size,
              'width': width, 'height': height,
              'preview': preview, 'title': title, 'description': description,
              }
    except Unauthorized:
        return None

res = []

portal = url_tool.getPortalObject()
if linkhere and portal is not context:
    data = info(context, False)
    if data:
        data['label'] = '. (%s)' % context.title_or_id()
        res.append(data)

if linkparent:
    if portal is not context and portal is not context.aq_parent:
        data = info(context.aq_parent, True)
        if data:
            data['label'] = '.. (Parent folder)'
            res.append(data)

for obj in values:
    data = info(obj, True)
    if data:
        res.append(data)
return res
