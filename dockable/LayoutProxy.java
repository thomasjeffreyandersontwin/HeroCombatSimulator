/*
 * LayoutProxy.java
 *
 * Created on January 3, 2001, 10:33 PM
 */

package dockable;

import java.awt.Rectangle;
import java.awt.Component;
/**
 *
 * @author  unknown
 * @version 
 */
public class LayoutProxy extends Object implements LayoutObject {

    private LayoutObject c;
    private Rectangle bounds = null;
    /** Creates new LayoutProxy */
    public LayoutProxy(LayoutObject c) {
        this.c = c;
    }

    public Rectangle getLayoutBounds() {
        if ( bounds == null ) {
            if ( c == null ) return new Rectangle();
            else return c.getLayoutBounds();
        }
        else {
            return bounds;
        }
    }
    
   public void setLayoutBounds(Rectangle r) {
        bounds = r;
    }
    
    public int getLayoutWidth() {
        return c.getLayoutWidth();
    }
    public int getLayoutHeight() {
        return c.getLayoutHeight();
    }
    public Component getLayoutComponent() {
        return c.getLayoutComponent();
    }
    
    public String toString() {
        Rectangle r = getLayoutBounds();
        return "LP:[" + c + "," + r.toString() + "]";
    }
    public LayoutObject getPreferredSibling() {
        return c.getPreferredSibling();
    }
    
    public boolean isVisible() {
        return true;
    }
}