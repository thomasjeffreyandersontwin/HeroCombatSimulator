/*
 * DockingFrame.java
 *
 * Created on January 1, 2001, 12:04 PM
 */

package dockable;

import java.awt.AWTEvent;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.datatransfer.DataFlavor;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.dnd.DropTargetListener;
import java.awt.event.AWTEventListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.event.EventListenerList;
import tjava.Destroyable;
import tjava.PartialWeakMap;

/**
 *
 * @author  unknown
 * @version
 */
public class DockingFrame extends JFrame implements DropTargetListener, Iterable<DockingPanel> {
    
    private JPanel dropWatcher;
    /** Holds value of property dockingPanels. */
    private static final int DEBUG = 0;
    
    /**
     *
     */
    public  static final int TOP_SIDE = 0;
    /**
     *
     */
    public static final int LEFT_SIDE = 1;
    /**
     *
     */
    public static final int RIGHT_SIDE = 2;
    /**
     *
     */
    public static final int BOTTOM_SIDE = 3;
    
    private Cursor[] dragCursor;
    
    /**
     *
     */
    protected Set<DockingPanel> dockingPanels = new HashSet<DockingPanel>();
    private EventListenerList listenerList = new EventListenerList();
    /**
     *
     */
    protected static PartialWeakMap registeredDockingFrames = new PartialWeakMap();
    private Double frameID = null;
    
    // Dragging information
    /**
     *
     */
    public static DataFlavor dockingPanelDataFlavor = new DataFlavor(DockingPanelIndex.class, "DockingPanelIndex");
    private Rectangle draggingSourceBounds = null;
    private Rectangle draggingDestinationBounds = null;
    private DockingPanel draggingSource = null;
    private DockingPanel draggingDestination = null;
    private int draggingSide;
    private DockingDropPanel dockingDropPanel;
    /**
     *
     */
    protected  DockingDropDestination dropDestination;
    
    // This is only protected so other classes can check if there is a docking bar at 
    // the indicated location...
    /**
     *
     */
    protected DockingBar[] dockingBars = new DockingBar[4];
    
    private Component currentLayoutComponent;
    
    private JPanel minimizedLayerPanel;
    private JPanel centerPanel;
    
    /** Creates new DockingFrame */
    public DockingFrame() {
        super();
        setupFrame();
        
        registerFrame();
        hideDropWatcher();
    }
    
    /**
     *
     * @param id
     */
    public DockingFrame(Double id) {
        super();
        if ( id != null ) frameID = id;
        setupFrame();
        
        registerFrame();
        hideDropWatcher();
    }
    
    /**
     *
     * @param p
     */
    public DockingFrame(Point p) {
        this();
        if ( p != null ) {
            this.setLocation(p);
        }
    }
    
    /**
     *
     * @param p
     * @param id
     */
    public DockingFrame(Point p, Double id) {
        this(id);
        
        if ( p != null ) {
            this.setLocation(p);
        }
    }


    
    
    /**
     *
     */
    public void setupFrame() {
        dropWatcher = new JPanel();
        dropWatcher.setOpaque(false);
        //dropWatcher.setBorder( BorderFactory.createLineBorder( Color.red ) );
        dropWatcher.setBounds(0,0,getWidth(), getHeight());
        getLayeredPane().add(dropWatcher,new Integer( 300 ) );
        new DropTarget(dropWatcher,DnDConstants.ACTION_MOVE,this);
        
   //     dockingDropPanel = new DockingDropPanel();
   //     dockingDropPanel.setVisible(false);
   //     getLayeredPane().add(dockingDropPanel, new Integer(299));
        
        getContentPane().setLayout( new BorderLayout() );
        
        centerPanel = new JPanel();
        centerPanel.setLayout( new BorderLayout() );
        getContentPane().add(centerPanel, BorderLayout.CENTER);
        
        pack();
    }
    
    /**
     *
     */
    protected void registerFrame() {
        registeredDockingFrames.put( getFrameID(), this);
        
    }
    
    /** Finds a frame, based on frame ID, from all known docking frames.
     *
     */
    static DockingFrame findFrame(Double frameID) {
        if ( registeredDockingFrames.containsKey(frameID) ) {
            return (DockingFrame)registeredDockingFrames.get(frameID);
        } else {
            return null;
        }
    }
    
    /** Returns the Unique ID associated with this DockingFrame.
     *
     * @return
     */
    public Double getFrameID() {
        if ( frameID == null ) {
            frameID = Math.random();
        }
        return frameID;
    }
    
    /** Docks the indicated panel into the frame.
     *
     * This method should not be called directly.  It is called by the
     * appropriate methods in DockingPanel when necessary.
     *
     * @param dp
     * @param p
     */
    protected void dockPanel(DockingPanel dp, Point p) {
        if ( DEBUG > 0 ) System.out.println( "Adding " + dp + " to " + this );
        
        if ( p != null ) {
            dp.setLocation(p);
        }
        if ( dockingPanels.contains(dp) == false ) {
            //  System.out.println( "Adding " + dp.toString() + " to " + this.toString() );
            dockingPanels.add(dp);
            addDockingFrameListener(dp);
            
            if ( dp.getJMenuBar() != null ) {
                setJMenuBar(dp.getJMenuBar());
                
            }
        }
        
        updateLayouts();
        
        fireFrameChange();
    }
    
