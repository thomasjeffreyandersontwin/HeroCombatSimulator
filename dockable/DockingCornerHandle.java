/*
 * DockingCornerHandle.java
 *
 * Created on September 7, 2005, 10:07 AM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 */

package dockable;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.MediaTracker;
import java.awt.Toolkit;
import javax.swing.JPanel;

/**
 *
 * @author 1425
 */
public class DockingCornerHandle extends JPanel {
    
    static private Image handleImage;
    static private Dimension preferredSize = new Dimension(10,10);
    
    /** Creates a new instance of DockingCornerHandle */
    public DockingCornerHandle() {
        setupGraphics();
    }
    
    protected void setupGraphics() {
        if ( getHandleImage() == null ) {
            Image handle = Toolkit.getDefaultToolkit().createImage(getClass().getResource("/dockable/graphics/dockHandle.gif") );
            setHandleImage(handle);
        }
    }
    
    public void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D)g;

        int x = getX();
        int y = getY();

        if ( handleImage != null ) {
            g2.drawImage(handleImage,x,0,handleImage.getWidth(null),handleImage.getHeight(null),null);
        }
    }

    public static Image getHandleImage() {
        return handleImage;
    }

    public static void setHandleImage(Image aHandleImage) {
        handleImage = aHandleImage;
        
        preferredSize = getImageSize(aHandleImage);
    }
    
    static public Dimension getImageSize(Image image) {
        MediaTracker mt = new MediaTracker(new JPanel());
        mt.addImage(image,0);
        Dimension d = new Dimension();
        try {
            mt.waitForID(0);
            d.width = image.getWidth(null);
            d.height = image.getHeight(null);
        } catch ( InterruptedException ie )  {
            return null;
        }
        return d;
    }
    
    public Dimension getPreferredSize() {
        return preferredSize;
    }
    
    
}
