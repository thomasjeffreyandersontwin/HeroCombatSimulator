/*
 * InlinePanel.java
 *
 * Created on January 26, 2001, 8:39 AM
 */

package champions.interfaces;

import champions.*;
import javax.swing.*;
import java.awt.*;
/**
 *
 * @author  unknown
 * @version 
 */
public interface InlinePanel {
    public Component getInlineComponent();
    public String getTitle();
    public String getInstructions();
    public Object[] getButtons();
    public void deactivateInline(InlineView inlineView, int value);
    public void activateInline(InlineView inlineView);
    public boolean buttonPressed(InlineView inlineView, int value);
    public boolean requiresScroll(InlineView inlineView);
}