    /** Docks the indicated panel into the frame.
     *
     * This method should not be called directly.  It is called by the
     * appropriate methods in DockingPanel when necessary.
     *
     * @param dp
     */
    protected  void dockPanel(DockingPanel dp) {
        if ( DEBUG > 0 ) System.out.println( "Adding " + dp + " to " + this );
        if ( dockingPanels.contains(dp) == false ) {
            //  System.out.println( "Adding " + dp.toString() + " to " + this.toString() );
            dockingPanels.add(dp);
            addDockingFrameListener(dp);
            // addWindowListener(dp);
            
            if ( dp.getJMenuBar() != null ) {
                setJMenuBar(dp.getJMenuBar());
            }
        }
        
        updateLayouts();
        
        fireFrameChange();
    }
    
    
    /** Undocks the indicated panel from the frame.
     *
     * This method should not be called directly.  It is called by the
     * appropriate methods in DockingPanel when necessary.
     *
     * @param dp
     */
    protected  void undockPanel(DockingPanel dp) {
        if ( DEBUG > 0 ) System.out.println( "Removing " + dp + " to " + this );
        if ( dockingPanels.contains(dp) ) {
            dockingPanels.remove(dp);
            removeDockingFrameListener(dp);
            //    removeWindowListener(dp);
            
            if ( dp.getJMenuBar() == getJMenuBar() ) {
                setJMenuBar(null);
            }
            
            
            if ( dockingPanels.size() == 0 ) {
                this.dispose();
            } else {
                updateLayouts();
            }
            
            fireFrameChange();
            
            
        }
        else {
            // Check for minimize panel too...
            if ( minimizedLayerPanel != null ) minimizedLayerPanel.remove(dp);
            for(int i = 0; i < 4; i++) {
                if ( dockingBars[i] != null && dockingBars[i].hasPanel(dp)) {
                    getDockingBar(i).removePanel(dp);
                    break;
                }
            }
        }
    }
    
    /**
     *
     */
    protected void updateLayouts() {
        if ( isVisible() ) {
            setupBorder() ;
            
            for(int i = 0; i < 4; i++) {
                if ( dockingBars[i] != null ) {
                    if( dockingBars[i].hasPanels() ) {
                        dockingBars[i].setVisible(true);
                    } else {
                        dockingBars[i].setVisible(false);
                    }
                }
            }
            
            layoutPanels();
            //
            getContentPane().validate();
            repaint();
            //pack();
            resetPanelLayouts();
        }

        updateTitle();
    }
    
    /** Sets the panel to be minimized.
     *
     *  This method is called by DockingPanel and drag code when the panel 
     *  is either minimized or switches minimization locations.
     * @param panel
     * @param location
     */
    protected void minimizePanel(DockingPanel panel, int location) {
        if ( dockingPanels.contains(panel) ) {
            resetPanelLayouts();
            
            dockingPanels.remove(panel);
            
            if(panel.getParent()!=null) {
                panel.getParent().remove(panel);
            }
            
            Rectangle layoutBounds = panel.getLayoutBounds();
            panel.setPreferredSize( new Dimension (layoutBounds.width, layoutBounds.height) );
            
            panel.setVisible(false);
            
            getDockingBar(location).addPanel(panel);
            
            panel.fireLayoutChanged();
            
            updateLayouts();
            
            fireFrameChange();
        }
        else {
            for(int i = 0; i < 4; i++) {
                if ( dockingBars[i] != null && dockingBars[i].hasPanel(panel) && i != location) {
                    getDockingBar(i).removePanel(panel);
                    getDockingBar(location).addPanel(panel);
                    
                    panel.fireLayoutChanged();
            
                    updateLayouts();
            
                    fireFrameChange();
                    
                    break;
                }
            }
        }
        
        if ( panel.isMinimized() != true ) panel.setMinimized(true,false);
    }
    
    /** Sets the panel to be minimized.
     *
     *  This method is called by DockingPanel and drag code when the panel 
     *  is either minimized or switches minimization locations.
     * @param panel
     */
    protected void maximizePanel(DockingPanel panel) {
        if ( !dockingPanels.contains(panel) ) {
            for(int i = 0; i < 4; i++) {
                if ( dockingBars[i] != null && dockingBars[i].hasPanel(panel)) {
                    getDockingBar(i).removePanel(panel);
                    break;
                }
            }
            if ( minimizedLayerPanel != null ) minimizedLayerPanel.remove(panel);

            dockingPanels.add(panel);
            if ( panel.isMinimized() != false ) panel.setMinimized(false,false);
            panel.setVisible(true);
            
            updateLayouts();

            fireFrameChange();
            
            updateMinimizedLayerPanelVisibility();
        }
    }
    
    /**
     *
     * @param location
     * @return
     */
    protected DockingBar getDockingBar(int location) {
        if ( dockingBars[location] == null ) {
            dockingBars[location] = new DockingBar(this, location);
            switch (location) {
                case TOP_SIDE:
                    getContentPane().add(dockingBars[location], BorderLayout.NORTH);
                    break;
                case BOTTOM_SIDE:
                    getContentPane().add(dockingBars[location], BorderLayout.SOUTH);
                    break;
                case LEFT_SIDE:
                    getContentPane().add(dockingBars[location], BorderLayout.WEST);
                    break;
                case RIGHT_SIDE:
                    getContentPane().add(dockingBars[location], BorderLayout.EAST);
                    break;
            }
        }
        
        return dockingBars[location];
    }
    
