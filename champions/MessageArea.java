/*
 * MessageArea.java
 *
 * Created on November 9, 2000, 10:36 PM
 */

package champions;

import tjava.ContextMenuListener;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.SwingUtilities;
import javax.swing.Timer;



/**
 *
 * @author  unknown
 * @version
 */
public class MessageArea extends JPanel
implements PropertyChangeListener, ContextMenuListener, ActionListener {
    
    private static final int DEBUG = 0;
    
    /** Holds value of property battleEvent. */
    private BattleEvent battleEvent;
    /** Holds value of property combatColor. */
    private Color combatColor = new Color(88,0,255);
    /** Holds value of property debugColor. */
    private Color debugColor = Color.yellow;
    /** Holds value of property abilityColor. */
    private Color abilityColor = Color.magenta;
    /** Holds value of property noticeColor. */
    private Color noticeColor = new Color(0,102,204);
    /** Holds value of property hitColor. */
    private Color hitColor = new Color(0,0,109);
    /** Holds value of property missColor. */
    private Color missColor = Color.red;
    
    /** Holds value of property utilityColor. */
    private Color utilityColor = Color.pink;
    /** Holds value of property rosterColor. */
    private Color rosterColor = Color.white;
    /** Holds value of property segmentColor. */
    private Color segmentColor = Color.blue;
    /** Holds value of property diceColor. */
    private Color diceColor = new Color(154,0,102 );
    /** Holds value of property endColor. */
    private Color endColor = new Color(0,102,255 );
    /** Holds value of property backgroundColor. */
    //  private Color backgroundColor;
    
    /** Holds value of property undone. */
    private boolean undone = false;
    
    private int fontHeight;
    private int fontAscent;
    private FontMetrics fm;
    private Timer timer;
    private Timer updateDelay;
    
    private Action debugAction, undoAction, redoAction;
    
    /** Holds value of property delay. */
    //  private int delay = 10;
    // private int position = 0;
    
    static public long beaTime = 0;
    static public long beaCount = 0;
    
    //  private int graph[] = {100, 80, 65, 55, 55, 50, 46, 42, 38, 34, 30, 27, 24, 21, 18, 15, 13, 11, 9, 7, 6, 5, 4, 3, 2, 1};
    
    
    /** Holds value of property flashEnabled. */
    private boolean flashEnabled = false;
    
    /** Holds value of property enabledEventColor. */
    private Color enabledEventColor;
    
    /** Holds value of property disabledEventColor. */
    private Color disabledEventColor;
    
    /** Holds value of property style. */
    private int style = FILLSTYLE;
    
    public static final int FILLSTYLE = 1;
    public static final int LINESTYLE = 2;
    
    /** Holds value of property startMessage. */
    private int startMessage;
    
    /** Holds value of property endMessage. */
    private int endMessage;
    
    /**
     * Creates new MessageArea
     */
    public MessageArea() {
        //setBackground( new Color(204,204,204));
        enabledEventColor = getBackground();
        disabledEventColor = enabledEventColor.darker();
        
        setRequestFocusEnabled( false );
        //setEditable(false);
        
        //repaint();
        
        //setEditable(false);
        fm = getFontMetrics(getFont());
        fontHeight = fm.getHeight();
        fontAscent = fm.getAscent();
        
        setStartMessage(0);
        setEndMessage(Integer.MAX_VALUE);
        
        //  timer = new Timer(delay, this);
        
        updateDelay = new Timer(100, this);
        updateDelay.setRepeats(false);
        
        setupActions();
        
        //   setBattleEvent(new BattleEvent());
    }
    
    public void setupActions() {
        debugAction = new AbstractAction( "Debug Event..." ) {
            public void actionPerformed(ActionEvent e) {
                if ( battleEvent != null ) battleEvent.debugDetailList(null);
            }
        };
    }
    
    public Color getTypeColor(int type) {
        Color color = getForeground();
        if ( ! isUndone() ) {
            //Enabled Colors
            if ( type == 1 ) color =  combatColor;
            if ( type == 2 ) color =  debugColor;
            if ( type == 3 ) color =  abilityColor;
            if ( type == 4 ) color =  utilityColor;
            if ( type == 5 ) color =  rosterColor;
            if ( type == 6 ) color =  segmentColor;
            if ( type == 7 ) color =  noticeColor;
            if ( type == 8 ) color =  hitColor;
            if ( type == 9 ) color =  missColor;
            if ( type == 10 ) color =  diceColor;
            if ( type == 11 ) color =  endColor;
        }
        else {
            // Disabled Colors
            if ( type ==1 ) color =  combatColor.darker();
            if ( type ==2 ) color =  debugColor.darker();
            if ( type ==3 ) color =  abilityColor.darker();
            if ( type ==4 ) color =  utilityColor.darker();
            if ( type ==5 ) color =  rosterColor.darker();
            if ( type ==6 ) color =  segmentColor.darker();
            if ( type ==7 ) color =  noticeColor.darker();
            if ( type ==8 ) color =  hitColor.darker();
            if ( type ==9 ) color =  missColor.darker();
            if ( type == 10 ) color =  diceColor.darker();
            if ( type == 11 ) color =  endColor.darker();
            
            if ( style == LINESTYLE ) {
                int total = 0;
                total += color.getRed() + color.getGreen() + color.getBlue();
                color = new Color( total/3, total/3, total/3 );
            }
        }
        
        
        //    color = adjustColor( color );
        
        
        return color;
    }
    
  /*  public Color adjustColor(Color color ) {
        if ( flashEnabled ) {
            int red = color.getRed();
            int blue = color.getBlue();
            int green = color.getGreen();
   
            if ( position >= 0 && position < graph.length ) {
                red = red + ( 255 - red ) * graph[position] / 100 ;
                blue = blue + ( 255 - blue ) * graph[position] / 100 ;
                green = green + ( 255 - green ) * graph[position] / 100 ;
            }
            return new Color( red, green, blue);
        }
        else {
            return color;
        }
    } */
    
    /** Getter for property battleEvent.
     * @return Value of property battleEvent.
     */
    public BattleEvent getBattleEvent() {
        return battleEvent;
    }
    /** Setter for property battleEvent.
     * @param battleEvent New value of property battleEvent.
     */
    public void setBattleEvent(BattleEvent battleEvent) {
        if ( battleEvent == this.battleEvent ) return;
        
        if ( this.battleEvent != null ) {
            this.battleEvent.removePropertyChangeListener("MESSAGE",this);
        }
        
        this.battleEvent = battleEvent;
        
        if ( this.battleEvent != null ) {
            this.battleEvent.addPropertyChangeListener("MESSAGE",this);
        }
        //  resetTimer();
        revalidate();
    }
    /** Getter for property combatColor.
     * @return Value of property combatColor.
     */
    public Color getCombatColor() {
        return combatColor;
    }
    /** Setter for property combatColor.
     * @param combatColor New value of property combatColor.
     */
    public void setCombatColor(Color combatColor) {
        this.combatColor = combatColor;
        repaint();
    }
    /** Getter for property debugColor.
     * @return Value of property debugColor.
     */
    public Color getDebugColor() {
        return debugColor;
    }
    /** Setter for property debugColor.
     * @param debugColor New value of property debugColor.
     */
    public void setDebugColor(Color debugColor) {
        this.debugColor = debugColor;
        repaint();
    }
    /** Getter for property abilityColor.
     * @return Value of property abilityColor.
     */
    public Color getAbilityColor() {
        return abilityColor;
    }
    /** Setter for property abilityColor.
     * @param abilityColor New value of property abilityColor.
     */
    public void setAbilityColor(Color abilityColor) {
        this.abilityColor = abilityColor;
        repaint();
    }
    /** Getter for property noticeColor.
     * @return Value of property noticeColor.
     */
    public Color getNoticeColor() {
        return noticeColor;
    }
    /** Setter for property noticeColor.
     * @param noticeColor New value of property noticeColor.
     */
    public void setNoticeColor(Color noticeColor) {
        this.noticeColor = noticeColor;
        repaint();
    }
    /** Getter for property hitColor.
     * @return Value of property hitColor.
     */
    public Color getHitColor() {
        return hitColor;
    }
    /** Setter for property hitColor.
     * @param hitColor New value of property hitColor.
     */
    public void setHitColor(Color hitColor) {
        this.hitColor = hitColor;
        repaint();
    }
    /** Getter for property missColor.
     * @return Value of property missColor.
     */
    public Color getMissColor() {
        return missColor;
    }
    /** Setter for property missColor.
     * @param missColor New value of property missColor.
     */
    public void setMissColor(Color missColor) {
        this.missColor = missColor;
        repaint();
    }
    /** This method gets called when a bound property is changed.
     * @param evt A PropertyChangeEvent object describing the event source
     *  	and the property that has changed.
     */
    public void propertyChange(PropertyChangeEvent evt) {
        //updateText();
        if ( evt.getPropertyName().equals("MESSAGE") ) {
            scheduleUpdate();
            //resetTimer();
            //updateArea();
        }
    }
    
    private void scheduleUpdate() {
        if ( DEBUG > 0 ) System.out.println("Scheduling Update at " + Long.toString( System.currentTimeMillis() ) + ".");
        updateDelay.restart();
    }
    
    private void updateArea() {
        revalidate();
        repaint();
        
        Rectangle r = getBounds();
        r.y = r.y + r.height - fontHeight;
        scrollRectToVisible( r );
        
        beaCount++;
    }
    
    public Dimension getPreferredSize() {
        //   long time = System.currentTimeMillis();
        
        Dimension d = new Dimension();
        
        if ( battleEvent != null ) {
            int index,count;
            int maxWidth = 0;
            int newWidth;
            
            String message;
            
            count = battleEvent.getMessageCount();
            Insets insets = getInsets();
            
            if ( count == 0 && DEBUG > 0) {
                d.height = insets.top + insets.bottom + fontHeight;
                newWidth = SwingUtilities.computeStringWidth(fm, "Error: Null Battle Event");
            }
            else if ( startMessage < count ) {
                
                int end = (endMessage < count) ? endMessage : count;
                int rows = end - startMessage;
                
                d.height = insets.top + insets.bottom + rows * fontHeight;
                for( index = startMessage; index < end; index++ ) {
                    message = battleEvent.getMessageText(index);
                    newWidth = SwingUtilities.computeStringWidth(fm, message);
                    if ( newWidth > maxWidth ) maxWidth = newWidth;
                }
            }
            else {
                d.height = insets.top + insets.bottom;
                maxWidth = 0;
            }
            
            d.width = insets.left + insets.right + maxWidth + 3;
            if ( style == LINESTYLE ) d.width += 4;
        }
        
        //   beaTime += System.currentTimeMillis() - time;
        //   beaCount += 1;
        return d;
    }
    
    /** Getter for property undone.
     * @return Value of property undone.
     */
    public boolean isUndone() {
        return undone;
    }
    /** Setter for property undone.
     * @param undone New value of property undone.
     */
    public void setUndone(boolean undone) {
        if ( undone != this.undone ) {
            this.undone = undone;
            repaint();
        }
    }
    /** Getter for property backgroundColor.
     * @return Value of property backgroundColor.
     */
    
    /** Getter for property utilityColor.
     * @return Value of property utilityColor.
     */
    public Color getUtilityColor() {
        return utilityColor;
    }
    /** Setter for property utilityColor.
     * @param utilityColor New value of property utilityColor.
     */
    public void setUtilityColor(Color utilityColor) {
        this.utilityColor = utilityColor;
    }
    /** Getter for property rosterColor.
     * @return Value of property rosterColor.
     */
    public Color getRosterColor() {
        return rosterColor;
    }
    /** Setter for property rosterColor.
     * @param rosterColor New value of property rosterColor.
     */
    public void setRosterColor(Color rosterColor) {
        this.rosterColor = rosterColor;
    }
    /** Getter for property segmentColor.
     * @return Value of property segmentColor.
     */
    public Color getSegmentColor() {
        return segmentColor;
    }
    /** Setter for property segmentColor.
     * @param segmentColor New value of property segmentColor.
     */
    public void setSegmentColor(Color segmentColor) {
        this.segmentColor = segmentColor;
    }
    
    protected void paintComponent(Graphics g) {
        //super.paintComponent(g);
        
        Insets insets = getInsets();
        
        if ( style == FILLSTYLE ) {
            g.setColor( undone ? disabledEventColor : enabledEventColor );
            g.fillRect(0,0,getWidth(),getHeight());
        }
        else {
            g.setColor( getBackground() );
            g.fillRect(0,0,getWidth(),getHeight());
        }
        
        int x,y;
        
        x = insets.left + 3;
        y = insets.top + fontAscent;
        
        if ( style == LINESTYLE ) {
            g.setColor( undone ? disabledEventColor : enabledEventColor );
            g.fillRect(insets.left + 2, insets.top, 3, getHeight() - insets.top - insets.bottom);
            x = insets.left + 7;
        }
        if ( battleEvent != null ) {
            int index, count;
            String message;
            int type;
            
            count = battleEvent.getMessageCount();
            
            if ( count == 0 && DEBUG > 0) {
                g.setColor( getTypeColor( BattleEvent.MSG_UTILITY ) );
                g.drawString( "Error: Null Battle Event", x, y);
            }
            else if ( startMessage < count ) {
                
                int end = (endMessage < count) ? endMessage : count;
                int rows = end - startMessage;
                
                for ( index = startMessage; index < end; index ++ ) {
                    message = battleEvent.getMessageText(index);
                    type = battleEvent.getMessageType(index);
                    if ( message == null  ) break;
                    
                    g.setColor( getTypeColor( type ) );
                    g.drawString( message, x, y);
                    y += fontHeight;
                }
            }
            else {
                // Do nothing
            }
        }
    }
    
 /*   public void resetTimer() {
        if ( flashEnabled ) {
            position = 0;
            timer.start();
        }
    } */
    /** Invoked when an action occurs.
     */
  /*  public void actionPerformed(ActionEvent e) {
        position ++;
        if ( position == graph.length ) timer.stop();
   
        repaint();
   
    } */
    
    public boolean invokeMenu(JPopupMenu popup,Component inComponent,Point inPoint) {
        if ( Battle.debugLevel >= 1 ) {
            popup.add(debugAction);

            return true;
        }
        return false;
    }
    /** Getter for property diceColor.
     * @return Value of property diceColor.
     */
    public Color getDiceColor() {
        return diceColor;
    }
    /** Setter for property diceColor.
     * @param diceColor New value of property diceColor.
     */
    public void setDiceColor(Color diceColor) {
        this.diceColor = diceColor;
    }
    /** Getter for property endColor.
     * @return Value of property endColor.
     */
    public Color getEndColor() {
        return endColor;
    }
    /** Setter for property endColor.
     * @param endColor New value of property endColor.
     */
    public void setEndColor(Color endColor) {
        this.endColor = endColor;
    }
    /** Getter for property flashEnabled.
     * @return Value of property flashEnabled.
     */
    public boolean isFlashEnabled() {
        return flashEnabled;
    }
    /** Setter for property flashEnabled.
     * @param flashEnabled New value of property flashEnabled.
     */
    public void setFlashEnabled(boolean flashEnabled) {
        this.flashEnabled = flashEnabled;
    }
    
    /** Getter for property enabledEventColor.
     * @return Value of property enabledEventColor.
     */
    public Color getEnabledEventColor() {
        return enabledEventColor;
    }
    
    /** Setter for property enabledEventColor.
     * @param enabledEventColor New value of property enabledEventColor.
     */
    public void setEnabledEventColor(Color enabledEventColor) {
        if ( this.enabledEventColor != enabledEventColor ) {
            this.enabledEventColor = enabledEventColor;
            if ( undone == false ) {
                repaint();
            }
        }
    }
    
    /** Getter for property disabledEventColor.
     * @return Value of property disabledEventColor.
     */
    public Color getDisabledEventColor() {
        return disabledEventColor;
    }
    
    /** Setter for property disabledEventColor.
     * @param disabledEventColor New value of property disabledEventColor.
     */
    public void setDisabledEventColor(Color disabledEventColor) {
        if ( this.disabledEventColor != disabledEventColor ) {
            this.disabledEventColor = disabledEventColor;
            if ( undone == true ) {
                repaint();
            }
        }
    }
    
    /** Getter for property style.
     * @return Value of property style.
     */
    public int getStyle() {
        return style;
    }
    
    /** Setter for property style.
     * @param style New value of property style.
     */
    public void setStyle(int style) {
        if ( this.style != style ) {
            this.style = style;
            
            repaint();
        }
    }
    
    /** Getter for property startMessage.
     * @return Value of property startMessage.
     */
    public int getStartMessage() {
        return startMessage;
    }
    
    /** Setter for property startMessage.
     * @param startMessage New value of property startMessage.
     */
    public void setStartMessage(int startMessage) {
        this.startMessage = startMessage;
        repaint();
    }
    
    /** Getter for property endMessage.
     * @return Value of property endMessage.
     */
    public int getEndMessage() {
        return endMessage;
    }
    
    /** Setter for property endMessage.
     * @param endMessage New value of property endMessage.
     */
    public void setEndMessage(int endMessage) {
        this.endMessage = endMessage;
        repaint();
    }
    
    /**
     * Invoked when an action occurs.
     */
    public void actionPerformed(ActionEvent e) {
        if ( e.getSource() == updateDelay ) {
            if ( DEBUG > 0 ) System.out.println("Updating Area at " + Long.toString( System.currentTimeMillis() ) + ".");
            updateArea();
        }
    }
    
}