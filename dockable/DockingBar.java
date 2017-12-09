/*
 * DockingBar.java
 *
 * Created on September 7, 2005, 6:01 PM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 */

package dockable;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import javax.swing.JPanel;


/**
 *
 * @author 1425
 */
public class DockingBar extends JPanel {
    
    private int location;
    
    private DockingFrame frame;
    
    private ArrayList buttons = new ArrayList();
    
    /** Creates a new instance of DockingBar */
    public DockingBar(DockingFrame frame, int location) {
        setFrame(frame);
        setBarLocation(location);
        switch(location) {
            case DockingFrame.TOP_SIDE:
                setLayout( new FlowLayout(FlowLayout.LEFT, 2, 1));
                break;
            case DockingFrame.BOTTOM_SIDE:
                setLayout( new FlowLayout(FlowLayout.LEFT, 2, 1));
                break;
            case DockingFrame.LEFT_SIDE:
                setLayout( new VerticalFlowLayout(VerticalFlowLayout.TOP, 1,  2));
                break;
            case DockingFrame.RIGHT_SIDE:
                setLayout( new VerticalFlowLayout(VerticalFlowLayout.TOP, 1,  2));
                break;
        }
        
    }
    
    public void addPanel(DockingPanel panel) {
        DockingBarButton button = createButtonForPanel(panel);
        
        buttons.add(button);
        add(button);
    }
    
    public void removePanel(DockingPanel panel) {
        DockingBarButton dbb = getPanelButton(panel);
        if ( dbb != null ) {
            remove(dbb);
            buttons.remove(dbb);
        }
    }
    
    public boolean hasPanel(DockingPanel panel) {
        return getPanelButton(panel) != null;
    }
    
    public int getPanelCount() {
        return buttons.size();
    }
    
    public DockingPanel getPanel(int index) {
        return ((DockingBarButton)buttons.get(index)).getPanel();
    }
    
    protected DockingBarButton getPanelButton(DockingPanel panel) {
        for(int i = 0; i < buttons.size(); i++) {
            if ( ((DockingBarButton)buttons.get(i)).getPanel() == panel) {
                return (DockingBarButton)buttons.get(i);
            }
        }
        return null;
    }
    
    public void setPanelVisible(DockingPanel panel, boolean visible) {
        DockingBarButton dbb = getPanelButton(panel);
        if ( dbb != null ) {
            dbb.setSelected(visible);
        }
    }
    