    /**
     *
     * @param panel
     */
    protected void showMinimizedPanel(DockingPanel panel) {
        
        if ( panel.isVisible() == false ) {
        
        DockingBar db = getMinimizedPanelDockingBar(panel);
        
        int location = db.getBarLocation();
        
        JPanel mlp = getMinimizedLayerPanel();
        
        Dimension pref = panel.getPreferredSize();
        Dimension min = panel.getMinimumSize();
        
        Dimension dp = new Dimension( pref.width, pref.height);
        
        
       // Rectangle dbb = db.getBounds();
        Rectangle ddf = mlp.getBounds();
        
        mlp.add(panel);
        
        switch(location) {
            case DockingFrame.TOP_SIDE:
                int top = 0;
                panel.setBounds(0, 0, Math.min((int)dp.getWidth(), ddf.width), Math.min((int)dp.getHeight(), ddf.height));
                break;
            case DockingFrame.BOTTOM_SIDE:
                int height = Math.min(ddf.height, (int)dp.getHeight());
                panel.setBounds(0, ddf.height - height, Math.min(ddf.width, (int)dp.getWidth()), height);
                break;
            case DockingFrame.LEFT_SIDE:
                int left = 0;
                panel.setBounds(0, 0, Math.min((int)dp.getWidth(), ddf.width), Math.min((int)dp.getHeight(), ddf.height));
                break;
            case DockingFrame.RIGHT_SIDE:
                int width = Math.min(ddf.width-2, (int)dp.getWidth());
                panel.setBounds(ddf.width - width, 0, width, Math.min((int)dp.getHeight(), ddf.height));
                break;
        }
        
        db.setPanelVisible(panel, true);
        mlp.setComponentZOrder(panel, 0);
        
        mlp.setVisible(true);
        
        panel.setVisible(true);
        panel.requestFocusInWindow();
        
        panel.revalidate();
        panel.repaint();
        }
    }
    
    /**
     *
     * @param panel
     */
    protected void hideMinimizedPanel(DockingPanel panel) {
        panel.setVisible(false);
        
        DockingBar db = getMinimizedPanelDockingBar(panel);
        if ( db != null ) db.setPanelVisible(panel, false);
        
        updateMinimizedLayerPanelVisibility();
        
    }
    
    /**
     *
     */
    protected void hideAllMinimizedPanels() {
        for(int i = 0; i < 4; i++) {
            if ( dockingBars[i] != null ) {
                for(int j = 0; j < dockingBars[i].getPanelCount(); j++) {
                    hideMinimizedPanel(dockingBars[i].getPanel(j));
                }
            }
        }
    }
    
    /**
     *
     */
    protected void updateMinimizedLayerPanelVisibility() {
        
        JPanel mlp = getMinimizedLayerPanel();
        
        boolean visible = false;
        for(int i = 0; i < mlp.getComponentCount(); i++) {
            if ( mlp.getComponent(i).isVisible() ) {
                visible = true;
                break;
            }
        }
        
        if ( visible == false ) {
            mlp.setVisible(false);
        }
    }
    
    /**
     *
     * @return
     */
    protected JPanel getMinimizedLayerPanel() {
        if ( minimizedLayerPanel == null ) {
            minimizedLayerPanel = new JPanel() {
                @Override
                public boolean isOptimizedDrawingEnabled() {
                    return false;
                }
            };
            minimizedLayerPanel.setOpaque(false);
            minimizedLayerPanel.setLayout(null);
            getLayeredPane().add(minimizedLayerPanel, JLayeredPane.PALETTE_LAYER);
            
            Toolkit.getDefaultToolkit().addAWTEventListener( new MinimizedLayerPanelMouseListener(),
                                AWTEvent.MOUSE_EVENT_MASK |
                                AWTEvent.MOUSE_MOTION_EVENT_MASK |
                                AWTEvent.MOUSE_WHEEL_EVENT_MASK);
            
            //minimizedLayerPanel.setBorder( new LineBorder(Color.RED, 2));
            
            centerPanel.addComponentListener( new MinimizedLayerPanelComponentListener() );
            
        }
        
        
        updateMinimizedLayerPanelBounds();
        
        return minimizedLayerPanel;
    }
    
    /**
     *
     */
    protected void updateMinimizedLayerPanelBounds() {
        if ( minimizedLayerPanel != null ) {
            Point p = getContentPane().getLocation();
            Rectangle r = centerPanel.getBounds();
            minimizedLayerPanel.setBounds(p.x+r.x, p.y+r.y, r.width, r.height  );
        }
    }
    
    /**
     *
     * @param panel
     * @return
     */
    protected DockingBar getMinimizedPanelDockingBar(DockingPanel panel) {
        for(int i = 0; i < 4; i++) {
            if ( dockingBars[i] != null && dockingBars[i].hasPanel(panel) ) {
                return dockingBars[i];
            }
        }
        return null;
    }
    
    /**
     *
     */
    protected void setupBorder() {
        if (  getContentPane() instanceof JComponent ) {
            if ( dockingPanels.size() > 1 ) {
                ((JComponent)getContentPane()).setBorder( new EmptyBorder(2,2,2,2));
            } else{
                ((JComponent)getContentPane()).setBorder(null);
            }
            
        }
    }
    
