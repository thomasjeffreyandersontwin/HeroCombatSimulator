/*
 * EffectPanel2.java
 *
 * Created on January 29, 2008, 9:55 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package champions;

import tjava.ContextMenu;
import tjava.ContextMenuListener;
import java.awt.Color;
import java.awt.Component;
import java.awt.Composite;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.Image;
import java.awt.Paint;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.RenderingHints.Key;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.font.FontRenderContext;
import java.awt.font.GlyphVector;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;
import java.awt.image.ImageObserver;
import java.awt.image.RenderedImage;
import java.awt.image.renderable.RenderableImage;
import java.text.AttributedCharacterIterator;
import java.util.List;
import java.util.Map;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.SwingUtilities;
import javax.swing.event.EventListenerList;

/**
 *
 * @author twalker
 */
public class EffectPanel2 extends JPanel implements ContextMenuListener {
    
    private List<Effect> effectList;
    private Rectangle[] bounds;
    private int selectedItem = -1;
    private Color selectionColor = Color.BLUE;
    private boolean multiline = false;
    private String title = null;
    
    private EventListenerList listenerList = new EventListenerList();
    
    
    
    public EffectPanel2() {
        addMouseListener(createMouseListener());
        ContextMenu.addContextMenu(this);
        setOpaque(false);
    }
    
    public void addActionListener(ActionListener listener) {
        listenerList.add(ActionListener.class, listener);
    }
    
    public void removeActionListener(ActionListener listener) {
        listenerList.remove(ActionListener.class, listener);
    }
    
    protected void fireActionEvent(Effect effect, int modifiers) {
        ActionEvent e = null;
        
        // Guaranteed to return a non-null array
        Object[] listeners = listenerList.getListenerList();
        // Process the listeners last to first, notifying
        // those that are interested in this event
        for (int i = listeners.length-2; i>=0; i-=2) {
            if (listeners[i]==ActionListener.class) {
                // Lazily create the event:
                if (e == null)
                    e = new ActionEvent(effect, 0, "clicked", modifiers);
                ((ActionListener)listeners[i+1]).actionPerformed(e);
            }
        }
    }
    
    public void paint(Graphics g) {
        
        if ( getEffectList() == null ) return;
        
        Graphics2D g2 = (Graphics2D)g;
        
        Dimension size = getSize();
        
        int startMargin = 0;
        
        
        
        Font normalFont = getFont();
        Font boldFont = normalFont.deriveFont(Font.BOLD);
        
        FontMetrics fm = null;
        
        FontMetrics fm1 = g.getFontMetrics(normalFont);
        FontMetrics fm2 = g.getFontMetrics(boldFont);
        
        int height = fm1.getHeight();
        
        int ascent = fm1.getAscent();
        int descent = fm1.getDescent();
        
        int x = startMargin;
        int y = ascent;
        
        if ( getTitle() != null ) {
            int titleWidth = fm1.stringWidth(getTitle());
            g2.setFont(normalFont);
            g2.setColor( getForeground() );
            
            g2.drawString(getTitle(), x, y);
            startMargin += titleWidth;
            x += titleWidth;
        }
        
        boolean comma = false;
        
        int commaWidth = fm1.stringWidth(", ");
        
        int i = 0;
        for(Effect effect : effectList ) {
            
            if ( comma ) {
                g2.setFont(normalFont);
                g2.drawString(", ",x,y);
                x += commaWidth;
            }
            
            
            if ( i == getSelectedItem() ) {
                g2.setColor( selectionColor );
            } else {
                Color c = effect.getEffectColor();
                if ( c == null ) c = getForeground();
                g2.setColor( c );
            }
            
            if ( effect.isCritical() ) {
                g2.setFont(boldFont);
                fm = fm2;
            } else {
                g2.setFont(normalFont);
                fm = fm1;
            }
            
            int width = fm.stringWidth(effect.getName());
            
            if ( isMultiline() && (x > size.width || (x + width + startMargin > size.width && y + height < size.height) ) ) {
                // Advance to the next line...
                x = startMargin;
                y += height;
                
                if ( y > size.height ) break;
            }
            
            g2.drawString(effect.getName(), x, y);
            bounds[i] = new Rectangle(x,y-ascent,width,ascent+descent);
            x += width;
            
            comma = true;
            i++;
        }
        
        for(; i < getEffectList().size(); i++ ) {
            bounds[i] = null;
        }
    }
    
    
    
