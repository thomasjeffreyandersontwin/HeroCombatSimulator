/*
 * TargetButtonStats.java
 *
 * Created on October 22, 2000, 3:28 PM
 */

package champions;

import champions.ColumnList;
import tjava.ContextMenuListener;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Vector;
import java.beans.*;

import champions.interfaces.*;
/**
 *
 * @author  unknown
 * @version
 */
public class TargetPanelHeader extends ColumnHeader
implements PropertyChangeListener, MouseListener, MouseMotionListener,
ContextMenuListener {



    public TargetPanelHeader() {

        int index;
        ColumnList dl = new ColumnList();
        index = dl.createIndexed( "Column","NAME","Name") ;
        dl.addIndexed(index,   "Column","TYPE", "SPECIAL",  true);
        dl.addIndexed(index,   "Column","STAT", "NAME",  true);
        dl.addIndexed(index,   "Column","WIDTH", new Integer(80) ,  true);

        index = dl.createIndexed( "Column","NAME","Body") ;
        dl.addIndexed(index,   "Column","TYPE", "STAT" ,  true);
        dl.addIndexed(index,   "Column","STAT", "BODY" ,  true);
        dl.addIndexed(index,   "Column","WIDTH", new Integer(40) ,  true);

        index = dl.createIndexed( "Column","NAME","Stun") ;
        dl.addIndexed(index,   "Column","TYPE", "STAT" ,  true);
        dl.addIndexed(index,   "Column","STAT", "STUN" ,  true);
        dl.addIndexed(index,   "Column","WIDTH", new Integer(40) ,  true);

        index = dl.createIndexed( "Column","NAME","PD/rPD") ;
        dl.addIndexed(index,   "Column","TYPE", "SPECIAL" ,  true);
        dl.addIndexed(index,   "Column","STAT", "PD" ,  true);
        dl.addIndexed(index,   "Column","WIDTH", new Integer(50) ,  true);


        index = dl.createIndexed( "Column","NAME","ED/rED") ;
        dl.addIndexed(index,   "Column","TYPE", "SPECIAL" ,  true);
        dl.addIndexed(index,   "Column","STAT", "ED" ,  true);
        dl.addIndexed(index,   "Column","WIDTH", new Integer(50) ,  true);

        index = dl.createIndexed( "Column","NAME","END") ;
        dl.addIndexed(index,   "Column","TYPE", "STAT" ,  true);
        dl.addIndexed(index,   "Column","STAT", "END" ,  true);
        dl.addIndexed(index,   "Column","WIDTH", new Integer(40) ,  true);
        
        index = dl.createIndexed( "Column","NAME","Effects") ;
        dl.addIndexed(index,   "Column","TYPE", "SPECIAL" ,  true);
        dl.addIndexed(index,   "Column","STAT", "CONDITIONS" ,  true);
        dl.addIndexed(index,   "Column","WIDTH", new Integer(200) ,  true);

        setColumnList(dl);
    }

    public boolean invokeMenu(JPopupMenu popup,Component inComponent,Point inPoint) {
        //JPopupMenu popup = new JPopupMenu();
        final int column = findColumnAt(inPoint);
        String name = "Column";

        if ( column != -1 ) {
            name = columnList.getIndexedStringValue(column, "Column" , "NAME");
            name = name.concat( " Column");
        }

        popup.add( new AbstractAction( "Remove " + name ) {
            public void actionPerformed(ActionEvent e) {
                removeColumn(column);
            }

            public boolean isEnabled() {
                return (column != -1 );
            }
        });
        
        JMenu menu = new JMenu( "Insert Column" );
        
        // Add Stats
        int index;
        
        for(index = 0; index < Character.statNames.length; index++ ) {
            final String stat = Character.statNames[index];
            menu.add( new AbstractAction( Character.statNames[index] ) {
                public void actionPerformed(ActionEvent e) {
                    addColumn("STAT", stat, stat, column);
                }
                
                public boolean isEnabled() {
                    return true;
                }
            });
        }
        
        popup.add( menu);
        
        return true;
    }

}