    @Override
    public void setVisible(boolean visible) {
        
        if ( visible == true && isVisible() == false ) {
            setupBorder() ;
            layoutPanels();
            
            for(int i = 0; i < 4; i++) {
                if ( dockingBars[i] != null ) {
                    dockingBars[i].validate();
                }
            }
            
            pack();
            resetPanelLayouts();
        }
        super.setVisible(visible);
        updateTitle();
    }
    
    /**
     *
     */
    public void resetPanelLayouts() {
        if ( isVisible() )  {
            Iterator i = dockingPanels.iterator();
            while ( i.hasNext() ) {
                ((DockingPanel)i.next()).resetLayout();
            }
        }
    }
    
    /**
     *
     */
    public void layoutPanels() {
        if ( DEBUG > 0 ) System.out.println("Generating Layout...");
        LayoutObject lo = generateLayout();
        if ( DEBUG > 0 ) System.out.println("Layout Complete.");
        Component c = lo.getLayoutComponent();
        
        setCurrentLayoutComponent(c);
        
        
    }
    
    /**
     *
     * @return
     */
    public LayoutObject generateLayout() {
        Set<LayoutObject> objectsToLayout = new HashSet<LayoutObject>(dockingPanels);
        
        Iterator i,j,k;
        LayoutObject a,b,m;
        LayoutGroup n = null;
        
        // Check to make sure that there are no overlap.
        // If there are, reduce the sizes until there aren't anymore.
        boolean changed = true;
        a = null;
        b = null;
        
        while ( changed == true ) {
            i = objectsToLayout.iterator();
            changed = false;
            while ( i.hasNext() ) {
                a = (LayoutObject)i.next();
                if ( a.isVisible() ) {
                    j = objectsToLayout.iterator();
                    while ( j.hasNext() ) {
                        b = (LayoutObject)j.next();
                        if ( b.isVisible() && a != b) {
                            if ( a.getLayoutBounds().intersects(b.getLayoutBounds()) ) {
                                changed = true;
                                break;
                            }
                        }
                    }
                    if ( changed == true ) break;
                }
            }
            
            if ( changed == true ) {
                if ( a.getClass() != LayoutProxy.class ) {
                    objectsToLayout.remove(a);
                    a = new LayoutProxy(a);
                    objectsToLayout.add(a);
                }
                if ( b.getClass() != LayoutProxy.class ) {
                    objectsToLayout.remove(b);
                    b = new LayoutProxy(b);
                    objectsToLayout.add(b);
                }
                removeOverlap((LayoutProxy)a,(LayoutProxy)b);
            }
        }
        
        /*     i = hash.iterator();
        System.out.println("[[Prelayout Hash]]");
        while ( i.hasNext() ) {
        a = (LayoutObject)i.next();
        System.out.println( a.toString() );
        } */
        
        changed = true;
        
        while ( objectsToLayout.size() > 1 && changed == true) {
            if ( objectsToLayout.size() == 2 ) {
                i = objectsToLayout.iterator();
                a = (LayoutObject)i.next();
                b = (LayoutObject)i.next();
                n = new LayoutGroup(a,b);
                changed = true;
            } else {
                changed = false;
                i = objectsToLayout.iterator();
                while ( i.hasNext() && changed == false) {
                    a = (LayoutObject)i.next();
                    j = objectsToLayout.iterator();
                    while ( j.hasNext() && changed == false) {
                        b = (LayoutObject)j.next();
                        if ( a == b ) continue;
                        
                        n = new LayoutGroup(a,b);
                        k = objectsToLayout.iterator();
                        boolean bad = false;
                        while ( k.hasNext() && changed == false) {
                            m = (LayoutObject)k.next();
                            if ( m == a || m == b ) continue;
                            if ( n.intersects(m) == true ) {
                                bad = true;
                                break;
                            }
                        }
                        if ( bad == false ) {
                            changed = true;
                        }
                    }
                }
            }
            
            if ( changed == true ) {
                objectsToLayout.remove( n.getA() );
                objectsToLayout.remove( n.getB() );
                objectsToLayout.add(n);
            } else {
                System.out.println("No changed occurred during loop:  Abitrarily grouping next two");
                // Since no change occurred, there must have been a unresolvable
                // set of objects.  Abitrarily remove the first two and group them.
                i = objectsToLayout.iterator();
                a = (LayoutObject)i.next();
                b = (LayoutObject)i.next();
                n = new LayoutGroup(a,b);
                objectsToLayout.remove(a);
                objectsToLayout.remove(b);
                objectsToLayout.add(n);
            }
        }
        
        //     System.out.println( hash.toString() );
        return objectsToLayout.iterator().next();
    }
    