    public Dimension getPreferredSize(Graphics2D g2, int maxWidth) {
        if ( effectList == null ) return super.getPreferredSize();
        
            if ( g2 == null ) return super.getPreferredSize();
            
            int startMargin = 0;
            
            int x = startMargin;
            int y = 0;
            
            int maxX = x;
            
            Font normalFont = getFont();
            Font boldFont = normalFont.deriveFont(Font.BOLD);
            
            FontMetrics fm = null;
            
            FontMetrics fm1 = g2.getFontMetrics(normalFont);
            FontMetrics fm2 = g2.getFontMetrics(boldFont);
            
            int height = fm1.getHeight();
            
            int ascent = fm1.getAscent();
            int descent = fm1.getDescent();
            
            y += ascent;
            
            if ( getTitle() != null ) {
                int titleWidth = fm1.stringWidth(getTitle());

                startMargin += titleWidth;
                x += titleWidth;
            }
            
            boolean comma = false;
            
            int commaWidth = fm1.stringWidth(", ");
            
            int i = 0;
            //System.out.println(effectList);
            for(Effect effect : effectList ) {
                if ( comma ) {
                    x += commaWidth;
                }
                
                if ( effect.isCritical() ) {
                    fm = fm2;
                } else {
                    fm = fm1;
                }
                
                int width = fm.stringWidth(effect.getName());
                
                if ( isMultiline() && x + width + startMargin > maxWidth ) {
                    // Advance to the next line...
                    //System.out.println("Advancing to next line.  x = " + (x + width + startMargin) + ", y = " + y + ", MW = " + maxWidth + ".");
                    maxX = Math.max(x,maxX);
                    x = startMargin;
                    y += height;
                    
                    
                }
                
                x += width;
                
                comma = true;
                i++;
            }
            
            maxX = Math.max(x,maxX);
            
            return new Dimension(maxX, y+descent);
    }
    
    public List<Effect> getEffectList() {
        return effectList;
    }
    
    public void setEffectList(List<Effect> effectList) {
        if ( this.effectList != effectList ){
            this.effectList = effectList;
            
            setSelectedItem(-1);
            
            if ( this.effectList == null ) {
                bounds = null;
            } else {
                bounds = new Rectangle[effectList.size()];
            }
        }
    }
    
    public MouseListener createMouseListener() {
        return new MouseListener() {
            public void mouseClicked(MouseEvent e) {
                
            }
            
            public void mousePressed(MouseEvent e) {
                if ( SwingUtilities.isLeftMouseButton(e) ) {
                    int item = getItemForPoint(e.getPoint());
                    
                    setSelectedItem(item);
                }
            }
            
            public void mouseReleased(MouseEvent e) {
                
                if ( getSelectedItem() != -1 ) {
                    boolean fire = false;
                    int item = getItemForPoint(e.getPoint());
                    
                    if ( item == getSelectedItem() ) {
                        fire = true;
                    }
                    setSelectedItem(-1);
                    
                    if ( fire ) {
                        fireActionEvent( effectList.get(item), e.getModifiers());
                    }
                }
            }
            
            public void mouseEntered(MouseEvent e) {
            }
            
            public void mouseExited(MouseEvent e) {
            }
        };
        
        
    }
    
    
    protected int getItemForPoint(Point p) {
        if ( bounds == null ) return -1;
        
        for(int i = 0; i < bounds.length; i++ ) {
            if ( bounds[i] != null && bounds[i].contains(p) ) return i;
        }
        return -1;
    }
    
    public int getSelectedItem() {
        return selectedItem;
    }
    
    public void setSelectedItem(int selectedItem) {
        if ( this.selectedItem != selectedItem ) {
            this.selectedItem = selectedItem;
            repaint();
        }
    }
    
    public boolean invokeMenu(JPopupMenu popup, Component inComponent, Point inPoint) {
        boolean rv = false;
        
        // Currently this doesn't work since the TreeTableEditorAdapter doesn't start
        // an editing session for a right click.  This is okay for now, but might be
        // important if I want to add context menus in the future.
        if ( inComponent == this ) {
            int item = getItemForPoint(inPoint);
            if ( item != -1 ) {
                
            }
        }
        
        return rv;
    }

    public boolean isMultiline() {
        return multiline;
    }

    public void setMultiline(boolean multiline) {
        this.multiline = multiline;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
    
}
