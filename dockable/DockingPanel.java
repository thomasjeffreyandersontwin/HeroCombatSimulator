/*
 * DockingPanel.java
 *
 * Created on December 31, 2000, 4:52 PM
 */
package dockable;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.awt.MediaTracker;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.TexturePaint;
import java.awt.Toolkit;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DragGestureEvent;
import java.awt.dnd.DragGestureListener;
import java.awt.dnd.DragSource;
import java.awt.dnd.DragSourceDragEvent;
import java.awt.dnd.DragSourceDropEvent;
import java.awt.dnd.DragSourceEvent;
import java.awt.dnd.DragSourceListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.image.BufferedImage;
import java.util.Iterator;
import java.util.Set;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JMenuBar;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.event.ChangeEvent;
import javax.swing.event.EventListenerList;

import champions.GlobalFontSettings;
import tjava.PartialWeakMap;

/**
 *
 * @author  unknown
 * @version
 */
public class DockingPanel extends JLayeredPane
        implements DragSourceListener, DockingFrameListener, WindowListener, LayoutObject,
                   DragGestureListener, MouseListener, MouseMotionListener {

    private static DockingPanel draggingPanel = null;

    /** Holds value of property leftImage. */
    private static Image leftTexture;

    /** Holds value of property rightImage. */
    private static Image rightTexture;

    /** Holds value of property centerImage. */
    private static TexturePaint centerTexture;

    /** Holds value of property leftImage. */
    private static Image leftTextureMouseOver;

    /** Holds value of property rightImage. */
    private static Image rightTextureMouseOver;

    /** Holds value of property centerImage. */
    private static TexturePaint centerTextureMouseOver;

    private static int titleHeight;

    private static Font textFont = null;

    private static Color textColor = null;

    private static Color shadowColor = null;

    private static int shadowXOffset = 1;

    private static int shadowYOffset = 1;

    private static Color borderColor = new Color(127, 157, 185);

    /** Creates new DockingPanel */
    private static final String uiClassID = "DockingPanelUI";

    private static PartialWeakMap registeredDockingPanels = new PartialWeakMap();

    /**
     * @return the draggingPanel
     */
    public static DockingPanel getDraggingPanel() {
        return draggingPanel;
    }

    /**
     * @param aDraggingPanel the draggingPanel to set
     */
    public static void setDraggingPanel(DockingPanel aDraggingPanel) {
        draggingPanel = aDraggingPanel;
    }
    private JLabel titleLabel;
    // private JLabel shadow;

    private boolean selected;

    /**
     * Holds value of property fullTitleBar.
     */
    private boolean fullTitleBar;

    private JPopupMenu menu;

    private Action undockAction;

    private static int nextIndex = 0;

    transient private int panelIndex;
    // protected DockingHandle dockingHandle;

    /**
     *
     */
    protected Container contentPane;

    /** Holds value of property dockingFrame. */
    private DockingFrame dockingFrame;

    /** Holds value of property handleVisible. */
    //private boolean handleVisible = false;

    private JMenuBar menuBar = null;

    private String panelName = null;

    private String frameName = null;

    private Icon panelIcon = null;

    /**
     *
     */
    protected Rectangle titleBounds;

    private DockingCornerHandle handle;

    private boolean minimizable;

    private boolean minimized;
    //private boolean cursorIsSet = false;

    private JButton minimizeButton;

    /** Holds value of property layoutBounds. */
    private Rectangle layoutBounds = new Rectangle(0, 0, 0, 0);

    private Rectangle preMinimizedLayoutBounds;

    private boolean resizeVertically = false;

    private boolean resizeHorizonally = false;

    private int preferredMinimizedLocation = DockingFrame.BOTTOM_SIDE;

    private EventListenerList dockingPanelListenerList = new EventListenerList();

    /**
     *
     */
    public DockingPanel() {
        registerPanel();

        setupComponents();
        setupActions();

        setLayout(new DockingPanel.DockingPanelLayoutManager());
        updateFullTitleBar();

        setFont(new Font("Dialog", 0, (int) (18 * GlobalFontSettings.SizeMagnification)));

        setOpaque(true);
    }

    /**
     *
     * @param panelName
     */
    public DockingPanel(String panelName) {
        this();
        setName(panelName);
    }

    /**
     *
     * @param contents
     */
    public DockingPanel(JComponent contents) {
        this(contents, contents.getName());
    }

    /**
     *
     * @param contents
     * @param panelName
     */
    public DockingPanel(JComponent contents, String panelName) {
        this();

        getContentPane().setLayout( new BorderLayout() );
        getContentPane().add(contents, BorderLayout.CENTER);

        setName(panelName);
    }

    /**
     *
     */
    protected void setupComponents() {
        setupImages();
        setupColors();
        setupActions();

        handle = new DockingCornerHandle();
        handle.addMouseListener(this);
        this.add(handle);

        contentPane = new Container();
        contentPane.setLayout(new BorderLayout());
        add(contentPane, new Integer(0));

        titleBounds = new Rectangle();

        titleLabel = new JLabel();
        this.add(titleLabel);
        titleLabel.setOpaque(false);

        createSource();

        addMouseListener(this);
        addMouseMotionListener(this);
    }

    /**
     *
     */
    protected void setupImages() {
        if (leftTexture == null) {
            Image image = Toolkit.getDefaultToolkit().createImage(getClass().getResource("/dockable/graphics/titleWindowLeft.gif"));
            setLeftImage(image);
        }

        if (rightTexture == null) {
            Image image = Toolkit.getDefaultToolkit().createImage(getClass().getResource("/dockable/graphics/titleWindowRight.gif"));
            setRightImage(image);
        }

        if (centerTexture == null) {
            Image image = Toolkit.getDefaultToolkit().createImage(getClass().getResource("/dockable/graphics/titleWindowFill.gif"));
            setCenterImage(image);
        }

        if (leftTextureMouseOver == null) {
            Image image = Toolkit.getDefaultToolkit().createImage(getClass().getResource("/dockable/graphics/titleWindowLeftMouseOver.gif"));
            setLeftImageMouseOver(image);
        }

        if (rightTextureMouseOver == null) {
            Image image = Toolkit.getDefaultToolkit().createImage(getClass().getResource("/dockable/graphics/titleWindowRightMouseOver.gif"));
            setRightImageMouseOver(image);
        }

        if (centerTextureMouseOver == null) {
            Image image = Toolkit.getDefaultToolkit().createImage(getClass().getResource("/dockable/graphics/titleWindowFillMouseOver.gif"));
            //Image image = Toolkit.getDefaultToolkit().createImage(getClass().getResource ("/dockable/graphics/titleWindowFill.gif") );

            setCenterImageMouseOver(image);
        }


    }

    /**
     *
     */
    protected void setupColors() {
        if (textColor != null) {
            titleLabel.setForeground(textColor);
        }
        if (textFont != null) {
            titleLabel.setFont(textFont);
        }
    }

    /**
     *
     */
    protected void setupActions() {
        undockAction = new AbstractAction("Undock Panel") {

            @Override
            public void actionPerformed(ActionEvent e) {
                undockFromFrame(true);
            }
        };

        menu = new JPopupMenu();
        menu.add(undockAction);
    }

    /**
     *
     * @return
     */
    public Container getContentPane() {
        return contentPane;
    }

    /**
     * 
     */
    protected void registerPanel() {
        panelIndex = getNextIndex();
        registeredDockingPanels.put(new Integer(panelIndex), this);

    }

    @Override
    public void setLayout(LayoutManager manager) {
        if (getLayout() == null || manager instanceof DockingPanel.DockingPanelLayoutManager) {
            super.setLayout(manager);
        }
        else {
            throw new IllegalArgumentException("Use getContentPane().setLayout() to set the layout.");
        }
    }

    /**
     *
     */
    protected void createSource() {
        // creating the recognizer is all thats necessary - it
        // does not need to be manipulated after creation
        DragSource dragSource = DragSource.getDefaultDragSource();
        dragSource.createDefaultDragGestureRecognizer(
                this, // component where drag originates
                DnDConstants.ACTION_MOVE, // actions
                this); // drag gesture listener

        dragSource.createDefaultDragGestureRecognizer(
                handle,
                DnDConstants.ACTION_MOVE,
                this);
    }

    /** A <code>DragGestureRecognizer</code> has detected
     * a platform-dependent drag initiating gesture and
     * is notifying this listener
     * in order for it to initiate the action for the user.
     * <P>
     * @param e
     */
    @Override
    public void dragGestureRecognized(DragGestureEvent e) {
        if (e.getComponent() instanceof DockingBarButton ||
                e.getComponent() == handle ||
                (isPointInControlArea(e.getDragOrigin()) && resizeVertically == false && resizeHorizonally == false)) {
            DockingPanel.initiateDragging(this);//draggingPanel = dockingPanel;

            if (this.isMinimized() && isVisible()) {
                getDockingFrame().hideMinimizedPanel(this);
            }

            e.startDrag(DragSource.DefaultMoveDrop, // cursor
                    new DockingPanelTransferable(this), // transferable
                    this); // drag source listener
        }

        //  System.out.println( "Initiating Drag of: " + this );
    }

    /**
     *
     * @param p
     * @return
     */
    public boolean isPointInControlArea(Point p) {
        if (fullTitleBar) {
            return titleBounds.contains(p);
        }

        return false;
    }

    /** Invoked when the mouse has been clicked on a component.
     * @param e
     */
    @Override
    public void mouseClicked(MouseEvent e) {
    }

    /** Invoked when a mouse button has been pressed on a component.
     * @param e
     */
    @Override
    public void mousePressed(MouseEvent e) {
        Point p = e.getPoint();
        if (isVerticalResizeArea(p)) {
            resizeVertically = true;
        }
        if (isHorizonalResizeArea(p)) {
            resizeHorizonally = true;
        }
        if (e.getSource() == handle || isPointInControlArea(e.getPoint())) {
            maybeShowPopup(e);
        }
    }

    /** Invoked when a mouse button has been released on a component.
     * @param e
     */
    @Override
    public void mouseReleased(MouseEvent e) {

        resizeVertically = false;
        resizeHorizonally = false;

        if (e.getSource() == handle || isPointInControlArea(e.getPoint())) {
            maybeShowPopup(e);
        }
    }

    /** Invoked when the mouse enters a component.
     * @param e
     */
    @Override
    public void mouseEntered(MouseEvent e) {
        Point p = e.getPoint();

        updateCursor(p);
        setSelected(isPointInControlArea(e.getPoint()));

    }

    /** Invoked when the mouse exits a component.
     * @param e
     */
    @Override
    public void mouseExited(MouseEvent e) {
        setSelected(false);
        setCursor(null);
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        Point p = e.getPoint();

        updateCursor(p);

        setSelected(isPointInControlArea(e.getPoint()));
    }

    @Override
    public void mouseDragged(MouseEvent mouseEvent) {
        maybeResize(mouseEvent.getPoint());
    }

    /**
     *
     * @param e
     */
    protected void maybeShowPopup(MouseEvent e) {
        if (menu != null && e.isPopupTrigger()) {
            menu.show(e.getComponent(), e.getX(), e.getY());
        }
    }

    /**
     *
     * @param p
     * @return
     */
    protected boolean isVerticalResizeArea(Point p) {
        if (isMinimized() && isVisible()) {
            int location = getMinimizedLocation();
            if (location != -1) {
                Dimension d = getSize();
                switch (location) {
                    case DockingFrame.TOP_SIDE:
                        return p.y > d.height - 4;
                    case DockingFrame.LEFT_SIDE:
                    case DockingFrame.RIGHT_SIDE:
                        return p.y > d.height - 4;
                    case DockingFrame.BOTTOM_SIDE:
                        return p.y < 4;
                }
            }
        }
        return false;
    }

    /**
     *
     * @param p
     * @return
     */
    protected boolean isHorizonalResizeArea(Point p) {
        if (isMinimized() && isVisible()) {
            int location = getMinimizedLocation();
            if (location != -1) {
                Dimension d = getSize();
                switch (location) {
                    case DockingFrame.TOP_SIDE:
                    case DockingFrame.LEFT_SIDE:
                        return p.x > d.width - 4 || (p.y < 4 && p.x > d.width - getTitleHeight());
                    case DockingFrame.BOTTOM_SIDE:
                        return (p.x > d.width - 4) || (p.y < 4 && p.x > d.width - getTitleHeight()); // special case of title bar
                    case DockingFrame.RIGHT_SIDE:
                        return (p.x < 4);
                }
            }
        }
        return false;
    }

    /** Returns the location that this window has been minimized to.
     *
     * This method will return the location (In terms of TOP_SIDE, LEFT_SIDE, BOTTOM_SIDE, RIGHT_SIDE from
     * DockingFrame that this panel is located at.
     *
     * @return Returns -1 if this panel doesn't have a frame or isn't minimized.
     *
     */
    public int getMinimizedLocation() {
        int location = -1;
        if (isMinimized()) {
            DockingBar db = getDockingFrame().getMinimizedPanelDockingBar(this);
            if (db != null) {
                location = db.getBarLocation();
            }
        }
        return location;
    }

    /**
     *
     * @param p
     */
    protected void maybeResize(Point p) {
        if (resizeVertically || resizeHorizonally) {
            int location = getMinimizedLocation();
            Rectangle b = getBounds();
            Dimension d = getParent().getSize();
            int xsize = 0;
            int ysize = 0;

            switch (location) {
                case DockingFrame.TOP_SIDE:
                    if (resizeHorizonally) {
                        b.width = Math.min(p.x, d.width - 2);
                    }
                    if (resizeVertically) {
                        b.height = Math.min(p.y, d.height - 2);
                    }
                    break;
                case DockingFrame.BOTTOM_SIDE:
                    if (resizeHorizonally) {
                        b.width = Math.min(p.x, d.width - 2);
                    }
                    if (resizeVertically) {
                        b.height = Math.min(b.height - p.y, d.height - 2);
                        b.y = d.height - b.height;
                    }
                    break;
                case DockingFrame.LEFT_SIDE:
                    if (resizeHorizonally) {
                        b.width = Math.min(p.x, d.width - 2);
                    }
                    if (resizeVertically) {
                        b.height = Math.min(p.y, d.height - 2);
                    }
                    break;
                case DockingFrame.RIGHT_SIDE:
                    if (resizeHorizonally) {
                        b.width = Math.min(b.width - p.x, d.width - 2);
                        b.x = d.width - b.width;
                    }
                    if (resizeVertically) {
                        b.height = Math.min(p.y, d.height - 2);
                    }
                    break;
            }
            setBounds(b);
            setPreferredSize(new Dimension(b.width, b.height));

            fireLayoutChanged();

            validate();

            repaint();
        }
    }

    /**
     *
     * @param p
     */
    protected void updateCursor(Point p) {
        if (minimized == false) {
            setCursor(null);
        }
        else {
            boolean v = false;
            boolean h = false;

            if (isVerticalResizeArea(p)) {
                v = true;
            }
            if (isHorizonalResizeArea(p)) {
                h = true;
            }

            if (v && h) {
                int location = getMinimizedLocation();
                switch (location) {
                    case DockingFrame.TOP_SIDE:
                        setCursor(Cursor.getPredefinedCursor(Cursor.SE_RESIZE_CURSOR));
                        break;
                    case DockingFrame.BOTTOM_SIDE:
                        setCursor(Cursor.getPredefinedCursor(Cursor.NE_RESIZE_CURSOR));
                        break;
                    case DockingFrame.LEFT_SIDE:
                        setCursor(Cursor.getPredefinedCursor(Cursor.SE_RESIZE_CURSOR));
                        break;
                    case DockingFrame.RIGHT_SIDE:
                        setCursor(Cursor.getPredefinedCursor(Cursor.SW_RESIZE_CURSOR));
                        break;
                }
            }
            else if (h) {
                setCursor(Cursor.getPredefinedCursor(Cursor.E_RESIZE_CURSOR));
                //setCursor( Cursor.getPredefinedCursor( Cursor.WAIT_CURSOR ) );
            }
            else if (v) {
                setCursor(Cursor.getPredefinedCursor(Cursor.N_RESIZE_CURSOR));
            }
            else {
                setCursor(null);
            }
        }
    }

    @Override
    public void setCursor(Cursor cursor) {
        if (getCursor() != cursor) {
            super.setCursor(cursor);
        }
    }

    private int getNextIndex() {
        return nextIndex++;
    }

    /**
     *
     * @return
     */
    public int getDockingPanelIndex() {
        return panelIndex;
    }

    /**
     *
     * @param index
     * @return
     */
    static public DockingPanel getDockingPanel(int index) {
        if (registeredDockingPanels.containsKey(new Integer(index))) {
            return (DockingPanel) registeredDockingPanels.get(new Integer(index));
        }
        else {
            return null;
        }
    }

    /**
     *
     * @param df
     * @param p
     * @return
     */
    public DockingFrame dockIntoFrame(DockingFrame df, Point p) {
        if (df == null) {
            return dockingFrame;
        }

        if (dockingFrame == df) {
            dockingFrame.dockPanel(this, p);
        }
        else {
            DockingFrame oldFrame = null;
            if (dockingFrame != null) {
                oldFrame = dockingFrame;
                dockingFrame.undockPanel(this);
                dockingFrame = null;
            }

            dockingFrame = df;
            df.dockPanel(this, p);

            fireFrameChange(oldFrame, dockingFrame);
        }
        return dockingFrame;
    }

    /**
     *
     * @param df
     * @return
     */
    public DockingFrame dockIntoFrame(DockingFrame df) {
        if (df == null) {
            return dockingFrame;
        }

        if (dockingFrame != df) {
            DockingFrame oldFrame = null;
            if (dockingFrame != null) {
                dockingFrame.undockPanel(this);
                dockingFrame = null;
            }

            dockingFrame = df;
            df.dockPanel(this);

            fireFrameChange(oldFrame, dockingFrame);
        }

        return dockingFrame;
    }

    /**
     *
     * @param id
     * @param p
     * @return
     */
    public DockingFrame dockIntoFrame(Double id, Point p) {
        DockingFrame df = DockingFrame.findFrame(id);

        DockingFrame oldFrame = null;

        if (dockingFrame != null) {
            oldFrame = dockingFrame;
            dockingFrame.undockPanel(this);
            dockingFrame = null;
        }

        if (df == null) {
            dockingFrame = new DockingFrame(p, id);
            dockingFrame.dockPanel(this);
        }
        else {
            dockingFrame = df;
            df.dockPanel(this, p);
        }

        fireFrameChange(oldFrame, dockingFrame);

        return dockingFrame;
    }

    /**
     *
     * @param newFrame
     */
    public void undockFromFrame(boolean newFrame) {
        resetLayout();
        Point p = null;

        if (newFrame) {
            p = getLocationOnScreen();
        }
        if (dockingFrame != null) {
            dockingFrame.undockPanel(this);
            dockingFrame = null;
        }

        if (newFrame) {
            dockingFrame = new DockingFrame(p);
            dockingFrame.dockPanel(this);
            dockingFrame.setVisible(true);
        }
    }

    /**
     *
     * @return
     */
    public DockingFrame dockIntoFrame() {
        if (dockingFrame == null) {
            dockingFrame = new DockingFrame();
            dockingFrame.dockPanel(this);
        }
        return dockingFrame;
    }

    @Override
    public void setName(String name) {
        super.setName(name);

        if (name != null) {
            titleLabel.setText(name);
        }

        if (getDockingFrame() != null) {
            getDockingFrame().updateTitle();
        }
    }

    /**
     *
     * @param name
     */
    public void setFrameName(String name) {
        frameName = name;
        if (getDockingFrame() != null) {
            getDockingFrame().updateTitle();
        }
    }

    /**
     *
     * @return
     */
    public String getFrameName() {
        return frameName;
    }

    @Override
    public void setFont(Font f) {
        titleLabel.setFont(f);
    }

    @Override
    public void setForeground(Color c) {
        titleLabel.setForeground(c);
        super.setForeground(c);
    }

    @Override
    public void setBackground(Color c) {
        titleLabel.setForeground(c);
        super.setBackground(c);
        //if ( contentPane != null ) contentPane.setBackground(c);
    }

    @Override
    public void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;


        if (isFullTitleBar()) {
            paintTitleBar(g);
        }
        super.paintComponent(g);
    }

    /**
     *
     * @param g
     */
    protected void paintTitleBar(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;

        int x = 0;
        int y = 0;
        int width = getWidth();
        int height = getHeight();

        int leftIndent = 0;
        int rightIndent = 0;

        if (!selected || leftTextureMouseOver == null) {

            if (leftTexture != null) {
                leftIndent = leftTexture.getWidth(null);
                g2.drawImage(leftTexture, 0, 0, leftIndent, leftTexture.getHeight(null), null);
                x += leftIndent;
                width -= leftIndent;
            }

            if (rightTexture != null) {
                rightIndent = rightTexture.getWidth(null);
                g2.drawImage(rightTexture, x + width - rightIndent, 0, rightIndent, rightTexture.getHeight(null), null);
                width -= rightIndent;
            }

            if (centerTexture != null) {
                g2.setPaint(centerTexture);
                g2.fillRect(x, y, width, getTitleHeight());
            }

        }
        else {
            if (leftTextureMouseOver != null) {
                leftIndent = leftTextureMouseOver.getWidth(null);
                g2.drawImage(leftTextureMouseOver, 0, 0, leftIndent, leftTextureMouseOver.getHeight(null), null);
                x += leftIndent;
                width -= leftIndent;
            }

            if (rightTextureMouseOver != null) {
                rightIndent = rightTextureMouseOver.getWidth(null);
                g2.drawImage(rightTextureMouseOver, x + width - rightIndent, 0, rightIndent, rightTextureMouseOver.getHeight(null), null);
                width -= rightIndent;
            }

            if (centerTextureMouseOver != null) {
                g2.setPaint(centerTextureMouseOver);
                //g2.setColor( Color.red );
                //g2.setStroke( new BasicStroke(0) );
                // g2.drawRect( x, y, width, height);
                g2.fillRect(x, y, width, getTitleHeight());
            }
        }

        g2.setColor(borderColor);
        g2.drawRect(0, getTitleHeight(), getWidth() - 1, getHeight() - getTitleHeight() - 1);
    }

    /**
     *
     * @param dp
     */
    public static void initiateDragging(DockingPanel dp) {
        //System.out.println("Initiating Drag");
        setDraggingPanel(dp);
        Set keys = DockingFrame.registeredDockingFrames.keySet();
        Iterator i = keys.iterator();
        while (i.hasNext()) {
            ((DockingFrame) DockingFrame.registeredDockingFrames.get(i.next())).showDropWatcher();
        }
    }

    /**
     *
     */
    public static void cancelDragging() {
        //System.out.println("Canceling Drag");
        setDraggingPanel(null);
        Set keys = DockingFrame.registeredDockingFrames.keySet();
        Iterator i = keys.iterator();
        while (i.hasNext()) {
            ((DockingFrame) DockingFrame.registeredDockingFrames.get(i.next())).hideDropWatcher();
        }
    }

    /** Called as the hotspot enters a platform dependent drop site.
     * This method is invoked when the following conditions are true:
     * <UL>
     * <LI>The logical cursor's hotspot initially intersects
     * a GUI <code>Component</code>'s  visible geometry.
     * <LI>That <code>Component</code> has an active
     * <code>DropTarget</code> associated with it.
     * <LI>The <code>DropTarget</code>'s registered
     * <code>DropTargetListener</code> dragEnter() method is invoked and
     * returns successfully.
     * <LI>The registered <code>DropTargetListener</code> invokes
     * the <code>DropTargetDragEvent</code>'s acceptDrag() method to
     * accept the drag based upon interrogation of the source's
     * potential drop action(s) and available data types
     * (<code>DataFlavor</code>s).
     * </UL>
     * <P>
     * @param dsde the <code>DragSourceDragEvent</code>
     */
    @Override
    public void dragEnter(DragSourceDragEvent dsde) {
        //System.out.println( "DockingPanel: DragEntered() called: " + dsde );
    }

    /** Called as the hotspot moves over a platform dependent drop site.
     * This method is invoked when the following conditions
     * are true:
     * <UL>
     * <LI>The cursor's logical hotspot has moved but still
     * intersects the visible geometry of the <code>Component</code>
     * associated with the previous dragEnter() invocation.
     * <LI>That <code>Component</code> still has a
     * <code>DropTarget</code> associated with it.
     * <LI>That <code>DropTarget</code> is still active.
     * <LI>The <code>DropTarget</code>'s registered
     * <code>DropTargetListener</code> dragOver() method
     * is invoked and returns successfully.
     * <LI>The <code>DropTarget</code> does not reject
     * the drag via rejectDrag()
     * </UL>
     * <P>
     * @param dsde the <code>DragSourceDragEvent</code>
     */
    @Override
    public void dragOver(DragSourceDragEvent dsde) {
        //System.out.println( "DockingPanel: DragOver() called.");
    }

    /** Called when the user has modified the drop gesture.
     * This method is invoked when the state of the input
     * device(s) that the user is interacting with changes.
     * Such devices are typically the mouse buttons or keyboard
     * modifiers that the user is interacting with.
     * <P>
     * @param dsde the <code>DragSourceDragEvent</code>
     */
    @Override
    public void dropActionChanged(DragSourceDragEvent dsde) {
        //System.out.println( "DockingPanel: DragActionChanged() called: " + dsde );
    }

    /** Called as the hotspot exits a platform dependent drop site.
     * This method is invoked when the following conditions
     * are true:
     * <UL>
     * <LI>The cursor's logical hotspot no longer
     * intersects the visible geometry of the <code>Component</code>
     * associated with the previous dragEnter() invocation.
     * </UL>
     * OR
     * <UL>
     * <LI>The <code>Component</code> that the logical cursor's hotspot
     * intersected that resulted in the previous dragEnter() invocation
     * no longer has an active <code>DropTarget</code> or
     * <code>DropTargetListener</code> associated with it.
     * </UL>
     * OR
     * <UL>
     * <LI> The current <code>DropTarget</code>'s
     * <code>DropTargetListener</code> has invoked rejectDrag()
     * since the last dragEnter() or dragOver() invocation.
     * </UL>
     * <P>
     * @param dse the <code>DragSourceEvent</code>
     */
    @Override
    public void dragExit(DragSourceEvent dse) {
        //System.out.println( "DockingPanel: DragExit() called.");
    }

    /** This method is invoked to signify that the Drag and Drop
     * operation is complete. The getDropSuccess() method of
     * the <code>DragSourceDropEvent</code> can be used to
     * determine the termination state. The getDropAction() method
     * returns the operation that the <code>DropTarget</code>
     * selected (via the DropTargetDropEvent acceptDrop() parameter)
     * to apply to the Drop operation. Once this method is complete, the
     * current <code>DragSourceContext</code> and
     * associated resources become invalid.
     * <P>
     * @param dsde the <code>DragSourceDropEvent</code>
     */
    @Override
    public void dragDropEnd(DragSourceDropEvent dsde) {
        //System.out.println( "DockingPanel: dragDropEnd() called:" + dsde);
        cancelDragging();
    }

    /** Getter for property dockingFrame.
     * @return Value of property dockingFrame.
     */
    public DockingFrame getDockingFrame() {
        return dockingFrame;
    }

    /** Setter for property dockingFrame.
     * @param dockingFrame New value of property dockingFrame.
     */
    public void setDockingFrame(DockingFrame dockingFrame) {
        this.dockingFrame = dockingFrame;
    }

    /**
     *
     * @param e
     */
    @Override
    public void frameChange(FrameChangeEvent e) {
        updateFullTitleBar();
    }

    /**
     *
     */
    protected void updateFullTitleBar() {
        if (isMinimized()) {
            setFullTitleBar(true);
        }
        else if (getDockingFrame() == null || getDockingFrame().getPanelCount() == 1) {
            setFullTitleBar(false);
        }
        else {
            setFullTitleBar(true);
        }
    }

    @Override
    public void setVisible(boolean visible) {
        if (isVisible() != visible) {
            super.setVisible(visible);

        }

        if (!minimized) {
            if (visible) {
                if (dockingFrame == null) {
                    dockIntoFrame();
                }
                getDockingFrame().setVisible(true);
            }

            getDockingFrame().updateLayouts();
        }
    }

    /**
     *
     */
    public void showMinimizedPanel() {
        if (minimized && getDockingFrame() != null) {
            getDockingFrame().showMinimizedPanel(this);
        }
    }

    /**
     *
     */
    public void hideMinimizedPanel() {
        if (minimized && getDockingFrame() != null) {
            getDockingFrame().hideMinimizedPanel(this);
        }
    }

    @Override
    public void windowActivated(WindowEvent e) {
    }

    @Override
    public void windowClosed(WindowEvent e) {
    }

    @Override
    public void windowClosing(WindowEvent e) {
    }

    @Override
    public void windowDeactivated(WindowEvent e) {
    }

    @Override
    public void windowDeiconified(WindowEvent e) {
    }

    @Override
    public void windowIconified(WindowEvent e) {
    }

    @Override
    public void windowOpened(WindowEvent e) {
    }

    /**
     *
     * @param mb
     */
    public void setJMenuBar(JMenuBar mb) {
        menuBar = mb;
    }

    /**
     *
     * @return
     */
    public JMenuBar getJMenuBar() {
        return menuBar;
    }

    /**
     *
     * @return
     */
    @Override
    public int getLayoutWidth() {
        return layoutBounds.width;
    }

    /**
     *
     * @return
     */
    @Override
    public int getLayoutHeight() {
        return layoutBounds.height;
    }

    /**
     *
     * @param x
     * @param y
     * @param width
     * @param height
     */
    protected void setTitleBounds(int x, int y, int width, int height) {
        titleBounds.setBounds(x, y, width, height);

        updateControlLocations();

    }

    /**
     *
     */
    protected void updateControlLocations() {
        int x = (int) titleBounds.getX();
        int y = (int) titleBounds.getY();
        int width = (int) titleBounds.getWidth();
        int height = (int) titleBounds.getHeight();

        int leftIndent = 0;
        int rightIndent = 0;

        if (leftTexture != null) {
            leftIndent = leftTexture.getWidth(null);
            x += leftIndent;
            width -= leftIndent;
        }

        if (rightTexture != null) {
            rightIndent = rightTexture.getWidth(null);
            width -= rightIndent;
        }

        if (minimizable) {
            width -= minimizeButton.getWidth();
            minimizeButton.setLocation(leftIndent + width, (getTitleHeight() - minimizeButton.getHeight()) / 2);
        }

        titleLabel.setBounds(leftIndent + 2, 0, width - 4, height);

        repaint();
    }

    /**
     *
     * @return
     */
    protected Rectangle getTitleBounds() {
        return titleBounds;
    }

    @Override
    public Dimension getPreferredSize() {
        if (minimized == false && layoutBounds.width > 0 && layoutBounds.height > 0) {
            return layoutBounds.getSize();
        }
        else {
            //return new Dimension(11,11);
            return super.getPreferredSize();

        }
        /*   Dimension d = super.getPreferredSize();
        if ( layoutBounds.width > 0 ) d.width = layoutBounds.width;
        if ( layoutBounds.height > 0 ) d.height = layoutBounds.height;
        System.out.println( "Returning Preferred size for " + this + " of " + d );
        return layoutBounds.getSize(); */

    }

    @Override
    public Dimension getMinimumSize() {
        Dimension d = super.getMinimumSize();
        if (minimized) {
            if (isFullTitleBar()) {
                d.height += getTitleHeight();
            }
        }
        else {
            if (layoutBounds.width != 0 && d.width > layoutBounds.width) {
                d.width = layoutBounds.width;
            }
            if (layoutBounds.height != 0 && d.height > layoutBounds.height) {
                d.height = layoutBounds.height;
            }
        }

        return d;
        //return d;
    }

    @Override
    public String toString() {
        return getClass() + "[" + getName() + ", LayoutBounds: " + getLayoutBounds() + "]";
    }

    /** Getter for property layoutBounds.
     * @return Value of property layoutBounds.
     */
    @Override
    public Rectangle getLayoutBounds() {
        return (Rectangle) layoutBounds.clone();
    }

    /** Setter for property layoutBounds.
     * @param layoutBounds New value of property layoutBounds.
     */
    public void setLayoutBounds(Rectangle layoutBounds) {
        if (layoutBounds != null && this.layoutBounds.equals(layoutBounds) == false) {

            this.layoutBounds = layoutBounds;
            fireLayoutChanged();
        }
    }

    /**
     *
     * @param d
     */
    public void setLayoutSize(Dimension d) {
        this.layoutBounds.setSize(d);
        fireLayoutChanged();
    }

    /**
     *
     * @param p
     */
    public void setLayoutLocation(Point p) {
        layoutBounds.setLocation(p);
        fireLayoutChanged();
    }

    /**
     *
     * @return
     */
    @Override
    public Component getLayoutComponent() {
        return this;
    }

    /**
     *
     */
    public void resetLayout() {
        Rectangle r = this.getBounds();
        setLayoutBounds(getBoundsInContentPane(r));
    }

    /** Returns the location of the Panel in Frame Coordinates.
     *
     * This will take into account docking bars and menu bars.
     * @param r
     * @return
     */
    public Rectangle getBoundsInFrame(Rectangle r) {
        if (dockingFrame == null) {
            return getBounds();
        }

        if (r == null) {
            r = getBounds();
        }
        else {
            getBounds(r);
        }

        Container p = getParent();
        while (p != null && p instanceof JLayeredPane == false) {
            r.x += p.getX();
            r.y += p.getY();
            p = p.getParent();
        }
        return r;
    }

    /** Returns the location of the Panel in Frame Coordinates.
     *
     * This will take into account docking bars and menu bars.
     * @param r 
     * @return
     */
    public Rectangle getBoundsInContentPane(Rectangle r) {
        if (dockingFrame == null) {
            return getBounds();
        }

        if (r == null) {
            r = getBounds();
        }
        else {
            getBounds(r);
        }

        Container p = getParent();
        while (p != null && p != dockingFrame.getContentPane()) {
            r.x += p.getX();
            r.y += p.getY();
            p = p.getParent();
        }
        return r;
    }

    @Override
    public void setBounds(int x, int y, int width, int height) {
        super.setBounds(x, y, width, height);
        if (minimized == false && dockingFrame != null && dockingFrame.isVisible()) {
            resetLayout();
        }
        doLayout();
    }

    @Override
    public void setBounds(Rectangle r) {
        setBounds(r.x, r.y, r.width, r.height);
    }

    @Override
    public void setSize(Dimension d) {
        super.setSize(d);
        doLayout();
    }

    @Override
    public void setSize(int width, int height) {
        super.setSize(width, height);
        doLayout();
    }
    

    /**
     *  Adds a <code>DockingPanelListener</code> listener.
     *
     *  @param l  the <code>DockingPanelListener</code> to add
     */
    public void addDockingPanelListener(DockingPanelListener l) {
        dockingPanelListenerList.add(DockingPanelListener.class, l);
    }

    /**
     * Removes a <code>DockingPanelListener</code> listener.
     *
     * @param l  the <code>DockingPanelListener</code> to remove
     */
    public void removeDockingPanelListener(DockingPanelListener l) {
        dockingPanelListenerList.remove(DockingPanelListener.class, l);
    }

    /**
     *
     */
    protected void fireLayoutChanged() {
        ChangeEvent e = null;
        // Guaranteed to return a non-null array
        Object[] listeners = dockingPanelListenerList.getListenerList();
        // Process the listeners last to first, notifying
        // those that are interested in this event
        for (int i = listeners.length - 2; i >= 0; i -= 2) {
            if (listeners[i] == DockingPanelListener.class) {
                // Lazily create the event:
                e = new ChangeEvent(this);
                // System.out.println( "Firing chance event to: " + listeners[i+1].toString());
                ((DockingPanelListener) listeners[i + 1]).layoutChanged(e);
            }
        }
    }

    /**
     *
     * @param oldFrame
     * @param newFrame
     */
    protected void fireFrameChange(DockingFrame oldFrame, DockingFrame newFrame) {
        ChangeEvent e = null;
        // Guaranteed to return a non-null array
        Object[] listeners = dockingPanelListenerList.getListenerList();
        // Process the listeners last to first, notifying
        // those that are interested in this event
        for (int i = listeners.length - 2; i >= 0; i -= 2) {
            if (listeners[i] == DockingPanelListener.class) {
                // System.out.println( "Firing chance event to: " + listeners[i+1].toString());
                ((DockingPanelListener) listeners[i + 1]).frameChanged(this, oldFrame, newFrame);
            }
        }
    }

    /**
     *
     * @return
     */
    @Override
    public LayoutObject getPreferredSibling() {
        return null;
    }

    /** Setter for property leftImage.
     * @param leftImage New value of property leftImage.
     */
    static public void setLeftImage(Image leftImage) {
        DockingPanel.leftTexture = leftImage;

        Dimension d = getImageSize(leftImage);
        if (d != null) {
            setTitleHeight((int) d.height);
        }
    }

    /** Setter for property rightImage.
     * @param rightImage New value of property rightImage.
     */
    static public void setRightImage(Image rightImage) {
        DockingPanel.rightTexture = rightImage;
    }

    /** Setter for property centerImage.
     * @param centerImage New value of property centerImage.
     */
    static public void setCenterImage(Image centerImage) {
        DockingPanel.centerTexture = textureFromImage(centerImage);
    }

    /** Setter for property leftImage.
     * @param leftImage New value of property leftImage.
     */
    static public void setLeftImageMouseOver(Image leftImage) {
        DockingPanel.leftTextureMouseOver = leftImage;

    }

    /** Setter for property rightImage.
     * @param rightImage New value of property rightImage.
     */
    static public void setRightImageMouseOver(Image rightImage) {
        DockingPanel.rightTextureMouseOver = rightImage;
    }

    /** Setter for property centerImage.
     * @param centerImage New value of property centerImage.
     */
    static public void setCenterImageMouseOver(Image centerImage) {
        DockingPanel.centerTextureMouseOver = textureFromImage(centerImage);
    }

    /**
     *
     * @param image
     * @return
     */
    static public TexturePaint textureFromImage(Image image) {
        MediaTracker mt = new MediaTracker(new JPanel());
        mt.addImage(image, 0);
        Graphics2D g2 = null;
        try {
            mt.waitForID(0);
            BufferedImage bi = new BufferedImage(image.getWidth(null), image.getHeight(null), BufferedImage.TYPE_INT_RGB);
            g2 = bi.createGraphics();
            g2.drawImage(image, 0, 0, image.getWidth(null), image.getHeight(null), null);

            return new TexturePaint(bi, new Rectangle(0, 0, bi.getWidth(), bi.getHeight()));
        } catch (InterruptedException ie) {
            return null;
        } finally {
            if (g2 != null) {
                g2.dispose();
            }
        }
    }

    /**
     *
     * @param image
     * @return
     */
    static public Dimension getImageSize(Image image) {
        MediaTracker mt = new MediaTracker(new JPanel());
        mt.addImage(image, 0);
        Dimension d = new Dimension();
        try {
            mt.waitForID(0);
            d.width = image.getWidth(null);
            d.height = image.getHeight(null);
        } catch (InterruptedException ie) {
            return null;
        }
        return d;
    }

    /**
     *
     * @param c
     */
    static public void setTextColor(Color c) {
        textColor = c;
    }

    /**
     *
     * @param f
     */
    static public void setTextFont(Font f) {
        textFont = f;
    }

    /**
     * Getter for property fullTitleBar.
     * @return Value of property fullTitleBar.
     */
    public boolean isFullTitleBar() {
        return fullTitleBar;
    }

    /**
     * Setter for property fullTitleBar.
     * @param full
     */
    public void setFullTitleBar(boolean full) {
        if (this.fullTitleBar != full) {
            this.fullTitleBar = full;

            titleLabel.setVisible(full);
            handle.setVisible(!full);

            if (minimizeButton != null) {
                minimizeButton.setVisible(full && minimizable);
            }

            validate();
        }
    }

    /**
     *
     * @return
     */
    public boolean isSelected() {
        return selected;
    }

    /**
     *
     * @param selected
     */
    public void setSelected(boolean selected) {
        if (this.selected != selected) {
            this.selected = selected;
            repaint();
        }
    }

    /**
     *
     * @return
     */
    protected JButton createMinimizeButton() {
        JButton button = new JButton();
        button.setText("");

        Image handle = Toolkit.getDefaultToolkit().createImage(getClass().getResource("/dockable/graphics/titleWindowMinimize.gif"));
        Image handle2 = Toolkit.getDefaultToolkit().createImage(getClass().getResource("/dockable/graphics/titleWindowMinimizeMouseOver.gif"));
        Image handle3 = Toolkit.getDefaultToolkit().createImage(getClass().getResource("/dockable/graphics/titleWindowMinimizePressed.gif"));

        Dimension d = getImageSize(handle);

        button.setSize(d);

        button.setBorderPainted(false);

        button.setIcon(new ImageIcon(handle));
        button.setRolloverIcon(new ImageIcon(handle2));
        button.setPressedIcon(new ImageIcon(handle3));

        button.setToolTipText("Minimize Button");

        return button;
    }

    /**
     *
     * @return
     */
    static public int getTitleHeight() {
        return titleHeight;
    }

    /**
     *
     * @param titleHeight
     */
    static protected void setTitleHeight(int titleHeight) {
        DockingPanel.titleHeight = titleHeight;
    }

    /**
     *
     */
    static public class DockingPanelLayoutManager extends Object
            implements LayoutManager {

        /** Adds the specified component with the specified name to
         * the layout.
         * @param name the component name
         * @param comp the component to be added
         */
        @Override
        public void addLayoutComponent(String name, Component comp) {
            // Do nothing
        }

        /** Removes the specified component from the layout.
         * @param comp the component to be removed
         */
        @Override
        public void removeLayoutComponent(Component comp) {
            // Do nothing
        }

        /** Calculates the preferred size dimensions for the specified
         * panel given the components in the specified parent container.
         * @param parent the component to be laid out
         *
         * @return
         * @see #minimumLayoutSize
         */
        @Override
        @SuppressWarnings("static-access")
        public Dimension preferredLayoutSize(Container parent) {
            if (parent instanceof DockingPanel) {
                boolean fullTitleBar = ((DockingPanel) parent).isFullTitleBar();
                if (fullTitleBar) {
                    Insets i = parent.getInsets();
                    Dimension d = ((DockingPanel) parent).getContentPane().getPreferredSize();
                    d.width += i.left + i.right + 2;
                    d.height += i.top + i.bottom + DockingPanel.getTitleHeight() + 2;
                    return d;
                }
                else {
                    Insets i = parent.getInsets();
                    Dimension d = ((DockingPanel) parent).getContentPane().getPreferredSize();
                    d.width += i.left + i.right;
                    d.height += i.top + i.bottom;
                    return d;
                }
            }
            else {
                return parent.getPreferredSize();
            }
        }

        /** Calculates the minimum size dimensions for the specified
         * panel given the components in the specified parent container.
         * @param parent the component to be laid out
         * @return
         * @see #preferredLayoutSize
         */
        @Override
        @SuppressWarnings("static-access")
        public Dimension minimumLayoutSize(Container parent) {
            if (parent instanceof DockingPanel) {
                boolean fullTitleBar = ((DockingPanel) parent).isFullTitleBar();
                if (fullTitleBar) {
                    Insets i = parent.getInsets();
                    Dimension d = ((DockingPanel) parent).getContentPane().getMinimumSize();
                    d.width += i.left + i.right + 2;
                    d.height += i.top + i.bottom + DockingPanel.getTitleHeight() + 2;
                    return d;
                }
                else {
                    Insets i = parent.getInsets();
                    Dimension d = ((DockingPanel) parent).getContentPane().getMinimumSize();
                    d.width += i.left + i.right;
                    d.height += i.top + i.bottom;
                    return d;
                }
            }
            else {
                return parent.getMinimumSize();
            }
        }

        /** Lays out the container in the specified panel.
         * @param parent the component which needs to be laid out
         */
        @Override
        @SuppressWarnings("static-access")
        public void layoutContainer(Container parent) {
            if (parent instanceof DockingPanel) {
                DockingPanel dp = (DockingPanel) parent;
                synchronized (parent.getTreeLock()) {
                    Insets i = parent.getInsets();
                    boolean fullTitleBar = dp.isFullTitleBar();
                    int dhHeight;
                    if (fullTitleBar) {
                        dhHeight = DockingPanel.getTitleHeight();
                        dp.setTitleBounds(i.left, i.top, parent.getWidth(), dhHeight);
                        dp.getContentPane().setBounds(i.left + 1, i.top + 1 + dhHeight, parent.getWidth() - i.left - i.right - 2, parent.getHeight() - i.top - i.bottom - dhHeight - 2);
                        dp.getContentPane().validate();//Dimension d = dp.handle.getPreferredSize();
                        //dp.getContentPane().repaint();
                        //dp.handle.setBounds(i.left+1,i.top+1+dhHeight,d.width, d.height);

                    }
                    else {
                        dhHeight = 0;
                        //dp.setTitleBounds(i.left,i.top,parent.getWidth(),dhHeight);
                        dp.getContentPane().setBounds(i.left, i.top + dhHeight, parent.getWidth() - i.left - i.right, parent.getHeight() - i.top - i.bottom - dhHeight);
                        dp.getContentPane().validate();
                        Dimension d = dp.handle.getPreferredSize();
                        dp.handle.setBounds(i.left, i.top, d.width, d.height);
                    }
                }
            }
        }
    }

    /**
     *
     * @return
     */
    public boolean isMinimized() {
        return minimized;
    }

    /**
     *
     * @param minimized
     * @param callFrameMethods
     */
    protected void setMinimized(boolean minimized, boolean callFrameMethods) {
        if (minimized != this.minimized) {
            this.minimized = minimized;
            if (minimized) {
                resetLayout();
                preMinimizedLayoutBounds = layoutBounds;

                setVisible(false);

                updateFullTitleBar();

                // This will lead to a loop, but it should be alright...
                if (callFrameMethods) {
                    dockingFrame.minimizePanel(this, getPreferredMinimizedLocation());
                }
            }
            else {
                if (preMinimizedLayoutBounds != null) {
                    layoutBounds = preMinimizedLayoutBounds;
                }
                // This will lead to a loop, but it should be alright...
                if (callFrameMethods) {
                    dockingFrame.maximizePanel(this);
                }

                updateFullTitleBar();
            }

            fireLayoutChanged();
        }
    }

    /**
     *
     * @param minimized
     */
    public void setMinimized(boolean minimized) {
        setMinimized(minimized, true);
    }

    /**
     *
     * @return
     */
    public boolean isMinimizable() {
        return minimizable;
    }

    /**
     *
     */
    public void minimizePanel() {
        setMinimized(true);
    }

    /**
     *
     */
    public void maximizePanel() {
        setMinimized(false);
    }

    /**
     *
     * @param minimizable
     */
    public void setMinimizable(boolean minimizable) {
        if (this.minimizable != minimizable) {
            if (minimizable == true && minimizeButton == null) {
                minimizeButton = createMinimizeButton();
                minimizeButton.addActionListener(new ActionListener() {

                    @Override
                    public void actionPerformed(ActionEvent e) {
                        if (isMinimized() == false) {
                            minimizePanel();
                        }
                        else {
                            maximizePanel();
                        }
                    }
                });
                this.add(minimizeButton);
            }



            this.minimizable = minimizable;

            if (minimizeButton != null) {
                minimizeButton.setVisible(minimizable);
            }

            updateControlLocations();

            repaint();
        }
    }

//    public int getMinimizedLocation() {
//        return minimizedLocation;
//    }
//
//    public void setMinimizedLocation(int minimizedLocation) {
//        this.minimizedLocation = minimizedLocation;
//    }
    /**
     *
     * @return
     */
    public int getPreferredMinimizedLocation() {
        return preferredMinimizedLocation;
    }

    /**
     *
     * @param preferredMinimizedLocation
     */
    public void setPreferredMinimizedLocation(int preferredMinimizedLocation) {
        this.preferredMinimizedLocation = preferredMinimizedLocation;
    }

    /**
     *
     * @return
     */
    public Icon getPanelIcon() {
        return panelIcon;
    }

    /**
     *
     * @param panelIcon
     */
    public void setPanelIcon(Icon panelIcon) {
        this.panelIcon = panelIcon;
    }

    /**
     *
     * @param junk
     */
    public void setDefaultCloseOperation(int junk) {
    }

    /**
     *
     * @return
     */
    public int getDefaultCloseOperation() {
        return javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE;
    }
}