    /**
     *
     * @param a
     * @param b
     */
    static public void removeOverlap(final LayoutProxy a, final LayoutProxy b) {
        Rectangle ra = (Rectangle)a.getLayoutBounds().clone();
        Rectangle rb = (Rectangle)b.getLayoutBounds().clone();
        int d,dx,dy;
        
        if ( ra.x <= rb.x ) {
            dx = Math.abs( ra.x + ra.width - rb.x );
        } else {
            dx = Math.abs( rb.x + rb.width - ra.x );
        }
        
        if ( ra.y <= rb.y ) {
            dy = Math.abs( ra.y + ra.height - rb.y );
        } else {
            dy = Math.abs( rb.y + rb.height - ra.y );
        }
        
        if ( dx < dy ) {
            if ( ra.x <= rb.x ) {
                // A is to the left
                d = dx / 2;
                ra.width -= d;
                rb.x += d+1;
                rb.width -= d+1;
            } else {
                d = dx / 2;
                rb.width -= d;
                ra.x += d+1;
                ra.width -= d+1;
            }
            if ( ra.width < 1 ) ra.width = 1;
            if ( rb.width < 1 ) rb.width = 1;
        } else {
            if ( ra.y <= rb.y ) {
                // A is to the left
                d = dy / 2;
                ra.height -= d;
                rb.y += d+1;
                rb.height -= d+1;
            } else {
                d = dy / 2;
                rb.height -= d;
                ra.y += d+1;
                ra.height -= d+1;
            }
            if ( ra.height < 1 ) ra.height = 1;
            if ( rb.height < 1 ) rb.height = 1;
        }
        
        a.setLayoutBounds(ra);
        b.setLayoutBounds(rb);
        
    }
    
    /** Finds the panel that is located at point p.
     *
     * The point p should be in frame coordianates (not content pane coordinates).
     * This method will only return panels that are non-minimized and visible.
     * @param p
     * @return
     */
    public DockingPanel findDockingPanelAt(Point p) {
      /*  p = (Point)p.clone();
        
        Container c = getContentPane();
        p.translate(-1 * c.getX(), -1 * c.getY());
        
        p.translate(-1 * centerPanel.getX(), -1 * centerPanel.getY()); */
       // System.out.println("Point is " + p);
        
        Iterator i = dockingPanels.iterator();
        while(i.hasNext()) {
            DockingPanel panel = (DockingPanel)i.next();
            if ( panel != null && panel.isVisible() && panel.getBoundsInFrame(null).contains(p)) {
                //System.out.println("  Point is in " + panel.getPanelName() + " w/ bounds " + panel.getBoundsInFrame(null));
                return panel;
            }
        }
        
        return null;
    }
    
    /**
     *
     * @param sr
     * @param side
     * @return
     */
    public Rectangle splitRectangle(Rectangle sr, int side) {
        Rectangle dr = new Rectangle();
        if ( side == TOP_SIDE ) {
            dr.x = sr.x;
            dr.width = sr.width;
            dr.y = sr.y;
            dr.height = sr.height / 2;
        } else if ( side == BOTTOM_SIDE ) {
            dr.x = sr.x;
            dr.width = sr.width;
            dr.y = sr.y + sr.height / 2 + 1;
            dr.height = sr.height / 2 - 1;
        } else if ( side == LEFT_SIDE ) {
            dr.x = sr.x;
            dr.width = sr.width / 2 - 1;
            dr.y = sr.y;
            dr.height = sr.height;
        } else if ( side == RIGHT_SIDE ) {
            dr.x = sr.x + sr.width / 2 + 1;
            dr.width = sr.width / 2 - 1;
            dr.y = sr.y;
            dr.height = sr.height;
        }
        return dr;
    }
    
    /**
     *
     * @param dp
     * @param p
     * @return
     */
    public int findSide(Rectangle dp, Point p) {
        int width = dp.width;
        int height = dp.height;
        int side = 0;
        //System.out.println( "Rect: " + dp.toString() + " Point: " + p.toString() + " Side: " + Integer.toString(side));
        if ( p.y >= (dp.y + height) - ( ( p.x - dp.x ) * height / width) ) side += 2;
        if ( p.y >= dp.y + ( ( p.x - dp.x ) * height / width ) ) side += 1;
        //System.out.println( "Rect: " + dp.toString() + " Point: " + p.toString() + " Side: " + Integer.toString(side));
        
        return side;
    }
    
    /**
     *
     * @param side
     * @return
     */
    public int getOppositeSide(int side) {
        if ( side == LEFT_SIDE ) return RIGHT_SIDE;
        if ( side == RIGHT_SIDE ) return LEFT_SIDE;
        if ( side == TOP_SIDE ) return BOTTOM_SIDE;
        if ( side == BOTTOM_SIDE ) return TOP_SIDE;
        return 0;
    }
    
    @Override
    public void processWindowEvent(WindowEvent e) {
        super.processWindowEvent(e);
        
        fireWindowEvent(e);
        
    }
    
    /** Invoked when the component's size changes.
     * @param e
     */
    public void componentResized(ComponentEvent e) {
        if ( e.getComponent() == getContentPane() ) {
            dropWatcher.setBounds( getContentPane().getBounds() );
            resetPanelLayouts();
        }
    }
    /** Invoked when the component's position changes.
     * @param e
     */
    public void componentMoved(ComponentEvent e) {
        if ( e.getComponent() == getContentPane() ) {
            dropWatcher.setBounds( getContentPane().getBounds() );
        } else if ( e.getComponent() == this ) {
            resetPanelLayouts();
        }
    }
    /** Invoked when the component has been made visible.
     * @param e
     */
    public void componentShown(ComponentEvent e) {
    }
    /** Invoked when the component has been made invisible.
     * @param e
     */
    public void componentHidden(ComponentEvent e) {
    }

    /**
     *
     * @param ddd
     */
    protected void setDropDestination(DockingDropDestination ddd) {
        if ( dropDestination != ddd ) {
            dropDestination = ddd;
            
            if ( ddd != null ) {
                showDockingDropPanel(ddd.getDropPanel());
            }
            else {
                hideDockingDropPanel();
            }
        }
    }
    
