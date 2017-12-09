/*
 * PADTree.java
 *
 * Created on February 27, 2002, 11:16 PM
 */

package champions.abilityTree.PADTree;

import champions.Target;
import champions.abilityTree.*;
import champions.interfaces.ChampionsConstants;
import java.awt.Color;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.UIManager;
import javax.swing.tree.TreePath;
import tjava.Destroyable;
import treeTable.DefaultTreeTable;
import treeTable.TreeTableHeader;



/**
 *
 * @author  twalker
 * @version
 */
public class PADTreeTable extends DefaultTreeTable
implements ChampionsConstants, Destroyable {
    
    protected PADTreeTableModel padTreeModel = null;
    
    /** Holds value of property target. */
    private Target target;
    
    /** Holds value of property tree. */
    private AbilityTreeTable abilityTreeTable;
    
    /** Holds the mask of the things you want to use. */
    private int mask;
    
    /** Holds the name you want for the title. */
    private String title;
    
    /** Creates new PADTree */
    public PADTreeTable(String title, int mask) {
        this.title = title;
        this.mask = mask;

        setLegacyDnDEnabled(true);
        
        setupModel();
       // setupListeners();  // This is called by DefaultTreeTable constructor
        
        TreeTableHeader tth = getTreeTableHeader();
        tth.getColumnModel().getColumn(0).setMinWidth(200);
        
        this.setRootVisible(false);
        
        setupUI();
    }
    
    public PADTreeTable() {
        this("Create Ability", ALL_MASK);
    }
    
    public void updateUI() {
        super.updateUI();
        
        setupUI();
    }
    
    private final void setupUI() {
        Color c;
        c = UIManager.getColor( "AbilityEditor.foreground" );
        if ( c != null ) this.setForeground(c);
        c = UIManager.getColor( "AbilityEditor.background" );
        if ( c != null ) this.setBackground(c);
    }
    
    protected void setupModel() {
      //  if ( padTreeModel == null ) {
            PADTreeTableNode root = new PADRootNode(mask);
            padTreeModel = new PADTreeTableModel(root, title);
            this.setTreeTableModel(padTreeModel);
      //  }
    }
    
    protected void setupListeners() {
        addMouseListener( new MouseAdapter() {
            /**
             * Invoked when the mouse has been clicked on a component.
             */
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2 && getAbilityTreeTable() != null  ) {
                    //int index = PADListListener.this.list.locationToIndex(e.getPoint());
                    TreePath clickPath = PADTreeTable.this.getSelectionPath();
                    TreePath abilityPath = abilityTreeTable.getSelectionPath();
                    if (abilityPath != null &&
                    clickPath != null && clickPath.getLastPathComponent() instanceof PADTreeTableNode ) {
                        ((PADTreeTableNode)clickPath.getLastPathComponent()).handleDoubleClick(PADTreeTable.this, abilityTreeTable, abilityPath);
                    }
                }
            }
        });
        
        addKeyListener( new KeyListener() {
            public void keyPressed(KeyEvent e) {
            }
            public void keyReleased(KeyEvent e) {
            }
            public void keyTyped(KeyEvent e) {
                TreePath clickPath = PADTreeTable.this.getSelectionPath();
                if ( abilityTreeTable != null ) {
                TreePath abilityPath = abilityTreeTable.getSelectionPath();
                    if (abilityPath != null &&
                    clickPath != null && clickPath.getLastPathComponent() instanceof PADTreeTableNode ) {
                        ((PADTreeTableNode)clickPath.getLastPathComponent()).handleKeyTyped(e, PADTreeTable.this, abilityTreeTable, abilityPath);
                    }
                }
            }
        });
    }
    
    /** Getter for property target.
     * @return Value of property target.
     */
    public Target getTarget() {
        return target;
    }
    
    /** Setter for property target.
     * @param target New value of property target.
     */
    public void setTarget(Target target) {
        this.target = target;
    }
    
    /** Getter for property tree.
     * @return Value of property tree.
     */
    public AbilityTreeTable getAbilityTreeTable() {
        return abilityTreeTable;
    }
    
    /** Setter for property tree.
     * @param tree New value of property tree.
     */
    public void setAbilityTreeTable(AbilityTreeTable tree) {
        this.abilityTreeTable = tree;
    }
    
    /** Getter for property mask.
     * @return Value of property mask.
     *
     */
    public int getMask() {
        return mask;
    }
    
    /** Setter for property mask.
     * @param mask New value of property mask.
     *
     */
    public void setMask(int mask) {
        if ( this.mask != mask ) {
            this.mask = mask;
            setupModel();
        }
    }
    
    /** Getter for property title.
     * @return Value of property title.
     *
     */
    public java.lang.String getTitle() {
        return title;
    }
    
    /** Setter for property title.
     * @param title New value of property title.
     *
     */
    public void setTitle(java.lang.String title) {
        this.title = title;
    }
    
    public void destroy() {
        super.destroy();
        
        padTreeModel = null;
        target = null;
        abilityTreeTable = null;
        title = null;
    }
    
}
