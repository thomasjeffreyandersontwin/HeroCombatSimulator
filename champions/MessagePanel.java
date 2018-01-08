/*
 * MessagePanel.java
 *
 * Created on April 20, 2002, 6:35 PM
 */

package champions;

import tjava.ContextMenu;
import champions.event.SegmentAdvancedEvent;
import champions.event.SequenceChangedEvent;
import champions.event.TargetSelectedEvent;
import champions.interfaces.BattleListener;
import tjava.ContextMenuListener;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Iterator;
import java.util.List;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.DefaultListSelectionModel;
import javax.swing.InputMap;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JSeparator;
import javax.swing.JViewport;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;
import javax.swing.Scrollable;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import VirtualDesktop.Attack.PhysicalObjectAdapter;
import VirtualDesktop.Character.CharacterAdaptor;

/**
 *
 * @author  Trevor Walker
 * @version
 */
public class MessagePanel extends JPanel
        implements BattleListener, Scrollable, PropertyChangeListener, MouseListener, ListSelectionListener,
        MouseMotionListener, ContextMenuListener, ActionListener {
    private static final int DEBUG = 0;
    
    private BattleEvent[] battleEvents = null;
    private int[] lineOffsets = null;
    private int allocated = 0;
    private int size = 0;
    private int completedPosition = 0;
    private int purgedMessages = 0;
    
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
    private Color rosterColor = Color.green;
    /** Holds value of property segmentColor. */
    private Color segmentColor = Color.blue;
    /** Holds value of property diceColor. */
    private Color diceColor = new Color(154,0,102 );
    /** Holds value of property endColor. */
    private Color endColor = new Color(0,102,255 );
    
    /** Holds value of property fontHeight. */
    private int fontHeight;
    
    /** Holds value of property maximumWidth. */
    private int maximumWidth;
    
    /** Event to Scroll to after validate has been done. */
    private int eventToScrollToNext = -1;
    
    
    /** Holds value of property undoneBackgroundColor. */
    private Color undoneBackgroundColor;
    
    /** Holds value of property fontAscent. */
    private int fontAscent;
    private FontMetrics fm;
    
    /** Holds value of property doneBackgroundColor. */
    private Color doneBackgroundColor;
    
    /** Holds value of property doneSelectionColor. */
    private Color doneSelectionColor;
    
    /** Holds value of property undoneSelectionColor. */
    private Color undoneSelectionColor;
    
    /** Holds value of property textSelectionColor. */
    private Color textSelectionColor;
    
    /** Holds value of property selectionModel. */
    private ListSelectionModel selectionModel;
    
    private Action copyAction;
    private EditBattleEventAction editEventAction;
    private ReprocessEventAction reprocessEventAction;
    private JMenuItem copyMenuItem, editMenuItem;
    
    private javax.swing.Timer updateDelay;
    
    //private boolean updateBlockedWhileProcessing = false;
    
    /**
     * Creates new MessagePanel
     */
    public MessagePanel() {
        initializeArrays(100);
        
        Battle.addBattleListener(this);
        setAutoscrolls(false);
        
        setSelectionModel( new DefaultListSelectionModel() );
        
        ContextMenu.addContextMenu(this);
        
        addMouseListener(this);
        addMouseMotionListener( this);
        
        setupActions();
        setupKeyBindings();
        
        updateDelay = new javax.swing.Timer(200, this);
        updateDelay.setRepeats(false);
    }
    
    public void updateUI() {
        super.updateUI();
        
        setupColors();
    }
    
    private void setupColors() {
        if ( UIManager.getColor( "Messages.doneBackgroundColor" ) != null) {
            setDoneBackgroundColor( UIManager.getColor( "Messages.doneBackgroundColor" ) );
        } else {
            setDoneBackgroundColor( getBackground() );
        }
        if ( UIManager.getColor( "Messages.undoneBackgroundColor" ) != null) {
            setUndoneBackgroundColor( UIManager.getColor( "Messages.undoneBackgroundColor" ) );
        } else {
            setUndoneBackgroundColor( getBackground().darker() );
        }
        if ( UIManager.getColor( "Messages.doneSelectionColor" ) != null) {
            setDoneSelectionColor( UIManager.getColor( "Messages.doneSelectionColor" ) );
        } else {
            setDoneSelectionColor( getBackground().brighter() );
        }
        if ( UIManager.getColor( "Messages.undoneSelectionColor" ) != null) {
            setUndoneSelectionColor( UIManager.getColor( "Messages.undoneSelectionColor" ) );
        } else {
            setUndoneSelectionColor( getBackground().brighter() );
        }
        if ( UIManager.getColor( "Messages.textSelectionColor" ) != null) {
            setTextSelectionColor( UIManager.getColor( "Messages.textSelectionColor" ) );
        }
        
    }
    
    public void setupActions() {
        if ( copyAction == null ) {
            copyAction = new CopyAction(this);
            copyMenuItem = new JMenuItem(copyAction);
            copyMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C, KeyEvent.CTRL_MASK ));
        }
        
        if ( editEventAction == null ) {
            editEventAction = new EditBattleEventAction();
            editMenuItem = new JMenuItem(editEventAction);
        }
        
        if ( reprocessEventAction == null ) {
            reprocessEventAction = new ReprocessEventAction();
            
        }
    }
    
    
    public void setupKeyBindings() {
        InputMap inputMap = new InputMap();
        inputMap.put( KeyStroke.getKeyStroke(KeyEvent.VK_C, KeyEvent.CTRL_MASK ), "CopyMessages" );
        setInputMap( WHEN_FOCUSED , inputMap );
        
        ActionMap actionMap = new ActionMap();
        actionMap.put( "CopyMessages", copyAction );
        setActionMap(actionMap);
    }
    
    private void updateEvents() {
        if ( Battle.currentBattle == null ) {
            clearEvents();
            revalidate();
            repaint();
        } else {
            int index;
            boolean revalidate = false;
            BattleEvent be1, be2;
            
            List<BattleEvent> v = Battle.currentBattle.getCompletedEventList();
            int vSize = Battle.currentBattle.getCompletedEventMaxRedo();
            
            int purgedSize = Battle.currentBattle.getPurgedMessageCount();
            
            if ( purgedSize != purgedMessages ) {
                revalidate = true;
                clearEvents();
                purgedMessages = purgedSize;
            }
            
            if ( size < vSize ) {
                // Our array is smaller, so run through the ones we know, checking up on things
                for (index = 0; index < size; index++ ) {
                    be1 = getBattleEvent(index);
                    be2 = (BattleEvent)v.get(index);
                    
                    if ( be1 != be2 ) {
                        // They aren't the same, so our array is snookered.
                        // Remove the old one and put back the new one...
                        be1.removePropertyChangeListener("MESSAGE", this);
                        
                        revalidate = true;
                        
                        setBattleEvent(index, be2);
                        setEventLineOffset(index, be2.getMessageCount() + ((index == 0) ? purgedMessages : getEventLineOffset(index-1)));
                        be2.addPropertyChangeListener("MESSAGE", this);
                    }
                }
                
                // Now add the extras that are on the end...
                for (; index < vSize; index++ ) {
                    be2 = (BattleEvent)v.get(index);
                    revalidate = true;
                    add(be2);
                    be2.addPropertyChangeListener("MESSAGE", this);
                }
            } else {
                // We have a bigger or equal array...
                for (index = 0; index < vSize; index++ ) {
                    be1 = getBattleEvent(index);
                    be2 = (BattleEvent)v.get(index);
                    
                    if ( be1 != be2 ) {
                        // They aren't the same, so our array is snookered.
                        // Remove the old one and put back the new one...
                        be1.removePropertyChangeListener("MESSAGE", this);
                        
                        revalidate = true;
                        
                        setBattleEvent(index, be2);
                        setEventLineOffset(index, be2.getMessageCount() + ((index == 0) ? purgedMessages : getEventLineOffset(index-1)));
                        be2.addPropertyChangeListener("MESSAGE", this);
                    }
                }
                
                // Now add the extras that are on the end...
                for (; index < size; index++ ) {
                    
                    be1 = getBattleEvent(index);
                    if ( be1 != null ) {
                        revalidate = true;
                        setBattleEvent(index, null);
                        setEventLineOffset(index, -1);
                        
                        be1.removePropertyChangeListener("MESSAGE", this);
                    }
                }
                size = vSize;
            }
            
            int cep = Battle.currentBattle.getCompletedEventPosition();
            if ( completedPosition != cep ) {
                revalidate = true;
                completedPosition = cep;
            }
            
            if ( revalidate ) {
                revalidate();
                repaint();
            }
        }
    }
    
    private Rectangle getPurgedMessageBounds(Rectangle r, int lineIndex) {
        if ( r == null ) r = new Rectangle();
        getBounds(r);
        Insets i = getInsets();
        
        r.x = i.left;
        r.width -= i.left + i.right;
        
        r.y = i.top + lineIndex * fontHeight;
        r.height = fontHeight;
        
        return r;
    }
    
    private Rectangle getEventBounds(Rectangle r, int messageIndex) {
        if ( r == null ) r = new Rectangle();
        getBounds(r);
        Insets i = getInsets();
        
        r.x = i.left;
        r.width -= i.left + i.right;
        
        r.y = i.top + ((messageIndex == 0) ? purgedMessages * fontHeight : getEventLineOffset(messageIndex - 1) * fontHeight);
        r.height = i.top + getEventLineOffset(messageIndex) * fontHeight - r.y;
        
        return r;
    }
    
    private Rectangle getMinimumUndoableBounds(Rectangle r) {
        if ( r == null ) r = new Rectangle();
        getBounds(r);
        Insets i = getInsets();
        
        r.x = i.left;
        r.width -= i.left + i.right;
        
        r.y = i.top + purgedMessages * fontHeight;
        r.height = fontHeight;
        
        return r;
    }
    
    public int findEventForLine(int lineIndex) {
        int i;
        
        if ( lineIndex != -1 && lineIndex >= purgedMessages ) {
            //lineIndex -= purgedMessages;
            
            for(i=0; i<size; i++) {
                if ( lineIndex < lineOffsets[i] && ( i == 0 || lineIndex >= lineOffsets[i-1] )) {
                    // i is the line...
                    return i;
                    //   break;
                }
            }
        }
        return -1;
    }
    
    public int findEventForPoint(Point p) {
        return findEventForLine( getLineForPoint(p) );
    }
    
    public int getLineForPoint(Point p) {
        Insets i = getInsets();
        
        int y = (int)p.getY();
        y -= i.top;
        
        int line = y / fontHeight;
        
        if ( line < 0 ) line = -1;
        else if ( line > getLastLine() ) line = -1;
        
        return line;
    }
    
    public String getMessageForLine(int lineIndex) {
        int i;
        int offset;
        
        if ( lineIndex < purgedMessages ) {
            return Battle.currentBattle.getPurgedMessageText(lineIndex);
        } else {
            for(i=0; i<size; i++) {
                if ( lineIndex < lineOffsets[i] && ( i == 0 || lineIndex >= lineOffsets[i-1] )) {
                    // i is the line...
                    int startLine = getEventStartLine(i);
                    
                    int messageLine = lineIndex - startLine;
                    
                    
                    if ( messageLine >= 0 && messageLine < battleEvents[i].getMessageCount() ) {
                        return battleEvents[i].getMessageText(messageLine);
                    } else {
                        throw new ArrayIndexOutOfBoundsException();
                    }
                    //   break;
                }
            }
        }
        return null;
    }
    
    public int findEvent(BattleEvent be) {
        for(int index=0; index<size; index++ ) {
            if ( getBattleEvent(index) == be ) return index;
        }
        return -1;
    }
    
    public void battleTargetSelected(TargetSelectedEvent e) {
    }
    public void battleSegmentAdvanced(SegmentAdvancedEvent e) {
    }
    public void battleSequenceChanged(SequenceChangedEvent e) {
    }
    public void stateChanged(BattleChangeEvent e) {
        
    }
    public void eventNotification(ChangeEvent e) {
        updateEvents();
        
        scrollToCurrentEvent();
    }
    public void processingChange(BattleChangeEvent event) {
    }
    public void combatStateChange(ChangeEvent e) {
    }
    
    public boolean getScrollableTracksViewportWidth() {
        return false;
    }
    
    public int getScrollableBlockIncrement(java.awt.Rectangle rectangle, int param, int param2) {
        return fontHeight * 5;
    }
    
    public boolean getScrollableTracksViewportHeight() {
        return false;
    }
    
    public java.awt.Dimension getPreferredScrollableViewportSize() {
        return getPreferredSize();
    }
    
    public int getScrollableUnitIncrement(java.awt.Rectangle rectangle, int param, int param2) {
        return fontHeight;
    }
    
    private void scrollToCurrentEvent() {
        int currentEvent = Battle.currentBattle.getCompletedEventPosition() - 1;
        Rectangle bounds = getEventBounds(null, currentEvent>0 ? currentEvent : 0);
        
        if ( bounds != null ) {
            Component p = getParent();
            
            if ( p instanceof JViewport ) {
                
                int height = p.getSize().height;
                
                if ( bounds.height > height ) {
                    bounds.y += bounds.height - height;
                    bounds.height = height;
                }
            }
            
            scrollRectToVisible( bounds );
        }
    }
    
    private void scrollToEvent(int eventIndex) {
        Rectangle bounds = getEventBounds(null, eventIndex);
        
        if ( bounds != null ) {
            Component p = getParent();
            
            if ( p instanceof JViewport ) {
                
                int height = p.getSize().height;
                
                if ( bounds.height > height ) {
                    bounds.y += bounds.height - height;
                    bounds.height = height;
                }
            }
            
            scrollRectToVisible( bounds );
        }
    }
    
    private void scrollToLine(int lineIndex) {
        Insets i = getInsets();
        Rectangle r = new Rectangle();
        
        r.y = i.top + lineIndex * fontHeight;
        r.x = 0;
        r.width = getSize().width;
        r.height = fontHeight;
        
        scrollRectToVisible(r);
    }
    /**
     * This method gets called when a bound property is changed.
     * @param evt A PropertyChangeEvent object describing the event source
     *  	and the property that has changed.
     */
    public void propertyChange(PropertyChangeEvent evt) {
        BattleEvent be = (BattleEvent)evt.getSource();
        int index = findEvent(be);
        
        if ( index != -1 ) {
            int real = be.getMessageCount();
            int cached = getCachedMessageCount(index);
            
            if ( real != cached ) {
                int difference = real - cached;
                for ( int i = index; i < size; i++ ) {
                    setEventLineOffset(i, getEventLineOffset(i) + difference);
                }
                
                int width = getWidth(be);
                if ( maximumWidth < width ) maximumWidth = width;
                
                scheduleUpdate( getEventLineOffset(index), getEventLineOffset(size-1), index);
                
            }
        }
    }
    
    private int getWidth(BattleEvent battleEvent) {
        int maxWidth = 0;
        if ( battleEvent != null ) {
            int index,count;
            
            int newWidth;
            
            String message;
            
            count = battleEvent.getMessageCount();
            Insets insets = getInsets();
            
       /*     if ( count == 0 && DEBUG > 0) {
                d.height = insets.top + insets.bottom + fontHeight;
                newWidth = SwingUtilities.computeStringWidth(fm, "Error: Null Battle Event");
            }
            else { */
            Iterator i = battleEvent.getMessageIterator();
            while ( i.hasNext() ) {
                message = ((MessagePair)i.next()).getMessage();
                newWidth = SwingUtilities.computeStringWidth(fm, message);
                if ( newWidth > maxWidth ) maxWidth = newWidth;
            }
            //  }
        }
        return maxWidth;
    }
    
    private int getWidth(String message) {
        return SwingUtilities.computeStringWidth(fm, message);
    }
    
    private void initializeArrays(int amount) {
        battleEvents = new BattleEvent[ amount ];
        lineOffsets = new int[ amount ];
        
        allocated = amount;
        size = 0;
    }
    
    private void expandArrays(int amount) {
        BattleEvent[] newEvents = new BattleEvent[ allocated + amount ];
        int[] newOffsets = new int[ allocated + amount ];
        
        for(int i=size-1; i>=0; i--) {
            newEvents[i] = battleEvents[i];
            battleEvents[i] = null;
            newOffsets[i] = lineOffsets[i];
        }
        
        battleEvents = newEvents;
        lineOffsets = newOffsets;
        
        allocated += amount;
    }
    
    private BattleEvent getBattleEvent(int eventIndex) {
        
        return battleEvents[eventIndex];
    }
    
    private int getEventLineOffset(int eventIndex) {
        return lineOffsets[eventIndex];
    }
    
    private void setBattleEvent(int eventIndex, BattleEvent be) {
        battleEvents[eventIndex] = be;
    }
    
    private void setEventLineOffset(int eventIndex, int offset) {
        lineOffsets[eventIndex] = offset;
    }
    
    private void add(BattleEvent be) {
        if ( size == allocated ) expandArrays( size );
        
        battleEvents[size] = be;
        lineOffsets[size] = be.getMessageCount() + ((size == 0) ? purgedMessages : lineOffsets[size-1]);
        
        size++;
    }
    
    private int getEventStartLine(int eventIndex) {
        return ((eventIndex == 0) ? purgedMessages : lineOffsets[eventIndex-1]);
    }
    
    private int getEventEndLine(int eventIndex) {
        return lineOffsets[eventIndex] - 1;
    }
    
    private int getLastLine() {
        if (size > 0 ) {
            return lineOffsets[size - 1] - 1;
        } else {
            return purgedMessages - 1;
        }
    }
    
    private int getCachedMessageCount(int messageIndex) {
        return lineOffsets[messageIndex] - ((messageIndex == 0) ? 0 : lineOffsets[messageIndex-1]);
    }
    
    private void clearEvents() {
        for(int i=size-1; i>=0; i--) {
            battleEvents[i] = null;
            lineOffsets[i] = 0;
        }
        size = 0;
        
        purgedMessages = 0;
    }
    
    private void scheduleUpdate(int startPosition, int endPosition, int eventToScrollTo) {
        if ( eventToScrollTo != -1 ) {
            eventToScrollToNext = eventToScrollTo;
        }
        updateDelay.restart();
    }
    
    private void performUpdate() {
        revalidate();
        repaint();
        if ( eventToScrollToNext != -1 ) {
            scrollToEvent(eventToScrollToNext);
            eventToScrollToNext = -1;
        }
    }
    
    /**
     * Invoked when an action occurs.
     */
    public void actionPerformed(ActionEvent e) {
        performUpdate();
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
    
    public void paintComponent(Graphics g) {
        synchronized(Battle.currentBattle) {
            Rectangle clip = g.getClipBounds();
            Rectangle eventBounds = new Rectangle();
            
            super.paintComponent(g);
            
            int lineIndex;
            for(lineIndex = 0; lineIndex < purgedMessages; lineIndex++) {
                getPurgedMessageBounds(eventBounds, lineIndex);
                
                if ( eventBounds.intersects(clip) ) {
                    paintPurgedMessage(g, lineIndex, eventBounds);
                }
            }
            
            for(int eventIndex = 0; eventIndex < size; eventIndex++) {
                getEventBounds(eventBounds, eventIndex);
                
                if ( eventBounds.intersects(clip) ) {
                    paintEvent(g, eventIndex, eventBounds);
                }
            }
            
            getMinimumUndoableBounds(eventBounds);
            if ( eventBounds.intersects(clip) ) {
                paintMinimumUndoable(g, eventBounds);
            }
        }
    }
    
    private void paintPurgedMessage(Graphics g, int lineIndex, Rectangle eventBounds) {
        if ( lineIndex < purgedMessages ) {
            String message;
            int type;
            int stringWidth;
            
            g.setColor( doneBackgroundColor );
            
            g.fillRect( eventBounds.x, eventBounds.y, eventBounds.width, eventBounds.height);
            
            // Now Draw all of the messages...
            int x = eventBounds.x;
            int y = eventBounds.y;
            
            message = Battle.currentBattle.getPurgedMessageText(lineIndex);
            type = Battle.currentBattle.getPurgedMessageType(lineIndex);
            
            if ( isMessageSelected(lineIndex) ) {
                // This message is selected, so fill the line with the appropriate color and draw the
                // text in textSelectionColor.
                g.setColor( doneSelectionColor );
                
                stringWidth = SwingUtilities.computeStringWidth(fm, message);
                g.fillRect( eventBounds.x, y, stringWidth+1, fontHeight);
                
                if ( textSelectionColor != null ) {
                    g.setColor( textSelectionColor ) ;
                } else {
                    g.setColor( getColorForType(type, false).darker() ) ;
                }
                g.drawString(message, x, y + fontAscent);
            } else {
                g.setColor( getColorForType(type, false));
                g.drawString(message, x, y + fontAscent);
            }
        }
    }
    
    private void paintEvent(Graphics g, int eventIndex, Rectangle eventBounds) {
        
        BattleEvent be = getBattleEvent(eventIndex);
        int messageCount = be.getMessageCount();
        int currentLine;
        
        int realLineNumber = getEventStartLine(eventIndex);
        
        String message;
        int type;
        int stringWidth;
        
        boolean undone = (eventIndex >= Battle.currentBattle.getCompletedEventPosition());
        // First, draw background...
        if ( !undone ) {
            g.setColor( doneBackgroundColor );
        } else {
            g.setColor( undoneBackgroundColor );
        }
        
        g.fillRect( eventBounds.x, eventBounds.y, eventBounds.width, eventBounds.height);
        
        // Now Draw all of the messages...
        int x = eventBounds.x;
        int y = eventBounds.y;
        
        Iterator i = be.getMessageIterator();
        
        while ( i.hasNext() ) {
            // for(currentLine = 0; currentLine < messageCount; currentLine ++ ) {
            //  message = be.getMessageText(currentLine);
            // type = be.getMessageType(currentLine);
            MessagePair mp = (MessagePair) i.next();
            message = mp.getMessage();
            
            type = mp.getType();
            
            if ( isMessageSelected(realLineNumber) ) {
                // This message is selected, so fill the line with the appropriate color and draw the
                // text in textSelectionColor.
                if ( !undone ) {
                    g.setColor( doneSelectionColor );
                } else {
                    g.setColor( undoneSelectionColor );
                }
                
                stringWidth = SwingUtilities.computeStringWidth(fm, message);
                g.fillRect( eventBounds.x, y, stringWidth+1, fontHeight);
                
                if ( textSelectionColor != null ) {
                    g.setColor( textSelectionColor ) ;
                } else {
                    g.setColor( getColorForType(type, undone).darker() ) ;
                }
                if(message.contains("activates")) {
                	 Insets ii = getInsets();
                	 int yy = ii.top + purgedMessages * fontHeight;
                	 g.setColor( Color.RED );
                	 g.drawLine( ii.left, yy, getWidth() - ii.right, yy);;
               }
                g.drawString(message, x, y + fontAscent);
            } else {
            	 if(message.contains("activates") && !message.contains("deactivates")) {
            		 g.setColor( Color.BLACK);
            		 //g.drawString("  ", x, y + fontAscent);
            		 //y+=fontHeight;
            		 g.drawString("-----"+message+"------", x, y + fontAscent);
            	 }
            	 else {
            		 if(message.contains("hit") || message.contains("missed") || message.contains("knocked back") || message.contains("knocked down"))
            		 {
            			 g.setColor( new Color(5,162,16));
            		 }
            		 else{
            			 g.setColor( getColorForType(type, undone));
            		 }
            		 if(message.contains("took")) {
            			 int end = message.indexOf(" took ");
            			 String targetName = message.substring(0, end);
            			 
            			 
		    			Target t = new CharacterAdaptor(targetName).target; 
		    			if(t==null) {
		    				
		    				t = new PhysicalObjectAdapter(targetName).target;
		    			}
		    			
		    			if((t.stunned==true ||t.isUnconscious()==true) && message.contains(" STUN.")) {
		    				g.setColor( Color.RED);
		    				if(message.contains("and is ")) {
		    					end = message.indexOf("and is ");
		    					message = message.substring(0, end);	
		    				}
		    				message+="and is ";
			    			if(t.stunned==true){
			    				message+= "Stunned";
							}
			    			if(t.unconscious==true){
			    				message+= ", Unconsious";
							}	
		    			}
		    			if( (t.isDying()==true ||t.isDead()==true) && message.contains(" BODY.")) 
		    			{
		    				g.setColor( Color.RED);
		    				if(message.contains("and is ")) {
		    					end = message.indexOf("and is ");
		    					message = message.substring(0, end);	
		    				}
		    				message+="and is ";
			    			if(t.isDying()==true){
			    				message+= "Dying";
							}
			    			if(t.isDead()==true){
			    				message+= "Dead";
							}	
		    			}
		    		}
            		 g.drawString(message, x, y + fontAscent);
            	 }
        		 
            }
            
            y += fontHeight;
            realLineNumber++;
        }
    }
    
    private void paintMinimumUndoable(Graphics g, Rectangle bounds) {
        if ( purgedMessages != 0 ) {
            Insets i = getInsets();
            int y = i.top + purgedMessages * fontHeight;
            
            g.setColor( Color.RED );
            g.drawLine( i.left, y, getWidth() - i.right, y);
        }
    }
    
    /** Getter for property fontHeight.
     * @return Value of property fontHeight.
     */
    private int getFontHeight() {
        return fontHeight;
    }
    
    /** Setter for property fontHeight.
     * @param fontHeight New value of property fontHeight.
     */
    private void setFontHeight(int fontHeight) {
        this.fontHeight = fontHeight;
    }
    
    public Dimension getPreferredSize() {
        Insets i = getInsets();
        Dimension d = new Dimension();
        
        d.height = getEventStartLine(size) * fontHeight + i.top + i.bottom;
        Component c = getParent();
        if ( c instanceof JViewport ) {
            double width = c.getSize().getWidth() ;
            d.width = Math.max( maximumWidth + i.left + i.right,(int) width);
        } else {
            d.width = maximumWidth + i.left + i.right;
        }
        
        return d;
    }
    
    public Dimension getMinimumSize() {
        Insets i = getInsets();
        Dimension d = new Dimension();
        
        d.height = getEventStartLine(size) * fontHeight + i.top + i.bottom;
        d.width = maximumWidth + i.left + i.right;
        
        return d;
    }
    
    /** Getter for property maximumWidth.
     * @return Value of property maximumWidth.
     */
    public int getMaximumWidth() {
        return maximumWidth;
    }
    
    /** Setter for property maximumWidth.
     * @param maximumWidth New value of property maximumWidth.
     */
    public void setMaximumWidth(int maximumWidth) {
        this.maximumWidth = maximumWidth;
    }
    
    /** Getter for property undoneBackgroundColor.
     * @return Value of property undoneBackgroundColor.
     */
    public Color getUndoneBackgroundColor() {
        return undoneBackgroundColor;
    }
    
    /** Setter for property undoneBackgroundColor.
     * @param undoneBackgroundColor New value of property undoneBackgroundColor.
     */
    public void setUndoneBackgroundColor(Color undoneBackgroundColor) {
        this.undoneBackgroundColor = undoneBackgroundColor;
    }
    
    /** Getter for property fontAscent.
     * @return Value of property fontAscent.
     */
    public int getFontAscent() {
        return fontAscent;
    }
    
    /** Setter for property fontAscent.
     * @param fontAscent New value of property fontAscent.
     */
    public void setFontAscent(int fontAscent) {
        this.fontAscent = fontAscent;
    }
    
    private Color getColorForType(int type, boolean undone) {
        Color color = getForeground();
        if ( ! undone ) {
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
        } else {
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
            
         /*   if ( style == LINESTYLE ) {
                int total = 0;
                total += color.getRed() + color.getGreen() + color.getBlue();
                color = new Color( total/3, total/3, total/3 );
            } */
        }
        
        return color;
    }
    
    /** Getter for property doneBackgroundColor.
     * @return Value of property doneBackgroundColor.
     */
    public Color getDoneBackgroundColor() {
        return doneBackgroundColor;
    }
    
    /** Setter for property doneBackgroundColor.
     * @param doneBackgroundColor New value of property doneBackgroundColor.
     */
    public void setDoneBackgroundColor(Color doneBackgroundColor) {
        this.doneBackgroundColor = doneBackgroundColor;
    }
    
    public void setFont(Font f) {
        fm = getFontMetrics(f);
        
        setFontHeight( fm.getHeight() );
        setFontAscent( fm.getAscent() );
        
        revalidate();
    }
    
    private boolean isMessageSelected(int lineIndex) {
        return ( selectionModel != null && selectionModel.isSelectedIndex(lineIndex) );
    }
    
    /** Getter for property doneSelectionColor.
     * @return Value of property doneSelectionColor.
     */
    public Color getDoneSelectionColor() {
        return doneSelectionColor;
    }
    
    /** Setter for property doneSelectionColor.
     * @param doneSelectionColor New value of property doneSelectionColor.
     */
    public void setDoneSelectionColor(Color doneSelectionColor) {
        this.doneSelectionColor = doneSelectionColor;
    }
    
    /** Getter for property undoneSelectionColor.
     * @return Value of property undoneSelectionColor.
     */
    public Color getUndoneSelectionColor() {
        return undoneSelectionColor;
    }
    
    /** Setter for property undoneSelectionColor.
     * @param undoneSelectionColor New value of property undoneSelectionColor.
     */
    public void setUndoneSelectionColor(Color undoneSelectionColor) {
        this.undoneSelectionColor = undoneSelectionColor;
    }
    
    /** Getter for property textSelectionColor.
     * @return Value of property textSelectionColor.
     */
    public Color getTextSelectionColor() {
        return textSelectionColor;
    }
    
    /** Setter for property textSelectionColor.
     * @param textSelectionColor New value of property textSelectionColor.
     */
    public void setTextSelectionColor(Color textSelectionColor) {
        this.textSelectionColor = textSelectionColor;
    }
    
    /** Getter for property selectionModel.
     * @return Value of property selectionModel.
     */
    public ListSelectionModel getSelectionModel() {
        return selectionModel;
    }
    
    /** Setter for property selectionModel.
     * @param selectionModel New value of property selectionModel.
     */
    public void setSelectionModel(ListSelectionModel selectionModel) {
        if ( this.selectionModel != selectionModel ) {
            if (this.selectionModel != null ) {
                this.selectionModel.removeListSelectionListener(this);
            }
            
            this.selectionModel = selectionModel;
            
            if (this.selectionModel != null ) {
                this.selectionModel.addListSelectionListener(this);
            }
        }
    }
    
    /**
     * Invoked when the mouse exits a component.
     */
    public void mouseExited(MouseEvent e) {
    }
    
    /**
     * Invoked when a mouse button has been released on a component.
     */
    public void mouseReleased(MouseEvent e) {
    }
    
    /**
     * Invoked when a mouse button has been pressed on a component.
     */
    public void mousePressed(MouseEvent e) {
        if (  selectionModel != null ) {
            grabFocus();
            
            Point p = e.getPoint();
            
            int line = getLineForPoint(p);
            if ( line != -1 ) {
                selectionModel.setAnchorSelectionIndex(line);
            }
        }
    }
    
    /**
     * Invoked when the mouse has been clicked on a component.
     */
    public void mouseClicked(MouseEvent e) {
        if ( e.isPopupTrigger() == false && selectionModel.isSelectionEmpty() == false ) {
            selectionModel.clearSelection();
        }
    }
    
    /**
     * Invoked when the mouse enters a component.
     */
    public void mouseEntered(MouseEvent e) {
    }
    
    /**
     * Invoked when a mouse button is pressed on a component and then
     * dragged.  Mouse drag events will continue to be delivered to
     * the component where the first originated until the mouse button is
     * released (regardless of whether the mouse position is within the
     * bounds of the component).
     */
    public void mouseDragged(MouseEvent e) {
        if ( e.isPopupTrigger() == false && selectionModel != null ) {
            Point p = e.getPoint();
            
            int line = getLineForPoint(p);
            
            if ( line != -1 ) {
                int anchor = selectionModel.getAnchorSelectionIndex();
                
                scrollToLine(line);
                
                selectionModel.setSelectionInterval(anchor, line);
                if ( DEBUG > 0 ) System.out.println(selectionModel);
            }
        }
    }
    
    /**
     * Invoked when the mouse button has been moved on a component
     * (with no buttons no down).
     */
    public void mouseMoved(MouseEvent e) {
    }
    
    /**
     * Called whenever the value of the selection changes.
     * @param e the event that characterizes the change.
     */
    public void valueChanged(ListSelectionEvent e) {
        // This could be tightened up...
        scheduleUpdate(-1,-1, -1);
    }
    
    public boolean invokeMenu(JPopupMenu popup, Component inComponent, Point inPoint) {
        copyMenuItem.setEnabled( selectionModel.isSelectionEmpty() == false );
        popup.add(copyMenuItem);
        
        if ( Battle.debugLevel >= 1 ) {
            popup.add( new JSeparator() );
            
            boolean battleEventFound = false;
            
            int eventIndex = findEventForPoint(inPoint);
            if ( eventIndex != -1 ) {
                BattleEvent be = getBattleEvent(eventIndex);
                if ( be != null ) {
                    editEventAction.setBattleEvent(be);
                    battleEventFound = true;
                    
                    reprocessEventAction.setBattleEvent(be);
                }
            }
            
            editEventAction.setEnabled(battleEventFound);
            popup.add( editMenuItem );
            
            if ( battleEventFound && Battle.currentBattle.getCompletedEventPosition() > eventIndex ) {
                popup.add( new JMenuItem(reprocessEventAction));
            }
        }
        
        return true;
    }
    
    protected void copySelection() {
        if ( selectionModel.isSelectionEmpty() == false ) {
            int start = selectionModel.getMinSelectionIndex();
            int end = selectionModel.getMaxSelectionIndex();
            
            StringBuffer sb = new StringBuffer();
            
            for(int index = start; index <= end; index++) {
                if ( selectionModel.isSelectedIndex(index) ) {
                    String message = getMessageForLine(index);
                    if ( message != null ) {
                        sb.append( message );
                        sb.append( "\n" );
                    }
                }
            }
            
            Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
            clipboard.setContents( new StringSelection( sb.toString() ), null);
        }
    }
    
    
    
    private static class CopyAction extends AbstractAction {
        private MessagePanel panel;
        
        public CopyAction(MessagePanel panel) {
            super("Copy");
            this.panel = panel;
        }
        
        public void actionPerformed(ActionEvent e) {
            if ( panel != null ) {
                
                // There is stuff to copy...
                panel.copySelection();
                
            }
        }
    }
    
    private static class EditBattleEventAction extends AbstractAction {
        private BattleEvent battleEvent;
        
        public EditBattleEventAction() {
            super("Debug BattleEvent...");
        }
        
        public void actionPerformed(ActionEvent e) {
            if ( battleEvent != null ) {
                
                battleEvent.debugDetailList("Battle Event Debug");
            }
        }
        
        public void setBattleEvent(BattleEvent be) {
            battleEvent = be;
        }
    }
    
    private static class ReprocessEventAction extends AbstractAction {
        private BattleEvent battleEvent;
        
        public ReprocessEventAction() {
            super("Undo and Reprocess BattleEvent...");
        }
        
        public void actionPerformed(ActionEvent e) {
            if ( battleEvent != null ) {
                
                ReprocessBattleEvent rbe = new ReprocessBattleEvent(battleEvent);
                Battle.getCurrentBattle().addEvent(rbe);
            }
        }
        
        public void setBattleEvent(BattleEvent be) {
            battleEvent = be;
        }
    }
}
