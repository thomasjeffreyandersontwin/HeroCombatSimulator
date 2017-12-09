package dockable;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DragSource;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;
import javax.swing.Icon;
import javax.swing.JToggleButton;

public class DockingBarButton extends JToggleButton {
    
    
    private Color borderColor = new Color(122,152,175);
    private Color selectedColor = new Color(254,254,254);
    
    private DockingPanel panel;
    private int side;
    
    private static final int ICON_TEXT_GAP = 2;
    private static final int ICON_V_GAP = 2;
    private static final int H_GAP = 2;
    private static final int TEXT_V_GAP = 2;
    
    public DockingBarButton(DockingPanel panel, int side) {
        setPanel(panel);
        setSide(side);
        setText( panel.getName());
        
        setBorderPainted(false);
        setOpaque(false);
        
        setSelectedColor(new Color(254,254,254));
        
        setFont( Font.getFont("SanSerif-9") );
        
        setRolloverEnabled(true);
        
        createSource();
        
       // addMouseListener( new RolloverTriggerer());
    }
    
    protected void createSource() {
        // creating the recognizer is all thats necessary - it
        // does not need to be manipulated after creation
        DragSource dragSource = DragSource.getDefaultDragSource();
        dragSource.createDefaultDragGestureRecognizer(
                this, // component where drag originates
                DnDConstants.ACTION_MOVE, // actions
                panel); // drag gesture listener
    }
    
    public DockingPanel getPanel() {
        return panel;
    }
    
    public void setPanel(DockingPanel panel) {
        this.panel = panel;
    }
    
    @Override
    public void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D)g;
        
        Rectangle r = getBounds();
        
        if ( isOpaque() ) {
            g2.setColor( getBackground() );
            g2.fillRect(0, 0, r.width, r.height);
        }
        
//        g2.setColor(Color.RED);
//        g2.drawRect(0, 0, r.width-1, r.height-1);
        
        
        
        Shape border = new RoundRectangle2D.Float(0, 0, r.width-1, r.height-1,5,5);
        
        if ( isSelected() ) {
            g2.setColor(getSelectedColor());
            g2.fill( border);
            
            g2.setColor(getBorderColor());
            g2.draw( border );
        }
        
        
        if ( isRolloverEnabled() && getModel().isRollover() ) {
            g2.setColor(getBorderColor());
            g2.draw( border );
        }
        
    //    g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        AffineTransform at = (AffineTransform)g2.getTransform().clone();
        
        
        if ( side == DockingFrame.LEFT_SIDE ) {
            Dimension d = getSize();
            
            //AffineTransform transform = new AffineTransform();
            //g2.translate(0,d.height);
            g2.rotate(Math.toRadians(-90));//, d.width, d.height/2+10);
            g2.translate(-d.height,0);
            //g2.setTransform(transform);
            
          //  g2.rotate(Math.toRadians(-90));
         //  g2.translate(0, d.height);
        }
        else if ( side == DockingFrame.RIGHT_SIDE) {
            Dimension d = getSize();
            
            
            g2.rotate(Math.toRadians(90));//, d.width, d.height/2+10);
            g2.translate(0,-d.width);
        }
        
        
        float nextx = H_GAP;
        
        if ( getIcon() != null ) {
            getIcon().paintIcon(this, g2, (int)nextx, ICON_V_GAP);
            nextx += getIcon().getIconWidth() + ICON_TEXT_GAP;
        }
        
        FontMetrics fm = getFontMetrics(getFont());
        
        g2.setColor( getForeground() );
        g2.setFont( getFont() );
        float texty = 0;
        if ( side == DockingFrame.LEFT_SIDE || side == DockingFrame.RIGHT_SIDE ) {
            // Have to use width cause we are really centering horizonally
            texty = ( r.width + fm.getAscent() ) / 2 - 2;
        }
        else {
            texty = ( r.height + fm.getAscent() ) / 2 - 2;
        }
        if ( panel.getName() != null ) g2.drawString(panel.getName(), nextx, texty);
        
        
        g2.setTransform(at);
    }

    public Color getBorderColor() {
        return borderColor;
    }

    public void setBorderColor(Color aBorderColor) {
        borderColor = aBorderColor;
    }

    public Color getSelectedColor() {
        return selectedColor;
    }

    public void setSelectedColor(Color selectedColor) {
        this.selectedColor = selectedColor;
    }
    
//    public void setText(String text) {
//        super.setText(text);
//        calculateSize();
//    }
//    
//    public void setIcon(Icon icon) {
//        super.setIcon(icon);
//        calculateSize();
//    }
    
    @Override
    public Dimension getPreferredSize() {
        
        Dimension d = new Dimension(H_GAP*2,0);
        
        if ( panel.getName() != null ) {
            Font f = getFont();
            Graphics g = getGraphics();
            if ( g != null ) {
                //g = 
                FontMetrics fm = g.getFontMetrics(f);

                Rectangle2D r = fm.getStringBounds(panel.getName(),g);

                d.width += (int)r.getWidth();
                d.height += (int)r.getHeight() + TEXT_V_GAP*2;
            
            }
        }
        
        
        if ( getIcon() != null ) {
            d.width += getIcon().getIconWidth() + ICON_TEXT_GAP;
            d.height = Math.max(d.height, getIcon().getIconHeight() + ICON_V_GAP * 2);
        }
        
        if ( side == DockingFrame.LEFT_SIDE || side == DockingFrame.RIGHT_SIDE ) {
            d.setSize( d.height, d.width);
        }
        
        return d;
    }
    
    @Override
    public Icon getIcon() {
        if ( panel != null ) return panel.getPanelIcon();
        return null;
    }
    

 /*   public void dragDropEnd(java.awt.dnd.DragSourceDropEvent dragSourceDropEvent) {
    }

    public void dragEnter(java.awt.dnd.DragSourceDragEvent dragSourceDragEvent) {
    }

    public void dragExit(java.awt.dnd.DragSourceEvent dragSourceEvent) {
    }

    public void dragOver(java.awt.dnd.DragSourceDragEvent dragSourceDragEvent) {
    }

    public void dropActionChanged(java.awt.dnd.DragSourceDragEvent dragSourceDragEvent) {
    }*/
    
 /*   static public class RolloverTriggerer implements MouseListener {
        static java.util.Timer timer;
        
        public DockingPanel panel;
        
        protected TimerTask timerTask;
        
        public RolloverTriggerer(DockingPanel panel) {
            if ( timer == null ) timer = new java.util.Timer("DockingBarButtonTimer");
            
            this.panel = panel;
        }
        
        public void mouseClicked(java.awt.event.MouseEvent mouseEvent) {
        }

        public void mouseEntered(java.awt.event.MouseEvent mouseEvent) {
        }

        public void mouseExited(java.awt.event.MouseEvent mouseEvent) {
        }

        public void mousePressed(java.awt.event.MouseEvent mouseEvent) {
        }

        public void mouseReleased(java.awt.event.MouseEvent mouseEvent) {
        }
        
        protected void startTimer() {
            if ( timerTask == null ) {
                timerTask = new TimerTask() {
                    public void run() {
                        
                    }
                }
            }
        }
        
        protected void stopTimer() {
            
        }
        
        protected void showPanel() {
            
        }
        
        protected void hidePanel() {
            
        }
        
    }*/

    public int getSide() {
        return side;
    }

    public void setSide(int side) {
        this.side = side;
    }
}

