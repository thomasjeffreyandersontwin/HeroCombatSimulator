/*
 * AttackTreeCellRenderer.java
 *
 * Created on June 11, 2001, 10:02 PM
 */

package champions.attackTree;

import champions.interfaces.ChampionsConstants;
import java.awt.*;

import java.awt.image.BufferedImage;
import java.io.Serializable;
import javax.swing.*;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreeCellRenderer;

/**
 *
 * @author  twalker
 * @version
 */
public class AttackTreeCellRenderer extends DefaultTreeCellRenderer 
implements TreeCellRenderer, ChampionsConstants {
    
    private JPanel panel = new JPanel();
    private JPanel container = new JPanel();
    //   private JLabel label = new JLabel();
    private IconTextSpacer spacer = new IconTextSpacer();
    
    private JTree tree;
    
    protected ImageIcon currentIcon = null;
    protected BufferedImage iconBuffer = null;
    protected ImageIcon editIcon = null;
    protected BufferedImage editIconBuffer = null;
    protected Icon criticalIcon, errorIcon, questionIcon;
    protected Icon childCriticalIcon, childErrorIcon, childQuestionIcon;
    /** Creates new AttackTreeCellRenderer */
    public AttackTreeCellRenderer() {
        //setOpaque(false);
        panel.setOpaque(false);
        container.setOpaque(false);
        container.setLayout( new BorderLayout() );
        
        panel.setLayout(new FlowLayout( FlowLayout.LEFT,0,0));
        
        spacer.setSpacerSize(1,1);
        //spacer.setBorder( new LineBorder(Color.yellow));
        
        panel.add(this);
        panel.add(spacer);
        panel.add(container);
        
        //    label.setBorder( new LineBorder( Color.yellow ) );
        //   container.setBorder( new LineBorder( Color.pink ) );
        criticalIcon = UIManager.getIcon( "AttackTree.criticalIcon" );
        errorIcon = UIManager.getIcon( "AttackTree.errorIcon" );
        questionIcon = UIManager.getIcon( "AttackTree.questionIcon" );
        childCriticalIcon = UIManager.getIcon( "AttackTree.childCriticalIcon" );
        childErrorIcon = UIManager.getIcon( "AttackTree.childErrorIcon" );
        childQuestionIcon = UIManager.getIcon( "AttackTree.childQuestionIcon" ); 
        
        currentIcon = new ImageIcon();
        iconBuffer = new BufferedImage(16,16, BufferedImage.TYPE_INT_RGB);
        currentIcon.setImage(iconBuffer);
        
        editIcon = new ImageIcon();
        editIconBuffer = new BufferedImage(16,16, BufferedImage.TYPE_INT_RGB);
        editIcon.setImage(editIconBuffer);

       setupColors();
    }
    
    public void setupColors() {
      //  panel.setBackground( UIManager.getColor("AbilityEditor.background") );
      //  container.setBackground( UIManager.getColor("AbilityEditor.background") );
      //  this.setBackgroundNonSelectionColor( UIManager.getColor("AbilityEditor.background") );
      //  this.setTextNonSelectionColor( UIManager.getColor("AbilityEditor.foreground") );
    }
    
    /**
     * Sets the value of the current tree cell to <code>value</code>.
     * If <code>selected</code> is true, the cell will be drawn as if
     * selected. If <code>expanded</code> is true the node is currently
     * expanded and if <code>leaf</code> is true the node represets a
     * leaf anf if <code>hasFocus</code> is true the node currently has
     * focus. <code>tree</code> is the JTree the receiver is being
     * configured for.
     * Returns the Component that the renderer uses to draw the value.
     *
     * @return	Component that the renderer uses to draw the value.
     */
    public Component getTreeCellRendererComponent(JTree tree, Object value, boolean selected, boolean expanded, boolean leaf, int row, boolean hasFocus) {
        super.getTreeCellRendererComponent(tree,value,selected,expanded,leaf,row,hasFocus);
        
        this.tree = tree;
        
        if ( value instanceof AttackTreeNode) {
            AttackTreeNode atn = (AttackTreeNode)value;
            
            Icon i = atn.getIcon(tree,selected,expanded,leaf,row,hasFocus);
            if ( i == null && leaf == false ) {
                if ( expanded ) {
                    i = super.getOpenIcon();
                }
                else {
                    i = super.getClosedIcon();
                }
            }
            makeIcon(i,atn);
            
            this.setIcon(currentIcon);
            
            TreeCellRenderer r = atn.getTreeCellRenderer(tree);
            
            container.removeAll();
            Component c = null;
            if ( r != null ) {
                c = r.getTreeCellRendererComponent(tree,value,selected,expanded,leaf,row,hasFocus);
            }
            
            if ( c != null ) {
                spacer.setVisible(true);
                this.setText("");
                container.add(c);
            }
            else {
                // Use the default of a text label with string
                spacer.setVisible(false);
                this.setText( value.toString() );
            }
        }
        return panel;
    }
    
    public Icon getEditIcon() {
        makeEditIcon();
        return editIcon;
    }
    
    

    
    public int getNodeStatus(AttackTreeNode node) {
        int status = node.getNodeStatus();
        
        int childStatus;
        int realStatus;
        
        realStatus = status;
        
        AttackTreeNode child;
        
        int index,count;
        count = node.getChildCount();
        for(index=0;index<count && status != CRITICAL_STATUS ;index++) {
            child = (AttackTreeNode)node.getChildAt(index);
            childStatus = getNodeStatus(child);
            if ( childStatus < status ) {
                status = childStatus;
                
                switch (childStatus) {
                    case CRITICAL_STATUS:
                        realStatus = CHILD_CRITICAL_STATUS;
                        break;
                    case ERROR_STATUS:
                        realStatus = CHILD_ERROR_STATUS;
                        break;
                    case QUESTION_STATUS:
                        realStatus = CHILD_QUESTION_STATUS;
                        break;
                        default:
                            realStatus = childStatus;
                }
            }
        }
        return realStatus;
    }
    
    public void makeIcon(Icon nodeIcon, AttackTreeNode node) {
        // currentIcon = new ImageIcon();
        //  iconBuffer = new BufferedImage(16,16, BufferedImage.TYPE_INT_RGB);
        //  currentIcon.setImage(iconBuffer);
        Graphics g = iconBuffer.getGraphics();
        
        if ( g != null ) {
            g.setColor(tree.getBackground());
            g.fillRect(0,0,16,16);
            
            if (nodeIcon != null ) {
                nodeIcon.paintIcon(panel, g, 0, 0);
            }
            
            int status = getNodeStatus(node);
            switch( status ) {
                case CRITICAL_STATUS:
                    if ( criticalIcon != null ) criticalIcon.paintIcon(panel, g, 0, 0);
                    break;
                case ERROR_STATUS:
                    if ( errorIcon != null ) errorIcon.paintIcon(panel, g, 0, 0);
                    break;
                case QUESTION_STATUS:
                    if ( questionIcon != null ) questionIcon.paintIcon(panel, g, 0, 0);
                    break;
                case CHILD_CRITICAL_STATUS:
                    if ( childCriticalIcon != null ) childCriticalIcon.paintIcon(panel, g, 0, 0);
                    break;
                case CHILD_ERROR_STATUS:
                    if ( childErrorIcon != null ) childErrorIcon.paintIcon(panel, g, 0, 0);
                    break;
                case CHILD_QUESTION_STATUS:
                    if ( childQuestionIcon != null ) childQuestionIcon.paintIcon(panel, g, 0, 0);
                    break;
                    
            }
            g.dispose();
        }
    }
    
    public void makeEditIcon() {
        Graphics g = editIconBuffer.getGraphics();
        
        if ( g != null ) {
            
            if (currentIcon != null ) {
                currentIcon.paintIcon(panel, g, 0, 0);
            }
            g.dispose();
        }
    }
    
    public class IconTextSpacer extends JPanel
    implements Serializable {
        private Dimension spacerSize = null;
        
        public IconTextSpacer() {
            setLayout(null);
        }
        
        public void setSpacerSize(int x, int y) {
            Dimension d = new Dimension(x,y);
            setPreferredSize(d);
            setMinimumSize(d);
        }
        
        public Dimension getPreferredSize() {
            return (spacerSize != null ) ? spacerSize : super.getPreferredSize();
        }
        
        public Dimension getMinimumSize() {
            return (spacerSize != null ) ? spacerSize : super.getPreferredSize();
        }
    }
    
}
