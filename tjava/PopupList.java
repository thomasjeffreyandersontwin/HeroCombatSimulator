/*
 * PopupList.java
 *
 * Created on June 28, 2001, 12:03 PM
 */

package tjava;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.MouseEvent;
import javax.swing.AbstractListModel;
import javax.swing.DefaultListModel;
import javax.swing.DefaultListSelectionModel;
import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListCellRenderer;
import javax.swing.ListModel;
import javax.swing.ListSelectionModel;
import javax.swing.Popup;
import javax.swing.PopupFactory;
import javax.swing.Scrollable;
import javax.swing.UIManager;
import javax.swing.border.EtchedBorder;
import javax.swing.event.AncestorEvent;
import javax.swing.event.AncestorListener;
import javax.swing.event.EventListenerList;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.MouseInputAdapter;


/**
 *
 * @author  twalker
 * @version
 */
public class PopupList extends JPanel
implements Scrollable, ListSelectionListener {
    
    static final long serialVersionUID = -5436012163130721853L;
    
    protected javax.swing.JList selectedList;
    protected javax.swing.JLabel iconLabel;
    protected javax.swing.JScrollPane scrollPane;
    
    /** Holds value of property model. */
    protected ListModel model;
    
    /** Holds value of property realSelectionModel.
     * The realSelectionModel is the selection model storing the selection for the whole control.
     * There are also two other selection models: the this.selectedList.getSelectionModel() stores the selection
     * for the display portion of the control.  And the getPopupList().getSelectionModel() stores the currently selected
     * indexes.
     */
    protected ListSelectionModel realSelectionModel;
    
    /** Holds value of property selectedItemsModel.
     * The selectedItemsModel is the model used by the JList visible in the control.
     * It is a subset of model, based on the current selection.
     */
    protected PopupList.SelectedItemsModel selectedItemsModel = new PopupList.SelectedItemsModel();
    
    /** Holds EventListenerList variable.
     */
    private EventListenerList eventListenerList = new EventListenerList();
    
    /** Popup Specific Variables */
    protected boolean hierarchySetup = false;
    protected JList popupList;
    protected JScrollPane popupScrollPane;
    protected JPanel popupPanel;
    protected JLabel popupCaption;
    
    protected JComponent popupCustomControls = null;
    
    protected boolean popupVisible = false;
    //   private static JWindow popupWindow;
    // private static JFrame popupWindow;
    //   private static PopupList popupOwner;
    //
    
    private Popup popup = null;
    /** Holds value of property minimumListSize. */
    private Dimension minimumListSize;
    
    private final static int DEBUG = 0;
    
    /** Creates new PopupList */
    public PopupList() {
        init();
        setModel( new DefaultListModel() );
        setSelectionModel( new DefaultListSelectionModel() );
    }
    
    protected void init() {
        scrollPane = new javax.swing.JScrollPane();
        selectedList = new javax.swing.JList();
        iconLabel = new javax.swing.JLabel();
        
        scrollPane.setOpaque(isOpaque());
        selectedList.setOpaque(isOpaque());
        iconLabel.setOpaque(isOpaque());
        
        setLayout(new java.awt.GridBagLayout());
        java.awt.GridBagConstraints gridBagConstraints1;
        
        gridBagConstraints1 = new java.awt.GridBagConstraints();
        gridBagConstraints1.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints1.weighty = 1.0;
        gridBagConstraints1.weightx = 1.0;
        add(scrollPane, gridBagConstraints1);
        
        gridBagConstraints1 = new java.awt.GridBagConstraints();
        gridBagConstraints1.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints1.weightx = 0.0;
        add(iconLabel, gridBagConstraints1);
        
        selectedList.setVisibleRowCount(5);
        
        scrollPane.setViewportView(selectedList);
        
        //    add(scrollPane, java.awt.BorderLayout.CENTER);
        
        iconLabel.setIcon(new javax.swing.ImageIcon(getClass().getResource("/graphics/listControlDropIcon.gif")));
        iconLabel.setVerticalAlignment(javax.swing.SwingConstants.TOP);
        
        //   add(iconLabel, java.awt.BorderLayout.EAST);
        
        selectedList.setBackground( getBackground() );
        iconLabel.setBackground( getBackground() );
        
        installMouseListener();
        installAncestorListener();
        
        // setup the models
        selectedList.setModel( selectedItemsModel );
        selectedList.setSelectionModel( new NullListSelectionModel());
        
        //   this.setBorder( new LineBorder( Color.red));
        selectedList.setCellRenderer( new CheckedCellRenderer() );
        
    }
    
    protected void installMouseListener() {
        iconLabel.addMouseListener( new MouseInputAdapter() {
            public void mousePressed(MouseEvent e) {
                if ( isPopupVisible() == false ) {
                    requestFocus();
                    showPopup();
                }
                else {
                    hidePopup();
                }
            }
        });
    }
    
    protected void installAncestorListener() {
        addAncestorListener(new AncestorListener(){
            public void ancestorAdded(AncestorEvent event){ /*hidePopup();*/}
            public void ancestorRemoved(AncestorEvent event){ hidePopup();}
            public void ancestorMoved(AncestorEvent event){ /*hidePopup();*/ }
        });
    }
    
    public void addListSelectionListener(ListSelectionListener listener) {
        eventListenerList.add(ListSelectionListener.class, listener);
    }
    
    public void addSelectionInterval(int anchor, int lead) {
        realSelectionModel.addSelectionInterval(anchor, lead);
        if ( isPopupVisible() ) {
            getPopupList().getSelectionModel().addSelectionInterval(anchor, lead);
        }
    }
    
    public void clearSelection() {
        realSelectionModel.clearSelection();
        if ( isPopupVisible() ) {
            getPopupList().getSelectionModel().clearSelection();
        }
    }
    
    protected ListSelectionModel createSelectionModel() {
        return new DefaultListSelectionModel();
    }
    
    public void ensureIndexIsVisible(int index) {
        selectedList.ensureIndexIsVisible(index);
    }
    
    protected void fireSelectionValueChanged(ListSelectionEvent e) {
        if ( e.getValueIsAdjusting() == true ) return;
        Object[] listeners = eventListenerList.getListenerList();
        for (int i = listeners.length - 2; i >= 0; i -= 2) {
            if (listeners[i] == ListSelectionListener.class) {
                ((ListSelectionListener)listeners[i+1]).valueChanged(e);
            }
        }
    }
    
    public int getAnchorSelectionIndex() {
        return realSelectionModel.getAnchorSelectionIndex();
    }
    
    public Rectangle getCellBounds(int index0, int index1) {
        return selectedList.getCellBounds(index0, index1);
    }
    
    public ListCellRenderer getCellRenderer() {
        return selectedList.getCellRenderer();
    }
    
    public int getFirstVisibleIndex() {
        return selectedList.getFirstVisibleIndex();
    }
    
    public int getFixedCellHeight() {
        return selectedList.getFixedCellHeight();
    }
    
    public int getFixedCellWidth() {
        return selectedList.getFixedCellWidth();
    }
    
    public int getLastVisibleIndex() {
        return selectedList.getLastVisibleIndex();
    }
    
    public int getLeadSelectionIndex() {
        return realSelectionModel.getLeadSelectionIndex();
    }
    
    public int getMaxSelectionIndex() {
        return realSelectionModel.getLeadSelectionIndex();
    }
    
    public int getMinSelectionIndex() {
        return realSelectionModel.getMinSelectionIndex();
    }
    
    public ListModel getModel() {
        return model;
    }
    
    public Dimension getPreferredScrollableViewportSize() {
        Dimension d = selectedList.getPreferredScrollableViewportSize();
        d.width += iconLabel.getPreferredSize().getWidth();
        return d;
    }
    
    public Dimension getPreferredSize() {
        Dimension d = selectedList.getPreferredScrollableViewportSize();
        if ( minimumListSize != null && d.width < minimumListSize.width ) d.width = minimumListSize.width;
        d.width += iconLabel.getPreferredSize().getWidth() + 2;
        d.height = 10;
        return d;
    }
    
    public Dimension getMinimumSize() {
        Dimension d = super.getMinimumSize();
        if ( minimumListSize != null && d.width < minimumListSize.width ){
            d.width = minimumListSize.width;
            d.width += iconLabel.getPreferredSize().getWidth() + 2;
        }
        return d;
    }
    
    public Object getPrototypeCellValue() {
        return selectedList.getPrototypeCellValue();
    }
    
    public int getScrollableBlockIncrement(Rectangle visibleRect, int orientation, int direction) {
        return selectedList.getScrollableBlockIncrement(visibleRect, orientation, direction);
    }
    
    public boolean getScrollableTracksViewportHeight() {
        return selectedList.getScrollableTracksViewportHeight();
    }
    
    public boolean getScrollableTracksViewportWidth() {
        return selectedList.getScrollableTracksViewportWidth() ;
    }
    
    public int getScrollableUnitIncrement(Rectangle visibleRect, int orientation, int direction) {
        return selectedList.getScrollableUnitIncrement(visibleRect, orientation, direction);
    }
    
    public int getSelectedIndex() {
        return realSelectionModel.getMinSelectionIndex();
    }
    
  /*  public int[] getSelectedIndices() {
        return realSelectionModel.getSelectedIndices();
    } */
    
    public Object getSelectedValue() {
        return null;
    }
    
    public Object[] getSelectedValues() {
        return null;
    }
    
    public Color getSelectionBackground() {
        return selectedList.getSelectionBackground();
    }
    
    public Color getSelectionForeground() {
        return selectedList.getSelectionForeground() ;
    }
    
    public int getSelectionMode() {
        return realSelectionModel.getSelectionMode();
    }
    
    public ListSelectionModel getSelectionModel() {
        return realSelectionModel;
    }
    
    public boolean getValueIsAdjusting() {
        return isPopupVisible();
    }
    
    public int getVisibleRowCount() {
        return selectedList.getVisibleRowCount();
    }
    
    public Point indexToLocation(int index) {
        return selectedList.indexToLocation(index);
    }
    
    public boolean isSelectedIndex(int index) {
        return realSelectionModel.isSelectedIndex(index);
    }
    
    public boolean isSelectionEmpty() {
        return realSelectionModel.isSelectionEmpty();
    }
    
    public int locationToIndex(Point location) {
        return selectedList.locationToIndex(location);
    }
    
    public void removeListSelectionListener(ListSelectionListener listener) {
        eventListenerList.remove(ListSelectionListener.class, listener);
    }
    
    public void removeSelectionInterval(int index0, int index1)  {
        realSelectionModel.removeSelectionInterval(index0,index1);
    }
    
    public void setCellRenderer(ListCellRenderer cellRenderer) {
        selectedList.setCellRenderer(cellRenderer);
    }
    
    public void setFixedCellHeight(int height) {
        selectedList.setFixedCellHeight(height);
    }
    
    public void setFixedCellWidth(int width) {
        selectedList.setFixedCellWidth(width);
    }
    
  /*  public void setListData(Object[] listData) {
   
    }
   
    public void setListData(Vector listData) {
   
    } */
    
    public void setModel(ListModel model) {
        this.model = model;
        hidePopup();
        selectedItemsModel.setModel(model);
        
        if ( hierarchySetup ) {
            popupList.setModel( getModel());
            popupList.setSelectionModel(getSelectionModel());
        }
    }
    
    public void setPrototypeCellValue(Object prototypeCellValue) {
        selectedList.setPrototypeCellValue(prototypeCellValue);
    }
    
    public void setSelectedIndex(int index) {
        realSelectionModel.setSelectionInterval(index,index);
        fireSelectionValueChanged( new ListSelectionEvent(this,index,index, false));
    }
    
    public void setSelectedIndices(int[] indices) {
        realSelectionModel.clearSelection();
        for(int i=0;i<indices.length;i++) {
            realSelectionModel.addSelectionInterval(indices[i], indices[i]);
        }
        
      //  if ( isPopupVisible() ) {
      //      setPopupSelection(realSelectionModel);
      //  }
        fireSelectionValueChanged( new ListSelectionEvent(this, -1,-1, false));
    }
    
    public void setSelectedIndices(ListSelectionModel model) {
        realSelectionModel.clearSelection();
        int count = model.getMaxSelectionIndex();
        for(int i=model.getMinSelectionIndex(); i <= count; i++) {
            if ( model.isSelectedIndex(i) ) realSelectionModel.addSelectionInterval(i,i);
        }
        fireSelectionValueChanged( new ListSelectionEvent(this, model.getMinSelectionIndex(), count, false));
    }
    
  /*  public void setSelectedValue(Object anObject, boolean shouldScroll) {
   
    } */
    
    public void setSelectionBackground(Color selectionBackground) {
        selectedList.setSelectionBackground(selectionBackground);
    }
    
    public void setSelectionForeground(Color selectionForeground) {
        selectedList.setSelectionForeground(selectionForeground);
    }
    
    public void setSelectionInterval(int anchor, int lead) {
        realSelectionModel.setSelectionInterval(anchor,lead);
        fireSelectionValueChanged( new ListSelectionEvent(this, anchor, lead, false));
        if ( isPopupVisible() ) {
            getPopupList().getSelectionModel().setSelectionInterval(anchor, lead);
        }
    }
    
    public void setSelectionMode(int selectionMode) {
        realSelectionModel.setSelectionMode(selectionMode);
    }
    
    public void setSelectionModel(ListSelectionModel selectionModel) {
        realSelectionModel = selectionModel;
        selectedItemsModel.setSelectionModel(realSelectionModel);
        fireSelectionValueChanged( new ListSelectionEvent(this, -1, -1, false));
        if ( hierarchySetup == true ) {
            getPopupList().setSelectionModel(selectionModel);
        }
    }
    
    public void setValueIsAdjusting(boolean b) {
        // Should really do this
    }
    
    public void setVisibleRowCount(int visibleRowCount) {
        selectedList.setVisibleRowCount(visibleRowCount);
    }
    
    /** Sets the custom popup controls.  
     * 
     * Custom popup controls are inserted at the bottom 
     * of the popup list, outside of the scroll panel.
     *
     * Setting it to null removes the custom controls.
     */
    public void setCustomPopupControls(JComponent c) {
        if ( popupCustomControls != null ) {
            if ( popupPanel != null ) popupPanel.remove(popupCustomControls);
        }
        
        popupCustomControls = c;
        
        if ( popupCustomControls != null ) {
            if ( popupPanel != null ) popupPanel.add(popupCustomControls, BorderLayout.SOUTH );
        }
    }
    
    public JComponent getCustomPopupControls() {
        return popupCustomControls;
    }
    
    protected void showPopup() {
        if ( DEBUG > 0 ) System.out.println("PopupList.showPopup(): Showing Popup.");
        if ( isPopupVisible() == false ) {
            // First Create the popup
            createPopup();
            if ( popup != null ) {
                
                popup.show();
                popupVisible = true;
            }
        }
    }
    
    protected void hidePopup() {
        if ( DEBUG > 0 ) System.out.println("PopupList.hidePopup(): Hiding Popup.");
        if ( isPopupVisible() == true && popup != null) {
            //setPopupVisible(this, false);
            popup.hide();
            popup = null;
            popupVisible = false;
        }
        
    }
    
    protected void createPopup() {
        if ( popup == null ) {
            
            if ( hierarchySetup == false ) {
                popupList = new JList();
                popupScrollPane = new JScrollPane();
                popupScrollPane.setViewportView(popupList);
                popupPanel = new JPanel();
                popupPanel.setLayout( new BorderLayout() );
                popupPanel.add(popupScrollPane, BorderLayout.CENTER);
                
                popupCaption = new JLabel("Add/Remove Items:");
                popupPanel.add(popupCaption, BorderLayout.NORTH);
                
                if (popupCustomControls != null ) popupPanel.add(popupCustomControls, BorderLayout.SOUTH);
                
                popupPanel.setBorder( new EtchedBorder() );
                
                hierarchySetup = true;
                
                popupList.setModel( getModel());
                popupList.setSelectionModel(getSelectionModel());
                
                popupList.setCellRenderer( new PopupCellRenderer());
                               
                FocusListener fl = new FocusListener() {
                    public void focusGained(FocusEvent e) {
                        if ( DEBUG > 0 ) System.out.println(e + "gained focus...");
                    }
                    public void focusLost(FocusEvent e) {
                        if ( DEBUG > 0 ) System.out.println(e + "lost focus...");
                        Component c = e.getOppositeComponent();
                        if ( e.isTemporary() == false && c != popupList && c != popupScrollPane
                            && c != popupScrollPane.getVerticalScrollBar() && c != popupScrollPane.getHorizontalScrollBar() ) {
                            hidePopup();
                        }
                    }
                };
                
                popupList.addFocusListener(fl);
                popupScrollPane.addFocusListener(fl);
                popupPanel.addFocusListener(fl);
            }
            
            PopupFactory pf = PopupFactory.getSharedInstance();
            Point p = getDropListLocation();
            popupPanel.setPreferredSize( getDropListSize() );
            popupPanel.invalidate();
            popup = pf.getPopup(this, popupPanel, p.x, p.y);
            
         /*   popupWindow.addWindowListener( new WindowListener() {
                public void windowActivated(WindowEvent e) {
                    System.out.println("popup windowActivated");
                }
                public void windowClosed(WindowEvent e) {
                    System.out.println("popup windowClosed");
                }
                public void windowClosing(WindowEvent e) {
                    System.out.println("popup windowClosing");
                }
                public void windowDeactivated(WindowEvent e) {
                    System.out.println("popup windowDeactivated");
                }
                public void windowDeiconified(WindowEvent e) {
                    System.out.println("popup windowDeiconified");
                }
                public void windowIconified(WindowEvent e) {
                    System.out.println("popup windowIconified");
                }
                public void windowOpened(WindowEvent e) {
                    System.out.println("popup windowOpened");
                }
            }); */
            
          /*  popupWindow.addWindowFocusListener( new WindowFocusListener() {
                public void windowGainedFocus(WindowEvent e) {
                    System.out.println("popup windowGainedFocus");
                }
                public void windowLostFocus(WindowEvent e) {
                    System.out.println("popup windowLostFocus");
                }
            }); */
            
            //isPopupSetup = true;
        }
    }
    
    /* Returns whether this PopupList is actually using the Popup menu.
     * @returns True if this PopupList is using the popup and the popup is visible.
     */
    protected boolean isPopupVisible() {
        return (popup != null && popupVisible);
    }
    
    protected JList getPopupList() {
        return popupList;
    }
    
    
    protected Dimension getDropListSize() {
        Dimension d = scrollPane.getSize();
       // if ( scrollPane.getWidth() > d.width ) d.width = scrollPane.getWidth();
        //if ( d.height < 50 ) d.height = 50;
        int h = (int)(popupScrollPane.getPreferredSize().getHeight()       
                            + popupCaption.getPreferredSize().getHeight());
        if ( popupCustomControls != null ) {
            h += popupCustomControls.getPreferredSize().getHeight();
        }
        
        d.height = (int)Math.max(d.height, h);
        return d;
    }
    
    protected Point getDropListLocation() {
        Point p = getLocationOnScreen();
        //p.y += 20;
        return p;
    }
    
    /**
     * Called whenever the value of the selection changes.
     * @param e the event that characterizes the change.
     */
    public void valueChanged(ListSelectionEvent e) {
        fireSelectionValueChanged(e);
    }
    
  /*  protected static void setPopupSelection(ListSelectionModel model) {
        popupList.clearSelection();
        int count = model.getMaxSelectionIndex();
        for(int i=model.getMinSelectionIndex(); i <= count; i++) {
            if ( model.isSelectedIndex(i) ) popupList.addSelectionInterval(i,i);
        }
    } */
    
 /*   protected static void setPopupVisible(PopupList owner, boolean visible) {
        initPopup();
        if ( visible ) {
            // First check to see if someone else is using the popup
            if ( owner.isPopupInUseByOther() ) {
                popupOwner.hidePopup();
            }
  
            if ( owner.isPopupVisible() == false ) {
                popupList.setModel(owner.getModel());
                setPopupSelection( owner.getSelectionModel() );
                //   popupList.addListSelectionListener( owner );
  
                Dimension d = owner.getDropListSize();
                Point p = owner.getDropListLocation();
  
                popupWindow.setLocation(p);
                popupWindow.setSize(d);
                popupScrollPane.setLocation(0,0);
                popupScrollPane.setSize(d);
  
                popupWindow.setVisible(true);
  
                popupOwner = owner;
            }
        }
        else {
            if ( owner.isPopupVisible() == true ) {
                popupWindow.setVisible(false);
                owner.setSelectedIndices( popupList.getSelectionModel() );
                //popupList.setModel(null);
                owner = null;
            }
        }
    } */
    
    public void setFont(Font font) {
        if ( selectedList != null ) selectedList.setFont(font);
        super.setFont(font);
    }
    
    public void setBackground(Color color) {
        if ( iconLabel != null ) iconLabel.setBackground(color);
        if ( selectedList != null ) selectedList.setBackground(color);
        super.setBackground(color);
    }
    
    public void setForeground(Color color) {
        if ( selectedList != null )  selectedList.setForeground(color);
        super.setForeground(color);
    }
    
    public void setOpaque(boolean opaque) {
        super.setOpaque(opaque);
        
        if ( scrollPane != null ) scrollPane.setOpaque(opaque);
        if ( selectedList != null ) selectedList.setOpaque(opaque);
        if ( iconLabel != null ) iconLabel.setOpaque(opaque);
    }
    
    /** Getter for property minimumListSize.
     * @return Value of property minimumListSize.
     */
    public Dimension getMinimumListSize() {
        return this.minimumListSize;
    }
    
    /** Setter for property minimumListSize.
     * @param minimumListSize New value of property minimumListSize.
     */
    public void setMinimumListSize(Dimension minimumListSize) {
        this.minimumListSize = minimumListSize;
    }
    
    private class SelectedItemsModel extends AbstractListModel
    implements ListModel, ListSelectionListener, ListDataListener {
        
        static final long serialVersionUID = -5130724360123161853L;
        
        /** Holds value of property model. */
        private ListModel model;
        
        /** Holds value of property selectionModel. */
        private ListSelectionModel selectionModel;
        
        private int size = 0;
        
        public SelectedItemsModel() {
            
        }
        /**
         * Returns the length of the list.
         */
        public int getSize() {
            return size;
        }
        
        /**
         * Returns the value at the specified index.
         */
        public Object getElementAt(int index) {
            int start, end;
            int i, count;
            
            end = model.getSize();
            
            count = 0;
            for(i=0; i < end; i++) {
                if ( selectionModel.isSelectedIndex(i) == true ) {
                    if ( count == index ) {
                        return model.getElementAt(i);
                    }
                    count ++;
                }
            }
            
            return null;
        }
        
        public void updateSize() {
            if ( model == null || selectionModel == null ) {
                size = 0;
            }
            else {
                if ( selectionModel.isSelectionEmpty() == true ) {
                    size = 0;
                }
                else {
                    int index, count;
                    
                    count = model.getSize();
                    
                    size = 0;
                    for( index = 0; index < count; index++) {
                        if ( selectionModel.isSelectedIndex(index) == true ) {
                            size ++;
                        }
                    }
                }
            }
            
            fireContentsChanged(this, -1,-1);
        }
        
        /** Getter for property model.
         * @return Value of property model.
         */
        public ListModel getModel() {
            return model;
        }
        
        /** Setter for property model.
         * @param model New value of property model.
         */
        public void setModel(ListModel model) {
            if ( this.model != model ) {
                if ( this.model != null ) {
                    this.model.removeListDataListener(this);
                }
                
                this.model = model;
           
                if ( this.model != null ) {
                    this.model.addListDataListener(this);
                }
            }
            updateSize();
        }
        
        /** Getter for property selectionModel.
         * @return Value of property selectionModel.
         */
        public ListSelectionModel getSelectionModel() {
            return selectionModel;
        }
        
        /** Setter for property selectionModel.
         * @param selectionModel New value of property selectionModel.
         */
        public void setSelectionModel(ListSelectionModel selectionModel) {
            if ( this.selectionModel != selectionModel ) {
                if ( this.selectionModel != null ) {
                    this.selectionModel.removeListSelectionListener(this);
                }
                this.selectionModel = selectionModel;
                updateSize();
                if ( this.selectionModel != null ) {
                    this.selectionModel.addListSelectionListener(this);
                }
            }
            
        }
        
        /**
         * Called whenever the value of the selection changes.
         * @param e the event that characterizes the change.
         */
        public void valueChanged(ListSelectionEvent e) {
            if ( e.getValueIsAdjusting() == false ) {
                updateSize();
                fireSelectionValueChanged( new ListSelectionEvent(PopupList.this, -1, -1, false) );
            }
        }

        public void intervalAdded(ListDataEvent e) {
            selectionModel.insertIndexInterval(e.getIndex0(), Math.abs(e.getIndex0()-e.getIndex1())+1, true);
        }

        public void intervalRemoved(ListDataEvent e) {
            selectionModel.removeIndexInterval(e.getIndex0(), e.getIndex1());
        }

        public void contentsChanged(ListDataEvent e) {
            selectionModel.removeSelectionInterval(e.getIndex0()+1, e.getIndex0()-1);
        }
        
    }
    
    private class NullListSelectionModel extends DefaultListSelectionModel {
        
        public boolean isSelectedIndex(int index) {
            return false;
        }
        
        public boolean isSelectionEmpty() {
            return true;
        }
        
    }
    
    class CheckedCellRenderer extends JLabel implements ListCellRenderer {
        
        private Icon checkedIcon = null;
        private Icon uncheckedIcon = null;
        
        public CheckedCellRenderer() {
            checkedIcon = UIManager.getIcon("PopupList.checkedIcon");
            uncheckedIcon = UIManager.getIcon("PopupList.uncheckedIcon");
        }
        // This is the only method defined by ListCellRenderer.
        // We just reconfigure the JLabel each time we're called.
        
        public Component getListCellRendererComponent(
        JList list,
        Object value,            // value to display
        int index,               // cell index
        boolean isSelected,      // is the cell selected
        boolean cellHasFocus)    // the list and the cell have the focus
        {
            String s = value.toString();
            setText(s);
            
            setIcon(checkedIcon);
            
            if (isSelected) {
                setBackground(list.getSelectionBackground());
                setForeground(list.getSelectionForeground());
            }
            else {
                setBackground(list.getBackground());
                setForeground(list.getForeground());
            }
            setEnabled(list.isEnabled());
            setFont(list.getFont());
            setOpaque(false);
            return this;
        }
    }
    
    class PopupCellRenderer extends JLabel implements ListCellRenderer {
        
        private Icon checkedIcon = null;
        private Icon uncheckedIcon = null;
        
        public PopupCellRenderer() {
            checkedIcon = UIManager.getIcon("PopupList.checkedIcon");
            uncheckedIcon = UIManager.getIcon("PopupList.uncheckedIcon");
        }
        // This is the only method defined by ListCellRenderer.
        // We just reconfigure the JLabel each time we're called.
        
        public Component getListCellRendererComponent(
        JList list,
        Object value,            // value to display
        int index,               // cell index
        boolean isSelected,      // is the cell selected
        boolean cellHasFocus)    // the list and the cell have the focus
        {
            String s = value.toString();
            setText(s);
            if (isSelected) {
                setBackground(list.getSelectionBackground());
                setForeground(list.getSelectionForeground());
                
                setIcon(checkedIcon);
            }
            else {
                setBackground(list.getBackground());
                setForeground(list.getForeground());
                
                setIcon(uncheckedIcon);
            }
            setEnabled(list.isEnabled());
            setFont(list.getFont());
            setOpaque(true);
            return this;
        }
    }
    
    public static class ToggleSelectionModel extends DefaultListSelectionModel
    {
        boolean gestureStarted = false;

        public void setSelectionInterval(int index0, int index1) {
            if ( DEBUG >= 1 ) System.out.println("PopupList.ToggleSelectionModel.setSelectionInterval(" 
                                + index0 + ", " + index1 + ")");
            if (isSelectedIndex(index0) && !gestureStarted) {
                super.removeSelectionInterval(index0, index1);
            }
            else {
                super.addSelectionInterval(index0, index1);
            }
            gestureStarted = true;
        }
        
        public void addSelectionInterval(int index0, int index1) {
            if ( DEBUG >= 1 )System.out.println("PopupList.ToggleSelectionModel.addSelectionInterval(" 
                                + index0 + ", " + index1 + ")");
            if (isSelectedIndex(index0) && !gestureStarted) {
                super.removeSelectionInterval(index0, index1);
            }
            else {
                super.addSelectionInterval(index0, index1);
            }
            gestureStarted = true;
        }
        
        public void removeSelectionInterval(int index0, int index1) {
            if ( DEBUG >= 1 )System.out.println("PopupList.ToggleSelectionModel.removeSelectionInterval(" 
                                + index0 + ", " + index1 + ")");
            if (isSelectedIndex(index0) && !gestureStarted) {
                super.removeSelectionInterval(index0, index1);
            }
            else {
                super.addSelectionInterval(index0, index1);
            }
            gestureStarted = true;
        }

        public void setValueIsAdjusting(boolean isAdjusting) {
            if (isAdjusting == false) {
                gestureStarted = false;
            }
            super.setValueIsAdjusting(isAdjusting);
        }
    } 
}
