##############################################################################
#
# Copyright (c) 2003-2004 Kupu Contributors. All rights reserved.
#
# This software is distributed under the terms of the Kupu
# License. See LICENSE.txt for license text. For a list of Kupu
# Contributors see CREDITS.txt.
#
##############################################################################
"""HTMLBody field

$Id: field.py 3415 2004-03-25 14:42:47Z philikon $
"""

from zope.interface import implements
from zope.schema import Bytes
from zope.schema.fieldproperty import FieldProperty

from interfaces import IHTMLBody

class HTMLBody(Bytes):
    implements(IHTMLBody)

    html2xhtml1 = FieldProperty(IHTMLBody['html2xhtml1'])
