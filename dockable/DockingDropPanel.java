/*
 * DockingDropPanel.java
 *
 * Created on January 5, 2001, 3:54 PM
 */

package dockable;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
/**
 *
 * @author  unknown
 * @version 
 */
public class DockingDropPanel extends javax.swing.JPanel {

    /** Creates new DockingDropPanel */
    public DockingDropPanel() {
        //this.setBorder( BorderFactory.createLineBorder( Color.RED, 2 ));
        this.setOpaque(false);
    }
    
    public void paintComponent(Graphics g) {
        Dimension d = getSize();
        
        Graphics2D g2 = (Graphics2D)g;
        
        g2.setColor(Color.RED);
        g2.setStroke( new BasicStroke(2));
        g2.drawRect(1,1, d.width-3, d.height-3);
    }

}