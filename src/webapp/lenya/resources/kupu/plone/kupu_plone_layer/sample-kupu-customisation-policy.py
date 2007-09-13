## Script (Python) "kupu-customisation-policy"
##bind container=container
##bind context=context
##bind namespace=
##bind script=script
##bind subpath=traverse_subpath
##parameters=
##title=Kupu Customisation Policy
##

# Make a copy of this script called 'kupu-customisation-policy'
# in any skin folder on your site and edit it to set up your own
# preferred kupu configuration.
from Products.CMFCore.utils import getToolByName

LINKABLE = ('MyObjectType',
            'AnotherObjectType',)

MEDIAOBJECT = ('MyImage',
               'Image',)

COLLECTION = ('Folder',
              'ATFolder',
              'Large Plone Folder')

EXCLUDED_HTML = [
  {'tags': ('center','span','tt','big','small','u','s','strike','basefont','font',),
   'attributes':(),
   'keep': 1 },
  
  {'tags':(),
  'attributes': ('dir','lang','valign','halign','border','frame',
      'rules','cellspacing','cellpadding','bgcolor'),
   'keep': 1},

  {'tags': ('table','th','td'),
   'attributes': ('width','height'),
   'keep': 1},

   {'tags': '', 'attributes': '' } # Must be dummy entry at end.
]

STYLE_WHITELIST = ['text-align', 'list-style-type', 'float']
CLASS_BLACKLIST = ['MsoNormal', 'MsoTitle', 'MsoHeader', 'MsoFootnoteText',
        'Bullet1', 'Bullet2']

TABLE_CLASSNAMES = ('plain', 'listing', 'grid', 'data')
PARAGRAPH_STYLES = (
    'Fancy|div|fancyClass',
    'Plain|div|plainClass',
)
    
tool = getToolByName(context, 'kupu_library_tool')
typetool = getToolByName(context, 'portal_types')

def typefilter(types):
    all_meta_types = dict([ (t.content_meta_type, 1) for t in typetool.listTypeInfo()])
    return [ t for t in types if t in all_meta_types ]

print "add resources"
tool.addResourceType('linkable', typefilter(LINKABLE))
tool.addResourceType('mediaobject', typefilter(MEDIAOBJECT))
tool.addResourceType('collection', typefilter(COLLECTION))

print "configure kupu"
tool.configure_kupu(linkbyuid=True,
    table_classnames = ('plain', 'listing', 'grid', 'data'),
    parastyles=PARAGRAPH_STYLES,
    html_exclusions = EXCLUDED_HTML,
    style_whitelist = STYLE_WHITELIST,
    class_blacklist = CLASS_BLACKLIST,
    installBeforeUnload=True)

return printed
