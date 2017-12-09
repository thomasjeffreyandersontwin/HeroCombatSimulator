/*
 * LayoutObject.java
 *
 * Created on January 2, 2001, 11:09 PM
 */

package dockable;

import java.awt.*;
/**
 *
 * @author  unknown
 * @version 
 */
public interface LayoutObject {
    public Rectangle getLayoutBounds();
    public int getLayoutWidth();
    public int getLayoutHeight();
    public Component getLayoutComponent();
    public LayoutObject getPreferredSibling();
    public boolean isVisible();
}
