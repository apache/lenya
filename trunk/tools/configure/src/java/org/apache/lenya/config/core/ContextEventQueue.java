/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 *  contributor license agreements.  See the NOTICE file distributed with
 *  this work for additional information regarding copyright ownership.
 *  The ASF licenses this file to You under the Apache License, Version 2.0
 *  (the "License"); you may not use this file except in compliance with
 *  the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 */

package org.apache.lenya.config.core;

import java.awt.AWTEvent;
import java.awt.Component;
import java.awt.EventQueue;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;

import javax.swing.AbstractAction;
import javax.swing.JPopupMenu;
import javax.swing.MenuSelectionManager;
import javax.swing.SwingUtilities;
import javax.swing.text.JTextComponent;

public class ContextEventQueue extends EventQueue {
    
    protected void dispatchEvent(AWTEvent event) {
        super.dispatchEvent(event);
        
        if (!(event instanceof MouseEvent))
            return;
        
        MouseEvent me = (MouseEvent) event;
        
        if (!me.isPopupTrigger())
            return;
        
        Component comp = SwingUtilities.getDeepestComponentAt(
                me.getComponent(), me.getX(), me.getY());
        
        if (!(comp instanceof JTextComponent))
            return;
        
        if (MenuSelectionManager.defaultManager().getSelectedPath().length > 0)
            return;
        
        JTextComponent tc = (JTextComponent) comp;
        JPopupMenu menu = new JPopupMenu();
        
        menu.add(new CopyAction(tc));
        menu.add(new CutAction(tc));
        menu.add(new PasteAction(tc));
        menu.addSeparator();
        menu.add(new SelectAllAction(tc));
        
        
        Point pt = SwingUtilities.convertPoint(me.getComponent(),
                me.getPoint(), tc);
        menu.show(tc, pt.x, pt.y);
    }
}

class CutAction extends AbstractAction {
    JTextComponent comp;
    
    public CutAction(JTextComponent comp) {
        super("Cut");
        this.comp = comp;
    }
    
    public void actionPerformed(ActionEvent e) {
        comp.cut();
    }
    
    public boolean isEnabled() {
        return comp.isEditable() && comp.isEnabled()
        && comp.getSelectedText() != null;
    }
}

class PasteAction extends AbstractAction {
    JTextComponent comp;
    
    public PasteAction(JTextComponent comp) {
        super("Paste");
        this.comp = comp;
    }
    
    public void actionPerformed(ActionEvent e) {
        comp.paste();
    }
    
    public boolean isEnabled() {
        if (comp.isEditable() && comp.isEnabled()) {
            Transferable contents = Toolkit.getDefaultToolkit()
            .getSystemClipboard().getContents(this);
            return contents.isDataFlavorSupported(DataFlavor.stringFlavor);
        } else
            return false;
    }
}

class DeleteAction extends AbstractAction {
    JTextComponent comp;
    
    public DeleteAction(JTextComponent comp) {
        super("Delete");
        this.comp = comp;
    }
    
    public void actionPerformed(ActionEvent e) {
        comp.replaceSelection(null);
    }
    
    public boolean isEnabled() {
        return comp.isEditable() && comp.isEnabled()
        && comp.getSelectedText() != null;
    }
}

class CopyAction extends AbstractAction {
    JTextComponent comp;
    
    public CopyAction(JTextComponent comp) {
        super("Copy");
        this.comp = comp;
    }
    
    public void actionPerformed(ActionEvent e) {
        comp.copy();
    }
    
    public boolean isEnabled() {
        return comp.isEnabled() && comp.getSelectedText() != null;
    }
}

class SelectAllAction extends AbstractAction {
    JTextComponent comp;
    
    public SelectAllAction(JTextComponent comp) {
        super("Select All");
        this.comp = comp;
    }
    
    public void actionPerformed(ActionEvent e) {
        comp.selectAll();
    }
    
    public boolean isEnabled() {
        return comp.isEnabled() && comp.getText().length() > 0;
    }
}
