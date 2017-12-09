/*
 * TargetButtonStats.java
 *
 * Created on October 22, 2000, 3:28 PM
 */

package champions;

import champions.enums.DefenseType;
import champions.event.SegmentAdvancedEvent;
import champions.event.SequenceChangedEvent;
import champions.event.TargetSelectedEvent;
import champions.interfaces.BattleListener;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Vector;
import javax.swing.ButtonModel;
import javax.swing.CellRendererPane;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import tjava.Destroyable;


/**
 *
 * @author  unknown
 * @version
 */

public class TargetButtonStats extends JPanel
        implements PropertyChangeListener, ChangeListener, ComponentListener, BattleListener, Destroyable {
    
    private static final int DEBUG = 0;
    
    /** Holds value of property statList. */
    private DetailList statList;
    
    /** Marks view as invalid and requiring update */
    private boolean invalid = false;
    
    private Vector columnName;
    private Vector columnStart;
    private Vector columnWidth;
    private Vector columnStat;
    private Vector columnType;
    
    private CellRendererPane rendererPane;
    private JLabel cellRenderer;
    private HTMLButtonPanel effectPanel = null;
    private HTMLButton htmlButton = null;
    
    /** Holds value of property color1. */
    private Color color1 = new Color(0,102,0);
    /** Holds value of property color2. */
    private Color color2 = new Color(153,0,153);
    /** Holds value of property color3. */
    private Color color3 = Color.red;
    /** Holds value of property target. */
    private Target target;
    /** Holds value of property model. */
    private ButtonModel model;
    /** Holds value of property pressedColor. */
    private Color pressedColor = Color.blue;
    /** Creates new TargetButtonStats */
    public TargetButtonStats() {
        columnName = new Vector();
        columnStart = new Vector();
        columnWidth = new Vector();
        columnStat = new Vector();
        columnType = new Vector();
        
        rendererPane = new CellRendererPane();
        this.add(rendererPane);
        setLayout(null);
        
        addComponentListener(this);
        
        cellRenderer = new JLabel();
        
        setCursor(null);
        
        Battle.addBattleListener(this);
    }
    
    /** Getter for property statList.
     * @return Value of property statList.
     */
    public DetailList getStatList() {
        return statList;
    }
    /** Setter for property statList.
     * @param statList New value of property statList.
     */
    public void setStatList(DetailList statList) {
        if ( this.statList != null ) {
            statList.removePropertyChangeListener( this );
        }
        
        this.statList = statList;
        
        if ( statList != null ) {
            statList.addPropertyChangeListener(this);
        }
        
        updateColumns();
        
    }
    
    public Dimension getPreferredSize() {
        Dimension d = new Dimension();
        
        FontMetrics fm = getFontMetrics( getFont() );
        Insets insets = getInsets();
        d.height = fm.getHeight() + insets.top + insets.bottom;
        d.width = 0;
        
        int count = columnStart.size();
        if ( count > 0 ) {
            d.width = ((Integer)columnStart.get(count-1)).intValue() + ((Integer)columnWidth.get(count-1)).intValue();
        }
        return d;
    }
    
    public void updateColumns() {
        int index,count;
        
        int effectColumn = -1;
        
        count = statList.getIndexedSize( "Column" );
        
        columnName.clear();
        columnWidth.clear();
        columnStat.clear();
        columnStart.clear();
        columnType.clear();
        
        for ( index = 0; index < count; index ++ ) {
            columnName.add( statList.getIndexedStringValue( index, "Column","NAME"));
            columnWidth.add( statList.getIndexedIntegerValue( index, "Column","WIDTH"));
            columnStart.add( statList.getIndexedIntegerValue( index, "Column","START"));
            columnStat.add( statList.getIndexedStringValue( index, "Column","STAT"));
            columnType.add( statList.getIndexedStringValue( index, "Column","TYPE"));
            
            if ( columnType.get(index).equals("SPECIAL") && columnStat.get(index).equals("CONDITIONS") ) {
                effectColumn = index;
            }
        }
        
        if ( effectColumn != -1 ) {
            if ( effectPanel == null ) {
                effectPanel = new HTMLButtonPanel();
                effectPanel.setModel(new EffectListModel(target));
                effectPanel.setBounds(1,1,1,1);
                effectPanel.setForeground(getForeground());
                effectPanel.setBackground(getBackground());
                add(effectPanel);
            }
            
            int x = ((Integer)columnStart.get(effectColumn)).intValue();
            int width = ((Integer)columnWidth.get(effectColumn)).intValue();
            int height = getHeight();
            int y = 0;
            effectPanel.setBounds( x,y, width, height);
            effectPanel.setFont( getFont() );
            effectPanel.setVisible(true);
        } else {
            if ( effectPanel != null ) {
                effectPanel.setVisible(false);
            }
        }
        
        revalidate();
        
        if (DEBUG > 0 ) System.out.println("TargetButtonStats.triggerUpdate called due to updateColumns method call.");
        triggerUpdate();
    }
    
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        
        if ( target == null ) return;
        
        int currentWidth = getWidth();
        int currentHeight = getHeight();
        int left = 0;
        int top = 0;
        
        String currentValue;
        Object o;
        int index,count;
        int cx,cy,cw,ch;
        Color textColor = getForeground();
        
        Insets insets = getInsets();
        int height = getHeight() - insets.top - insets.bottom;
        int width = getWidth() - insets.left - insets.right;
        
        cy = insets.top;
        ch = height;
        
        count = columnName.size();
        
        cellRenderer.setFont( getFont() );
        
        for ( index = 0; index < count; index ++ ) {
            currentValue = "n/a";
            textColor = getForeground();
            String stat = (String)columnStat.get(index);
            if ( columnType.get(index).equals("STAT") ) {
                
                if ( target.hasStat( stat ) ) {
                    int intValue = target.getCurrentStat(stat);
                    int base = target.getAdjustedStat(stat);
                    
                    
                    if ( stat.equals("OCV") ) {
                        intValue = target.getCalculatedOCV();
                    } else if ( stat.equals("DCV") ) {
                        intValue = target.getCalculatedDCV();
                    } else                     if ( stat.equals("PD") ) {
                        intValue = target.getDefense(DefenseType.PD);
                    } else if ( stat.equals("rPD") ) {
                        intValue = target.getDefense(DefenseType.rPD);
                    } else                     if ( stat.equals("ED") ) {
                        intValue = target.getDefense(DefenseType.ED);
                    } else if ( stat.equals("rED") ) {
                        intValue = target.getDefense(DefenseType.rED);
                    }
                    
                    currentValue = Integer.toString(intValue);
                    
                    if ( intValue == base ) {
                        // Do nothing
                    } else if ( intValue <= 0 ) {
                        textColor = getColor3();
                    } else if ( intValue > base ) {
                        textColor = getColor1();
                    } else if ( intValue < base ) {
                        textColor =  getColor2();
                    }
                }
            } else if ( columnType.get(index).equals("KEY") ) {
                String key = ((String)columnStat.get(index));
                //String keyName = key.substring(0, key.indexOf("."));
                // String keyType = key.substring(key.indexOf(".")+1);
                
                if ( ( o = target.getStringValue( key ) ) != null ) {
                    currentValue = (String)o;
                } else if ( ( o = target.getIntegerValue( key ) ) != null ) {
                    currentValue = ((Integer)o).toString();
                }
                
            } else if ( columnType.get(index).equals("SPECIAL") ) {
                if ( columnStat.get(index).equals("NAME") ) {
                    currentValue = target.getName();
                } else if ( columnStat.get(index).equals("PD") ) {
                    currentValue = Integer.toString( target.getDefense(DefenseType.PD) ) + "/" + Integer.toString( target.getDefense(DefenseType.rPD) );
                } else if ( columnStat.get(index).equals("ED") ) {
                    currentValue = Integer.toString( target.getDefense(DefenseType.ED) ) + "/" + Integer.toString( target.getDefense(DefenseType.rED) );
                } else if ( columnStat.get(index).equals("CONDITIONS") ) {
                    currentValue = "";
                }
            }
            
            cx = ((Integer)columnStart.get(index)).intValue();
            cw = ((Integer)columnWidth.get(index)).intValue();
            
            if ( !currentValue.equals("")) {
                cellRenderer.setText( currentValue );
                
                if ( model != null && getModel().isPressed() ) {
                    textColor = getPressedColor();
                }
                
                cellRenderer.setForeground( textColor );
                g.setColor( textColor );
                rendererPane.paintComponent(g, cellRenderer, this, cx, cy, cw, ch, true);
                
                if ( index == 0 && model != null && getModel().isArmed() ) {
                    FontMetrics fm = getFontMetrics( getFont() );
                    int baseLine = cy + fm.getAscent();
                    g.drawLine( cx, baseLine+ 1,
                            cx + (int)fm.getStringBounds(currentValue,g).getWidth() - 1, baseLine + 1);
                }
            }
        }
        
        
    }
    /** Getter for property color1.
     * @return Value of property color1.
     */
    public Color getColor1() {
        return color1;
    }
    /** Setter for property color1.
     * @param color1 New value of property color1.
     */
    public void setColor1(Color color1) {
        this.color1 = color1;
    }
    /** Getter for property color2.
     * @return Value of property color2.
     */
    public Color getColor2() {
        return color2;
    }
    /** Setter for property color2.
     * @param color2 New value of property color2.
     */
    public void setColor2(Color color2) {
        this.color2 = color2;
    }
    /** Getter for property color3.
     * @return Value of property color3.
     */
    public Color getColor3() {
        return color3;
    }
    /** Setter for property color3.
     * @param color3 New value of property color3.
     */
    public void setColor3(Color color3) {
        this.color3 = color3;
    }
    
    /** Getter for property target.
     * @return Value of property target.
     */
    public Target getTarget() {
        return target;
    }
    /** Setter for property target.
     * @param target New value of property target.
     */
    public void setTarget(Target target) {
        if ( this.target != target ) {
            if ( this.target != null ) {
                this.target.removePropertyChangeListener( this );
            }

            this.target = target;
            
            if ( effectPanel != null ) effectPanel.setModel(new EffectListModel(target));

            if ( target != null ) {
                target.addPropertyChangeListener(this);
            }
            if (DEBUG > 0 ) System.out.println("TargetButtonStats.triggerUpdate called due to Target change to " + target + ".");
            triggerUpdate();
        }
    }
    /** This method gets called when a bound property is changed.
     * @param evt A PropertyChangeEvent object describing the event source
     *  	and the property that has changed.
     */
    public void propertyChange(PropertyChangeEvent evt) {
        if ( evt.getSource() instanceof Target ) {
            triggerUpdate();
            if (DEBUG > 0 ) System.out.println("TargetButtonStats.triggerUpdate called due to prop \"" + evt.getPropertyName() + "\".");
        } else {
            if ( statList.getBooleanValue("Columns.ADJUSTING" ) == false ) {
                updateColumns();
            }
        }
    }
    
    public void triggerUpdate() {
        if ( Battle.getCurrentBattle() != null && Battle.getCurrentBattle().isProcessing() == false ) {
            if (DEBUG > 0 ) System.out.println("TargetButtonStats.triggerUpdate: repaint fired.");
            repaint();
            invalid = false;
        } else {
            if (DEBUG > 0 ) System.out.println("TargetButtonStats.triggerUpdate: repaint queued.");
            invalid = true;
        }
    }
    /** Getter for property model.
     * @return Value of property model.
     */
    public ButtonModel getModel() {
        return model;
    }
    /** Setter for property model.
     * @param model New value of property model.
     */
    public void setModel(ButtonModel model) {
        if ( this.model != null ) {
            model.removeChangeListener(this);
        }
        
        this.model = model;
        
        if ( this.model != null ) {
            model.addChangeListener(this);
        }
    }
    /** Invoked when the target of the listener has changed its state.
     *
     * @param e  a ChangeEvent object
     */
    public void stateChanged(BattleChangeEvent e) {
        TargetButton2.buttonUpdates++;
        if (DEBUG > 0 ) System.out.println("TargetButtonStats.triggerUpdate called due to stateChanged event.");
        triggerUpdate();
    }
    
    public void stateChanged(ChangeEvent e) {
        TargetButton2.buttonUpdates++;
        if (DEBUG > 0 ) System.out.println("TargetButtonStats.triggerUpdate called due to stateChanged event.");
        triggerUpdate();
    }
    
    /** Invoked when the component's size changes.
     */
    public void componentResized(ComponentEvent e) {
        if ( effectPanel != null ) {
            effectPanel.setSize( effectPanel.getWidth(), this.getHeight() );
        }
    }
    /** Invoked when the component's position changes.
     */
    public void componentMoved(ComponentEvent e) {
    }
    /** Invoked when the component has been made visible.
     */
    public void componentShown(ComponentEvent e) {
    }
    /** Invoked when the component has been made invisible.
     */
    public void componentHidden(ComponentEvent e) {
    }
    /** Getter for property pressedColor.
     * @return Value of property pressedColor.
     */
    public Color getPressedColor() {
        return pressedColor;
    }
    /** Setter for property pressedColor.
     * @param pressedColor New value of property pressedColor.
     */
    public void setPressedColor(Color pressedColor) {
        this.pressedColor = pressedColor;
    }
    
    public void setBackground(Color bg) {
        super.setBackground(bg);
        if ( effectPanel != null ) effectPanel.setBackground(bg);
    }
    
    public void setForeground(Color fg) {
        super.setForeground(fg);
        if ( effectPanel != null ) effectPanel.setForeground(fg);
    }
    
    /** Target Selected Event Occured.
     * Indicates that a target has become active.
     * @param e
     */
    public void battleTargetSelected(TargetSelectedEvent e) {
    }
    
    /** Battle Segment has advanced.
     * Indicates the battle time has advanced at least one segment.
     * @param e
     */
    public void battleSegmentAdvanced(SegmentAdvancedEvent e) {
    }
    
    /** Sequence of Targets in Battle has changed.
     * Indicates that the sequence of upcoming targets has changed.  This can occur due to
     * a number of reasons, including new rosters/characters being added and speed changes.
     * @param e
     */
    public void battleSequenceChanged(SequenceChangedEvent e) {
    }
    
    /** The Event list has changed.
     * Indicates some change occurred in the event list for the battle.  Either events were added, removed,
     * undone, or redone.
     *
     * EventNotifications are gauranteed to be sent out for all event list changes.  In the case of Undo/Redo both an
     * eventNotification and a stateChanged will be sent.
     *
     * @param e
     */
    public void eventNotification(ChangeEvent e) {
    }
    
    /** Combat State changed for Participant in battle
     * Indicates that the combat state of one of the participant changed in the current battle.  Usually, the
     * change occurred to the active target, but all changes to combat states of any target will fire this event.
     *
     * @param e
     */
    public void combatStateChange(ChangeEvent e) {
    }
    
    /** Processing state of BattleEngine changed
     * Indicates the battleEngine has either started or stopped processing events.
     * Check the Battle.getCurrentBattle().isProcessing() for the current state of the battleEngine.
     * @param e
     */
    public void processingChange(BattleChangeEvent event) {
        if ( invalid == true && Battle.getCurrentBattle().isProcessing() == false ) {
            if (DEBUG > 0 ) System.out.println("TargetButtonStats.triggerUpdate called due to processingChange event.");
            triggerUpdate();
        }
    }

    public void destroy() {
        setTarget(null);
        Battle.removeBattleListener(this);
    }
    
}