    /**
     *
     * @param dropPanel
     */
    protected void showDockingDropPanel( DockingDropPanel dropPanel ) {
        if ( this.dockingDropPanel != null && this.dockingDropPanel != dropPanel ) {
            hideDockingDropPanel();
        }
        
        if ( this.dockingDropPanel != dropPanel ) {
            this.dockingDropPanel = dropPanel;
            
            if ( dockingDropPanel != null ) {
                getLayeredPane().add(dockingDropPanel, 299);

                Rectangle r = dockingDropPanel.getBounds();

               /* r.x += getContentPane().getX();
                r.y += getContentPane().getY(); */

               // dockingDropPanel.setBounds(r);
                if ( dockingDropPanel.isVisible() == false ) {
                    dockingDropPanel.setVisible(true);
                }
            }
        }
    }
    
    /**
     *
     */
    protected void hideDockingDropPanel( ) {
        if ( dockingDropPanel != null ) {
            getLayeredPane().remove(dockingDropPanel);
            repaint();
            dockingDropPanel = null;
        }
    }
    
    /**
     *
     */
    public void showDropWatcher() {
        Rectangle r = this.getBounds();
        r.x = 0;
        r.y = 0;
        dropWatcher.setBounds(r);
        dropWatcher.setVisible(true);
    }
    
    /**
     *
     */
    public void hideDropWatcher( ) {
        dropWatcher.setVisible(false);
    }
    
    /** Called when a drag operation has
     * encountered the <code>DropTarget</code>.
     * <P>
     * @param dtde the <code>DropTargetDragEvent</code>
     */
    @Override
    public void dragEnter(DropTargetDragEvent dtde) {
        if ( dtde.isDataFlavorSupported( dockingPanelDataFlavor ) && DockingPanel.getDraggingPanel() != null ) {
            hideAllMinimizedPanels();
            
            draggingSource = DockingPanel.getDraggingPanel();
           // draggingSourceBounds = draggingSource.getLayoutBounds();
            resetPanelLayouts();
            if ( updateDragDestination(dtde.getLocation()) ) {
                dtde.acceptDrag(DnDConstants.ACTION_MOVE);
            }
            else {
                dtde.rejectDrag();
            }
        }
        else {
            dtde.rejectDrag();
        }
    }
    /** Called when a drag operation is ongoing
     * on the <code>DropTarget</code>.
     * <P>
     * @param dtde the <code>DropTargetDragEvent</code>
     */
    @Override
    public void dragOver(DropTargetDragEvent dtde) {
        if ( dtde.isDataFlavorSupported( dockingPanelDataFlavor ) && draggingSource != null) {
            if ( updateDragDestination(dtde.getLocation()) ) {
                dtde.acceptDrag(DnDConstants.ACTION_MOVE);
            }
            else {
                dtde.rejectDrag();
            }
        }
        else {
            dtde.rejectDrag();
        }
    }
    
    
    /** Called if the user has modified
     * the current drop gesture.
     * <P>
     * @param dtde the <code>DropTargetDragEvent</code>
     */
    @Override
    public void dropActionChanged(DropTargetDragEvent dtde) {
    }
    /** The drag operation has departed
     * the <code>DropTarget</code> without dropping.
     * <P>
     * @param dte the <code>DropTargetEvent</code>
     */
    @Override
    public void dragExit(DropTargetEvent dte) {
        // System.out.println ( "Where did you go" );
        
        dropDestination = null;
        hideDockingDropPanel();
    }
    
    
    /** The drag operation has terminated
     * with a drop on this <code>DropTarget</code>.
     * This method is responsible for undertaking
     * the transfer of the data associated with the
     * gesture. The <code>DropTargetDropEvent</code>
     * provides a means to obtain a <code>Transferable</code>
     * object that represents the data object(s) to
     * be transferred.<P>
     * From this method, the <code>DropTargetListener</code>
     * shall accept or reject the drop via the
     * acceptDrop(int dropAction) or rejectDrop() methods of the
     * <code>DropTargetDropEvent</code> parameter.
     * <P>
     * Subsequent to acceptDrop(), but not before,
     * <code>DropTargetDropEvent</code>'s getTransferable()
     * method may be invoked, and data transfer may be
     * performed via the returned <code>Transferable</code>'s
     * getTransferData() method.
     * <P>
     * At the completion of a drop, an implementation
     * of this method is required to signal the success/failure
     * of the drop by passing an appropriate
     * <code>boolean</code> to the <code>DropTargetDropEvent</code>'s
     * dropComplete(boolean success) method.
     * <P>
     * Note: The actual processing of the data transfer is not
     * required to finish before this method returns. It may be
     * deferred until later.
     * <P>
     * @param e
     */
    @Override
    public void drop(DropTargetDropEvent e) {
        if ( dropDestination != null ) {
            dropDestination.handleDrop(e);
        }
        
        hideDockingDropPanel();
    }
    