    protected DockingBarButton createButtonForPanel(final DockingPanel panel) {
        DockingBarButton button = new DockingBarButton( panel, location );
        button.addActionListener( new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if ( panel.isVisible() ) {
                    getFrame().hideMinimizedPanel(panel);
                }
                else {
                    getFrame().showMinimizedPanel(panel);
                }
            }
        });
        
        return button;
    }
    
    public boolean hasPanels() {
        return buttons.size() > 0;
    }
    
    public int getBarLocation() {
        return location;
    }
    
    public void setBarLocation(int location) {
        this.location = location;
    }
    
   public DockingFrame getFrame() {
        return frame;
    }
    
    public void setFrame(DockingFrame frame) {
        this.frame = frame;
    }
    
 
    static public class VerticalFlowLayout implements LayoutManager, java.io.Serializable {

        /**
         * This value indicates that each row of components
         * should be left-justified.
         */
        public static final int TOP 	= 0;

        /**
         * This value indicates that each row of components
         * should be centered.
         */
        public static final int CENTER 	= 1;

        /**
         * This value indicates that each row of components
         * should be right-justified.
         */
        public static final int BOTTOM 	= 2;


        /**
         * <code>newAlign</code> is the property that determines
         * how each row distributes empty space for the Java 2 platform,
         * v1.2 and greater.
         * It can be one of the following three values:
         * <ul>
         * <code>LEFT</code>
         * <code>RIGHT</code>
         * <code>CENTER</code>
         * <code>LEADING</code>
         * <code>TRAILING</code>
         * </ul>
         *
         * @serial
         * @since 1.2
         * @see #getAlignment
         * @see #setAlignment
         */
        int newAlign;       // This is the one we actually use

        /**
         * The flow layout manager allows a seperation of
         * components with gaps.  The horizontal gap will
         * specify the space between components and between
         * the components and the borders of the
         * <code>Container</code>.
         *
         * @serial
         * @see #getHgap()
         * @see #setHgap(int)
         */
        int hgap;

        /**
         * The flow layout manager allows a seperation of
         * components with gaps.  The vertical gap will
         * specify the space between rows and between the
         * the rows and the borders of the <code>Container</code>.
         *
         * @serial
         * @see #getHgap()
         * @see #setHgap(int)
         */
        int vgap;

        /*
         * JDK 1.1 serialVersionUID
         */
         private static final long serialVersionUID = -7262534872234282631L;

        /**
         * Constructs a new <code>FlowLayout</code> with a centered alignment and a
         * default 5-unit horizontal and vertical gap.
         */
        public VerticalFlowLayout() {
            this(CENTER, 5, 5);
        }

        /**
         * Constructs a new <code>FlowLayout</code> with the specified
         * alignment and a default 5-unit horizontal and vertical gap.
         * The value of the alignment argument must be one of
         * <code>FlowLayout.LEFT</code>, <code>FlowLayout.RIGHT</code>,
         * <code>FlowLayout.CENTER</code>, <code>FlowLayout.LEADING</code>,
         * or <code>FlowLayout.TRAILING</code>.
         * @param align the alignment value
         */
        public VerticalFlowLayout(int align) {
            this(align, 5, 5);
        }

        /**
         * Creates a new flow layout manager with the indicated alignment
         * and the indicated horizontal and vertical gaps.
         * <p>
         * The value of the alignment argument must be one of
         * <code>FlowLayout.LEFT</code>, <code>FlowLayout.RIGHT</code>,
         * <code>FlowLayout.CENTER</code>, <code>FlowLayout.LEADING</code>,
         * or <code>FlowLayout.TRAILING</code>.
         * @param      align   the alignment value
         * @param      hgap    the horizontal gap between components
         *                     and between the components and the 
         *                     borders of the <code>Container</code>
         * @param      vgap    the vertical gap between components
         *                     and between the components and the 
         *                     borders of the <code>Container</code>
         */
        public VerticalFlowLayout(int align, int hgap, int vgap) {
            this.hgap = hgap;
            this.vgap = vgap;
            setAlignment(align);
        }

        /**
         * Gets the alignment for this layout.
         * Possible values are <code>FlowLayout.LEFT</code>,
         * <code>FlowLayout.RIGHT</code>, <code>FlowLayout.CENTER</code>,
         * <code>FlowLayout.LEADING</code>,
         * or <code>FlowLayout.TRAILING</code>.
         * @return     the alignment value for this layout
         * @see        java.awt.FlowLayout#setAlignment
         * @since      JDK1.1
         */
        public int getAlignment() {
            return newAlign;
        }

        /**
         * Sets the alignment for this layout.
         * Possible values are
         * <ul>
         * <li><code>FlowLayout.LEFT</code>
         * <li><code>FlowLayout.RIGHT</code>
         * <li><code>FlowLayout.CENTER</code>
         * <li><code>FlowLayout.LEADING</code>
         * <li><code>FlowLayout.TRAILING</code>
         * </ul>
         * @param      align one of the alignment values shown above
         * @see        #getAlignment()
         * @since      JDK1.1
         */
        public void setAlignment(int align) {
            this.newAlign = align;
        }

        /**
         * Gets the horizontal gap between components
         * and between the components and the borders
         * of the <code>Container</code>
         *
         * @return     the horizontal gap between components
         *             and between the components and the borders
         *             of the <code>Container</code>
         * @see        java.awt.FlowLayout#setHgap
         * @since      JDK1.1
         */
        public int getHgap() {
            return hgap;
        }

        /**
         * Sets the horizontal gap between components and
         * between the components and the borders of the
         * <code>Container</code>.
         *
         * @param hgap the horizontal gap between components
         *             and between the components and the borders
         *             of the <code>Container</code>
         * @see        java.awt.FlowLayout#getHgap
         * @since      JDK1.1
         */
        public void setHgap(int hgap) {
            this.hgap = hgap;
        }

        /**
         * Gets the vertical gap between components and
         * between the components and the borders of the
         * <code>Container</code>.
         *
         * @return     the vertical gap between components
         *             and between the components and the borders
         *             of the <code>Container</code>
         * @see        java.awt.FlowLayout#setVgap
         * @since      JDK1.1
         */
        public int getVgap() {
            return vgap;
        }

        /**
         * Sets the vertical gap between components and between
         * the components and the borders of the <code>Container</code>.
         *
         * @param vgap the vertical gap between components
         *             and between the components and the borders
         *             of the <code>Container</code>
         * @see        java.awt.FlowLayout#getVgap
         * @since      JDK1.1
         */
        public void setVgap(int vgap) {
            this.vgap = vgap;
        }

        /**
         * Adds the specified component to the layout.
         * Not used by this class.
         * @param name the name of the component
         * @param comp the component to be added
         */
        public void addLayoutComponent(String name, Component comp) {
        }

        /**
         * Removes the specified component from the layout.
         * Not used by this class.
         * @param comp the component to remove
         * @see       java.awt.Container#removeAll
         */
        public void removeLayoutComponent(Component comp) {
        }

        /**
         * Returns the preferred dimensions for this layout given the 
         * <i>visible</i> components in the specified target container.
         *
         * @param target the container that needs to be laid out
         * @return    the preferred dimensions to lay out the
         *            subcomponents of the specified container
         * @see Container
         * @see #minimumLayoutSize
         * @see       java.awt.Container#getPreferredSize
         */
        public Dimension preferredLayoutSize(Container target) {
          synchronized (target.getTreeLock()) {
            Dimension dim = new Dimension(0, 0);
            int nmembers = target.getComponentCount();
            boolean firstVisibleComponent = true;

            for (int i = 0 ; i < nmembers ; i++) {
                Component m = target.getComponent(i);
                if (m.isVisible()) {
                    Dimension d = m.getPreferredSize();
                    dim.width = Math.max(dim.width, d.width);
                    if (firstVisibleComponent) {
                        firstVisibleComponent = false;
                    } else {
                        dim.height += vgap;
                    }
                    dim.height += d.height;
                }
            }
            Insets insets = target.getInsets();
            dim.width += insets.left + insets.right + hgap*2;
            dim.height += insets.top + insets.bottom + vgap*2;
            return dim;
          }
        }

        /**
         * Returns the minimum dimensions needed to layout the <i>visible</i>
         * components contained in the specified target container.
         * @param target the container that needs to be laid out
         * @return    the minimum dimensions to lay out the
         *            subcomponents of the specified container
         * @see #preferredLayoutSize
         * @see       java.awt.Container
         * @see       java.awt.Container#doLayout
         */
        public Dimension minimumLayoutSize(Container target) {
          synchronized (target.getTreeLock()) {
            Dimension dim = new Dimension(0, 0);
            int nmembers = target.getComponentCount();

            for (int i = 0 ; i < nmembers ; i++) {
                Component m = target.getComponent(i);
                if (m.isVisible()) {
                    Dimension d = m.getMinimumSize();
                    dim.width = Math.max(dim.width, d.width);
                    if (i > 0) {
                        dim.height += vgap;
                    }
                    dim.height += d.height;
                }
            }
            Insets insets = target.getInsets();
            dim.width += insets.left + insets.right + hgap*2;
            dim.height += insets.top + insets.bottom + vgap*2;
            return dim;
          }
        }

        /**
         * Centers the elements in the specified row, if there is any slack.
         * @param target the component which needs to be moved
         * @param x the x coordinate
         * @param y the y coordinate
         * @param width the width dimensions
         * @param height the height dimensions
         * @param rowStart the beginning of the row
         * @param rowEnd the the ending of the row
         */
        private void moveComponents(Container target, int x, int y, int width, int height,
                                    int columnStart, int columnEnd, boolean ltr) {
          synchronized (target.getTreeLock()) {
            switch (newAlign) {
                case TOP:
                    y += ltr ? 0 : height;
                    break;
                case CENTER:
                    y += height / 2;
                    break;
                case BOTTOM:
                    y += ltr ? height : 0;
                    break;
            }
            for (int i = columnStart ; i < columnEnd ; i++) {
                Component m = target.getComponent(i);
                if (m.isVisible()) {
                    if (ltr) {
                        m.setLocation(x + (width - m.getWidth()) / 2, y);
                    } else {
                        m.setLocation(x + (width - m.getWidth()) / 2, target.getHeight() - y - m.getHeight());
                    }
                    y += m.getHeight() + vgap;
                }
            }
          }
        }

        /**
         * Lays out the container. This method lets each 
         * <i>visible</i> component take
         * its preferred size by reshaping the components in the
         * target container in order to satisfy the alignment of
         * this <code>FlowLayout</code> object.
         *
         * @param target the specified component being laid out
         * @see Container
         * @see       java.awt.Container#doLayout
         */
        public void layoutContainer(Container target) {
          synchronized (target.getTreeLock()) {
            Insets insets = target.getInsets();
            int maxheight = target.getHeight() - (insets.top + insets.bottom + vgap*2);
            int nmembers = target.getComponentCount();
            int y = 0 ;
            int x = insets.left + hgap;
            int columnw = 0, start = 0;

            boolean ltr = target.getComponentOrientation().isLeftToRight();

            for (int i = 0 ; i < nmembers ; i++) {
                Component m = target.getComponent(i);
                if (m.isVisible()) {
                    Dimension d = m.getPreferredSize();
                    m.setSize(d.width, d.height);

                    if ((y == 0) || ((y + d.height) <= maxheight)) {
                        if (y > 0) {
                            y += vgap;
                        }
                        y += d.height;
                        columnw = Math.max(columnw, d.width);
                    } else {
                        moveComponents(target, x, insets.top + vgap, columnw, maxheight-y, start, i, ltr);
                        y = d.height;
                        x += hgap + columnw;
                        columnw = d.width;
                        start = i;
                    }
                }
            }
            moveComponents(target, x, insets.top + vgap, columnw, maxheight-y, start, nmembers, ltr);
          }
        }

        /**
         * Returns a string representation of this <code>FlowLayout</code>
         * object and its values.
         * @return     a string representation of this layout
         */
        public String toString() {
            String str = "";
            switch (newAlign) {
              case TOP:        str = ",align=top"; break;
              case CENTER:      str = ",align=center"; break;
              case BOTTOM:       str = ",align=bottom"; break;
            }
            return getClass().getName() + "[hgap=" + hgap + ",vgap=" + vgap + str + "]";
        }


    }

    
}
