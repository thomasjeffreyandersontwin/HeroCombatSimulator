/*
 * HTMLButton.java
 *
 * Created on October 24, 2000, 3:19 PM
 */

package champions;

import tjava.HTMLColor;
import tjava.ContextMenu;
import tjava.ContextMenuListener;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.border.*;

import champions.interfaces.*;

/**
 *
 * @author  unknown
 * @version
 */
public class HTMLButton extends JButton 
implements ContextMenuListener {

    static private Color defaultPressedColor = new Color(85,26,139);
    static private Color defaultEnabledColor = new Color(0,0,238);
    /** Holds value of property pressedColor. */
    private Color pressedColor = null;
    /** Holds value of property shadowed. */
    private boolean shadowed = true;
    /** Holds value of property enabledColor. */
    private Color enabledColor = null;
    /** Creates new HTMLButton */
    public HTMLButton() {
        setBorder( new EmptyBorder(0,0,0,0) );
        setBorderPainted( false );
        setContentAreaFilled( false );    
        
        ContextMenu.addContextMenu(this);
    }

    protected void paintComponent(Graphics g) {

        
        FontMetrics fm = g.getFontMetrics();
        Insets insets = getInsets();
        int x = insets.left;
        int y = insets.top;
        int width = getWidth();
        int height = getHeight();

        int baseLine;

        if ( getVerticalAlignment() == SwingConstants.CENTER ) {
            baseLine = y + ( height - insets.top - insets.bottom - fm.getHeight() ) / 2 + fm.getAscent();
        }
        else if ( getVerticalAlignment() == SwingConstants.TOP ) {
            baseLine = y + fm.getAscent();
        }
        else  {
            baseLine = y + fm.getDescent() + insets.bottom;
        }

        String text = getText();

        g.setFont( getFont() );
        
        if ( this.isOpaque() ) {
            g.setColor(getBackground());
            g.fillRect(x, y, width - insets.left - insets.right, height - insets.top - insets.bottom);
        }

        if ( text != null ) {
            /* Draw the Text */
            if( model.isEnabled() ) {
                /*** paint the text normally */
                if ( model.isPressed() ) {
                    g.setColor( getPressedColor() );
                }
                else {
                    g.setColor( getEnabledColor());
                }
                g.drawString(text,x ,baseLine );
                if ( model.isArmed() ) {
                    g.drawLine( x, baseLine+ 1,
                    x + (int)fm.getStringBounds(getText(),g).getWidth() - 1, baseLine + 1);
                }

            }
            else {
                if ( isShadowed() ) {
                    /*** paint the text disabled ***/
                    g.setColor(getBackground().brighter());
                    g.drawString(text,x + 1, baseLine + 1);
                    g.setColor(getBackground().darker());
                    g.drawString(text,x, baseLine);
                }
                else {
                    g.setColor( getForeground() );
                    g.drawString(text,x, baseLine);
                }
            }
        }
    }

    /** Getter for property pressedColor.
     * @return Value of property pressedColor.
     */
    public Color getPressedColor() {
        Action action = getAction();
        if ( action instanceof HTMLColor ) {
            Color c = ((HTMLColor)action).getPressedColor();
            if ( c != null ) return c;
        }
        if ( pressedColor == null ) return defaultPressedColor;
        return pressedColor;
    }
    /** Setter for property pressedColor.
     * @param pressedColor New value of property pressedColor.
     */
    public void setPressedColor(Color pressedColor) {
        this.pressedColor = pressedColor;
    }

    public Dimension getPreferredSize() {
        Dimension d = super.getPreferredSize();
        FontMetrics fm = getFontMetrics( getFont() );
        d.height = fm.getHeight();
        return d;
    }

    public Dimension getMinimumSize() {
        Dimension d = super.getMinimumSize();
        FontMetrics fm = getFontMetrics( getFont() );
        d.height = fm.getHeight();
        return d;
    }
    /** Getter for property shadowed.
     * @return Value of property shadowed.
     */
    public boolean isShadowed() {
        return shadowed;
    }
    /** Setter for property shadowed.
     * @param shadowed New value of property shadowed.
     */
    public void setShadowed(boolean shadowed) {
        this.shadowed = shadowed;
    }
    /** Getter for property enabledColor.
     * @return Value of property enabledColor.
     */
    public Color getEnabledColor() {
        Action action = getAction();
        if ( action instanceof HTMLColor ) {
            Color c = ((HTMLColor)action).getEnabledColor();
            if ( c != null ) return c;
        }
        if ( enabledColor == null ) return defaultEnabledColor;
        return enabledColor;
    }
    /** Setter for property enabledColor.
     * @param enabledColor New value of property enabledColor.
     */
    public void setEnabledColor(Color enabledColor) {
         this.enabledColor = enabledColor;
    }

    public boolean invokeMenu(JPopupMenu popup,Component inComponent,Point inPoint) {
        Action action = getAction();
        if ( action != null && action instanceof ContextMenuListener ) {
            return ((ContextMenuListener)action).invokeMenu(popup, inComponent, inPoint);
        }
        else {
            return false;
        }
    }
}