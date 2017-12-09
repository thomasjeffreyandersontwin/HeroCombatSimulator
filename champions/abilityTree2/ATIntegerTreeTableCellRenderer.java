/*
 * ATIntegerTreeTableCellRenderer.java
 *
 * Created on January 28, 2008, 11:29 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package champions.abilityTree2;

import java.awt.Color;
import java.awt.Font;
import treeTable.DefaultTreeTableCellRenderer;

/**
 *
 * @author twalker
 */
public class ATIntegerTreeTableCellRenderer extends DefaultTreeTableCellRenderer {
    private Color zeroColor = Color.RED;
    private int zeroFont = Font.BOLD;
    /** Creates a new instance of ATIntegerTreeTableCellRenderer */
    public ATIntegerTreeTableCellRenderer() {
    }

    protected Font getFontForValue(Object value) {
        Font f = super.getFontForValue(value);
        
        if ( value instanceof Integer ) {
            int v = (Integer)value;
            if ( v > 0 ) {
                return f;
            }
            else {
                return f.deriveFont(getZeroFont());
            }
        }
        else if ( value instanceof Double ) {
            double v = (Double) value;
            if ( v > 0 ) {
                return f;
            }
            else {
                return f.deriveFont(getZeroFont());
            }
        }
        else {
            return f;
        }
            
    }

    protected Color getForgroundForValue(Object value) {
        Color f = super.getForgroundForValue(value);
        
        if ( value instanceof Integer ) {
            int v = (Integer)value;
            if ( v > 0 ) {
                return f;
            }
            else {
                return getZeroColor();
            }
        }
        else if ( value instanceof Double ) {
            double v = (Double) value;
            if ( v > 0 ) {
                return f;
            }
            else {
                return getZeroColor();
            }
        }
        else {
            return f;
        }
    }

    public Color getZeroColor() {
        return zeroColor;
    }

    public void setZeroColor(Color zeroColor) {
        this.zeroColor = zeroColor;
    }

    public int getZeroFont() {
        return zeroFont;
    }

    public void setZeroFont(int zeroFont) {
        this.zeroFont = zeroFont;
    }
    
    


    

    
}