    /** Updates the drag destination information.
     *
     * This will show/hide the dropPanel appropriately.
     *
     * This method will update the dropDestination and call show/hide 
     * on the appropriate drop panel.
     *
     * Returns true if there is a valid drag destination.
     * @param p 
     * @return
     */
    protected boolean updateDragDestination(Point p) {
        // This point will always come in in global frame coordinates...
        // Make sure to translate appropriately...
        
        // Try the existing destination, just in case it is still valid
        if ( dropDestination != null ) {
            if ( dropDestination.isInBounds(p)) return true;
        }
        
        // Check the edges first...
        Rectangle bounds = getContentPane().getBounds();
        if ( draggingSource.isMinimizable() ) {
            if ( p.x < bounds.x + 20 ) {
                if ( draggingSource.isMinimized() == false || 
                     draggingSource.getDockingFrame() != this || 
                     draggingSource.getMinimizedLocation() != LEFT_SIDE) {
                        DockingBarDropDestination ddd = new DockingBarDropDestination(this, LEFT_SIDE);
                        setDropDestination(ddd);
                        return true;
                }
            }
            
            if ( p.x > bounds.x + bounds.width - 20 ) {
                if ( draggingSource.isMinimized() == false || 
                     draggingSource.getDockingFrame() != this || 
                     draggingSource.getMinimizedLocation() != RIGHT_SIDE) {
                        DockingBarDropDestination ddd = new DockingBarDropDestination(this, RIGHT_SIDE);
                        setDropDestination(ddd);
                        return true;
                }
            }
            
            if ( p.y < bounds.y + 20 ) {
                if ( draggingSource.isMinimized() == false || 
                     draggingSource.getDockingFrame() != this || 
                     draggingSource.getMinimizedLocation() != TOP_SIDE) {
                        DockingBarDropDestination ddd = new DockingBarDropDestination(this, TOP_SIDE);
                        setDropDestination(ddd);
                        return true;
                }
            }
            
            if ( p.y > bounds.y + bounds.height - 20 ) {
                if ( draggingSource.isMinimized() == false || 
                     draggingSource.getDockingFrame() != this || 
                     draggingSource.getMinimizedLocation() != BOTTOM_SIDE) {
                        DockingBarDropDestination ddd = new DockingBarDropDestination(this, BOTTOM_SIDE);
                        setDropDestination(ddd);
                        return true;
                }
            }
        }
       
        // It wasn't in the boundary areas, so try the normal area
        DockingPanel dp = findDockingPanelAt(p);
        if ( dp != null && dp != draggingSource) {
            Rectangle dpb = dp.getBoundsInFrame(null);
            int side = findSide(dpb,p);
            DockingDropDestination ddd = new DockingPanelDropDestination(this, dp, splitRectangle(dpb, side), side);
            setDropDestination(ddd);
            return true;
        }
        
        // If we got this far, there isn't a good drop destination...
        setDropDestination(null);
        return false;
        
    }
    /** Getter for property dockingPanels.
     * @return Value of property dockingPanels.
     */
    protected Set<DockingPanel> getDockingPanels() {
        return dockingPanels;
    }
    /** Setter for property dockingPanels.
     * @param dockingPanels New value of property dockingPanels.
     */
    protected void setDockingPanels(HashSet dockingPanels) {
        this.dockingPanels = dockingPanels;
        updateTitle();
    }
    
    /**
     *
     * @return
     */
    public int getPanelCount() {
        
        return dockingPanels.size();
    }
    
    /**
     *
     * @deprecated 
     * @return
     */
    public Iterator<DockingPanel> getDockingPanelIterator() {
        return dockingPanels.iterator();
    }

    @Override
    public Iterator<DockingPanel> iterator() {
        return dockingPanels.iterator();
    }
    
    /**
     *
     */
    public void updateTitle() {
        //if ( isVisible() ) {
        String title = null;
        
        if ( dockingPanels.size() == 1 ) {
            title = (dockingPanels.iterator().next()).getName();
        } else {
            // Just grab the frame name from the first Panel
            Iterator i = dockingPanels.iterator();
            while ( i.hasNext() && title == null ) {
                title = ((DockingPanel)i.next()).getFrameName();
            }
        }
        
        setTitle( title == null ? "" : title);
        // }
    }
    
    /**
     *  Adds a <code>DockingFrameListener</code> listener.
     *
     *  @param l  the <code>DockingFrameListener</code> to add
     */
    public void addDockingFrameListener(DockingFrameListener l) {
        listenerList.add(DockingFrameListener.class,l);
    }
    
    /**
     * Removes a <code>DockingFrameListener</code> listener.
     *
     * @param l  the <code>DockingFrameListener</code> to remove
     */
    public void removeDockingFrameListener(DockingFrameListener l) {
        listenerList.remove(DockingFrameListener.class,l);
    }

    @Override
    public void dispose() {
        setVisible(false);

        Set<DockingPanel> panels = new HashSet<DockingPanel>(dockingPanels);

        for (DockingPanel dockingPanel : panels) {
            dockingPanel.undockFromFrame(false);
            if (dockingPanel instanceof Destroyable) {
                ((Destroyable) dockingPanel).destroy();
            }
        }

        super.dispose();
    }
    
    /**
     *
     */
    protected void fireFrameChange() {
        FrameChangeEvent e = null;
        // Guaranteed to return a non-null array
        Object[] listeners = listenerList.getListenerList();
        // Process the listeners last to first, notifying
        // those that are interested in this event
        for (int i = listeners.length-2; i>=0; i-=2) {
            if (listeners[i]==DockingFrameListener.class) {
                // Lazily create the event:
                e = new FrameChangeEvent(this, dockingPanels.size());
                // System.out.println( "Firing chance event to: " + listeners[i+1].toString());
                ((DockingFrameListener)listeners[i+1]).frameChange(e);
            }
        }
    }
    
