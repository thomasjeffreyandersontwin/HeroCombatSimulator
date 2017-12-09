/*
 * OnDeckList.java
 *
 * Created on September 12, 2001, 8:23 AM
 */

package champions;

import tjava.ContextMenu;
import tjava.ContextMenuListener;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Point;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.ListCellRenderer;
import javax.swing.UIManager;

/**
 *
 * @author  twalker
 * @version
 */
public class OnDeckList extends JList 
implements ContextMenuListener {
    
    private Font activeFont = null;
    private Font nonactiveFont = null;
    private Color activeColor = null;
    private Color nonactiveColor = null;
    
    /** Creates new OnDeckList */
    public OnDeckList() {
        setCellRenderer( new OnDeckList.OnDeckRenderer() );
        setBackground( new Color(204, 204, 204));
        setBorder( null);
        
        ContextMenu.addContextMenu(this);
        
        activeFont = UIManager.getFont( "OnDeckList.activeFont");
        nonactiveFont = UIManager.getFont( "OnDeckList.nonActiveFont");
        activeColor = UIManager.getColor( "OnDeckList.activeColor");
        nonactiveColor = UIManager.getColor( "OnDeckList.nonActiveColor");
    }
    
    public boolean invokeMenu(JPopupMenu popup, Component inComponent, Point inPoint) {
        return false;
    }
    
    class OnDeckRenderer extends JPanel implements ListCellRenderer {
        
        private JLabel segmentLabel;
        private JLabel targetLabel;
        // This is the only method defined by ListCellRenderer.
        // We just reconfigure the JLabel each time we're called.
        
        public OnDeckRenderer () {
            segmentLabel = new JLabel();
            targetLabel = new JLabel();
            
            segmentLabel.setPreferredSize( new Dimension(30,10) );
            
            this.setLayout( new BorderLayout() );
            this.add(segmentLabel, BorderLayout.WEST);
            this.add(targetLabel, BorderLayout.CENTER);
            
            setOpaque(false);
        }
        
        public Component getListCellRendererComponent(
        JList list,
        Object value,            // value to display
        int index,               // cell index
        boolean isSelected,      // is the cell selected
        boolean cellHasFocus)    // the list and the cell have the focus
        {
            BattleSequencePair bsp = (BattleSequencePair) value;
            Object target = bsp.getTarget();
            Chronometer time = bsp.getTime();
            
            Color bg, fg, segFG ;
            Font font;
            
            if (isSelected) {
                bg = list.getSelectionBackground();
                fg = list.getSelectionForeground();
            }
            else {
                bg = list.getBackground();
                fg = list.getForeground();
            }
            
            if ( Battle.currentBattle != null && time.equals(Battle.currentBattle.getTime()) && Battle.currentBattle.getActiveTarget() == target ) {
                if ( activeColor != null ) {
                    segFG = activeColor;
                }
                else {
                    segFG = fg;
                }
                
                if ( activeFont != null ) {
                    font = activeFont;
                }
                else {
                    font = list.getFont();
                }
                
            }
            else {
                if ( nonactiveColor != null ) {
                    segFG = nonactiveColor;
                }
                else {
                    segFG = fg;
                }
                
                if ( nonactiveFont != null ) {
                    font = nonactiveFont;
                }
                else {
                    font = list.getFont();
                }
            }
            
            segmentLabel.setForeground(segFG);
            targetLabel.setForeground(segFG);
            setBackground(bg);
            
            setEnabled(list.isEnabled());
            segmentLabel.setFont(font);
            targetLabel.setFont(font);
            
            int segment = time.getSegment();
            
            if ( time.isTurnEnd() ) {
                segmentLabel.setText( Integer.toString(segment) + "P." );
            }
            else {
                segmentLabel.setText( Integer.toString(segment) + "." );
            }
            
            String s;
            if ( target instanceof BattleEvent ) {
                BattleEvent be = (BattleEvent)target;
                if (be.getType() == BattleEvent.ACTION ) {
                    s = be.getAction().getValue("NAME").toString(); 
                }
                else {
                    s =  "Delayed Event: " + ((BattleEvent)target).getSource().getName() + "'s " + ((BattleEvent)target).getAbility().getName();
                }
            }
            else {
                s= ((Target)target).getName();
            }
            
           // if ( time.isTurnEnd() ) s = s + "(Post-12)";
            targetLabel.setText( s );
            
            return this;
        }

    }
}
