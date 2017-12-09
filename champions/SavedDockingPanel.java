/*
 * SavedDockingPanel.java
 *
 * Created on January 8, 2001, 9:54 PM
 */

package champions;

import dockable.DockingFrame;
import dockable.DockingPanel;
import dockable.DockingPanelListener;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import javax.swing.Icon;
import javax.swing.UIManager;

/**
 *
 * @author  trewalke
 * @version
 */
public class SavedDockingPanel extends DockingPanel implements DockingPanelListener, ComponentListener {
    
    protected String windowID;
    /** Creates new SavedDockingPanel */
    public SavedDockingPanel(String id) {
        this.windowID = id;
        
        Rectangle bounds = getPreferenceBounds();
        Double frameID = Preferences.getPreferenceList().getWindowID( id );
        Point p = getPreferenceLocation();
        setLayoutBounds( bounds );
        DockingFrame df = dockIntoFrame(frameID,p);
        
        if ( getPreferenceMinimized() ) {
            setPreferredMinimizedLocation( getPreferenceMinimzedLocation() );
            setMinimized(true);
            setPreferredSize( getPreferenceMinimizedSize() );
        }
        
        Icon icon = UIManager.getIcon("WindowIcon." + id);
        if ( icon != null ) {
            setPanelIcon(icon);
        }
        
        this.addDockingPanelListener( this );
        df.addComponentListener( this );
    }
    
    public void layoutChanged(javax.swing.event.ChangeEvent e) {
        if ( SavedDockingPanel.this.windowID != null ) {
            Double d = null;
            Point p = null;
            //System.out.println( "Setting Prefs for " + dp + " to " + dp.getLayoutBounds() );
            //Preferences.getPreferenceList().add(  windowID, "BOUNDS", getLayoutBounds(), true);
            Preferences.getPreferenceList().setWindowBounds(windowID, getLayoutBounds() );
            
            if ( getDockingFrame() != null ) {
                d = getDockingFrame().getFrameID();
                p = getDockingFrame().getLocation();
                
                if ( isMinimized() ) {
                    int minimizedLocation = getMinimizedLocation();
                    Dimension preferredSize = getSize();
                    Preferences.getPreferenceList().setWindowMinimized(windowID, true); //add( windowID, "MINIMIZED", "TRUE", true );
                    Preferences.getPreferenceList().setWindowMinimizeSide(windowID, minimizedLocation); //add( windowID, "MINIMIZEDSIDE", new Integer(minimizedLocation), true );
                    Preferences.getPreferenceList().setWindowMinimizedSize(windowID, preferredSize);   //add( windowID, "PREFERREDSIZE", preferredSize, true);
                } else {
                    Preferences.getPreferenceList().setWindowMinimized(windowID, false); //add( windowID, "MINIMIZED", "FALSE", true );
                }
            }
        }
    }
    
    public void frameChanged(DockingPanel panel, DockingFrame oldFrame, DockingFrame newFrame) {
        if ( oldFrame != null ) {
            oldFrame.removeComponentListener(this);
        }
        
        if ( newFrame != null ) {
            newFrame.addComponentListener(this);
            
            Preferences.getPreferenceList().setWindowID(windowID, newFrame.getFrameID());
        } else {
            
        }
    }
    
    
    protected Rectangle getPreferenceBounds() {
        Rectangle bounds = (Rectangle)Preferences.getPreferenceList().getWindowBounds(windowID);
        return (bounds!=null)?bounds:new Rectangle(0,0,100,100);
    }
    
    protected Point getPreferenceLocation() {
        Point p = (Point)Preferences.getPreferenceList().getWindowLocation(windowID);
        return (p!=null)?p:new Point(0,0);
    }
    
    protected boolean getPreferenceMinimized() {
        return Preferences.getPreferenceList().isWindowMinimized(windowID);
    }
    
    protected int getPreferenceMinimzedLocation() {
        Integer p = Preferences.getPreferenceList().getWindowMinimizedSide(windowID);
        return (p!=null)?p.intValue():DockingFrame.BOTTOM_SIDE;
    }
    
    protected Dimension getPreferenceMinimizedSize() {
        Dimension size = (Dimension)Preferences.getPreferenceList().getWindowMinimizedSize(windowID);
        return (size!=null)?size:new Dimension(100,100);
    }
    
    public void componentResized(ComponentEvent e) {
    }
    
    public void componentMoved(ComponentEvent e) {
        if ( getDockingFrame() != null ) {
            //Double d = getDockingFrame().getFrameID();
            Point p = getDockingFrame().getLocation();
            
            Preferences.getPreferenceList().setWindowLocation(windowID, p);
        }
    }
    
    public void componentShown(ComponentEvent e) {
    }
    
    public void componentHidden(ComponentEvent e) {
    }
    
}