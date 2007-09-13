/*****************************************************************************
 *
 * Copyright (c) 2003-2004 Kupu Contributors. All rights reserved.
 *
 * This software is distributed under the terms of the Kupu
 * License. See LICENSE.txt for license text. For a list of Kupu
 * Contributors see CREDITS.txt.
 *
 *****************************************************************************/

// $Id$

function startKupu() {
    var iframeids = new Array('kupu_1', 'kupu_2', 'kupu_3');
    var kupu = initKupu(iframeids); 
    kupu.initialize();

    return kupu;
};
