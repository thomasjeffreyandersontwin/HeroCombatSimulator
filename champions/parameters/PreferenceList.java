/*
 * PanelParameterList.java
 *
 * Created on September 24, 2007, 3:40 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package champions.parameters;

import champions.parameters.ParameterList.ParameterEntry;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import treeTable.TreeTableColumnModel;

/**
 *
 * @author twalker
 */
public class PreferenceList extends ParameterList implements Serializable {
    
    static final long serialVersionUID = 5123831234342707402L;
    
    List<String> panels = new ArrayList<String>();
    
    /** Creates a new instance of PanelParameterList */
    public PreferenceList() {
    }
    
    protected ParameterEntry createParameterEntry(Parameter parameter, boolean visible, boolean enabled) {
        return new PanelParameterEntry(parameter,visible, enabled);
    }
    
    public String getParameterPanel(String name) {
        return ((PanelParameterEntry)getParameterEntry(name)).panel;
    }
    
    public int getPanelCount() {
        return panels.size();
    }
    
    public String getPanel(int index) {
        return panels.get(index);
    }
    
    public void setPanel(String parameterName, String panelName) {
        PanelParameterEntry pp = (PanelParameterEntry)getParameterEntry(parameterName);
        
        if ( pp != null ) {
            pp.panel = panelName;
            
            if ( panels.contains(panelName) == false ) {
                panels.add(panelName);
            }
        }
    }
    
    public void setHelpText(String parameterName, String helpText) {
        PanelParameterEntry pp = (PanelParameterEntry)getParameterEntry(parameterName);
        
        if ( pp != null ) {
            pp.helpText = helpText;
        }
    }
    
    
    public String getHelpText(String parameterName) {
        return ((PanelParameterEntry)getParameterEntry(parameterName)).helpText;
    }
    
    protected static class PanelParameterEntry extends ParameterList.ParameterEntry implements Serializable {
        
        public String panel;
        public String helpText;
        
        public PanelParameterEntry(Parameter parameter, boolean visible, boolean enabled) {
            super(parameter,visible,enabled);
        }
    }
    
    public void setWindowBounds(String window, Rectangle bounds) {
        if ( window != null ) {
            String parameterName = window + "." + "BOUNDS";
            if ( parameters.containsKey(parameterName) == false) {
                addObjectParameter(parameterName, parameterName, "Preference Window Bounds", null );
                setVisible(parameterName, false);
            }
            
            setParameterValue(parameterName, bounds);
        }
    }
    
    public Rectangle getWindowBounds(String window) {
        if ( window != null ) {
            String parameterName = window + ".BOUNDS";
            if ( parameters.containsKey(parameterName) )
                return (Rectangle) getParameterValue(parameterName);
        }
        return null;
    }
    
    public void setColumnModel(String parameterName, TreeTableColumnModel columnModel) {
        if ( parameterName != null ) {
            if ( parameters.containsKey(parameterName) == false) {
                addObjectParameter(parameterName, parameterName, "Preference Window Bounds", null );
                setVisible(parameterName, false);
            }
            
            setParameterValue(parameterName, columnModel);
        }
    }
    
    public TreeTableColumnModel getColumnModel(String parameterName) {
        if ( parameters.containsKey(parameterName) ) {
            return (TreeTableColumnModel)getParameterEntry(parameterName).value;
        }
        return null;
    }
    
    public Double getWindowID(String windowName) {
        if ( windowName != null ) {
            String parameterName = windowName + ".FrameID";
            if ( parameters.containsKey(parameterName) )
                return (Double) getParameterValue(parameterName);
        }
        return null;
    }
    
    public void setWindowID(String windowName, double frameID) {
        if ( windowName != null ) {
            String parameterName = windowName + ".FrameID";
            if ( parameters.containsKey(parameterName) == false ) {
                addDoubleParameter(parameterName, parameterName, "Window's Frame ID", null, 0.0);
            }
            
            setParameterValue(parameterName, frameID);
        }
    }
    
    public Point getWindowLocation(String windowName) {
        if ( windowName != null ) {
            String parameterName = windowName + ".FRAMELOCATION";
            if ( parameters.containsKey(parameterName) )
                return (Point) getParameterValue(parameterName);
        }
        return null;
    }
    
    public void setWindowLocation(String windowName, Point location) {
        if ( windowName != null ) {
            String parameterName = windowName + ".FRAMELOCATION";
            if ( parameters.containsKey(parameterName) == false ) {
                addObjectParameter(parameterName, parameterName, "Window's location", null);
            } else {
                setParameterValue(parameterName, location);
            }
        }
    }
    
    public boolean isWindowMinimized(String windowName) {
        if ( windowName != null ) {
            String parameterName = windowName + ".MINIMIZED";
            if ( parameters.containsKey(parameterName) )
                return (Boolean) getParameterValue(parameterName);
        }
        return false;
    }
    
    public void setWindowMinimized(String windowName, boolean minimized) {
        if ( windowName != null ) {
            String parameterName = windowName + ".MINIMIZED";
            if ( parameters.containsKey(parameterName) == false ) {
                addBooleanParameter(parameterName, parameterName, "Window's location", minimized);
            } else {
                setParameterValue(parameterName, minimized);
            }
        }
    }
    
    public int getWindowMinimizedSide(String windowName) {
        if ( windowName != null ) {
            String parameterName = windowName + ".MINIMIZEDSIDE";
            if ( parameters.containsKey(parameterName) )
                return (Integer) getParameterValue(parameterName);
        }
        return 0;
    }
    
    public void setWindowMinimizeSide(String windowName, int side) {
        if ( windowName != null ) {
            String parameterName = windowName + ".MINIMIZEDSIDE";
            if ( parameters.containsKey(parameterName) == false ) {
                addIntegerParameter(parameterName, parameterName, "Window's location", null);
            } else {
                setParameterValue(parameterName, side);
            }
        }
    }
    
    public void setWindowMinimizedSize(String window, Dimension size) {
        if ( window != null ) {
            String parameterName = window + "." + "PREFERREDSIZE";
            if ( parameters.containsKey(parameterName) == false) {
                addObjectParameter(parameterName, parameterName, "Preference Window Bounds", null );
                setVisible(parameterName, false);
            }
            
            setParameterValue(parameterName, size);
        }
    }
    
    public Dimension getWindowMinimizedSize(String window) {
        if ( window != null ) {
            String parameterName = window + ".PREFERREDSIZE";
            if ( parameters.containsKey(parameterName) )
                return (Dimension) getParameterValue(parameterName);
        }
        return null;
    }
    
}
