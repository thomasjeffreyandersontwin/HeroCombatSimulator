/*
 * AutoBypassButton.java
 *
 * Created on November 29, 2001, 7:46 PM
 */

package champions;

import tjava.MyPopupMenu;
import java.awt.event.*;
import javax.swing.*;
/**
 *
 * @author  twalker
 * @version
 */
public class AutoBypassButton extends JLabel
implements MouseListener {
    
    /** Holds value of property target. */
    private Target target;
    
    /** Holds value of property option. */
    private String option;
    
    private JPopupMenu menu;
    
    private Icon showPanelIcon, skipPanelIcon, defaultShowPanelIcon, defaultSkipPanelIcon;
    
    /** Creates new AutoBypassButton */
    public AutoBypassButton() {
        showPanelIcon = UIManager.getIcon("ProfileOption.trueIcon");
        skipPanelIcon = UIManager.getIcon("ProfileOption.falseIcon");
        defaultShowPanelIcon = UIManager.getIcon("ProfileOption.defaultTrueIcon");
        defaultSkipPanelIcon = UIManager.getIcon("ProfileOption.defaultFalseIcon");
        
        this.addMouseListener(this);
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
        this.target = target;
        buildMenu();
        setupIcon();
    }
    
    /** Getter for property option.
     * @return Value of property option.
     */
    public String getOption() {
        return option;
    }
    
    /** Setter for property option.
     * @param option New value of property option.
     */
    public void setOption(String option) {
        this.option = option;
        buildMenu();
        setupIcon();
    }
    
    public void reset() {
        menu = null;
        setTarget(null);
        setOption(null);
    }
    
    public void buildMenu() {
        if ( target != null && option != null && menu == null ) {
            menu = new MyPopupMenu();
            
            Action a;
            JMenuItem m;
            
            a = new AbstractAction("Always show this panel for " + getTarget().getName()) {
                public void actionPerformed(ActionEvent e) {
                    target.setBooleanProfileOption(option, true);
                    setupIcon();
                }
            };
            
            m = new JMenuItem(a);
            m.setIcon(showPanelIcon);
            
            menu.add(m);
            
            a = new AbstractAction("Always skip this panel for " + getTarget().getName()) {
                public void actionPerformed(ActionEvent e) {
                    target.setBooleanProfileOption(option, false);
                    setupIcon();
                }
            };
            
            m = new JMenuItem(a);
            m.setIcon(skipPanelIcon);
            
            menu.add(m);
            
            a = new AbstractAction("Show or Skip panel according to " + getTarget().getName() + "'s profile") {
                public void actionPerformed(ActionEvent e) {
                    target.unsetBooleanProfileOption(option);
                    setupIcon();
                }
            };
            
            m = new JMenuItem(a);
            Profile p = target.getRosterProfile();
            if ( p == null ) p = ProfileManager.getDefaultProfile();
            if ( p.getBooleanProfileOption(option) ) {
                m.setIcon(defaultShowPanelIcon);
            }
            else {
                m.setIcon(defaultSkipPanelIcon);
            }
            
            menu.add(m);
            
        }
    }
    
    public void setupIcon() {
        if ( target != null && option != null ) {
            if ( target.getProfileOptionIsSet(option) ) {
                if ( target.getBooleanProfileOption(option) ) {
                    setIcon(showPanelIcon);
                }
                else {
                    setIcon(skipPanelIcon);
                }
            }
            else {
                Profile p = target.getRosterProfile();
                if ( p == null ) p = ProfileManager.getDefaultProfile();
                if ( p.getBooleanProfileOption(option) ) {
                    setIcon(defaultShowPanelIcon);
                }
                else {
                    setIcon(defaultSkipPanelIcon);
                }
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
        if ( menu != null ) {
            menu.show(this, e.getX(), e.getY());
        }
    }
    
    /**
     * Invoked when the mouse has been clicked on a component.
     */
    public void mouseClicked(MouseEvent e) {
    }
    
    /**
     * Invoked when the mouse enters a component.
     */
    public void mouseEntered(MouseEvent e) {
    }
    
}
