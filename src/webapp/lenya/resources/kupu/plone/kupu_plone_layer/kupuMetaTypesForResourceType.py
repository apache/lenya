## Script (Python) "kupuMetaTypesForResourcType"
##title=Provide a list of meta types for a resource type
##bind container=container
##bind context=context
##bind namespace=
##bind script=script
##bind subpath=traverse_subpath
##parameters=resource_type, includeCollections=False
from Products.CMFCore.utils import getToolByName
kupu_tool = getToolByName(context, 'kupu_library_tool')
types_tool = getToolByName(context, 'portal_types')

coll_types = kupu_tool.queryPortalTypesForResourceType('collection', ())
portal_types = kupu_tool.queryPortalTypesForResourceType(resource_type, ())

if includeCollections:
    portal_types += coll_types
return [types_tool.getTypeInfo(p_type).Metatype() for p_type in portal_types if types_tool.getTypeInfo(p_type)]
