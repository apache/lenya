/*****************************************************************************
 *
 * Copyright (c) 2003-2004 Kupu Contributors. All rights reserved.
 *
 * This software is distributed under the terms of the Kupu
 * License. See LICENSE.txt for license text. For a list of Kupu
 * Contributors see CREDITS.txt.
 *
 *****************************************************************************/

// $Id: kupusaveonpart.js 7222 2004-11-11 16:19:52Z duncan $

function saveOnPart(evt) {
    /* ask the user if (s)he wants to save the document before leaving */
    if (!evt) evt = window.event;
    if (kupu.content_changed) {
        var msg = 'You have unsaved changes in Kupu.';
        if (evt) {
            evt.returnValue = msg;
        }
        return msg;
    };
};