    /**
     *
     * @param e
     */
    protected void fireWindowEvent(WindowEvent e) {
        if ( e == null ) return;
        // Guaranteed to return a non-null array
        Object[] listeners = listenerList.getListenerList();
        
        for (int i = listeners.length-2; i>=0; i-=2) {
            if (listeners[i]==DockingFrameListener.class) {
                // Lazily create the event:
                
                // System.out.println( "Firing chance event to: " + listeners[i+1].toString());
                WindowListener windowListener = ((DockingFrameListener)listeners[i+1]);
                switch(e.getID()) {
                    case WindowEvent.WINDOW_OPENED:
                        windowListener.windowOpened(e);
                        break;
                    case WindowEvent.WINDOW_CLOSING:
                        windowListener.windowClosing(e);
                        break;
                    case WindowEvent.WINDOW_CLOSED:
                        windowListener.windowClosed(e);
                        break;
                    case WindowEvent.WINDOW_ICONIFIED:
                        windowListener.windowIconified(e);
                        break;
                    case WindowEvent.WINDOW_DEICONIFIED:
                        windowListener.windowDeiconified(e);
                        break;
                    case WindowEvent.WINDOW_ACTIVATED:
                        windowListener.windowActivated(e);
                        break;
                    case WindowEvent.WINDOW_DEACTIVATED:
                        windowListener.windowDeactivated(e);
                        break;
                    default:
                        break;
                }
            }
        }
    }

    /**
     *
     * @return
     */
    public Component getCurrentLayoutComponent() {
        return currentLayoutComponent;
    }

    /**
     *
     * @param currentLayoutComponent
     */
    public void setCurrentLayoutComponent(Component currentLayoutComponent) {
        if ( this.currentLayoutComponent != currentLayoutComponent ) {
            if(this.currentLayoutComponent != null ) {
                centerPanel.remove(this.currentLayoutComponent);
            }
        
            this.currentLayoutComponent = currentLayoutComponent;
            
            if(this.currentLayoutComponent != null ) {
                centerPanel.add(this.currentLayoutComponent, BorderLayout.CENTER);
            }
            
            centerPanel.validate();
        }
    }
    
    /**
     *
     */
    public class MinimizedLayerPanelMouseListener implements MouseListener, AWTEventListener {
        @Override
        public void mouseClicked(MouseEvent mouseEvent) {
            
        }

        @Override
        public void mouseEntered(MouseEvent mouseEvent) {
            
        }

        @Override
        public void mouseExited(MouseEvent mouseEvent) {
            
        }

        @Override
        public void mousePressed(MouseEvent mouseEvent) {
            if ( getContentPane().getComponentAt(mouseEvent.getPoint()) instanceof DockingBar == false) {
                hideAllMinimizedPanels();
            }
        }

        @Override
        public void mouseReleased(MouseEvent mouseEvent) {
            
        }
        
        @Override
        public void eventDispatched(AWTEvent ev) {
            Component src = null;
            switch (ev.getID()) {
            case MouseEvent.MOUSE_PRESSED:
                src = (Component)ev.getSource();
                if ( getComponentDockingFrame(src) != DockingFrame.this || shouldHideMinizedPanels(src) == false ) {
                    return;
                }
                else {
                    hideAllMinimizedPanels();
                }
                break;
            case MouseEvent.MOUSE_RELEASED:
                src = (Component)ev.getSource();
                if ( getComponentDockingFrame(src) != DockingFrame.this || shouldHideMinizedPanels(src) == false) {
                    return;
                }
                else {
                    hideAllMinimizedPanels();
                }
                break;
            case MouseEvent.MOUSE_WHEEL:
                src = (Component)ev.getSource();
                if ( getComponentDockingFrame(src) != DockingFrame.this || shouldHideMinizedPanels(src) == false) {
                    return;
                }
                else {
                    hideAllMinimizedPanels();
                }
                break;
            }
        }
        
        /**
         *
         * @param src
         * @return
         */
        protected boolean shouldHideMinizedPanels(Component src) {
            if ( src instanceof DockingBar ) return false;
            if ( src instanceof DockingBarButton ) return false;
            
            // Run through the parents to figure out if this is a dockingPanel that is minimized
            while ( src != null ) {
                if ( src instanceof DockingPanel && ((DockingPanel)src).isMinimized() == false) 
                    return true;
                src = src.getParent();
            }
            return false;
        }
        
        /**
         *
         * @param src
         * @return
         */
        protected DockingFrame getComponentDockingFrame(Component src) {
            while ( src != null ) {
                if ( src instanceof DockingFrame ) return (DockingFrame) src;
                src = src.getParent();
            }
            return null;
        }
    }
    
    

    /**
     *
     */
    public class MinimizedLayerPanelComponentListener implements ComponentListener {
        
        @Override
        public void componentHidden(ComponentEvent componentEvent) {
        }

        @Override
        public void componentMoved(ComponentEvent componentEvent) {
            updateMinimizedLayerPanelBounds();
        }

        @Override
        public void componentResized(ComponentEvent componentEvent) {
            updateMinimizedLayerPanelBounds();
        }

        @Override
        public void componentShown(ComponentEvent componentEvent) {
        }
    }
    
    
}